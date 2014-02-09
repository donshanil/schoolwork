#include <stdlib.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <pthread.h>

#include "socket.h"
#include "maildir.h"
#include "logging.h"
#include "pop.h"

#define BUF_SIZE 512
#define MAX_THREADS 100 //set this to anything you please

/////////////////////////////
// Thomas Grundy - 541 802 - tgrundy
// Ben Chong - 540 928 - bkchong
/////////////////////////////

pthread_mutex_t write_mutex; //globally accessible mutex

typedef struct thread_struct ThreadStruct;
typedef struct handoff_struct HandoffStruct;

struct thread_struct
{
	pthread_t *thread;	
	int used;
};

struct handoff_struct
{
	int newsocket;
	int threadid;
};

ThreadStruct thread_list[MAX_THREADS];
HandoffStruct hand_list[MAX_THREADS];

void *work_function(void *param)
{
	HandoffStruct *hand = param;
	int newsock = hand->newsocket;
	int this_threadid = hand->threadid;

	//perform the pop protocol given our socket
	pop_protocol(newsock);

	//once it returns, close our connection
	close_connection(newsock);

	//thread is ready for reuse
	thread_list[this_threadid].used = 0;
	fprintf(stderr, "Freeing thread %i\n", this_threadid);
	
	return (void *)0;
}

int main(int args, char **argv){
	
	int portno, sockfd, newsockfd;
	char *maildir;
	char *logfile;

	if(args != 4){
		printf("USAGE: pop3 [port] [maildir path] [log file]\n");
		printf("NOTE: the maildir path must end with an '/'\n");
		exit(1);
	} 
	
	portno = atoi(argv[1]);
	maildir = argv[2];
	logfile = argv[3];	
	
	//initial setup
	log_setUp(logfile);
	dir_set_path(maildir);

	//we need to initialise our struct of threads
	
	int i;
	for(i=0; i<MAX_THREADS; i++)
	{
		thread_list[i].thread = malloc(sizeof(pthread_t));
		thread_list[i].used = 0;
		hand_list[i].threadid = i;
		hand_list[i].newsocket = 0;
	}

	//setup our listener socket
	sockfd = socket_setup(portno);
	log_write("Main", "POP3 Server Started", "", "");
	
	while(1)
	{
	//	fprintf(stderr, "new thread\n");
		newsockfd = socket_get_new_connection(sockfd);
		//pthread_t *t;
		//t = malloc(sizeof(pthread_t));

		//we must find a free process.
		//this is a very bad linear search for now
		i = 0;
		while(i<MAX_THREADS)
		{
			if(thread_list[i].used == 0)
			{
				fprintf(stderr, "Handing off thread %i\n", i);
				thread_list[i].used = 1;
				//this is a struct that we handover which contains required info
				hand_list[i].newsocket = newsockfd;
				hand_list[i].threadid = i;		
				pthread_create(thread_list[i].thread, NULL, work_function,
				&hand_list[i]);
				break;
			}
			
			i++;
		}
		//no free connection, close connection

		if(i>=MAX_THREADS)
		{
			socket_write(newsockfd, "\nServer is overcapacity.\nPlease try again later\n\n");
			fprintf(stderr, "connection rejected, tubes clogged\n");
			close_connection(newsockfd);
		}	

	}
	//closes listener socket
	close_socket(sockfd);
	fprintf(stderr,"DONE!\n");
	
	return 0;
}

