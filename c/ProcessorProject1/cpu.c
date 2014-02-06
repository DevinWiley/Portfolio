#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#define INTERRUPT_LOCATION	1000;

typedef struct cpu{
	int PC, SP, IR, AC, X, Y;
	int state; //system state: 0 for user, 1 for kernel
} CPU;

void CPU_Start(CPU* pThis);
void CPU_FetchMem(CPU* pThis);
void CPU_Process(CPU* pThis);
void CPU_IncPC(CPU* pThis);
void CPU_WriteAddr(CPU* pThis, int address, int value);
int CPU_FetchOperand(CPU* pThis);
int CPU_FetchData(CPU* pThis, int operand);
void CPU_LoadValue(CPU* pThis, int operand);
void CPU_LoadAddress(CPU* pThis, int addressData);
void CPU_StoreAddr(CPU* pThis, int operand);
void CPU_AddX(CPU* pThis);
void CPU_AddY(CPU* pThis);
void CPU_SubY(CPU* pThis);
void CPU_SubX(CPU* pThis);
void CPU_Get(CPU* pThis);
void CPU_PutPort(CPU* pThis, int operand);
void CPU_CopyToX(CPU* pThis);
void CPU_CopyToY(CPU* pThis);
void CPU_CopyFromX(CPU* pThis);
void CPU_CopyFromY(CPU* pThis);
void CPU_JumpAddr(CPU* pThis, int operand);
void CPU_JumpIfEqual(CPU* pThis, int operand);
void CPU_JumpIfNotEqual(CPU* pThis, int operand);
void CPU_CallAddr(CPU* pThis, int operand);
void CPU_Ret(CPU* pThis);
void CPU_IncX(CPU* pThis);
void CPU_IncY(CPU* pThis);
void CPU_DecX(CPU* pThis);
void CPU_LoadIndXAddr(CPU* pThis, int addressData);
void CPU_LoadIndYAddr(CPU* pThis, int addressData);
void CPU_Push(CPU* pThis);
void CPU_Pop(CPU* pThis);
void CPU_Int(CPU* pThis);
void CPU_IRet(CPU* pThis);
void CPU_END(CPU* pThis);

int pcpu[2];
int pmem[2];
unsigned char r;
unsigned char w;

int main(int argc, char *argv[]){
	int result;

	if (pipe(pcpu) == -1 || pipe(pmem)){
		exit(1);
	}
	result = fork();
	switch (result){
		case -1:
			exit(1);
		case 0:
			{
				dup2(pmem[0], STDIN_FILENO);	//replace stdin with pmem[0]
				dup2(pcpu[1], STDOUT_FILENO);	//replace stdout with pcpu[1]
				close(pcpu[0]);
				close(pcpu[1]);
				close(pmem[0]);
				close(pmem[1]);
				if (argc == 3){
					execlp("memory", "memory", argv[1], argv[2], (char *) 0);
				} else {
					execlp("memory", "memory", argv[1], (char *) 0);
				}
				write(STDERR_FILENO, "Exec failed\n", 12);
				_exit(3);
			}
		default:
			{
				CPU *cpu = malloc(sizeof(CPU));
				CPU_Start(cpu);
				r = 'r';
				w = 'w';
				while (cpu->IR != 50){
					CPU_Process(cpu);
				}
			}
	}
	//wait for memory to end and collect zombie
	waitpid(-1, NULL, 0);
}

//initiallizes CPU to begin processing
void CPU_Start(CPU* pThis){
	pThis->AC = 0;
	pThis->state = 0;
	pThis->PC = 0;
	pThis->SP = 999;
}

//Fetches to data from memory at the given PC
void CPU_FetchMem(CPU* pThis){
	pThis->IR = CPU_FetchData(pThis, pThis->PC);
	CPU_IncPC(pThis);
	//printf("IR: %d\n", pThis->IR);
}

