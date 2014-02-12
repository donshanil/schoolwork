#include <stdbool.h>
#include <stddef.h>
#include <stdlib.h>

#include "process.h"
#include "memory.h"

//HIERACHY
/* running -> terminated - 1
	blocked -> ready
	running -> blocked
	new -> ready
	running -> ready (quantum - end)
	running -> ready (pre - empt)
	ready -> running (scheduler)
*/
ProcessList* get_last_list(ProcessList* list_in)
{
	//navigates through a linked list and returns the last member.
	
	ProcessList* working_list;

	if(list_in != NULL)
	{
		working_list = list_in;
		//working_list->next = NULL;
		while(working_list->next != NULL)
		{
			working_list = working_list->next;
			if(working_list->next == working_list)
			{
				printf("why is the NEXT equal to itself");
				
			}
		}
		
	}
	else{
		working_list = list_in;
	}
	return working_list;
}

void update_times(Scheduler *s)
{
	//STRICTLY ONLY UPDATE TIMES. RUNNING AND BLOCKED QUEUE.
	//updating global clock time is already handled by sched.c
	//update currently running process time
	if (s->running != NULL) //hasnt arrived - or waiting for IO
	{
		ProcessList* current_list;
		current_list = s->running;
		int check = 0;
		int i = 0;
		//find which runtime is not complete
		while(check == 0)
		{
			if(current_list->proc.runtime_completed[i] > 0 ) //ie. runtime already completed
			{
				i++;
			}
			else
			{
				current_list->proc.runtime[i]--;
				check = 1;
				if(current_list->proc.runtime[i] == 0)
				//mark runtime completed
				{
					//current_list->proc.runtime_completed[i] = s.clock_time;
				}
			}		
		}
		
		s->running_quantum_remaining--; //subtract 1 from running quantum.
	}	
	//update IO time. this means anything which is blocked.
	if (s->blocked != NULL)
	{
		ProcessList* current_list;
		current_list = s->blocked;
		while(current_list != NULL)
		{
			int check = 0;
			int i = 0;
			while(check == 0)
			{
				if(current_list->proc.iotime_completed[i] > 0)
				{
					i++;
				}
				else
				{
					current_list->proc.iotime[i]--;
					check = 1;
					
					if(current_list->proc.iotime[i] == 0)
					{	
						//current_list->proc->iotime_completed[i] = s.clock_time;
					}
				}
				
			}
			
			current_list = current_list->next;
			
		}
	}
	
}
void terminating_process(Scheduler *s)
// 1. running -> terminated
//terminates process which is in running queue and has TTL of 0.
//ie, ALL runtimes + IO complete.
//the process should be removed from memory.
{
	if (s->running != NULL)
	{
		ProcessList* current_list;
		current_list = s->running;

		
		//THIS CODE SNIPPET WILL ASSUME THAT RUNNING WILL ALWAYS OCCUR LAST!
		if(current_list->proc.runtime[(current_list->proc.num_runtimes)-1] == 0)
		{
			//MARK FINISH TIME
			current_list->proc.runtime_completed[current_list->proc.num_runtimes] = s->clock_time;
			remove_from_memory(s, current_list->proc.pid);
			//NOW, move it to end of finished list.
			ProcessList* finished_list;
			finished_list = get_last_list(s->finished);
			if(finished_list == NULL)
			{
				s->finished = current_list;
			}
			else //IE, finished list is not EMPTY 
			{
				finished_list->next = current_list;
			}
			
			s->running = NULL; //clear running list.
		}
	}
}
void blocking_process(Scheduler* s)
{
//2. blocked -> ready
//this should never stuff around with queue levels.
	if(s->blocked != NULL)
	{
		ProcessList* current_list;
		ProcessList* previous_list;
		previous_list = NULL;
		current_list = s->blocked;
		
		while(current_list != NULL)
		{
			int check = 0;
			int i=0;
			//check if process has completed iotime, and mark if appropriate.
			while(check == 0)
			{
				if(current_list->proc.iotime_completed[i] > 0)
				{
					i++;
				}
				else //found correct value of i to check
				{
					check = 1;
					if(current_list->proc.iotime[i] == 0) //IO COMPLETE
					{	
						current_list->proc.iotime_completed[i] = s->clock_time;
						//move to runnable queue.
						ProcessList* runnable_list;
						int queue_level;
						queue_level = current_list->proc.last_queue_level;

						runnable_list = get_last_list(s->runnable[queue_level]);
						if(runnable_list == NULL)
						{
							s->runnable[queue_level] = current_list;
						}
						else
						{
							runnable_list->next = current_list;
						}
						//NOW FILL IN THE GAP in blocked.
						if(s->blocked == current_list)
						{
					
							s->blocked = current_list->next; //set to null if applicable
							current_list->next = NULL;
					
						
						}
						else
						{
							previous_list->next = current_list->next;
							current_list->next = NULL; //since it is now appended to 'runnable'
				
						}

					}
					else
					{
						//NO MATCH FOUND
						previous_list = current_list;
					}
				}
			}
			current_list = current_list->next; //move to next item in list.
		}
	}
//3. running -> blocked
//move and process with runtime complete to IO queue.
	if (s->running != NULL){
	//WE assume that if it gets to this stage, then IO is NOT complete.
		ProcessList* current_list;
		current_list = s->running;
		int i = 0;
		int check = 0;
		while(check == 0)
		{
			if(current_list->proc.runtime_completed[i] > 0 ) //ie. runtime already completed
			{
				i++;
			}
			else
			{
				check = 1;
				if(current_list->proc.runtime[i] == 0)
				//mark runtime completed
				//this wasnt already handled by 'terminate'
				{
					current_list->proc.runtime_completed[i] = s->clock_time;
					//anything which is blocked gets moved to a higher priority queue.
					int queue_level;
					queue_level = current_list->proc.last_queue_level;
					queue_level--;
					if(queue_level < 0)
					{
						queue_level = 0;
					}
					current_list->proc.last_queue_level = queue_level;
					ProcessList* blocked_queue = get_last_list(s->blocked);
					//now move to blocked queue.
					if(blocked_queue == NULL){
						s->blocked = current_list;
					}	
					else
					{
						blocked_queue->next = current_list;
					}
					s->running = NULL;
				}
				

			}
		}	
	}
}
void start_new_processes(Scheduler* s)
{
// 4. new -> ready
	
	if(s->not_started != NULL){
		ProcessList* current_list;
		ProcessList* previous_list = NULL;
		
		
		current_list = s->not_started;
		//we will walkthrough not_started to check for anything
		//that should be moved to the running queue.
		while(current_list != NULL) 
		{
			if(current_list->proc.time_created <= s->clock_time)
			{
				ProcessList* runnable_list;
				runnable_list = get_last_list(s->runnable[0]);
				if(runnable_list == NULL)
				{
					s->runnable[0]=current_list;
					
				}
				else
				{
					runnable_list->next = current_list;
				}
				//now remove from the not started list
				if(s->not_started == current_list)
				{
					
					s->not_started = current_list->next; //set to null if applicable
					current_list->next = NULL;
					
				}
				else
				{
					previous_list->next = current_list->next;
					current_list->next = NULL; //since it is now appended to 'runnable'
				
				}
			}
			else
			{
				previous_list = current_list;
			}
			current_list = current_list->next;
		}
	}
}

