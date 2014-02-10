/*virus.c*/
//COMP20006 Project 2
//Authored by Ben Chong ID540928
//HIDE_END

//Please compile with registers.c! Stage F extension not attempted

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <getopt.h>
#include "registers.h"

#define MAX_SIZE 1000 
#define MAX_REG 32
#define TYPE1	0
#define BGEZ	1
#define BLTZ	1
#define J		2
#define JAL		3
#define BEQ		4
#define	BNE		5
#define BLEZ	6
#define BGTZ	7
#define JR 		8
#define JALR	9
#define BGEZAL	0x11
#define BLTZAL	0x10

void int_swap (int*, int*); //prototyping the int swap
int sort_int_array(int[], int);
void *checked_malloc(int);

//given two ints, will swap their positions in memory
void int_swap (int *a, int *b){ 
	int t;
	t= *a;
	*a = *b;
	*b = t;
}

void *checked_malloc (int size) //this code snippet taken from the slides
{
	char *p;

	p = malloc(size);
	if (p==NULL){
		printf("Ran out of memory\n");
		exit(1);
	}
	return p;
}

int sort_int_array(int array[], int n){
	int i, swap=1;
	while(swap){	//when we run out of things to sort
		swap=0;
		for(i=0;i<n-1; i++){
			if(array[i] > array[i+1]){
				int_swap(&array[i], &array[i+1]);
				swap = 1;
			}
			else if (array[i] == array[i+1]){
				int_swap(&array[i+1], &array[n-1]); 
				//put duplicate value at end of array, reduce array size -1
				n--;
				swap=1;
			}
		}
	}
	return n;
}


extern int optopt, optind, opterr;

//we will define our struct bb here, since it applies to more than one stage. 
typedef struct bb {
	unsigned int start_address; //start address
	unsigned int end_address; //end address
	int n_successors; //num successor
	int reachable;
	int v0_D; //will take value based on whether its always reachable, or only
			//sometimes.
	int v0_E; // used for E section. easier to think about it seperately.  		
	struct bb **successors; //index of successor blocks
} BLOCK;


