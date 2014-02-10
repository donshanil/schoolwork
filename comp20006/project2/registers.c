// COMP20006 Project 2 registers.c
// Author: Peter Stuckey

#include "registers.h"
#include <stdio.h>

// opcodes (first 5 bits)
#define TYPE1 0
#define BGEZ  1
#define BLTZ  1
#define J     2
#define JAL   3
#define BEQ   4
#define BNE   5
#define BLEZ  6
#define BGTZ  7
#define ADDI  8
#define ADDIU 9
#define ANDI  0xc
#define OX1C  0x1c
#define ORI   0xd
#define XORI  0xe
#define LUI   0xf
#define SLTI  0xa
#define SLTIU 0xb
#define BCC   0x11
#define LB    0x20
#define LH    0x21
#define LW    0x23
#define SB    0x28
#define SH    0x29
#define SW    0x2b

// last 6 bit codes 0x1c
#define BLTZ1  0
#define BGEZ1  1
#define BGEZAL 0x11
#define BLTZAL 0x10

// last 6 bit codes 0x1c
#define CLO   0x21
#define CLZ   0x20
#define MUL   2
#define MADD  0
#define MADDU 1
#define MSUB  4
#define MSUBU 5

// last 6 bit codes TYPE1 (0)
#define JR      8
#define JALR    9
#define SYSCALL 0xc
#define ADD     0x20
#define ADDU    0x21
#define AND     0x24
#define DIV     0x1a
#define DIVU    0x1b
#define MULT    0x18
#define MULU    0x19
#define NOR     0x27
#define OR      0x25
#define SLL     0
#define SLLV    4
#define SRA     3
#define SRAV    7
#define SRL     2
#define SRLV    6
#define SUB     0x22
#define SUBU    0x23
#define XOR     0x26
#define SLT     0x2a
#define SLTU    0x2b  
#define TEQ     0x34
#define TNE     0x36
#define MFHI    0x10
#define MFLO    0x12

int         debug2 = 0;
 
void registers_used(unsigned int instr, 
		   unsigned int *reads, unsigned int *writes) {
  unsigned    opcode;
  unsigned    rs;
  unsigned    rt;
  unsigned    rd;
  unsigned    last6;

  opcode = instr >> 26;
  switch (opcode) {
  case TYPE1:
    if (debug2) printf("TYPE1\n");
    rs = (instr >> 21) & 0x1f;
    rt = (instr >> 16) & 0x1f;
    rd = (instr >> 11) & 0x1f;
    last6 = instr & 0x3f;
    switch (last6) {
    case JR:
      if (debug2) printf("JR\n");
      *reads = 1 << rs;
      *writes = 0;
      return;
      break;
    case JALR:
      if (debug2) printf("JALR\n");
      *reads = 1 << rs;
      *writes = 1 << rd;
      return;
      break;
    case SYSCALL:
      *reads = 1 << 2 | 1 << 4 | 1 << 5 | 1 << 6;  // reads $v0 and $a0, $a1, $a2
      *writes = 0;                                 // ignore fact it could write $v0 and $a0 
      return;
      break;
    default:
      if (debug2) printf("DEFTYPE1\n");
      *reads = 1 << rs | 1 << rt;
      *writes = 1 << rd;
      return;
      break;
    }
    break;
  case OX1C:
    if (debug2) printf("OX1C\n");
    rs = (instr >> 21) & 0x1f;
    rt = (instr >> 16) & 0x1f;
    rd = (instr >> 11) & 0x1f;
    last6 = instr & 0x3f;
    switch (last6) {
    case MUL:
    case CLO:
    case CLZ:
      if (debug2) printf("MUL\n");
      *reads = 1 << rs | 1 << rt;
      *writes = 1 << rd;
      return;
      break;
    default:
      if (debug2) printf("DEFOX1C\n");
      *reads = 1 << rs | 1 << rt;
      *writes = 0;
      return;
      break;
    }
    break;
  case J:
    if (debug2) printf("J\n");
    *reads = 0;
    *writes = 0;
    return;
    break;
  case JAL:
    if (debug2) printf("JAL\n");
    *reads = 0;
    *writes = 1 << 31;
    return;
    break;
  case BGEZ:
  case BGTZ:
  case BLEZ:
    //	case BLTZ:
  case BEQ:
  case BNE:
    if (debug2) printf("BRANCH\n");
    rs = (instr >> 21) & 0x1f;
    rt = (instr >> 16) & 0x1f;
    *writes = 0;
    if (opcode == BGEZ) {
      switch (rt) {
      case BLTZAL:
      case BGEZAL:
	*writes = 1 << 31;
	// fall through
      case BGEZ1:
      case BLTZ1:
	*reads = 1 << rs;
 	break;
      }} else {
      *reads = 1 << rs | 1 << rt;
    }
    return;
    break;
  case LW:
  case LB:
  case LH:
    if (debug2) printf("LW\n");
    rs = (instr >> 21) & 0x1f;
    rt = (instr >> 16) & 0x1f;
    *reads = 1 << rs;
    *writes = 1 << rt;
    return;
    break;
  case LUI:
    if (debug2) printf("LUI\n");
    rs = (instr >> 21) & 0x1f;
    rt = (instr >> 16) & 0x1f;
    *reads = 0;
    *writes = 1 << rt;
    return;
    break;
  case SW:
  case SH:
  case SB:
    if (debug2) printf("SW\n");
    rs = (instr >> 21) & 0x1f;
    rt = (instr >> 16) & 0x1f;
    *reads = 1 << rs | 1 << rt;
    *writes = 0;
    return;
    break;
  default:
    if (debug2) printf("DEFAULT\n");
    rs = (instr >> 21) & 0x1f;
    rt = (instr >> 16) & 0x1f;
    *reads = 1 << rs;
    *writes = 1 << rt;
    return;
    break;
  }
  return;
}
