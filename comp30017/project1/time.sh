#!/bin/bash

#created by Ben Chong for COMP30017 Project 1
# given an input file/argument, will run all algorithms
# 2nd argument determines memory size.
# if given a 3rd argument, will save output to it.
echo -e "USAGE: ./time.sh <FILENAME> <MEMORY_SIZE>\n"
echo -e "Running FCFS on "$1". Memory size $2.\n"
sched -f $1 -a fcfs -m $2
echo -e "\nRunning rr on "$1". Memory size $2.\n"
sched -f $1 -a rr -m $2
echo -e "\nRunning multi-level-feedback- NO PREEMPT on "$1". Memory size $2.\n"
sched -f $1 -a mlfq_np -m $2 
echo -e "\nRunning PREEMPT mlfq on "$1". Memory size $2 \n"
sched -f $1 -a mlfq_pre -m $2

