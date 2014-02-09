#ifndef PROCESS_H
#define PROCESS_H

#include <stdbool.h>
#include <limits.h>

enum algorithm_enum {fcfs, rr, mlfq_np, mlfq_pre};
typedef enum algorithm_enum Algorithm;

#define MLFQ_LEVELS 3
//INT_MAX isn't infinite, but it is over a bilion, close enough
static const unsigned int fcfs_quantum = INT_MAX;
static const unsigned int rr_quantum = 8;
static const unsigned int mlfq_np_quanta[MLFQ_LEVELS] = {4, 8, 16};
static const unsigned int mlfq_pre_quanta[MLFQ_LEVELS] = {4, 8, 32};

typedef struct process_struct Process;
typedef struct process_list_struct ProcessList;
typedef struct scheduler_struct Scheduler;
typedef struct memory_chunk_struct MemoryChunk;

#define MAX_RUNTIMES 100
struct process_struct
{
	unsigned int time_created;
	unsigned int pid;
	unsigned int mem_size;
	unsigned int runtime[MAX_RUNTIMES];
	unsigned int runtime_completed[MAX_RUNTIMES];
	unsigned int num_runtimes;
	unsigned int iotime[MAX_RUNTIMES];
	unsigned int iotime_completed[MAX_RUNTIMES];
	unsigned int num_iotimes;
	int last_queue_level;
};

struct process_list_struct
{
	Process proc;
	ProcessList* next;
};

struct scheduler_struct
{
	unsigned int clock_time;
	ProcessList* not_started;
	ProcessList* runnable[MLFQ_LEVELS];
	ProcessList* blocked;
	ProcessList* running; /*this list should only ever contain zero or one process*/
	ProcessList* finished;
	unsigned int running_queue_level;
	unsigned int running_quantum_remaining;
	MemoryChunk* memory;
};

static const int free_mem = -1;

struct memory_chunk_struct
{
	MemoryChunk* next;
	int pid;
	unsigned int size;
	unsigned int time;
};

bool work_remaining(Scheduler* s);

#endif
/*PROCESS_H*/
