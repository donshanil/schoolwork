#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <dirent.h>
#include <stddef.h>
#include <errno.h>
#include <unistd.h>
#include <sys/types.h>
#include <dirent.h>
#include <limits.h>


#include "maildir.h"
#include "logging.h"

static DIR *dirp;
static char* mailpath;
char *module = "MailDir";

//////////////////////////////////////
// Thomas Grundy - 541 802 - tgrundy
// Ben Chong - 540 928 - bkchong
//////////////////////////////////////

/*
* This function is to be called once at the beginning
* to set the root path of the maildirs.
*/

void dir_set_path(char* path){
	
	mailpath = path;
	if ((dirp = opendir(path)) == NULL) {
		fprintf(stderr, "Failed to open dir\n");
		exit(1);
	} 
}

/*
* This function checks the given username and password
* against the stored values.
*
* If the username is valid (has a corresponding folder)
* and the given password matches the pass file 
* then the function should return 'true'.
*
* Otherwise it is to return 'false'.
*
* Note 'true' and 'false' are defined in maildir.h
*/
bool check_user(char* username, char* password){
	
	struct dirent *entry;
	DIR *userDirp;
	bool userFound = false;
	bool passFound = false;
	FILE *fp;

	rewinddir(dirp);
	//check to see if any user on the system matches input
	while((entry = readdir(dirp)) != NULL) {
		if(!strncmp(entry->d_name,username,strlen(entry->d_name))) {
			userFound = true;
			break;
		}
	}
	
	if (userFound != true) {
		return false;
	}
	


	char userPath[255];
	sprintf(userPath,"%s%s/",mailpath,entry->d_name);

	if ((userDirp = opendir(userPath)) == NULL){
		fprintf(stderr, "Failed to open userDir\n");
		exit(1);
	}

	//User dir is now open, open pass file and read it


	//does the pass file exist?
	while((entry = readdir(userDirp)) != NULL) {
		if(!strcmp(entry->d_name,"pass")) {
			passFound = true;
			break;	
		}
	}
	//if pass doesnt exist.
	if (passFound != true) {
		free(userPath);
		closedir(userDirp);
		return false;
	}
	

	//now open and actually read the pass file
	char userPassPath[255];
	sprintf(userPassPath,"%s%s/",userPath,"pass");
	fp = fopen(userPassPath,"r");
	if (fp == NULL) {
		fprintf(stderr,"can't open file\n");
		exit(1);
	}
	
	//its like a cassette tape!
	//gets size/length of password
	fseek(fp, 0, SEEK_END);
	long fsize = ftell(fp);
	rewind(fp);

	//now malloc a temporary string to hold password to compare
	char *passFromFile = malloc(fsize + 1);
	if (passFromFile == NULL) {
		fprintf(stderr, "out of mem\n");
		exit(1);
	}
	fread(passFromFile,fsize,1,fp);
	fclose(fp);
	passFromFile[fsize-1] = 0;

	//check if password length matches + string matches too
	if((!strncmp(password,passFromFile,strlen(passFromFile))) && 
			(strlen(password) == strlen(passFromFile))) 
	{	
		free(userPath);
		free(userPassPath);
		closedir(userDirp);
		fclose(fp);
		return true;
	}
	//no match - sadface
	free(userPath);
	free(userPassPath);
	closedir(userDirp);
	fclose(fp);
	return false;

}

/*
* Constructs a FilesStruct as described in maildir.h
* for the maildir of the given user.
*/
FilesStruct* dir_get_list(char* user){
	
	FilesStruct* files;
	struct dirent *entry;
	DIR *userDirp;
	bool userFound = false;
	char *userPath = NULL;

	rewinddir(dirp);
	log_write(module, user, "GetList", "");


	//create an empty filestruct
    files = malloc(sizeof(FilesStruct));
	if(files == NULL) {
		fprintf(stderr,"out of mem\n");
		exit(1);
	}

	files->count = 0;
	
	while((entry = readdir(dirp)) != NULL) {
		if(!strcmp(entry->d_name,user)) {
			userFound = true;
			break;
		}
	}
	//get the userpath to look for filesin
	userPath=malloc(sizeof(char) * 
			(strlen(entry->d_name) + strlen(mailpath) + 1));
	if(userPath == NULL){
		fprintf(stderr,"out of mem\n");
	}
	sprintf(userPath,"%s%s/",mailpath,entry->d_name);

	if ((userDirp = opendir(userPath)) == NULL){
		fprintf(stderr, "Failed to open userDir\n");
		exit(1);
	}
	//ignore any files which begin with '.' or 'pass'.
	while((entry = readdir(userDirp)) != NULL) {
		if ((strncmp(entry->d_name,".",1)) &&
			(strncmp(entry->d_name,"pass",4))) {
			files->count++;
		}
	}

	//we reserve enough space here
	files->FileNames = malloc(files->count * sizeof(char*));

	if(files->FileNames == NULL) {
		fprintf(stderr,"out of mem\n");
		exit(1);
	}

	files->FileSize = malloc(sizeof(int) * files->count);

	if(files->FileSize == NULL) {
		fprintf(stderr,"out of mem\n");
		exit(1);
	} 
	//now populate files struct
	rewinddir(userDirp);
	int iter = 0;
	while((entry = readdir(userDirp)) != NULL) {
		if ((strncmp(entry->d_name,".",1))&&
			(strncmp(entry->d_name,"pass",4))){
			files->FileNames[iter] = entry->d_name;
			files->FileSize[iter] = entry->d_reclen;
			iter++;
		}
	}
	//return back to pop_protocol
	free(userPath);
	closedir(userDirp);
	return files;
}

