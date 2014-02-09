pop3: main.o pop.o socket.o maildir.o logging.o
	gcc -g -O0 -Wextra -Wall -lpthread -lrt -lsocket -lnsl -lm -o pop3 main.o pop.o socket.o maildir.o logging.o

main.o: main.c pop.h socket.h maildir.h logging.h 
	gcc -Wextra -Wall -g -O0 -c -lpthread -lrt -lsocket -lnsl main.c

pop.o: pop.c pop.h socket.h maildir.h logging.h 
	gcc -Wall -Wextra -g -O0 -c -lpthread -lrt -lsocket -lnsl -lm pop.c

socket.o: socket.c socket.h 
	gcc -Wall -Wextra -g -O0 -c -lpthread -lrt -lsocket -lnsl socket.c

maildir.o:  maildir.c maildir.h logging.h 
	gcc -Wall -Wextra -g -O0 -c -lpthread -lrt -lsocket -lnsl maildir.c

logging.o: logging.c logging.h 
	gcc -Wall -Wextra -g -O0 -c -lpthread -lrt -lsocket -lnsl logging.c