void end_quantum(Scheduler *s, Algorithm alg)
{
	//5. running -> ready (quantum end)
	//if anything triggers this, it is moved to the back of the runnable queue

	switch(alg)
	{
		case fcfs:
			break;
		case rr:
		{
			if (s->running != NULL)
			{
				ProcessList* current_list;
				current_list = s->running;
				if(s->running_quantum_remaining == 0)
				{
					//printf("QUANTUM END!");
					//move to back of runnable queue
					ProcessList* runnable_list;
					runnable_list = get_last_list(s->runnable[0]);
					if(runnable_list == NULL)
					{
						s->runnable[0] = current_list;
						
					}
					else{
						runnable_list->next = current_list;
					}
					s->running = NULL;
				}
			}
			break;
		}
		case mlfq_np:
		case mlfq_pre:
		{
			//np has NO PRE-EMPTING.
			//if a process hits the end of quantum, then it is downgraded
			//to next queue level.
			if (s->running != NULL)
			{
				ProcessList* current_list;
				current_list = s->running;
				if(s->running_quantum_remaining == 0)
				{
					//MOVE TO NEXT QUEUE LEVEL
					int queue_level;
					queue_level = s->running->proc.last_queue_level;
					queue_level++;
					if(queue_level >= MLFQ_LEVELS)
					{
						queue_level = MLFQ_LEVELS -1;
						
					}
					s->running->proc.last_queue_level = queue_level;
					
					ProcessList* runnable_list;
					runnable_list = get_last_list(s->runnable[queue_level]);
					if(runnable_list == NULL)
					{
						s->runnable[queue_level] = current_list;
						
					}
					else{
						runnable_list->next = current_list;
					}
					s->running = NULL;
				}
			}
		}
		
	//these two handle quantum ending in the same way.
	}
}
void preempt(Scheduler* s, Algorithm alg)
{
	switch(alg)
	{
		case fcfs: //fallthrough
		case rr:
		case mlfq_np:
			break;
		case mlfq_pre:
		{
			//this will interrupt the currently running process if a higher
			//priority process is in runnable.
			//runnable should already be populated.
			if(s->running != NULL)
			{
				int i=0;
				int current_level;
				current_level = s->running->proc.last_queue_level;
				
				while(i < current_level)
				{
					if(s->runnable[i] != NULL && s->running != NULL)
					{
						//preempt time!!!
						//move the currently running process to the same queue it was in
						
						ProcessList* runnable_list;
						ProcessList* current_list;
						current_list = s->running;
						int queue_level;
						queue_level = s->running->proc.last_queue_level;
						runnable_list = get_last_list(s->runnable[queue_level]);
						if(runnable_list == NULL)
						{
							s->runnable[queue_level] = current_list;
							
						}
						else{
							runnable_list->next = current_list;
						}
						s->running = NULL;
						break;
							
					}
					i++;
				}
			}	
		}		
			break;
	}
}
bool schedule(Scheduler *s, Algorithm alg)
{
	//ready->running
	if(s->running == NULL){
		switch(alg)
		{
			case fcfs:
			{
				//choose a ready process to put into the running queue.
				if(s->runnable[0] != NULL)
				{
					//otherwise return false. maybe we are waiting for IO, or nothing has arrived yet.
					ProcessList* current_list;
					//take first thing in line. we should only be using queue 0 for fcfs.
					current_list = s->runnable[0];
					//navigate through list.
					s->running = current_list;
					s->runnable[0] = current_list->next; //will set to null if empty.
					current_list->next = NULL;
					return true;
				}
			}
			case rr:
			{
				if(s->runnable[0] != NULL)
				{
					ProcessList* current_list;
					current_list = s->runnable[0];
					s->running = current_list;
					s->runnable[0] = current_list->next; //will set to null if empty.
					current_list->next = NULL;
					s->running_quantum_remaining = rr_quantum;
					return true;
				}
			}
			case mlfq_np:
			case mlfq_pre:
			{
				//we want to walkthrough each queue looking for a process to run
				//if a process is blocked, it is returned to the same queue it was once in
				int i = 0;
				while(i<MLFQ_LEVELS) //0, 1, 2 are valid values.
				{
					if(s->runnable[i] != NULL)
					{
						ProcessList* current_list;
						current_list = s->runnable[i];
						s->running = current_list;
						s->runnable[i] = current_list->next;
						current_list->next = NULL;
						if(alg == mlfq_np)
						{
							s->running_quantum_remaining = mlfq_np_quanta[i];
						}
						else
						{
							s->running_quantum_remaining = mlfq_pre_quanta[i];
						}
						return true;
					}
					i++;
				}
				
			}
		}
	}
	return false;
		
}

