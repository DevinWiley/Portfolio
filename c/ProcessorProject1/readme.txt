cpu.c 	 - source code for the CPU process for the project
memory.c - source code for the Memory process for the project
sort.txt - program provided for use with CPU process
		in the bottom of the file there is a
		group of numbers labelled 'unsorted list'
		the program will run through and print
		the sorted list, max to min with
		complexity O(n^2)
				
Compile:
	gcc memory.c -o memory
	gcc cpu.c -o cpu
	
Running:
	cpu sort.txt			//runs sort.txt
	cpu sample1.txt				
	cpu sample2.txt
	cpu sample3.txt inter.txt