int main (int argc, char *argv[]) {
	
	int size=0;
	unsigned int read_i;
	unsigned int *read_addr = &read_i;
	unsigned int write_i;
	unsigned int *write_addr = &write_i;
	int i, addr_in;
	int c, aflag, bflag, cflag, dflag, eflag, fflag;
	unsigned int instruct[MAX_SIZE];

	if (argc <= 1){ //check if no command line arguments
		eflag=1; //default flag is -E
	}	
	else {	
    	while ((c = getopt(argc, argv, "ABCDEF")) != EOF) {
	    	switch (c)
   			{
        		case 'A':
            		aflag = 1;
            		break;
        		case 'B':
            		bflag = 1;
            		break;
        		case 'C':
            		cflag = 1;
            		break;
        		case 'D':
            		dflag = 1;
            		break;
				case 'E':
            		eflag = 1;
            		break;
 //       		case 'F':
 //          		fflag = 1;
 //         		break;
        		default: //if invalid option, program will exit.
					printf("Stage %c not supported\n", optopt);
           	//	printf("Usage: %s [-ABCDEF]. Upper case is important.\n", argv[0]);
            		exit(1);
            		break;
        	}
    	}
	}
	//reading in our data using stdin.
	for(i=0; i < MAX_SIZE; i++){
		scanf("%x", &addr_in);
		if(addr_in == 0xffffffff){ //TERMINATES the input when 0xffffffff
			break;
		}
		else {
			instruct[i] = addr_in;
			size++;
		}
	}
	int current_address = 0x400000;
	int instruct_A = 0x0;
	int instruct_B = 0x0;
//COMMENCE Stage A
	if (aflag==1){
		//we will iterate through our instruction array, checking to see if we
		//have two consecutive insturctions of 0x34020012 and 0x00000000c
		for(i=0; i<size; i++){
			if (i == 0){
				instruct_B = instruct[i];
			}
			else{
				instruct_A = instruct_B;
				instruct_B = instruct[i];
				if (instruct_A == 0x34020012 && instruct_B == 0xc){
					printf("INFECTED(A) %08x\n", current_address);
				}
			}
			current_address = current_address + 0x4;
		}	
	}

	int B_trigger = 0; 
	int opcode = 0x0;
	int last6 = 0x0;
//STAGE B
//we will step through our instructions methodically again
//if we find 0x34020012, set trigger = 1. If we encounter syscall, print
//v0 is register $2, therefore we are looking for 0100 -> 0x4
	current_address = 0x400000; //reinit just in case
	if (bflag==1) {
		for(i=0;i<size;i++){
			*read_addr = 0x0;
			*write_addr = 0x0;
			if(B_trigger==1){
				if(instruct[i] == 0xc){
					B_trigger =0;
					printf("INFECTED(B) %08x\n", current_address);
				}
				else if (instruct[i]==0x34020012){
					B_trigger =1;
				}	
				else{
					opcode = instruct[i] >> 26;
					last6 = instruct[i] & 0x3f;
					//j, j, branches. we need to use a switch. we also need to
					//be careful because of the tricksy ones using last 6
					//bits.
					switch (opcode) {
					case TYPE1:	//the special icky ones with opcode 0
						last6 = instruct[i] && 0x3f;
						switch(last6){
						case JR: //fallthrough
						case JALR:
							B_trigger = 0; //reset
							break;
						}
						break;
					case J:
					case JAL:
					case BGEZ: //all fallthrough as we do the same thing
					case BLEZ:
					case BEQ:
					case BNE:
						B_trigger =0;
						break;
					default:
						break;
					}
					//if none of that triggered, now we check for write to v0.
					registers_used(instruct[i], &read_i, &write_i);
					if((*write_addr & 0x4 )== 0x4){//just in case
						B_trigger =0;
					}	
				}
			}	
			if (instruct[i]==0x34020012){
				B_trigger=1;
			
			}
		//	printf("B_FLAG= %01i, ANALYSING %08x\n, writing %08x\n",
		//	B_trigger, current_address, *write_addr);
			current_address = current_address + 0x4;	
		}
	}

//STAGE C: BASIC BLOCKS
//we will iterate through and find the leaders first
//this sets the baseline for stage C, D and E.
	if (cflag == 1 || dflag ==1 || eflag ==1 ){
		int leaders[MAX_SIZE]; //reserve space for leaders
		int count = 0;
		int jump_addr = 0x0;
		int branch_value = 0;
		int trigger10 = 0;
		int shift_addr = 0x0;
		current_address = 0x400000; //reinit just in case
		for(i=0;i<size;i++){
			opcode = instruct[i] >> 26;
			if ((trigger10 == 1) && (instruct[i] == 0xc)){ //check for sequence
				trigger10 = 0;
				leaders[count] = current_address + 0x4;
				count++;
				//printf("syscall trigger\n%08x\n", current_address);
			}				
			else if (instruct[i] == 0x3402000a) { //represents li $v0, 10
				trigger10 = 1;
			}
			else{
				trigger10 = 0;
				switch (opcode) {
				case TYPE1: //opcode zero
					last6 = instruct[i] & 0x3f;
					switch(last6){
					case JR: //FALLTHROUGH
					case JALR:
					//	printf("JR\n");
						leaders[count] = current_address + 0x4;
						count++;
						break;
					}
					break;
				case J: //FALTHROUGH
				case JAL:
					leaders[count] = current_address + 0x4;
					count++;
					jump_addr = instruct[i] & 0x3ffffff;
					shift_addr = jump_addr << 2; //takes immediate, shift left 2.
					leaders[count] = shift_addr;
				//	printf("jump:%08x\n", shift_addr);
					count++;
					break;
				case BGEZ:
				case BGTZ:
				case BLEZ:
				case BEQ:
				case BNE:
					//intermediate incidates X instructions forward/back
					branch_value = instruct[i] & 0xffff;
					leaders[count] = current_address + (branch_value*0x4);
					count++;
					leaders[count] = current_address + 0x4;
					count++;
				default:
					break;
				}
			}
			if ((trigger10 == 1) && (instruct[i] == 0xc)){ //check for sequence
				trigger10 = 0;
				leaders[count] = current_address + 0x4;
				count++;
			//	printf("syscall trigger");
			}
			current_address = current_address + 0x4;	
		}//end for loop
		//at this point, we have found our leaders. now we need to remove
		//duplicates and sort.
		//count holds the number of elements in our array
		//so if we use the sort array function, we return our modified count.
		//minus duplicates
		count = sort_int_array(leaders, count);

		int reached_address = 0x400000 + (size-1)*0x4;
		if (leaders[count-1] > reached_address){ //if leader > last address
			count--; //in effect we no longer look at the last address.
			//this contingency is for branch/j being the last instr
		}	

		//we now define space for the array of blocks.
		BLOCK *blk_array; //pointer to the zero element of blk_array
		int next_addr;
		int work_instruct;
		blk_array = checked_malloc((count+1) * sizeof(BLOCK));
		int j;

//THIS part propogates blk_array with the blocks.
		for(i=0;i<=count;i++){
			if(i==0){
				blk_array[i].start_address = 0x400000;
			}
			else{
			blk_array[i].start_address = leaders[i-1];
			}
			if (i == count) {
				next_addr = reached_address;
			}
			else{
				next_addr = leaders[i] - 0x4;
			}	
			blk_array[i].end_address = next_addr;
		}
		int notfound=1;
		int index =0;
		int branch_address;
		for(i=0;i<=count;i++){
//we found our end_addresses. now we look through and find the
//instruction. the index in instruct = (END-0x400000) / 0x4
//for simplicity sake, this is seperate
			work_instruct = instruct[(blk_array[i].end_address -
			0x400000)/0x4];
			opcode = work_instruct >> 26;
			//now we look at our instruction, and find out what its looking
			//for.
		
			switch (opcode) {
			case TYPE1:	//the special icky ones with opcode 0
				last6 = instruct[i] && 0x3f;
				switch(work_instruct){
				case 0x03e00008: //fallthrough - represents jr $31
				case 0x03e0f809://JALR - every block after the jump is a successor.
					//we need to add an entry for every block after. so.
					j = i+1;
					index = 0;
					blk_array[i].successors
					=checked_malloc((count-i)*sizeof(BLOCK*));
					while (j<=count){
						blk_array[i].successors[index] = &blk_array[j];
						j++;
						index++;
					}	
					break;
				case 0x0c: //syscall. we must check that it isn't syscall 10.
					//check instruction before this one.
					if
					(instruct[((blk_array[i].end_address-0x400000)/0x4)-1]==0x3402000a)
					{
						blk_array[i].n_successors = 0;
						break;	//do nothing
					}
					else{
						blk_array[i].successors =
						checked_malloc(1*sizeof(BLOCK*));
						blk_array[i].n_successors = 1;
						blk_array[i].successors[0] = &blk_array[i+1];
						break;
					}	
				}
				break;

			case J:
			case JAL:
			//take address of block in jump, corresponds to start
			//(since its a leader)
				jump_addr = work_instruct & 0x3ffffff;
				shift_addr = jump_addr << 2;
				//we now have our addresses. 
				notfound=1;
				index=0;
				blk_array[i].n_successors = 1;
				blk_array[i].successors = checked_malloc(sizeof(BLOCK*));
				while(notfound==1) {
				//match it to start address of successor block.
					if(blk_array[index].start_address == shift_addr){
						blk_array[i].successors[0] = &blk_array[index];
						notfound--;
					}
					else{
						index++;
					}
				}	
				break;
			case BGEZ: //all fallthrough as we do the same thing
			case BLEZ:
			case BEQ:
			case BNE:
				blk_array[i].n_successors = 2;
				blk_array[i].successors = checked_malloc(2*sizeof(BLOCK*));
				blk_array[i].successors[0] = &blk_array[i+1]; //store the next block index
				//now we find where it branches to.
				branch_value = work_instruct & 0xffff;
				
				branch_address = blk_array[i].end_address +(branch_value*0x4); 
				notfound = 1;
				index = 0;
				while (notfound==1){
					if(blk_array[index].start_address==branch_address){
						blk_array[i].successors[1] = &blk_array[index]; 
						notfound--;
					}	
					else{
						index++;	
					}
				}	
				break;
			default:
				blk_array[i].n_successors = 1;
				blk_array[i].successors = checked_malloc(1*sizeof(BLOCK));
				blk_array[i].successors[0]=&blk_array[i+1]; 
				//the project spec says nothing about last block not being a j
				//or branch. we will assume that it always is.
				break;
			}		
		}	
		//now we have our successors. we will now add the reachable flag.
		for (i=0;i<=count;i++){ //so this will be blk_array
			if (blk_array[i].n_successors != 0){
				for (j=0;j<blk_array[i].n_successors;j++){
					blk_array[i].successors[j]->reachable = 1;
				}
			}	
		}
		int start, end;
		//now we look through each block for the stage B style pattern.
		if(cflag ==1){
			current_address = 0x400000;
			for(i=0;i<=count;i++){
				
				start = (((blk_array[i].start_address)-0x400000)/0x4);
				end = (((blk_array[i].end_address)-0x400000)/0x4);
				B_trigger =0;
				for(j=start;j<=end;j++){
					*read_addr = 0x0;
					*write_addr = 0x0;
					if(B_trigger==1){
						if(instruct[j] == 0xc){
							B_trigger =0;
							if (blk_array[i].reachable == 1){
								// i know this is lazy but i'm pushed for
								// time.
								printf("INFECTED(C) %08x\n", current_address);
							}	
						}
						else if (instruct[j]==0x34020012){
							B_trigger =1;
						}		
						else{
							opcode = instruct[j] >> 26;
							last6 = instruct[j] & 0x3f;
						//j, j, branches. we need to use a switch. we also need to
						//be careful because of the tricksy ones using last 6
						//bits.
							switch (opcode) {
							case TYPE1:	//the special icky ones with opcode 0
								last6 = instruct[j] && 0x3f;
								switch(last6){
								case JR: //fallthrough
								case JALR:
									B_trigger = 0; //reset
									break;
								}
								break;
							case J:
							case JAL:
							case BGEZ: //all fallthrough as we do the same thing
							case BLEZ:
							case BEQ:
							case BNE:
								B_trigger =0;
								break;
							default:
								break;
							}
						//if none of that triggered, now we check for write to v0.
						registers_used(instruct[j], &read_i, &write_i);
							if((*write_addr & 0x4 )== 0x4){//just in case
							B_trigger =0;
							}
						}	
							
					}
				
					if (instruct[j]==0x34020012){
						B_trigger=1;
					}

					current_address = current_address + 0x4;	
				}
			}	
		}
		if (dflag == 1||eflag == 1){ //ALWAYS ENTERED WITH v0=18
			//we scan each block. if v0=18 at the end, we set flag = 1 in
			//successors.
			//we always assume initial execution block has flag 0.
			blk_array[0].v0_D = 0;
			int v0flag = 0;
			//first we initially propagate blk_array[i].
			for(i=1;i<=count;i++){
				if(blk_array[i].reachable == 1){
					blk_array[i].v0_D = 1; 
				}
			}
			//we run our loop twice, using the information from the last loop.
			//we need to propagate info from the 'bottom' of the program
			for (i=0;i<=count;i++){
				start = (((blk_array[i].start_address)-0x400000)/0x4);
				end = (((blk_array[i].end_address)-0x400000)/0x4);
				v0flag = blk_array[i].v0_D;
				//now we look through the block and see if it sets v0 = 18
				for(j=start;j<=end;j++){
					*read_addr = 0x0;
					*write_addr = 0x0;

					if (instruct[j] == 0x34020012){
						v0flag =1;
					}
					else {
						registers_used(instruct[j], &read_i, &write_i);
						if((*write_addr & 0x4) == 0x4){
							v0flag = 0;
						}
					}
				}
				//so after we've gone and checked for v0.
				//we know check if at the end of block execution, v0 'stayed'
				//18.
				for (j=0;j<blk_array[i].n_successors;j++){ 
					
					if (blk_array[i].successors[j]->v0_D !=0 ){ 
					//this is if it is ALWAYS entered with v0=18.
						blk_array[i].successors[j]->v0_D = v0flag;
					}
					else{
						blk_array[i].successors[j]->v0_D = 0;
					}	
				}	
				
			}
//RUN THE ENTIRE LOOP ONCE MORE 
			for (i=0;i<=count;i++){ 
				start = (((blk_array[i].start_address)-0x400000)/0x4);
				end = (((blk_array[i].end_address)-0x400000)/0x4);
				v0flag = blk_array[i].v0_D;
//now we look through the block and see if it sets v0 = 18
				for(j=start;j<=end;j++){
					*read_addr = 0x0;
					*write_addr = 0x0;

					if (instruct[j] == 0x34020012){
						v0flag =1;
					}
					else {
						registers_used(instruct[j], &read_i, &write_i);
						if((*write_addr & 0x4) == 0x4){
							v0flag = 0;
						}
					}
				}
//so after we've gone and checked for v0.
//we know check if at the end of block execution, v0 'stayed'
//18.
				for (j=0;j<blk_array[i].n_successors;j++){ 
					
					if (blk_array[i].successors[j]->v0_D !=0 ){ 
					//this is if it is ALWAYS entered with v0=18.
						blk_array[i].successors[j]->v0_D = v0flag;
					}
					else{
						blk_array[i].successors[j]->v0_D = 0;
					}	
				}	
				
			}	

//PRINT STAGE D
			//now we look through all the blocks again with this knowledge
			if (dflag==1){
				current_address = 0x400000;
				for(i=0;i<=count;i++){
					//we use the start,end to find the index corresponding to
					//instruct, we get our initial value from D
					v0flag = blk_array[i].v0_D;
					start = (((blk_array[i].start_address)-0x400000)/0x4);
					end = (((blk_array[i].end_address)-0x400000)/0x4);
					for(j=start;j<=end;j++){
						*read_addr = 0x0;
						*write_addr = 0x0;
						
						if (instruct[j] == 0x34020012){
							v0flag = 1;
						}
						else if (v0flag == 1) {
							if (instruct[j] == 0xc){ //syscall
								if(blk_array[i].reachable == 1){
									v0flag = 0;
									printf("INFECTED(D) %08x\n", current_address);
								}
							}	
							registers_used(instruct[j], &read_i, &write_i);	
							if((*write_addr & 0x4) == 0x4){
								v0flag = 0;
								
							}	
						}
						current_address = current_address + 0x4;		
					}
				}
			}
//STAGE E
			if(eflag ==1){ //we now check for possibly suspicious
				
				blk_array[0].v0_E = -1; //-1 will denote sometimes.
				for(i=1;i<=count;i++){
					if(blk_array[i].reachable == 1){
						blk_array[i].v0_E = 2;
					}
					else{
						blk_array[i].v0_E = -99;
			//first we assume its always entered
					}
				}
			//we run our loop twice, using the information from the last loop.
			//first time around we set it to 1, and then we check a second
			//time for cases where it might not enter.
				for (i=0;i<=count;i++){
					start = (((blk_array[i].start_address)-0x400000)/0x4);
					end	=(((blk_array[i].end_address)-0x400000)/0x4);
					v0flag = blk_array[i].v0_E;
					//now we look through the block and see if it sets v0 = 18
					for(j=start;j<=end;j++){
						*read_addr = 0x0;
						*write_addr = 0x0;

						if (instruct[j] == 0x34020012){
							v0flag =1;
						}
						else {
							registers_used(instruct[j], &read_i, &write_i);
							if((*write_addr & 0x4) == 0x4){
								v0flag = 0;
							}
						}
					}
				//so after we've gone and checked for v0.
				//we know check if at the end of block execution, v0 'stayed'
				//18.
					for (j=0;j<blk_array[i].n_successors;j++){ 
						if(blk_array[i].reachable == 1){	
							switch (blk_array[i].successors[j]->v0_E){
							case 2: 
								blk_array[i].successors[j]->v0_E = v0flag;
								break;
							case 1:
								if (v0flag == 0||v0flag == -1){
									blk_array[i].successors[j]->v0_E = -1;
								}
								break;
							
							case 0:
								if(v0flag == 1||v0flag == -1 ){
									blk_array[i].successors[j]->v0_E = -1;
								}
								break;
							}
						}
					}	
				
				}

				for (i=0;i<=count;i++){
					start = (((blk_array[i].start_address)-0x400000)/0x4);
					end	=(((blk_array[i].end_address)-0x400000)/0x4);
					v0flag = blk_array[i].v0_E;
					//now we look through the block and see if it sets v0 = 18
					for(j=start;j<=end;j++){
						*read_addr = 0x0;
						*write_addr = 0x0;

						if (instruct[j] == 0x34020012){
							v0flag =1;
						}
						else {
							registers_used(instruct[j], &read_i, &write_i);
							if((*write_addr & 0x4) == 0x4){
								v0flag = 0;
							}
						}
					}
				//so after we've gone and checked for v0.
				//we know check if at the end of block execution, v0 'stayed'
				//18.
					for (j=0;j<blk_array[i].n_successors;j++){ 
						if(blk_array[i].reachable == 1){	
							switch (blk_array[i].successors[j]->v0_E){
							case 2: 
								blk_array[i].successors[j]->v0_E = v0flag;
								break;
							case 1:
								if (v0flag == 0||v0flag == -1){
									blk_array[i].successors[j]->v0_E = -1;
								}
								break;
						
							case 0:
								if(v0flag == 1||v0flag == -1 ){
									blk_array[i].successors[j]->v0_E = -1;
								}
								break;
							}
						}
					}	
				}
//PRINT STAGE E
//first print INFECTED, then suspected.

				current_address = 0x400000;	
				for(i=0;i<=count;i++){
					v0flag = 0;
					if(blk_array[i].v0_E == -1){
						v0flag = -1;
					}
					
					if(blk_array[i].v0_E == 1 || blk_array[i].v0_D == 1){
						v0flag = 1; 
					}	
					start = (((blk_array[i].start_address)-0x400000)/0x4);
					end = (((blk_array[i].end_address)-0x400000)/0x4);
					for(j=start;j<=end;j++){
						*read_addr = 0x0;
						*write_addr = 0x0;
					
						if (instruct[j] == 0x34020012){
							v0flag = 1;
						}
						else if (v0flag == 1){
							if(instruct[j] == 0xc) {
								if(blk_array[i].reachable ==1){
									printf("INFECTED(E) %08x\n",
									current_address);
								}
							}
						}	

						else if (v0flag == -1) {
							if (instruct[j] == 0xc){ //syscall
								if(blk_array[i].reachable == 1 ){
									printf("SUSPICIOUS(E) %08x\n", current_address);
								}
							}	
						registers_used(instruct[j], &read_i, &write_i);	
							if((*write_addr & 0x4) == 0x4){
								v0flag = 0;
							}
						}	
					
						current_address = current_address + 0x4;		
					}
				}	
			}//E BLOCK
		}//end D,E block
		
	}//end C,d,E block
	
return 0;	
}					
//END PROGRAM


