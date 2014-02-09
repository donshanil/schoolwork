CC     =	gcc
CFLAGS =	-Wall -Wextra -g -O0 -std=gnu99
LIBS   =	
PSRC   =	sched.c memory.c algorithms.c process.c utils.c bstrlib.c
POBJ   =	sched.o memory.o algorithms.o process.o utils.o bstrlib.o

sched: $(POBJ)
	$(CC) $(CFLAGS) -o sched $(POBJ) $(LIBS)

clean:
	/bin/rm $(POBJ) 

clobber:
	/bin/rm $(POBJ) sched

sched.o: process.h bstrlib.h utils.h memory.h algorithms.h
process.o: process.h utils.h
memory.o: memory.h process.h utils.h
algorithms.o: algorithms.h process.h utils.h memory.h
bstring.o: bstrlib.h
