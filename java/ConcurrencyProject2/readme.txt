Compile: All source files in same directory
	javac -d ./ *.java
	
Run: Class files should be in Project2 subdirectory
	java Project2.Project2
	
Other details:
	I increase the number of max user processes allowed so I simply ran:
		ulimit -u 1024
	The default is set to 100, but this causes my program to 
	throw java.lang.OutOfMemoryError while creating threads
	The program only uses ~40 kB of memory, with 50% being
	java.lang.Thread and char[]