//   Anonymised analyse.c for peer review
//

#include <stdio.h>
#include <stdlib.h>

//sort function used for qsort
static int sort(const void *x, const void *y) {
	return (*(int*)x - *(int*)y);
}

int
main(void)
{
	#define SIZE 31
	#define SIZE3 1031
	int i; //increment counter for loops
	int lim; //counter to find number of instructions
	int input[SIZE3]; //initial array to load all input values
	int regs[SIZE]; //array for register values

	//loop to scan in input values
	for (i = 0; i < SIZE3; i++) {
		scanf(" %x", &input[i]);

		if (input[i] == 0xffffffff) { 
			lim = i; //lim takes value of i at break point
			break; //break if input is ffffffff
		}
	}


	if (lim < 31) {
		//Terminates if less than 32 values inputted
		printf("No instructions inputted");
		exit(1);
	}

	//loop to transfer register values from input to new array
	for (i = 0; i < SIZE; i++) { 
		regs[i] = input[i];
	}

	#define SIZE2 (lim - SIZE) //size of instruction array 
	//no of instructions = total input values - no of register values
	int instr[SIZE2]; //array for instructions
	for (i = 0; i < SIZE2; i++) { 
		instr[i] = input[(i + 31)]; //input[31] is start of instructions
	}

	int addr = 0x00400000; //assume first instruction at 0x00400000
	int PC; //program counter used in certain instructions
	int imm; //immediate value used in certain instructions
	int leader_count = 0; //counts number of leaders
	int leaders[SIZE2]; //array to store leaders

	for (i = 0; i < SIZE2; i++) { 
		addr = addr + 4;

		//syscall
		if (instr[i] == 12) { 
			leaders[leader_count] = addr;
			leader_count = leader_count + 1;
			//prints next instruction addr
			printf("%08x\n", addr); 

		//jalr
		} else if (((instr[i] >> 26) == 0) &&
			((instr[i] & 0x0000003f) == 9)) {
			leaders[leader_count] = addr;
			leader_count = leader_count + 1;
			imm = ((instr[i] & 0x03e00000) >> 21);
			//prints value of register imm and next instruction addr
			printf("%08x\n", regs[(imm - 1)]);
			printf("%08x\n", addr);

		//jr
		} else if (((instr[i] >> 26) == 0) &&
			((instr[i] & 0x0000003f) == 8)) {
			imm = ((instr[i] & 0x03e00000) >> 21);
			//only prints value of register imm
			printf("%08x\n", regs[(imm -1)]);

		//j
		} else if ((instr[i] >> 26) == 2) {
			PC = (addr + 4) & 0xf0000000;
			leaders[leader_count] = (PC |  
			((instr[i] & 0x3ffffff) << 2));
			leader_count = leader_count + 1;
			//prints addr jumped to
			printf("%08x\n", (PC | ((instr[i] & 0x3ffffff) << 2)));

		//jal
		} else if ((instr[i] >> 26) == 3) {
			PC = (addr) & 0xf0000000;
			leaders[leader_count] = (PC | 
			((instr[i] & 0x3ffffff) << 2));
			leader_count = leader_count + 1;
			leaders[leader_count] = addr;
			leader_count = leader_count + 1;
			//prints addr jumped to and next instruction addr
			printf("%08x\n", (PC | ((instr[i] & 0x3ffffff) << 2)));	
			printf("%08x\n", addr);

		//branch
		} else if (((instr[i] >> 26) == 1) || 
			((instr[i] >> 26) == 4) ||	
			((instr[i] >> 26) == 5) ||  
			((instr[i] >> 26) == 6) ||
			((instr[i] >> 26) == 7)) {
			//imm = number of instructions to skip 
			imm = instr[i] & 0x0000ffff; 
			leaders[leader_count] = (4 * (imm) - 4 + addr);
			leader_count = leader_count + 1;
			leaders[leader_count] = addr;
			leader_count = leader_count + 1;
			//prints addr branched to and next instruction addr
			printf("%08x\n", (4 * (imm) - 4 + addr));
			printf("%08x\n", addr);
		}

	}
	printf("\n"); //new line to separate part A and B

	//qsort sorts leaders by ascending value
	qsort(leaders, leader_count, sizeof(int), sort); 

	int distinct_ldrs[leader_count]; //array to store only unique values
	//code requires first value stored to compare values and find duplicates
	distinct_ldrs[0] = leaders[0]; 
	//block count starts at 2 to account for first leader and 0x00400000
	int block_count = 2; 

	for (i = 1; i < leader_count; i++) {
		if (leaders[i] != distinct_ldrs[(i - 1)]) {
			//store leader if not equal to previous leader
			distinct_ldrs[i] = leaders[i];
			block_count = block_count + 1;
		} else {
			//fill in value with termination code ffffffff
			//ffffffff is only value impossible to exist in array
			distinct_ldrs[i] = 0xffffffff; 
		}
	}

	//qsort used to put all the ffffffff values at the top to be removed
	qsort(distinct_ldrs, leader_count, sizeof(int), sort);

	int blocks[(block_count)]; //initialise array for basic blocks

	//blocks array is the same as distinct_ldrs except size is adjusted
	for (i = 1; i < block_count; i++) {
		blocks[i] = distinct_ldrs[leader_count - block_count + i];
	}

	blocks[0] = 0x00400000; //default starting basic block
	
	for (i = 0; i < (block_count - 1); i++) {
		//prints leaders and blocks except for last value
		printf("%08x - %08x\n", blocks[i], blocks[(i + 1)] - 4);
	}
	//prints last leader and block which are the same (unique case)
	printf("%08x - %08x\n", blocks[block_count-1], blocks[block_count-1]);

	return 0;
}
