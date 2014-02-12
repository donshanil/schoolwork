#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

#include "socket.h"
#include "logging.h"

#define BUF_SIZE 512

//////////////////////////////////////////////////
// Thomas Grundy - 541 802 - tgrundy
// Ben Chong - 540 928 - bkchong
//////////////////////////////////////////////////

/*
* Creates a new socket and returns the socket descriptor.
*/
int socket_setup(int portno){
	struct sockaddr_in server;
	int sockfd;
	
	bzero((char *) &server, sizeof(server));

	sockfd = socket(AF_INET, SOCK_STREAM, 0);

	if (sockfd < 0) 
	{
		perror("Error opening the socket");
		exit(1);
	}

	server.sin_family = AF_INET;
	server.sin_addr.s_addr = INADDR_ANY;
	server.sin_port = htons(portno);

	if (bind(sockfd, (struct sockaddr *) &server, sizeof(server)) < 0)
	{
		perror("Error on binding socket");
		exit(1);
	}
	return sockfd;
}

/*
* Listens for a new connection,
* once there is a new connection
* the file descriptor of the new open connection is returned.
*/
int socket_get_new_connection(int sockfd){
	
	int clilen, newsockfd;
	struct sockaddr_in client;

	//listen takes a sockfd and an int, which is the max size of its 
	//"backlog", which is a queue of pending connections
	//NOT SURE IF THEY HAVE TOLD US WHAT THE BACKLOG SIZE SHOULD BE
	//IS IT MEANT TO BE 100 AS YOU NEED TO SUPPORT 100 USERS
	listen(sockfd,5);

	clilen = sizeof(client);

	newsockfd = accept(sockfd, (struct sockaddr *) &client, &clilen);

	if (newsockfd < 0) {
		perror("Error accepting socket");
		exit(1);
	}
	//sleep(60);
	log_write("Main", "New Connection", "", "");
	return newsockfd;
}

/*
* Reads a 'line' from the given connection.
* A 'line' means a sequence of characters ending with a CRLF pair.
* The string being returned must include the CRLF pair.
*/
char* socket_get_line(int fd){
	char buffer[BUF_SIZE];
	int read_check;

	bzero(buffer,512);

	read_check = read(fd,buffer,BUF_SIZE); //SHOULD THIS BE BUF_SIZE - 1

	if (read_check<0) 
	{
		perror("Error reading from socket");
		exit(1);
	}
	
	return buffer;

}


/*
* Writes the given string to the given connection descriptor.
*/
void socket_write(int fd, char* string){
	int write_check;
	write_check = write(fd,string,strlen(string));
	if (write_check < 0) {
		perror("Error writing to socket");
		exit(1);
	}
}


/*
* Closes the given connection descriptor.
*/
void close_connection(int fd){
	close(fd);
}

/*
* Closes the given socket.
*/
void close_socket(int fd){
	close(fd);
}

