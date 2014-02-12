2013 COMP30017 Operating Systems and Network Services
======
All coded in C with a few shell scripts scattered throughout


Project 1
----
Using some meagre skeleton code, we were instructed to 'recreate' a process scheduler on a CPU
"The aim of this project is to increase your familiarity with process scheduling and issues in memory
management."

We had to recreate 4 different scheduling algorithms - 
* First-come first-served
* round robin
* multi-level feedback queue without pre-emption.
* multi-level queue WITH pre-emption.

We also had to simulate 'mallocing' memory and swapping to disk where necessary.

Project 2
----
A collaborative project with Tom Grundy.

We were tasked with implementing a primitive POP3 server. We also had to learn how to multithread properly, and use mutexes to prevent race conditions.
The server was required to serve more than 1 client at a time - as such, it had to be able to launch multiple concurrent threads.
These threads could potentially causes race conditions with each other if they were for instance, writing to the same log file.

I was responsible mostly for the multithreading & mutex code within main.c & logging.c, and part of pop.c
