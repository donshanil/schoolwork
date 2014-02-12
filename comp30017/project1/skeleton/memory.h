#ifndef MEMORY_H
#define MEMORY_H

#include <stdbool.h>

#include "process.h"

bool first_fit(Scheduler *s);
void free_biggest_oldest(Scheduler *s);
void remove_from_memory(Scheduler* s, int pid);
void merge_memory_holes(Scheduler* s);

unsigned int memory_size(Scheduler *s);
unsigned int memory_in_use(Scheduler* s);
unsigned int memory_in_use_fraction(Scheduler* s);
unsigned int processes_in_memory(Scheduler* s);
unsigned int holes_in_memory(Scheduler* s);
bool memory_empty(Scheduler* s);
bool is_in_memory(Scheduler* s, int pid);

#endif
/*MEMORY_H*/
