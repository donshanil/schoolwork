#include <assert.h>
#include <stdio.h>

#include "process.h"
#include "utils.h"

bool work_remaining(Scheduler* s)
{
	if(s->not_started != NULL)
		return true;
	if(s->running != NULL)
		return true;
	if(s->blocked != NULL)
		return true;
	for(int i = 0; i < MLFQ_LEVELS; i++)
	{
		if(s->runnable[i] != NULL)
			return true;
	}
	return false;
}


