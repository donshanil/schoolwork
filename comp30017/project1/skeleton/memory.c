#include <stdbool.h>

#include "process.h"

bool first_fit(Scheduler *s);
void free_biggest_oldest(Scheduler *s);
void remove_from_memory(Scheduler* s, int pid);
void merge_memory_holes(Scheduler* s);

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
		if(current_chunk->next != NULL);
		{
			//move to the next memory chunk
			current_chunk = current_chunk->next
		}
		else
		{
			check = 0;
		}
	}	
	return memory_count;
}
unsigned int memory_in_use(Scheduler* s);
unsigned int memory_in_use_fraction(Scheduler* s);
unsigned int processes_in_memory(Scheduler* s);
unsigned int holes_in_memory(Scheduler* s);
bool memory_empty(Scheduler* s);
bool is_in_memory(Scheduler* s, int pid);

#endif
/*MEMORY_H*/
