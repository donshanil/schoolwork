#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include <unistd.h>

#include "process.h"
#include "utils.h"
#include "algorithms.h"
#include "memory.h"
#include "bstrlib.h"

extern int optind;
extern char* optarg;

void usage(char* name);
void init_scheduler(Scheduler* s, unsigned int mem_size);
void read_input(char* filename, Scheduler* s, unsigned int mem_size);
void parse_input(bstring* input, Scheduler* s, unsigned int mem_size);
void print_status(Scheduler *s);
void print_queue(Scheduler *s, int index);
void print_memory(Scheduler *s);
int simulation(Scheduler* s, Algorithm alg);

int main(int argc, char** argv)
{
	int c;
	char* filename = NULL;
	int memsize  = -1;
	int algorithm = -1;
	int end_time;
	Scheduler s;

	while ((c = getopt(argc, argv, "f:a:m:")) != EOF)
	{
		switch (c)
		{
		case 'f':
			filename = optarg;
			break;
		case 'a':
			if (strcmp(optarg, "fcfs") == 0)
				algorithm = fcfs;
			else if (strcmp(optarg, "rr") == 0)
				algorithm = rr;
			else if (strcmp(optarg, "mlfq_np") == 0)
				algorithm = mlfq_np;
			else if (strcmp(optarg, "mlfq_pre") == 0)
				algorithm = mlfq_pre;
			else
			{
				fprintf(stderr, "ERROR: Unknown algorithm: %s\n", optarg);
				usage(argv[0]);
			}
			break;
		case 'm':
			memsize = atoi(optarg);
			break;
		default:
			usage(argv[0]);
			break;
		}
	}
	if (filename == NULL)
	{
		fprintf(stderr, "ERROR:	An input file must be specified\n");
		usage(argv[0]);
	}

	if (algorithm == -1)
	{
		fprintf(stderr, "ERROR: A placement algorithm to be used must be specified\n");
		usage(argv[0]);
	}

	if (memsize == -1)
	{
		fprintf(stderr, "ERROR: The size of main memory must be specified\n");
		usage(argv[0]);
	}
	assert(memsize > 0);

	//initialisation
	read_input(filename, &s, memsize);
	//simulation
	end_time = simulation(&s, algorithm);
	printf("time %d, simulation finished.\n", end_time);

	return EXIT_SUCCESS;
}

void usage(char* name)
{
	char* ptr;
	//get the basename of program
	ptr = strrchr(name, '/');
	if (ptr == NULL)
		ptr = name;
	else
		ptr++;

	fprintf(stderr, "usage: %s -f filename -a algorithm {fcfs,rr,mlfq_pre,mlfq_np} -m memsize\n", ptr);

	exit(EXIT_FAILURE);
}

void read_input(char* filename, Scheduler* s, unsigned int mem_size)
{
	FILE* fp;
	bstring fp_bs;
	fp = fopen(filename, "rb");
	if(fp == NULL)
	{
		fprintf(stderr, "ERROR: Unable to read from file\n");
		exit(EXIT_FAILURE);
	}
	//read whole file into a bstring
	fp_bs = bread ((bNread) fread, fp);
	init_scheduler(s, mem_size);
	parse_input(&fp_bs, s, mem_size);
	fclose (fp);
	bdestroy (fp_bs);
	return;
}


void init_scheduler(Scheduler* s, unsigned int mem_size)
{
	s->clock_time = 0;
	s->not_started = NULL;
	for(unsigned int i = 0; i < MLFQ_LEVELS; i++)
		s->runnable[i] = NULL;
	s->blocked = NULL;
	s->running = NULL;
	s->finished = NULL;
	s->running_queue_level = 0;
	s->running_quantum_remaining = 0;
	
	MemoryChunk* empty_memory = malloc_wrapper(sizeof(MemoryChunk));
	empty_memory->next = NULL;
	empty_memory->size = mem_size;
	empty_memory->time = 0;
	empty_memory->pid = free_mem;
	s->memory = empty_memory;
	return;
}