int CPU_FetchOperand(CPU* pThis){
	int operand;
	operand = CPU_FetchData(pThis, pThis->PC);
	CPU_IncPC(pThis);
	//printf("Operand: %d\n", operand);
	return operand;
}
int CPU_FetchData(CPU* pThis, int operand){
	int addressData;
	write(pmem[1], &r, sizeof(r));
	char temp[12] = {0x0};
	sprintf(temp, "%11d", operand);
	write(pmem[1], temp, sizeof(temp));
	read(pcpu[0], temp, sizeof(temp));
	//printf("\nValue of FetchData: %s\n", temp);
	sscanf(temp, "%11d", &addressData);
	//printf("Address[%d]: %d\n", operand, addressData);
	return addressData;
}
void CPU_LoadValue(CPU* pThis, int operand){
	pThis->AC = operand;
}
void CPU_LoadAddress(CPU* pThis, int addressData){
	pThis->AC = addressData;
}
void CPU_StoreAddr(CPU* pThis, int operand){
	CPU_WriteAddr(pThis, operand, pThis->AC);
}
void CPU_WriteAddr(CPU* pThis, int address, int value){
	write(pmem[1], &w, sizeof(w));
	char temp1[12] = {0x0}, temp2[12] = {0x0};
	//printf("\nAddress and value: %d %d\n", address, value);
	sprintf(temp1, "%11d", address);
	write(pmem[1], temp1, sizeof(temp1));
	read(pcpu[0], temp1, sizeof(temp1));
	sprintf(temp2, "%11d", value);
	write(pmem[1], temp2, sizeof(temp2));
	read(pcpu[0], temp2, sizeof(temp2)); //check memory
}

void CPU_AddX(CPU* pThis){
	pThis->AC += pThis->X;
}
void CPU_AddY(CPU* pThis){
	pThis->AC += pThis->Y;
}
void CPU_SubX(CPU* pThis){
	pThis->AC -= pThis->X;
}
void CPU_SubY(CPU* pThis){
	pThis->AC -= pThis->Y;
}
void CPU_CopyToX(CPU* pThis){
	pThis->X = pThis->AC;
	//printf("X: %d\n", pThis->X);
}
void CPU_CopyToY(CPU* pThis){
	pThis->Y = pThis->AC;
	//printf("Y: %d\n", pThis->Y);
}
void CPU_CopyFromX(CPU* pThis){
	pThis->AC = pThis->X;
	//printf("AC: %d\n", pThis->AC);
}
void CPU_CopyFromY(CPU* pThis){
	pThis->AC = pThis->Y;
	//printf("AC: %d\n", pThis->AC);
}
void CPU_IncX(CPU* pThis){
	pThis->X += 1;
}
void CPU_DecX(CPU* pThis){
	pThis->X -= 1;
}
void CPU_LoadIndXAddr(CPU* pThis, int addressData){
	pThis->AC = addressData;
}
void CPU_LoadIndYAddr(CPU* pThis, int addressData){
	pThis->AC = addressData;
}

void CPU_JumpAddr(CPU* pThis, int operand){
	if (operand >= 1000 && pThis->state != 1){
		printf("Illegal access: %d\nExiting\n", operand);
		_exit(1);
	} else {
		pThis->PC = operand;
	}
}
void CPU_JumpIfEqual(CPU* pThis, int operand){
	if (operand >= 1000 && pThis->state != 1){
		printf("Illegal access: %d\nExiting\n", operand);
		_exit(1);
	} else if (pThis->AC == 0){
		pThis->PC = operand;
	}
}
void CPU_JumpIfNotEqual(CPU* pThis, int operand){
	//printf("Value of AC and operand: %d %d\n", pThis->AC, operand);
	if (operand >= 1000 && pThis->state != 1){
		printf("Illegal access: %d\nExiting\n", operand);
		_exit(1);
	} else if (pThis->AC != 0){
		pThis->PC = operand;
		//printf("Value of PC: %d\n", pThis->PC);
	}
}
void CPU_CallAddr(CPU* pThis, int operand){
	if (operand >= 1000 && pThis->state != 1){
		printf("Illegal access: %d\nExiting\n", operand);
		_exit(1);
	} else {
		CPU_WriteAddr(pThis, pThis->SP, pThis->PC);
		pThis->PC = operand;
		pThis->SP -= 1;
	}
}
void CPU_Ret(CPU* pThis){
	pThis->SP += 1;
	if (pThis->SP >= 1000){
		printf("Stack Pointer Exception\nExiting\n");
		_exit(1);
	}
	pThis->PC = CPU_FetchData(pThis, pThis->SP);
}
void CPU_Push(CPU* pThis){
	//printf("\nValue before push: %d\n", pThis->AC);
	//printf("SP: %d\n", pThis->SP);
	CPU_StoreAddr(pThis, pThis->SP);
	pThis->SP -= 1;
}
void CPU_Pop(CPU* pThis){
	pThis->SP += 1;
	if (pThis->SP >= 1000){
		printf("Stack Pointer Exception\nExiting\n");
		_exit(1);
	}
	//printf("SP: %d\n", pThis->SP);
	int addressData = CPU_FetchData(pThis, pThis->SP);
	CPU_LoadAddress(pThis, addressData);
	//printf("Value after pop: %d\n", pThis->AC);
}
void CPU_Int(CPU* pThis){
	pThis->state = 1;
	CPU_CallAddr(pThis, 1000);
}
void CPU_IRet(CPU* pThis){
	pThis->state = 0;
	CPU_Ret(pThis);
}
void CPU_Get(CPU* pThis){
	srand(time(NULL));
	pThis->AC = rand() % 100 + 1;
}
void CPU_PutPort(CPU* pThis, int operand){
	if (operand == 1){
		printf("%d", pThis->AC);
	} else if (operand == 2){
		char cAC = (pThis->AC);
		printf("%c", cAC);
	} else {
		printf("Incorrect operand supplied\nExiting\n");
		_exit(1);
	}
}

