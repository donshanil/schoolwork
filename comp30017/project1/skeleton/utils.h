#ifndef UTILS_H
#define UTILS_H

#include <stdlib.h>

void* malloc_wrapper(size_t size);
void free_wrapper(void* ptr);
int debug_printf(const char* restrict format, ...);

#endif 
/*UTILS_H*/
