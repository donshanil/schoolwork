
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "logging.h"
#include <pthread.h>
#include <time.h>

/////////////////////////////
// Thomas Grundy - 541 802 - tgrundy
// Ben Chong - 540 928 - bkchong
////////////////////////////
/*
* This function is to be called once, 
* before the first call to log_write is made.
*  
* log_file_name is the path and file name of the file to be used for logging.
*/

	static char* file_name;
	extern pthread_mutex_t write_mutex;

void log_setUp(char* log_file_name)
{
	file_name = log_file_name;
	
	//initialise empty log file
	FILE *fp;
	fp = fopen(file_name, "w"); //w flag clears log file.
	fclose(fp);
}

/*
* Writes a log entry to the log file.
* entries must be formatted with this pattern: "%s: %s: %s: %s: %s\n"
* From left to right the %s are to be:
*  - a time stamp, in local time.
*  - module (which part of the program created the entry?)
*  - p1
*  - p2
*  - p3
* 
* Note: This function may be called from multiple threads and hence must
*       ensure that only one thread is writing to the file at any given time.
*/
void log_write(char* module, char* p1, char* p2, char* p3)
{
	pthread_mutex_lock(&write_mutex);
	
	FILE *fpo;
	fpo = fopen(file_name, "a"); //a flag appends
	
	time_t now;
	time(&now);
	//we need to strip the trailling \n character
	char* temp_time = ctime(&now);
	char* good_time;
	char* tok_ptr;
	
	good_time = malloc(sizeof(char)*strlen(ctime(&now))+1);
	strcpy(good_time, strtok_r(temp_time, "\n", &tok_ptr));
	
	fprintf(fpo, "%s: %s: %s: %s: %s\n", good_time, module, p1, p2, p3);
	
	fclose(fpo);
	free(good_time); //WHY DOES THIS SEGFAULT

	pthread_mutex_unlock(&write_mutex);
}