//sends string to memory that doesn't have any decimal values, the memory will then end.
void CPU_END(CPU* pThis){
	char str = 'e';
	write(pmem[1], &str, 1);
}

//CPU_Process will fetch next instruction and dispatch instruction to the correct method to process the instruction.
void CPU_Process(CPU* pThis){
	int operand, addressData;
	CPU_FetchMem(pThis);
	switch (pThis->IR){
		case 1: 		//load value into AC
			operand = CPU_FetchOperand(pThis);	
			CPU_LoadValue(pThis, operand);
			break;
		case 2:			//load address, place value at address into AC
			operand = CPU_FetchOperand(pThis);
			addressData = CPU_FetchData(pThis, operand);
			CPU_LoadAddress(pThis, addressData);
			break;
		case 3:			//store value in AC into the address
			operand = CPU_FetchOperand(pThis);
			CPU_StoreAddr(pThis, operand);
			break;
		case 4:			//add value of X to AC
			CPU_AddX(pThis);
			break;
		case 5:			//add value of Y to AC
			CPU_AddY(pThis);
			break;
		case 6:			//subtract value of X from AC
			CPU_SubX(pThis);
			break;
		case 7:			//subtract value of Y from AC
			CPU_SubY(pThis);
			break;
		case 8:			//get random int from 1 to 100
			CPU_Get(pThis);
			break;
		case 9: 		//put port, port = 1: write AC to screen as int
					//	    port = 2: write AC to screen as char
			operand = CPU_FetchOperand(pThis);
			CPU_PutPort(pThis, operand);
			break;
		case 10:		//Copy value in AC to X
			CPU_CopyToX(pThis);
			break;
		case 11:		//copy value in AC to Y
			CPU_CopyToY(pThis);
			break;
		case 12:		//copy value in X to AC
			CPU_CopyFromX(pThis);
			break;
		case 13:		//copy value in Y to AC
			CPU_CopyFromY(pThis);
			break;
		case 14:		//jump to the address
			operand = CPU_FetchOperand(pThis);
			CPU_JumpAddr(pThis, operand);
			break;
		case 15:		//jump if the value in AC is zero
			operand = CPU_FetchOperand(pThis);
			if (pThis->state == 1){
				operand += 1000;
			}
			CPU_JumpIfEqual(pThis, operand);
			break;
		case 16:		//jump if value in AC is not zero
			operand = CPU_FetchOperand(pThis);
			if (pThis->state == 1){
				operand =+ 1000;
			}
			CPU_JumpIfNotEqual(pThis, operand);
			break;
		case 17:		//push return address onto stack, jump to the address
			operand = CPU_FetchOperand(pThis);
			CPU_CallAddr(pThis, operand);
			break;
		case 18:		//pop return address from stack, jump to the address
			CPU_Ret(pThis);
			break;
		case 19:		//increment value in X
			CPU_IncX(pThis);
			break;
		case 20:		//decrement value in X
			CPU_DecX(pThis);
			break;
		case 21:		//Load the value at address + X into AC
			operand = CPU_FetchOperand(pThis);
			addressData = CPU_FetchData(pThis, (operand + pThis->X));
			CPU_LoadIndXAddr(pThis, addressData);
			break;
		case 22:		//load the value at address + Y into AC
			operand = CPU_FetchOperand(pThis);
			addressData = CPU_FetchData(pThis, (operand + pThis->Y));
			CPU_LoadIndYAddr(pThis, addressData);
			break;
		case 23:		//push AC onto stack
			CPU_Push(pThis);
			break;
		case 24:		//pop from stack into AC
			CPU_Pop(pThis);
			break;
		case 25:		//push return address, set system mode, set PC to int handler
			CPU_Int(pThis);
			break;
		case 26:		//pop return address into PC, set user mode
			CPU_IRet(pThis);
			break;
		case 50:		//end execution
			CPU_END(pThis);
			break;
		default:
			printf("Invalid instruction recieved: %d\n", pThis->IR);
			printf("Exiting\n");
			_exit(1);
	}	
}

void CPU_IncPC(CPU* pThis){
	pThis->PC += 1;
}
