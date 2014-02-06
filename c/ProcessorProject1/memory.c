#include <stdio.h>
#include <stdlib.h>
#include <stdio_ext.h>
#include <unistd.h>

typedef struct mem{
	int addresses[2000];
} Memory;

void Memory_LoadMemory(Memory* pThis, int argc, char *argv[]);
void Memory_TransferData(Memory* pThis, int address);
void Memory_WriteData(Memory* pThis, int address, int data);

int main(int argc, char *argv[]){
	Memory *memory = malloc(sizeof(Memory));
	int i = 0;
	for (i = 0; i < 2000; i++){
		memory->addresses[i] = 0;
	}
	Memory_LoadMemory(memory, argc, argv);
	int address, data;
	char access;
	while(scanf("%c", &access) == 1){
		if (access == 'w'){
			char temp[12] = {0x0};
			scanf("%11d", &address);
			__fpurge(stdin);
			sprintf(temp, "%11d", address);
			printf("%s", temp);
			fflush(stdout);
			scanf("%11d", &data);
			__fpurge(stdin);
			//fprintf(stderr, "\nRecived address and data: %d %d\n", address, data);
			Memory_WriteData(memory, address, data);
		} else if (access == 'r'){
			scanf("%11d", &address);
			//fprintf(stderr, "\nRecived address: %d\n", address);
			__fpurge(stdin);
			Memory_TransferData(memory, address);
		} else if (access == 'e'){
			break;
		}
	}
	return 0;
}

void Memory_LoadMemory(Memory* pThis, int argc, char *argv[]){
	char line[64];
	int i;
	//fprintf(stderr, "argc: %d\n", argc);
	for(i = 1; i < argc; i++){
		//fprintf(stderr, "argv[%d]: %s\n", i, argv[i]);
		FILE *file = fopen(argv[i], "r");
		int input = 0, address;
		
		if (i == 1){
			address = 0;
		} else {
			address = 1000;
		}
		//fprintf(stderr, "testing if file is null\n");
		if (file != NULL){
			while(fgets(line, 64, file) != NULL){
				int n = sscanf(line, "%d %*s", &input);
				if (n == 1){
					pThis->addresses[address++] = input;
				}
			}
		} else {
			perror("Error opening file");
			_exit(1);
		}
	}
}

void Memory_WriteData(Memory* pThis, int address, int data){
	pThis->addresses[address] = data;
	char temp[12] = {0x0};
	sprintf(temp, "%11d", pThis->addresses[address]);
	printf("%s", temp);
	fflush(stdout);
	//fprintf(stderr, "writing: %d at %d\n", data, address);
}

void Memory_TransferData(Memory* pThis, int address){
	//if (address >= 1000){
	//	fprintf(stderr, "inside interupt address: %d\n", address);
	//}
	char temp[12] = {0x0};
	sprintf(temp, "%11d", pThis->addresses[address]);
	printf("%s", temp);
	fflush(stdout);
	//fprintf(stderr, "returning: %d\n", pThis->addresses[address]);
}
