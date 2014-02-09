#include <stdbool.h>

#include "process.h"
#include "utils.h"
#include <stddef.h>

void free_biggest_oldest(Scheduler *s);
void merge_memory_holes(Scheduler* s);
void remove_from_memory(Scheduler* s, int pid_in);

bool first_fit(Scheduler *s)
{
	//given the runnable list, fit the memory chunk in.
	//operation complete
	MemoryChunk* current_chunk;
	ProcessList* current_list;
	current_list = s->running;
	current_chunk = s->memory;
	while (current_chunk != NULL)
	{
		if (current_chunk->pid == -1)
		{
			if(current_chunk->size >= current_list->proc.mem_size)
			{
				//space found!
				MemoryChunk* new_chunk = malloc_wrapper(sizeof(MemoryChunk));
				current_chunk->size = current_chunk->size - (current_list->proc.mem_size);
				new_chunk->size = current_list->proc.mem_size;
				new_chunk->time = s->clock_time;
				new_chunk->pid = current_list->proc.pid;
				if(current_chunk->next != NULL)
				{	
					new_chunk->next = current_chunk->next;
				}
				else
				{
					new_chunk->next = NULL;
				}
				current_chunk->next = new_chunk;

				return true;
			}
		}
		current_chunk = current_chunk->next;
	}
	
	return false; //no space found.
}

void remove_from_memory(Scheduler* s, int pid_in)
{
	//searches for target PID and removes it from the memory
	//hole joining can be handled by merge memory holes.

	MemoryChunk* current_chunk;
	current_chunk = s->memory;
	//check very first chunk
	while (current_chunk != NULL)
	{
		if (current_chunk->pid == pid_in)
		{
			current_chunk->pid = -1;
			//hole joining by merge memory holes.
		}
		current_chunk = current_chunk->next;
		
	}	
	merge_memory_holes(s);
};

void free_biggest_oldest(Scheduler *s)
{
	//we need to walk through our memory
	//free the biggest memory chunk first
	//if there is a tie, will use oldest of the two.
	MemoryChunk* current_chunk;
	current_chunk = s->memory;
	int pid = -1; //keep track of biggest chunk
	unsigned int largest_size = 0;
	unsigned int earliest_time = s->clock_time;
	while(current_chunk != NULL)
	{
		if(current_chunk->pid != -1)
		{
			if(current_chunk->size > largest_size)
			{
				pid = current_chunk->pid;
				largest_size = current_chunk->size;
				earliest_time = current_chunk->time;
			}
			else if (current_chunk->size == largest_size)
			{	
				if(current_chunk->time < earliest_time)
				{
					pid = current_chunk->pid;
					earliest_time = current_chunk->time;
				}
			}
		}		
		current_chunk = current_chunk->next;
		
	}
	
	remove_from_memory(s, pid);
	
};

void merge_memory_holes(Scheduler* s)
{
	//if we have two consecutive memory chunks which are 'free'
	//ie. PID = -1, then we should merge them!
	MemoryChunk* current_chunk;
	MemoryChunk* previous_chunk;
	previous_chunk = s->memory;
	//we check just in case there is only 1 chunk
	if(previous_chunk->next != NULL){
		current_chunk = previous_chunk->next;
		while(current_chunk != NULL)
		{
			if((previous_chunk->pid == -1) && (current_chunk->pid == -1))
			{
				//adjacent holes spotted! merge for great justice!
				
				if(current_chunk->next != NULL)
				{
					//merge sizes
					previous_chunk->size += current_chunk->size;
					previous_chunk->next = current_chunk->next;
					free(current_chunk);
					previous_chunk->time = 0;
					current_chunk = previous_chunk->next;
					//current_chunk should no longer exist
				}
				else
				{
					//last chunk in the list. break out of loop.
					previous_chunk->size += current_chunk->size;
					free(current_chunk);
					previous_chunk->next = NULL;
					break;
				}
			}
			else //no consecutive hole found
			{
				previous_chunk = current_chunk;
				current_chunk = current_chunk -> next;
			}

		}
	}
};

unsigned int memory_size(Scheduler *s)
{
	//calculate total memory size using only scheduler
	//look through each 'memory chunk', and add to a running total
	unsigned int memory_count = 0;
	int check = 1;
	MemoryChunk* current_chunk;
	current_chunk = s->memory;
	while (check == 1){
		memory_count += current_chunk->size;
		if(current_chunk->next != NULL)
		{
			//move to the next memory chunk
			current_chunk = current_chunk->next;
		}
		else
		{
			check = 0;
		}
	}	
	return memory_count;
};
unsigned int memory_in_use(Scheduler* s)
{
	unsigned int memory_count = 0;
	int check = 1;
	MemoryChunk* current_chunk;
	current_chunk  = s->memory;
	while (check == 1){
		//check that its not free memory or memory hole
		if(current_chunk->pid != -1){
			memory_count += current_chunk->size;
		}
		if(current_chunk->next != NULL) {
			current_chunk = current_chunk-> next;
		}
		else
		{
			check = 0;
		}
	}
	return memory_count;
}
unsigned int memory_in_use_fraction(Scheduler* s);
//this is already defined in sched.c

unsigned int processes_in_memory(Scheduler* s)
{
	//given the scheduler, we must calculate how many processes
	//are in memory. 
	unsigned int process_count = 0;
	MemoryChunk* current_chunk;
	current_chunk = s->memory;
	while (current_chunk != NULL)
	{
		if(current_chunk->pid != -1)
		{
			process_count++;
		}
		current_chunk = current_chunk->next;
		
	}

	return process_count;
}
unsigned int holes_in_memory(Scheduler* s)
{
	//check how many additional times we get the PID of -1.
	//if free space is contiguous, we should only get -1 once.
	unsigned int holes = 0;
	//int check = 1;
	//int minus_found = 0;
	MemoryChunk* current_chunk;
	current_chunk = s->memory;
	while (current_chunk != NULL){
		if(current_chunk->pid == -1){
			holes++;
		//check if this is the first free memory we found
			
			// if(minus_found ==0){
				// minus_found = 1;
			// }
			// else
			// {
				// holes++;
			// }
		}
		current_chunk = current_chunk->next;
	}
	return holes;
}
bool memory_empty(Scheduler* s)
{
	return true;
}
bool is_in_memory(Scheduler* s, int pid)
{
	//scans through the memory chunks to check if the process
	//is in memory
	int check = 1;
	bool found = false;
	MemoryChunk* current_chunk;
	current_chunk = s->memory;
	while(check==1){
		if(current_chunk->pid == pid){
			found = true;
			check = 0;
			break;
		}
		else
		{
			if(current_chunk->next != NULL){
				current_chunk = current_chunk->next;
			}
			else
			{	
				check = 0;
				//last chunk
				break;
			}
		}
	}
	return found;
}