void parse_input(bstring* input, Scheduler* s, unsigned int mem_size)
{
	bstring start = bfromcstr("start");
	bstring end = bfromcstr("end");
	struct bstrList* lines = bsplit(*input, '\n');
	debug_printf("%d lines\n", lines->qty);
	ProcessList* last_not_started = NULL;
	for(int i = 0; i < lines->qty; i++)
	{
		struct bstrList* words;
		words = bsplit(lines->entry[i], ' ');
		debug_printf("%d words - ", words->qty);
		for(int j = 0; j < words->qty; j++)
		{
			debug_printf("%s - ", bstr2cstr(words->entry[j], ' '));
		}
		debug_printf("\n");
		if(words->qty < 6)
			continue;
		ProcessList* new_process = malloc_wrapper(sizeof(ProcessList));
		new_process->next = NULL;
		for(unsigned int j = 0; j < MAX_RUNTIMES; j++)
		{
			new_process->proc.runtime[j] = 0;
			new_process->proc.runtime_completed[j] = 0;
			new_process->proc.iotime[j] = 0;
			new_process->proc.iotime_completed[j] = 0;
		}
		new_process->proc.time_created = atoi(bstr2cstr(words->entry[0], ' '));
		new_process->proc.pid = atoi(bstr2cstr(words->entry[1], ' '));
		new_process->proc.mem_size = atoi(bstr2cstr(words->entry[2], ' '));
		assert(new_process->proc.mem_size <= mem_size);
		assert(biseq(start, words->entry[3]));
		new_process->proc.num_runtimes = 0;
		new_process->proc.num_iotimes = 0;
		for(int j = 4; j < words->qty; j++)
		{
			if(biseq(end, words->entry[j]))
				break;
			if(j % 2 == 0)
			{
				//even number, this string is a runtime
				new_process->proc.runtime[new_process->proc.num_runtimes] = atoi(bstr2cstr(words->entry[j], ' '));
				assert(new_process->proc.runtime[new_process->proc.num_runtimes] > 0);
				new_process->proc.num_runtimes++;
			}
			else
			{
				//odd number, this string is an iotime
				new_process->proc.iotime[new_process->proc.num_iotimes] = atoi(bstr2cstr(words->entry[j], ' '));
				assert(new_process->proc.iotime[new_process->proc.num_iotimes] > 0);
				new_process->proc.num_iotimes++;
			}
		}
		//should be an odd number of runtimes and iotimes
		unsigned int total = new_process->proc.num_runtimes + new_process->proc.num_iotimes;
		unsigned int odd_even = total % 2;
		assert(odd_even == 1);
		//add to scheduler's not started list
		if(s->not_started == NULL)
			s->not_started = new_process;
		else
			last_not_started->next = new_process;
		last_not_started = new_process;
		bstrListDestroy(words);
	}
	bstrListDestroy(lines);
	return;
}

int simulation(Scheduler* s, Algorithm alg)
{
	while(true)
	{
		//update the progress time on running and blocked processes
		update_times(s);
		//if the currently running process is terminating
		terminating_process(s);
		//if the currently running process is blocking
		blocking_process(s);
		//if there are any new processes being created
		start_new_processes(s);
		//if the currently running process has a quantum expiring
		end_quantum(s, alg);
		//if a newly queued process should preempt the running one
		preempt(s, alg);
		if(!work_remaining(s))
			break;
		//if the processor is running a process, keep going to the next timestep
		//otherwise, choose a process to run (if available)
		if(schedule(s, alg))
		{
			//if the process to run is not in memory
			if(!is_in_memory(s, s->running->proc.pid))
			{
				while(!first_fit(s))
				{
					free_biggest_oldest(s);
				}
			}
			print_status(s);
		}
		//update the time for the next cycle
		s->clock_time++;
		//merge_memory_holes(s);
		//debug_printf("clock time is now %d\n", s->clock_time);
		//print_status(s);
	}
	return s->clock_time;
}

unsigned int memory_in_use_fraction(Scheduler *s)
{
	unsigned int used_mem = memory_in_use(s);
	unsigned int total_mem = memory_size(s);
	assert(total_mem > 0);
	unsigned int fraction = ((100 * used_mem) + (total_mem - 1)) / total_mem;
	return fraction;
}

void print_status(Scheduler* s)
{
	int time = s->clock_time;
	int process = s->running->proc.pid;
	int in_mem = processes_in_memory(s);
	int holes = holes_in_memory(s);
	int memusage = memory_in_use_fraction(s);
	printf("time %d, %d running, numprocesses=%d, numholes=%d, memusage=%d%%\n",
		time, process, in_mem, holes, memusage);
	return;
}

void print_queue(Scheduler* s, int index)
{
	debug_printf("queue %d ->\n", index);
	ProcessList* curr;
	if(index >= 0)
		curr = s->runnable[index];
	else
		curr = s->not_started;
	while(curr != NULL)
	{
		debug_printf("(pid %d mem %d created %d) ->\n",
		curr->proc.pid, curr->proc.mem_size, curr->proc.time_created);
		curr = curr->next;
	}
	debug_printf("(END)\n");
	return;
}

void print_memory(Scheduler* s)
{
	debug_printf("memory ->");
	MemoryChunk* curr = s->memory;
	while(curr != NULL)
	{
		if(curr->pid < 0)
			debug_printf("(free area size %d time %d) ->", curr->size, curr->time);
		else
			debug_printf("(pid %d size %d time %d) ->", curr->pid, curr->size, curr->time);
		curr = curr->next;
	}
	debug_printf("(END)\n");
	return;
}
