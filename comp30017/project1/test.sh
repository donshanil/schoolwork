#!/bin/bash

#set -x
#limit to 100Mb memory
ulimit -v 100000
#limit to 30 seconds of CPU time
ulimit -t 30

# warn about running on non-CSSE machines
machines="cat lister rimmer holly toaster munyoo queeg mundula"
hname=`hostname`
inlist=`printf "%s\n" $machines | grep "^$hname"`
if [ ! "$hname" = "$inlist" ]
then
	echo "Warning - not a CSSE machine"
fi

# display maximum line lengths
echo "Maximum line lengths in each file"
for i in `ls *.c *.h Makefile`
do
	len=`awk ' { if ( length > x ) { x = length } }END{ print x }' $i`
	echo $i $len
done

dirname=`pwd`
bdirname=`basename $dirname`
exe="$dirname/sched"

# check makefile exists
if [ ! -s "Makefile" ]
then
	echo "Error - Makefile doesn't exist"
	exit 0
else
	# try to compile it
	echo "Makefile exists"
	echo "Cleaning..."
	make clobber
	echo "Making..."
	make
	if [ ! -x $exe ]
	then
		echo "Error - No executable called sched was produced by the compilation"
		exit 0
	fi
fi

# use rlimit if it exists on the system
if command -v rlimit > /dev/null 2>&1
then
	#30 seconds CPU time
	#30 seconds wall time
	#500kb file limit
	#no coredump file
	rlim="rlimit -t 30 -T 30 -f 500k -c 0"
else
	echo "Warning - unable to use rlimit to restrict tested program"
	rlim=""
fi

# make a temporary directory and trap so it gets cleaned up later
user=`whoami`
tempdir="/tmp/sched-$user-$RANDOM"
function cleanup()
{
	/bin/rm -rf $tempdir
}
mkdir $tempdir
trap cleanup EXIT
# set permissions to keep others out of temp directory
chmod 700 $tempdir
cp -p $exe $tempdir/sched

#function for running a single test
function tsched()
{
	prog=$1
	opts=$2
	infile=$3
	expfile=$4
	echo "COMMAND LINE ARGUMENTS"
	echo $opts
	echo "INPUT"
	cat $infile
	echo "EXPECTED STDOUT"
	cat $expfile
	$rlimit $tempdir/sched $opts -f $infile > $tempdir/test.stdout 2> $tempdir/test.stderr
	echo "STDOUT"
	cat $tempdir/test.stdout
	echo "STDERR"
	cat $tempdir/test.stderr
	echo "DIFFERENCE"
	# unified diff format
	# print a status line if files are the same
	diff -s -u $expfile $tempdir/test.stdout
}

tests_dir=~tjtg/osproj1_tests
# run some tests using the function above
echo "================================"
tsched $tempdir/sched "-a fcfs -m 300" $tests_dir/spec.in $tests_dir/spec_fcfs.out
#echo "================================"
#tsched $tempdir/sched "-a rr -m 400" $tests_dir/spec.in $tests_dir/spec_rr.out


exit 0
