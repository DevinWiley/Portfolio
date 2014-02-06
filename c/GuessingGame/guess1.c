#include <stdbool.h>
#include <stdio.h>

int main(){
	char answer = 'y';
	int guessAmmount = 0, totalPlays = 0;
	double avgGuesses = 0.0;
	while (answer == 'y'){
		int reply = guessGame();
		guessAmmount += reply;
		totalPlays++;
		printf("Do you want to play again? (y/n): ");
		scanf("\n%c", &answer);
	}
	if (totalPlays != 0){
		avgGuesses = ((double)guessAmmount / (double)totalPlays);
		printf("The average ammount of guesses was %.1f.\n", avgGuesses);
	}
	printf("Goodbye.\n");
	return 0;
}

int guessGame(){
	char input;
	bool found = false;
	int max = 100, min = 1;
	int compGuess, i = 0;
	printf("Guess a number between 1 and 100, and answer questions\n");
	printf("based on that number. Example: Number < Guess\n");
	while (!found){
		compGuess = (max + min) / 2.0;
		printf("Guess %d: %d (<,=,>)? ", i + 1, compGuess);
		scanf("\n%c", &input);
		if (input == '='){
			printf("Good Game!\n");
			found = true;
			i++;
		} else if (input == '>'){
			min = compGuess + 1;
			i++;
		} else if (input == '<'){
			max = compGuess - 1;
			i++;
		}
		if (min == max){
			printf("Your number is %d\n", min);
			printf("Good Game!\n");
			found = true;
		}
	}
	return i;
}