/*
* Delete the fiven file from the maildir of the given user.
*/
void delete_mail(char* user, char* filename){

	struct dirent *entry;
	DIR *userDirp;
	bool userFound = false;
	bool fileFound = false;
	char *userPath = NULL;

	rewinddir(dirp);	

	log_write(module, user, "Delete", filename);
	//Find user folder
	while((entry = readdir(dirp)) != NULL) {
		if(!strcmp(entry->d_name,user)) {
			userFound = true;
			break;
		}
	}
	if (!userFound) {
		return;
	}
	//Create path to user folder	
	userPath=malloc(sizeof(char) * 
			(strlen(entry->d_name) + strlen(mailpath) + 1));
	if(userPath == NULL){
		fprintf(stderr,"out of mem\n");
	}
	sprintf(userPath,"%s%s/",mailpath,entry->d_name);

	if ((userDirp = opendir(userPath)) == NULL){
		fprintf(stderr, "Failed to open userDir\n");
		exit(1);
	}
	//find the file you want to delete
	while((entry = readdir(userDirp)) != NULL) {
		if(!strcmp(entry->d_name,filename)) {
			fileFound = true;
			break;	
		}
	}
	//create the path to the file you want to delete
	char fileToDeletePath[255];
	sprintf(fileToDeletePath,"%s%s",userPath,filename);

	//delete the file
	if(fileFound) {
		free(userPath);
		closedir(userDirp);
		if ( remove(fileToDeletePath) != 0) {
			perror("Error deleting file\n");
		} else {
		}
	}
}

/*
* Returns the contents of a given file from a user's maildir.
*
* This function MUST also byte-stuff the termination character 
* as described in the project spec.
*
* This function MUST also append the termination character (`.') and a CRLF pair 
* to the end.
*/
char* get_file(char* user, char* filename){
	
	struct dirent *entry;
	DIR *userDirp;
	bool userFound = false;
	bool fileFound = false;
	char *userPath = NULL;
	FILE *fp;
	log_write(module, user, "GetFile", filename);

	rewinddir(dirp);
	//find the user folder
	while((entry = readdir(dirp)) != NULL) {
		if(!strcmp(entry->d_name,user)) {
			userFound = true;
			break;
		}
	}
	if (!userFound) {
		return NULL;
	}
	//create the path to the user folder
	userPath=malloc(sizeof(char) * 
			(strlen(entry->d_name) + strlen(mailpath) + 1));
	if(userPath == NULL){
		fprintf(stderr,"out of mem\n");
	}
	sprintf(userPath,"%s%s/",mailpath,entry->d_name);

	if ((userDirp = opendir(userPath)) == NULL){
		fprintf(stderr, "Failed to open userDir\n");
		exit(1);
	}
	//find the file you want to get
	while((entry = readdir(userDirp)) != NULL) {
		if(!strcmp(entry->d_name,filename)) {
			fileFound = true;
			break;	
		}
	}

	if (!fileFound) {
		return NULL;
	}
	//create the path to the file you want to get
	char userFilePath[255];
	sprintf(userFilePath,"%s%s/",userPath,filename);
	fp = fopen(userFilePath,"r");
	if (fp == NULL) {
		fprintf(stderr,"can't open file\n");
		exit(1);
	}
	//read the file
	fseek(fp, 0, SEEK_END);
	long fsize = ftell(fp);
	rewind(fp);

	char *emailText = malloc(fsize + 1);
	if (emailText == NULL) {
		fprintf(stderr, "out of mem\n");
		exit(1);
	}
	fread(emailText,fsize,1,fp);
	fclose(fp);
	emailText[fsize] = 0;

	//Byte Stuffing
	int i = 1;
	int stringLength = strlen(emailText);
	while (i < stringLength) {
		if((!strcmp(&emailText[i-1],"\r\n")) && 
			(!strcmp(&emailText[i],"."))) 
		{
			realloc(emailText, sizeof(emailText) + sizeof(char));


			strcpy(&emailText[i+1],&emailText[i]);

			stringLength++;
			i++;
		}
		i++;
	}

	return emailText;

}

