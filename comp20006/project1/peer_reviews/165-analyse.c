//   Anonymised analyse.c for peer review


// COMP20006 Project 1/

#include <stdio.h>

#define REGISTERS 31			//number of registers (excluding zero register)
#define MAX_SIZE 1000			//max number of instructions
#define WORD_SIZE 4				//word size
#define JR 0					//opcode of jr, jalr and syscall instruction
#define BGEZ_BLTZ 1				//opcode for bgez and bltz instructions
#define J 2						//opcode of jump (j) instruction
#define JAL 3					//opcode of jal instruction
#define BEQ 4					//opcode of beq instruction
#define BNE 5					//opcode of bne instruction
#define BLEZ 6					//opcode of blez instruction
#define BGTZ 7					//opcode of bgtz instruction
#define JRF 8					//code for function component of jr instruction
#define JALRF 9					//code for function component of jalr instruction
#define SYSF 12					//code for function component of syscall instruction
#define LUI 15					//opcode for lui instruction
#define LB 32					//opcode for lb instruction
#define LH 33					//opcode for lh instruction
#define LW 35					//opcode for lw instruction
#define SB 40					//opcode for sb instruction
#define SH 41					//opcode for sh instruction
#define SW 43					//opcode for sw instruction


int sort_int_array(int[], int);  		//function to sort an array into ascending order with no repeated entries
void int_swap(int*, int*);				//function to swap two integers, used in sort_int_array
int already_used(int[], int, int);		//function to determine if a register has already been read or written

int
sort_int_array(int A[], int n){
	int i, didswaps=1;
	while(didswaps){
		didswaps = 0;
		for(i=0; i<n-1; i++){
			if(A[i] > A[i+1]){
				int_swap(&A[i], &A[i+1]);
				didswaps = 1;
			}else if (A[i] == A[i+1]){			//if have a repeated entry put one of the entries to the
				int_swap(&A[i+1], &A[n-1]);		//end of the matrix and decrement the size by one so
				n--;							//that entry is no longer included
				didswaps = 1;
			}
		}
	}
	return n;
}

void
int_swap(int *a, int *b){
	int t;
	t = *a;
	*a = *b;
	*b = t;
}

int already_used(int A[], int n, int reg){
	int i;
	for(i=0; i < n; i++){
		if(A[i] == reg){
			return 1;
		}
	}
	return 0;
}

