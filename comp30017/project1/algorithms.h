#ifndef ALGORITHMS_H
#define ALGORITHMS_H

#include <stdbool.h>

#include "process.h"

void update_times(Scheduler *s);
void terminating_process(Scheduler *s);
void start_new_processes(Scheduler* s);
void blocking_process(Scheduler* s);
void preempt(Scheduler* s, Algorithm alg);
void end_quantum(Scheduler *s, Algorithm alg);
bool schedule(Scheduler *s, Algorithm alg);

#endif
/*ALGORITHMS_H*/
