// COMP20006 Project 2 registers.h
// Author: Peter Stuckey

/*------------------------------------------------------------------------------
  registers_used(instr, reads, writes)
  Given a MIPS machine code instruction "instr", 
  this calculates the registers used by the instruction.
  It sets the unsigned int pointed to by "reads" to have a bit set if the 
  corresponding register is read. 
  NOTE that bit 0 is this unsigned int has no meaning
         e.g.  *read = 0x00000012  means registers 4 and 1 are read

  Similarly it sets the unsigned int pointed to by "writes" to have a bit set 
  if the corresponding register is written. 
  NOTE that bit 0 in this unsigned int has no meaning
         e.g. *write = 0x80000003  means registers 31 and 1 are written 
  (note 0 bit ignored)
--------------------------------------------------------------------------------*/
void registers_used(unsigned int instr, 
		    unsigned int *reads, unsigned int *writes); 
