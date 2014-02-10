/* analyse.c, intended to analyse a MIPS program*/
//COMP20006 Project 1
//Authored by Ben Chong ID540928
//HIDE_END

//IMPORTANT: Part C does not give correct output. However, the code has been left in for
//viewing and logic checking purposes.

//TO RUN:, analyse (or what you compile it as) <input file> in command line

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define MAX_SIZE 10000 
#define MAX_REG 32

int hecs(char x[]); //prototype
//function to convert hex into computer readable hex
int hecs(char x[]) {
	int converted ;
	converted = strtoul(x, NULL, 16);
	return converted;
}

//This program will only read the first argument, the file name. 
int main (int argc, char *argv[]) {
	
	FILE *fp;
	char input_stream[MAX_SIZE];
	char extra[MAX_SIZE];
	char* tokens[1000];
	int x;
	int partc[32] = {0x1, 0x2, 0x4, 0x8, 0x10, 0x20, 0x40, 0x80, 0x100,
	0x200, 0x400, 0x800, 0x1000, 0x2000, 0x4000, 0x8000, 0x10000, 0x20000,
	0x40000, 0x80000, 0x100000, 0x200000, 0x400000, 0x800000, 0x1000000,
	0x2000000, 0x4000000, 0x8000000, 0x10000000, 0x20000000, 0x40000000,
	0x80000000};

//opens file in fp.
	if (argc!=1){
		for(x=1;x<argc;x++)
		{
				fp = fopen(argv[x], "r"); 
		}
	}
	else {
		printf("No file specified. Exiting\n");
		return 0;
		}
	if (fp == NULL)
		{
		printf("Input invalid. Exiting\n");
		return 0;
		}

fgets(input_stream, MAX_SIZE, fp);			
	
	while (fgets(extra, MAX_SIZE, fp) != NULL ) //reads input string.
		{
		strcat(input_stream, extra);
		}

//now we split our input string using space and new line  as delimiter

	tokens[0] = strtok(input_stream, " \n");
	int j, i=0;
	while ( tokens[i] != NULL) {
		i++;
		tokens[i] = strtok(NULL, " \n");
	}
//we have our split instructions. lets now fill our registers
	int registers[32];
	int reg_int;
	registers[0] = 0x0;
	for (reg_int=0; reg_int<=31; reg_int++) {
		registers[reg_int+1] = hecs(tokens[reg_int]);
		}
/*  now we interpret the instructions. 
in this first part, we are tracking branch instructions. 
We must distinguish between other instructions, as some share the same format. 
To retrieve our opcode, we bitshift 26 places to the right. */
	
	int current_pos = 0x400000; //current step/pos in memory
	int instruct_count = 0;	//the number of leaders found
	int shifted;	//used as a bitshifted 26 places right to get the opcode
	int instruct; //used as a proper hex version of our input
	int approved_instruct[1000] ; // this will be our array of leaders
	int block[i-31];// this array is for Part B, stores locations of jumps.
	int reader[i-31]; //part C registers read
	int writer[i-31]; //registers written
	for (j=0; j<=i-31; j++){ //we must initialise it with zeros
		block[j]=0;
		reader[j]=0x0;
		writer[j]=0x0;
		}
	for (j=31; j<=i-1; j++) { //
		instruct = hecs(tokens[j]);
		shifted = (hecs(tokens[j])>>26);
		if (shifted >=0x4 && shifted <=0x5 ) { //beq4 bne5 reads s t 
			approved_instruct[instruct_count] = current_pos+(0xFFFF &
			instruct)*4;
			instruct_count++;
			approved_instruct[instruct_count]= current_pos+4;
			instruct_count++;
			block[j-31] = 1;
			int jump_number = ((current_pos+(0xFFFF & instruct)*4)-0x400000)/0x4;
			block[jump_number-1] =1;
			reader[j-31] = (reader[j-31] | ((instruct>>21)&0x1F)); //s
			reader[j-31] = (reader[j-31] | ((instruct>>16)&0x1F)); //t
			}
		else if (shifted >=0x6 && shifted <=0x7 ) { // blez6 bgtz7 reads s
			approved_instruct[instruct_count] = current_pos+(0xFFFF &
			instruct)*4;
			instruct_count++;
			approved_instruct[instruct_count]= current_pos+4;
			instruct_count++;
			block[j-31] = 1;
			int jump_number = ((current_pos+(0xFFFF & instruct)*4)-0x400000)/0x4;
			block[jump_number-1] =1;
			reader[j-31] = (reader[j-31] | ((instruct>>21)&0x1F)); //s
			}

		else if (shifted == 0x1	) { //BGEZ and bltz
			if (((instruct>>16) & 0x401) == 0x401){	//bgez
				approved_instruct[instruct_count] = current_pos+(0xFFFF &
				instruct)*4;
				instruct_count++;
				approved_instruct[instruct_count] = current_pos+4;
				instruct_count++;
				block[j-31]=1; 
				int jump_number = ((current_pos+(0xFFFF & instruct)*4)-0x400000)/0x4;
				block[jump_number-1] =1;
				reader[j-31] = (reader[j-31] | ((instruct>>21)&0x1F)); //s
				}
			else if (((instruct>>16) & 0x400) == 0x400){ //bltz
				approved_instruct[instruct_count] = current_pos+(0xFFFF &
				instruct)*4;
				instruct_count++;
				approved_instruct[instruct_count] = current_pos+4;
				instruct_count++;
				block[j-31]=1;
				int jump_number = ((current_pos+(0xFFFF & instruct)*4)-0x400000)/0x4;
				block[jump_number-1] =1;
				reader[j-31] = (reader[j-31] | ((instruct>>21)&0x1F)); //s
				}
			}
 		/*we check for jumps. we take the address from the instruction, and
		  shift it left 2 bits like the real MIPS  */
		else if (shifted == 0x2){ //jump 
			approved_instruct[instruct_count] = ((instruct & 0x3ffffff)<<2);
			instruct_count++;
			block[j-31]=1;
			int jump_number = (((instruct & 0x3ffffff)<<2)-0x400000)/0x4;
			block[jump_number-1] =1;
				}
		else if (shifted == 0x3){ //jal
			approved_instruct[instruct_count] = ((instruct & 0x3ffffff)<<2);
			instruct_count++;
			approved_instruct[instruct_count] = current_pos+4;
			instruct_count++;
			block[j-31]=1;
			int jump_number = (((instruct & 0x3ffffff)<<2)-0x400000)/0x4;
			block[jump_number-1] = 1;
			writer[j-31] = (writer[j-31] | 0x1F);
				}
		/*using our registers that we calculated earlier, we find the register
		  number, and look it up in the register array*/
		else if (shifted == 0x0){ //we test for jr  jalr and syscall
			if ((instruct & 0xF) == 0x8 ){ //jr
			approved_instruct[instruct_count] = (registers[instruct>>21]);
			instruct_count++;
			block[j-31]=1;
			int jump_number = ((registers[instruct>>21])-0x400000)/0x4;
			block[jump_number-1]=1;
			reader[j-31] = (reader[j-31] | ((instruct>>21)&0x1F));
				}
			else if ((instruct & 0xFF) == 0x12){ //jalr jump to addr register
				approved_instruct[instruct_count] = (registers[instruct>>21]);
				instruct_count++;
				approved_instruct[instruct_count] = current_pos+4;
				instruct_count++;
				block[j-31]=1;
				int jump_number = ((registers[instruct>>21])-0x400000)/0x4;
				block[jump_number-1]=1;
				writer[j-31] = (writer[j-31] | 0x1F);
				reader[j-31] = (reader[j-31] | ((instruct>>21)&0x1F));

				}
			else if ((instruct & 0x3F) == 0xC) { //syscall check
				approved_instruct[instruct_count] = current_pos+4;
				instruct_count++;
				block[j-31]=1;
				}
			else if (((instruct & 0x7FF) >= 0x21 && (instruct & 0x7FF)
			<=0x27 )|| (instruct & 0x7FF) == 0x2A || (instruct & 0x7ff) ==
			0x2B){ //read s,t and write to d
			reader[j-31] = (reader[j-31] | ((instruct>>21)&0x1F)); //s
			reader[j-31] = (reader[j-31] | ((instruct>>16)&0x1F)); //t
			writer[j-31] = (writer[j-31] | ((instruct>>11)&0x1F)); //d
			}
		}
		else if(shifted == 0x8 || shifted == 0x9 || shifted == 0xA || shifted
		==0x20 || shifted ==0x23 || shifted == 0x25 || (shifted >= 0xA &&
		shifted<=0xB)||(shifted >= 0xD && shifted<=0xE)){
		//these all read from s, write to t
			reader[j-31] = (reader[j-31] | ((instruct>>21)&0x1F));
			writer[j-31] = (writer[j-31] | ((instruct>>16)&0x1F));
			}
		else if(shifted == 0xF) { //lui writes to t only
			writer[j-31] = (writer[j-31] | ((instruct>>16)&0x1F));
			}
		else if(shifted == 0x1C && 0xFF==0x2){ //mult pseudo reads st, write d
			reader[j-31] = (reader[j-31] | ((instruct>>21)&0x1F)); //s
			reader[j-31] = (reader[j-31] | ((instruct>>16)&0x1F)); //t
			writer[j-31] = (writer[j-31] | ((instruct>>11)&0x1F)); //d
			}
		else if(shifted == 0x2B || shifted == 0x28){ //read s, t only
			reader[j-31] = (reader[j-31] | ((instruct>>21)&0x1F)); //s
			reader[j-31] = (reader[j-31] | ((instruct>>16)&0x1F)); //t

			}

		current_pos += 4; //move to next instruction, tick up by one

	}

//PART A print statement - we run through our array with all our leaders. 
	for (j=0; j<=instruct_count-1; j++){
		printf("%08x\n", approved_instruct[j]);
		}
//PART B Block array print test
	printf(" \n");
//ive made an array which stores the locations of jumps. each time theres a
//jump, we close the block and start a new one.
	int start_block, end_block;
	start_block = 0x400000;
		for (j=1; j<=i-32; j++){
		if (block[j] == 1){
			end_block = 0x400000 + j*4;
			printf("%08x - %08x\n", start_block, end_block);
			start_block = end_block +4;
			j++;
			}
		else if (j==i-32){
			end_block = 0x400000 +(j-1)*4;
			printf("%08x - %08x\n", start_block, end_block);
			}
	}

//PART C PRINT
	printf("\n");
	int r_read, r_write;
	start_block = 0x400000;
	//now we need to initialise arrays because c messes up.
	r_read = partc[reader[0]];
	r_write = partc[writer[0]];
	for (j=1; j<=i-32; j++){
		if (block[j] == 1){
			end_block = 0x400000 + j*4;
			r_read = r_read | partc[reader[j]];
			r_write = r_write | partc[writer[j]];
			printf("%08x - %08x : %08x %08x\n", start_block, end_block,
			r_read, r_write);
			start_block = end_block +4;
			j++;
			r_read = partc[reader[j+1]];
			r_write = partc[writer[j+1]];
			}
		else if (j==i-32){
			end_block = 0x400000 +(j-1)*4;
			r_read = r_read | partc[reader[j]];
			r_write = r_write | partc[writer[j]];
			printf("%08x - %08x : %08x %08x\n", start_block, end_block,
			r_read, r_write);
			}
		else {
			r_read= r_read | partc[reader[j]];
			r_write = r_write | partc[writer[j]];
				}
		}
	fclose(fp);
	return 0;
}


