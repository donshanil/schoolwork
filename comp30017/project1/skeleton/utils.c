#include <stdlib.h>
#include <assert.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdbool.h>

#include "utils.h"

const bool debug_printing = false;

void* malloc_wrapper(size_t size)
{
	assert(size != 0);
	void* temp = malloc(size);
	assert(temp != NULL);
	return temp;
}

void free_wrapper(void* ptr)
{
	assert(ptr != NULL);
	free(ptr);
	return;
}

int debug_printf(const char* restrict format, ...)
{
	va_list args;
	va_start(args, format);
	int temp = vfprintf(stderr, format, args);
	va_end(args);
	return temp;
}