int
main(int argc, char **argv){
	int i;
	int registers[REGISTERS];			//matrix to store register values
	int instructions[MAX_SIZE];			//matrix to store instructions
	int address;
	int size = 0;

	//read in values of registers and instructions to two separate matrices//
	for(i=0; i < MAX_SIZE; i++){
		scanf("%x", &address);
		if(address == 0xffffffff){
			break;
		}else if(i < REGISTERS){
			registers[i] = address;
		}else{
			instructions[i-REGISTERS] = address;
			size++;
		}

	}
	//STAGE A: Leader Determination and STAGE B: Basic Blocks//
	//combined so as the leaders are determined every possible basic block address resulting is listed
	//hence by sorting a matrix of these to only include one value each time in ascending order the
	//lists of both the leaders and basic blocks are created//
	int k = 26;
	int l = 16;
	int m = 6;
	int n = 21;
	int mask1 = (1 << k) - 1; 			//create a mask of k 1 bits
	int mask2 = (1 << l) - 1;			//create a mask of l 1 bits
	int mask3 = (1 << m) - 1;			//create a mask of m 1 bits
	int mask4 = (1 << n) - 1;
	int current_address = 0x00400000;
	int jump_address;					//address leaders will jump to
	int opcode, function;
	int basic_blocks[size];				//matrix for basic block limits, will
	int count = 1;						//be sorted later to print desired results
	basic_blocks[0] = current_address;
	//cycle through instructions and print jump and next addresses as required//
	for(i=0; i < size; i++){
		opcode = ((instructions[i]) & (~mask1)) >> k;				//first 6 bits of instruction = opcode
		if(opcode == JR){
			function = ((instructions[i]) & mask3);					//various codes have an opcode of 0
			if(function == JRF){									//the last 6 bits (function) differentiates them
				basic_blocks[count] = current_address;
				count++;
				jump_address = registers[REGISTERS - 1];
				printf("%08x\n", jump_address);
			}else if(function == JALRF){
				jump_address = registers[((instructions[i] & (~mask4)) >> n)];	//target address stored at a register for jalr defined by 5 bits after opcode, which is zero
				basic_blocks[count] = current_address;
				count++;
				basic_blocks[count] = current_address + WORD_SIZE;		//block will continue from here
				count++;
				basic_blocks[count] = jump_address;
				count++;
				basic_blocks[count] = jump_address - WORD_SIZE;
				count++;
				printf("%08x\n", jump_address);
			}else if(function == SYSF){
				basic_blocks[count] = current_address;
				count++;
				basic_blocks[count] = current_address + WORD_SIZE;		//block will continue from here
				count++;
				printf("%08x\n", current_address + WORD_SIZE);
			}
		}else if((opcode >= BGEZ_BLTZ) && (opcode <= BGTZ)){
			basic_blocks[count] = current_address;
			count++;
			basic_blocks[count] = current_address + WORD_SIZE;			//block will continue from here
			count++;
			if(opcode == J || opcode == JAL){
				jump_address = (instructions[i] & mask1) << 2;
				basic_blocks[count] = jump_address;
				count++;
				basic_blocks[count] = jump_address - WORD_SIZE;
				count++;
			}else{
				jump_address = ((instructions[i] & mask2) << 2) + current_address;
				basic_blocks[count] = jump_address;
				count++;
				basic_blocks[count] = jump_address - WORD_SIZE;
				count++;
			}
			printf("%08x\n", jump_address);
			if(opcode != J){
				printf("%08x\n", (current_address + WORD_SIZE));
			}
		}
		current_address += WORD_SIZE;		//keep track of current address as cycle through instructions
	}
	//sorting and printing for stage B//
	printf("\n");
	count = sort_int_array(basic_blocks, count);
	if(count%2 != 0){
		basic_blocks[count] = basic_blocks[count - 1];
		count++;
	}
	for(i=0; i < count; i += 2){
		printf("%08x - %08x\n", basic_blocks[i], basic_blocks[i+1]);
	}
	//STAGE C: Extension Stage - Recording Register Details in basic blocks//
	//note: will reuse masks from stage A//
	int maskrs = mask1 & ~mask4;				//mask to determine the rs (source register) in all basic block instructions
	int	rs = 0;
	int maskrtu = mask4 & ~mask2;				//mask to determine the rt or ru source registers (secondary read for two different instruction types)
	int rtu = 0;								//note ru register is read in a branch instruction and written in a non branch instruction
	int read = 0;								//32 bit number to record registers read in basic blocks
	int o = 11;
	int mask5 = (1 << o) - 1;					//mask of o 1 bits
	int maskrd = mask2 & ~mask5;				//mask to determine the rd (destination register) in all basic block instructions
	int rd = 0;
	int write = 0;								//32 bit number to record registers written to in basic blocks
	int j = 1;
	int read_reg[REGISTERS] = {0};				//array to record registers read so they aren't counted mutliple times
	int write_reg[REGISTERS] = {0};				//array to record registers written to so they aren't counted mutliple times
	int read_used = 0;							//counts the number of unique registers read
	int write_used = 0;							//counts the number of unique registers written to
	current_address = 0x00400000;
	printf("\n");
	for(i=0; i < size; i++){
		opcode = ((instructions[i]) & (~mask1)) >> k;
		if(opcode == JR){
			function = ((instructions[i]) & mask3);
			if(function == JRF){
				read += (1 << 31);
			}else if(function == SYSF){
				//do nothing//
			}else{
				rs = (instructions[i] & maskrs) >> n;		//determine the rs register (its number)
				if(((already_used(read_reg, read_used, rs)) == 0) && (rs != 0)){
					read += (1 << rs);						//add one to the read number in the relevant
					read_reg[read_used] = rs;				//register position and track registers used
					read_used++;							//same done below for other instructions and
				}											//registers including those written to
				rtu = ((instructions[i]) & maskrtu) >> l;	//determine the rt or ru register (its number)
				if(((already_used(read_reg, read_used, rtu)) == 0) && (rtu != 0)){
					read += (1 << rtu);
					read_reg[read_used] = rtu;
					read_used++;
				}
				rd = (instructions[i] & maskrd) >> o;		//determine the rd register (its number)
				if(((already_used(write_reg, write_used, rd)) == 0) && (rd != 0)){
					write += (1 << rd);
					write_reg[write_used] = rd;
					write_used++;
				}
			}
		}else if(opcode == JAL){
			write += (1 << 31);			//the JAL instruction has an in built write to register 31
		}else if(opcode == J){
			//do nothing//
		}else if((opcode == BGEZ_BLTZ) || ((opcode >= BEQ) && (opcode <= BGTZ))){
			rs = (instructions[i] & maskrs) >> n;
			if(((already_used(read_reg, read_used, rs)) == 0) && (rs != 0)){
				read += (1 << rs);
				read_reg[read_used] = rs;
				read_used++;
				}
			rtu = ((instructions[i]) & maskrtu) >> l;		//for a branch instruction ru is written to
			if(((already_used(read_reg, read_used, rtu)) == 0) && (rtu != 0)){
				read += (1 << rtu);
				read_reg[read_used] = rtu;
				read_used++;
			}
		}else{
			rs = (instructions[i] & maskrs) >> n;
			if(((already_used(read_reg, read_used, rs)) == 0) && (rs != 0)){
				read += (1 << rs);
				read_reg[read_used] = rs;
				read_used++;
			}
			rtu = ((instructions[i]) & maskrtu) >> l;				//for a non-branch instruction ru is written to unless it is an arithmetic instruction
			rd = (instructions[i] & maskrd) >> o;					//it seems that if arithmetic instructions use two source register there is an rd
			if((rd > 0) && (rd <= 31) && (opcode != LUI)
			&& (opcode != LB) && (opcode != LH) && (opcode != LW)	//load and store instructions also behave slightly differently to other types
			&& (opcode != SB) && (opcode != SH) && (opcode != SW)){
				if(((already_used(write_reg, write_used, rd)) == 0)){		//if the rd bits are a register of interest
					write += (1 << rd);
					write_reg[write_used] = rd;
					write_used++;
				}
				if(((already_used(read_reg, read_used, rtu)) == 0) && (rtu != 0)){
					read += (1 << rtu);
					read_reg[read_used] = rtu;
					read_used++;
				}
			}else if(((already_used(write_reg, write_used, rtu)) == 0) && (rtu != 0)){
				write += (1 << rtu);
				write_reg[write_used] = rtu;
				write_used++;
			}
		}
		//print 32 bit read and write values for each basic block//
		if(current_address == basic_blocks[j]){
			printf("%08x - %08x : %08x %08x\n", basic_blocks[j-1], basic_blocks[j], read, write);
			read = 0;
			write = 0;
			int h;
			for(h = 0; h < REGISTERS; h++){
				read_reg[h] = 0;		//reset for next basic block//
				write_reg[h] = 0;
			}
			read_used = 0;				//reset for next basic block//
			write_used = 0;
			j += 2;
		}
		current_address += WORD_SIZE;
	}
	return 0;
}

