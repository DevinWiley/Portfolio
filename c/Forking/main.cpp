#include <iostream>
#include <string>
#include <sstream>
#include <fstream>
#include <cstdio>
#include <cstdlib>

using namespace std;

const int MAX = 21;
int pipefd[MAX][2];
char operations[MAX];

int main(int argc, char* argv[]){
	char testc;
	string input;
	int datainput[MAX + 1];

	if (argc != 2){
		cout << "Only enter the filename containing data on the command line." << endl;
		exit(EXIT_FAILURE);
	}
	
	ifstream inputFile(argv[1]);

	if (!inputFile.is_open()){
		fprintf(stderr, "File failed to open");
		exit(EXIT_FAILURE);
	}

	getline(inputFile, input);
	istringstream iss(input);
	int i = 0, n = 0, j = 0;;
	while (iss >> testc){
		switch (testc){
			case '-':
			case '*':
			case '/':
			case '+':
				operations[i++] = testc;
		}
	}
	n = i;
	i = 0;
	
	while(inputFile.good()){
		getline(inputFile, input);
		istringstream issData(input);
		while(issData >> datainput[i]){
			i++;
		}
	}
	j = i;
	
	for (int k = 0; k <= n; k++){
		if (pipe(pipefd[2 * k]) == -1 || pipe(pipefd[2 * k + 1]) == -1){
			fprintf(stderr, "Pipe failure");
			exit(EXIT_FAILURE);
		} 
	}
	//close(pipefd[2 * n + 1][0]);
	//close(pipefd[2 * n + 1][1]);
	
	int l = 0;
	for (int k = 0; k < n; k++){
		if (fork() == 0){
			close(0);
			dup(pipefd[2 * k][0]);
			//close(pipefd[2 * k][1]);
			//close(pipefd[2 * k][0]);
			close(3);
			dup(pipefd[2 * k + 1][0]);
			//close(pipefd[2 * k + 1][1]);
			//close(pipefd[2 * k + 1][0]);
			close(1);
			dup(pipefd[2 * k + 2][1]);
			//close(pipefd[2 * k + 2][0]);
			//close(pipefd[2 * k + 2][1]);
			
			switch (operations[k]){
				case '*':
					execl("multiply", "multiply", NULL);
				case '/':
					execl("divide", "divide", NULL);
				case '+':
					execl("sum", "sum", NULL);
				case '-':
					execl("subtract", "subtract", NULL);
			}
			cerr << "execl failed.\n";
		}
	}
	//set input to be recieved from pipe 0
	close(0);
	dup(pipefd[2 * n][0]);
	//close(pipefd[2 * n][1]);
	close(pipefd[2 * n][0]);
	//these two pipes will look like this for every number of expresions
	close(1);
	dup(pipefd[0][1]);
	//close(pipefd[0][1]);
	//close(pipefd[0][0]);
	close(3);
	dup(pipefd[1][1]);
	//close(pipefd[1][1]);
	//close(pipefd[1][0]);
		
	for (int k = 2; k <= n; k++){
		close(2 * k + 1);
		dup(pipefd[2 * k - 1][1]);
		//close(pipefd[2 * k - 1][1]);
		//close(pipefd[2 * k - 1][0]);
	}
	
	for (i = 0; i < j / (n + 1); i++){
		write(1, (char *)&(datainput[l++]), sizeof(int));
		write(3, (char *)&(datainput[l++]), sizeof(int));
		for (int k = 2; k <= n; k++){
			write(2 * k + 1,(char *)&(datainput[l++]), sizeof(int));
			int testint = k;
		}
		int result;
		read(0, (char *)&result, sizeof(int));
		cerr << "Result recieved = " << result << endl;		
	}
	for (int k = 0; k <=n; k++){
		close(pipefd[2 * k][0]);
		close(pipefd[2 * k][1]);
		close(pipefd[2 * k + 1][0]);
		close(pipefd[2 * k + 1][1]);
		if (k >=2){
			close(2 * k + 1);
		}
	}
}
