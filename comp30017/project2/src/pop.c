#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#include "pop.h"
#include "socket.h"
#include "maildir.h"
#include "logging.h"

#define maxUserLen 50
#define maxPassLen 50


int update_files_struct(FilesStruct* files, char* user);

////////////////////////////////////////////////////
// Thomas Grundy - 541 802 - tgrundy
// Ben Chong - 540 928 - bkchong
////////////////////////////////////////////////////

/*
* This function is the starting point of the POP protocol
* on the given connection.
*/
void pop_protocol(int connection_descriptor){
	
	//Make this into a big switch statement
	
	char *state = "AUTH";

	char *inputGet;
	char *user;
	char *pass;
	char *module = "POP";
	bool userReceived = false;
	bool passReceived = false;
	bool statCalled = false;
	int totalSize = 0;
	bool *deleteFlag;
	FilesStruct* files = NULL;
	int argInt;
	int i, numOfFiles;

	socket_write(connection_descriptor, 
				"+OK Welcome to turtle-server 20X6.\r\n");

	//Authentication State

	while(state == "AUTH") {

		//checks to see if both correct username and password
		//are satisfied. moves to transaction state if applicable
		if(userReceived && passReceived) {
			//this function is in maildir.c
			if (check_user(user, pass)) {
				
				//all conditions satisfied, move to next step
				socket_write(connection_descriptor,
						"+OK authenticated ok\r\n");
				state = "TRAN";
				
				log_write(module, user, "TRANSACTION STATE", "");
				break;
			} else {
				//reset these because we were having problems
				userReceived = false;
				passReceived = false;
				socket_write(connection_descriptor,
						"-ERR Wrong username or password\r\n");
			}
		}
		//waits for input
		inputGet = socket_get_line(connection_descriptor);
		inputGet[strlen(inputGet) - 1] = '\0'; 
		
		//if first 4 characters = user, perform username interpret
		if((!strncmp(inputGet,"user",4)) || 
			(!strncmp(inputGet,"USER",4))) 
		{

			//strip command to get just username

			if (strlen(inputGet) > 5) {
				user = NULL;
				user = strdup(&inputGet[5]);
				if (user == NULL) {
					fprintf(stderr,"out of mem\n");
					exit(1);
				}
				user[strlen(user)-1] = NULL; 


				socket_write(connection_descriptor,"+OK got username\r\n");
				log_write(module,"No Auth","USER", user);

				userReceived = true;
			} else {
				socket_write(connection_descriptor,
							"+ERR no username received\r\n");
			}

		//strips input to get password
		} else if((!strncmp(inputGet,"pass",4)) ||
				(!strncmp(inputGet,"PASS",4))) 
		{
			if (strlen(inputGet) > 5) {
				pass = NULL;
				//get all input from the character 5 onwards.
				pass = strdup(&inputGet[5]);
				if (pass == NULL) {
					fprintf(stderr,"out of mem\n");
					exit(1);
				}
				//remove return character
				pass[strlen(pass)-1] = NULL;
				
				log_write(module,"No Auth", "PASS", pass);
				passReceived = true;
			} else {
				socket_write(connection_descriptor,
						"+ERR no password received\r\n");
			}
		
		//quit before transaction
		} else if((!strncmp(inputGet,"quit",4)) || 
				(!strncmp(inputGet,"QUIT",4)))
		{	
			//no login, free any data already stored
			if (userReceived) {
				free(user);
			}
			if (passReceived) {
				free(pass);
			}
			socket_write(connection_descriptor,
						"+OK quitting now\r\n");
			log_write(module, "No Auth", "QUIT", "");		
			//returns control back to main.c which will close the socket.
			return;
	//no commands recognised		
		} else {
			socket_write(connection_descriptor,
						"-ERR Try logging in again\r\n"); 
		}
	}


	files = malloc(sizeof(FilesStruct));
	if (files == NULL) {
		fprintf(stderr,"out of mem\n");
		exit(1);
	}
	statCalled = true;
	files = dir_get_list(user);
	
	deleteFlag = malloc(sizeof(bool) * files->count);

	int iter;
	for (iter = 0; iter < files->count; iter++) {
		deleteFlag[iter] = false;
	}
	//Transaction state 

	while (state == "TRAN") {
		//wait for input and strip terminating character
		inputGet = socket_get_line(connection_descriptor);
		inputGet[strlen(inputGet) - 1] = '\0';

		//exit transaction state
		if ((!strncmp(inputGet,"quit",4)) || 
				(!strncmp(inputGet,"QUIT",4)))
		{
			log_write(module,user,"QUIT","");
			state = "UPDA";
			break;
	
	//gets stats about mailbox
		} else if((!strncmp(inputGet,"stat",4)) ||
				(!strncmp(inputGet,"STAT",4))) 
		{
			statCalled = true;
			log_write(module,user,"STAT","");
			files = dir_get_list(user);

			numOfFiles = 0;
			//add up filesizes
			totalSize = 0;
			for(i = 0; i < files->count; i++) {
				if(!deleteFlag[i]) {
					totalSize += files->FileSize[i];
					numOfFiles++;
				}
			}
			char string[60];
			sprintf(string, "+OK %d %d\r\n", numOfFiles, totalSize);
			socket_write(connection_descriptor,string);
			free(string);
			free(files);
		
		//lists all messages if no argument
		//if argument present, show info about message
		} else if((!strncmp(inputGet,"list",4)) ||
				(!strncmp(inputGet,"LIST",4)))
		{
			log_write(module, user, "LIST", "");
			statCalled=true;
			files = dir_get_list(user);
			char listInfo[60];

			if (strlen(inputGet) > 6) {
				char arg[41];
				strncpy(arg,&inputGet[5],strlen(&inputGet[5])-1);
				
				argInt = atoi(arg);
				if ((argInt > 0) && (argInt <= (files->count)) &&
						(!deleteFlag[argInt-1])) {
					sprintf(listInfo, 
							"+OK %d %d\r\n",argInt,
							files->FileSize[argInt-1]);

					socket_write(connection_descriptor, listInfo);
				} else {
					socket_write(connection_descriptor,
								"-ERR try another number\r\n");
				}
				argInt = 0;
			} else {
				socket_write(connection_descriptor,
							"+OK listing emails\r\n");			

				for(i = 0; i < files->count; i++) {
					if(!deleteFlag[i]){
						sprintf(listInfo,"%d %d\r\n",
								i+1,files->FileSize[i]);
						socket_write(connection_descriptor,listInfo);
					}
				}
				socket_write(connection_descriptor,".\r\n");
			}

	
		//retrieves target message. must NOT retrieve deleted number
		} else if((!strncmp(inputGet,"retr",4)) ||
				(!strncmp(inputGet,"RETR",4)))
		{
		//	log_write(module, user, "RETR", );
			if (strlen(inputGet) > 6) {
				char arg[41];
				strncpy(arg,&inputGet[5],strlen(&inputGet[5])-1);
				
				argInt = atoi(arg);
				int numdigits;
				numdigits = log10((double) argInt) + 1;
				char* arg_string;
				arg_string = malloc(sizeof(char)*numdigits+1);
				sprintf(arg_string,"%d",argInt);			
				log_write(module, user, "RETR", arg_string);	

				if ((argInt > 0) && (argInt <= (files->count)) &&
						(!deleteFlag[argInt - 1])) 
				{	
					
					char *filename = files->FileNames[argInt-1];
					char *email = get_file(user, filename);
					socket_write(connection_descriptor,"+OK email follows\r\n");
					socket_write(connection_descriptor,email);
					
				}
				else
				{
					socket_write(connection_descriptor,"-ERR, message marked for deletion");
				}
			socket_write(connection_descriptor,".\r\n");
			}
		
		} else if((!strncmp(inputGet,"dele",4)) ||
				(!strncmp(inputGet,"DELE",4)))
		{
			//mark file for deletion in update state
			//don't forget to log write.

			if (strlen(inputGet) > 6) {
				char arg[41];
				strncpy(arg,&inputGet[5],strlen(&inputGet[5])-1);
				
				argInt = atoi(arg);
				if ((argInt > 0) && 
					(argInt-1 < files->count))
				{
					deleteFlag[argInt-1] = true;
				}
				
				
				//this overcomplicated bit of code trims down the arg so
				//we arent printing 41 characters
				int numdigits;
				numdigits = log10((double) argInt) + 1;
				char* arg_string;
				arg_string = malloc(sizeof(char)*numdigits+1);
				sprintf(arg_string,"%d",argInt);			
				log_write(module, user, "DELE", arg_string);

				socket_write(connection_descriptor,
						"+OK file marked for deletion\r\n");
			} else {
				socket_write(connection_descriptor,
						"-ERR Please provide a file number\r\n");
			}

		} else if((!strncmp(inputGet,"noop",4)) ||
				(!strncmp(inputGet,"NOOP",4)))
		{
			log_write(module, user, "NOOP", "");
			socket_write(connection_descriptor,
						"+OK Hello? Is it me you're looking for~\r\n");
						
		} else if((!strncmp(inputGet,"rset",4)) ||
				(!strncmp(inputGet,"RSET",4)))
			//RESET files marked for deletion.
		{
			log_write(module, user, "RSET", "");	
			int i;
			for (i = 0; i < files->count; i++)
			{
				deleteFlag[i] = false;
			}
			socket_write(connection_descriptor,
						"+OK all files marked for deletion reset\r\n");
		} else {
			socket_write(connection_descriptor,
						"-ERR Please try that command again\r\n");
		}
	}

	while (state == "UPDA") {
		//remember to only do this once.
		log_write(module, user, "UPDATE STATE", "");
	
		//Do deleting and other cleanup stuff before quitting completely

		for (i = 0; i < files->count; i++) {
			if (deleteFlag[i]) {
				delete_mail(user,files->FileNames[i]);
			}
		}
		log_write(module, user, "Closing Connection", "");

		free(user);
		free(pass);
		if (statCalled) {
			//free(files->FileSize);
			//free(files->FileNames);
			free(files);
		}
		socket_write(connection_descriptor,
					"+OK quitting now\r\n");
		return;
	}
}




