#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

int guessGame();

int main(){
	char answer = 'y';
	int guessAmmount = 0, timesPlayed = 0;
	double avgGuesses;
	while (answer == 'y'){
		int reply = guessGame();
		guessAmmount += reply;
		timesPlayed++;
		printf("Do you want to play again? (y/n): ");
		scanf("\n%c", &answer);
	}
	if (timesPlayed != 0){
		avgGuesses = ((double)guessAmmount / (double)timesPlayed);
		printf("Average number of guesses: %.1f\n", avgGuesses);
	}
	printf("Goodbye.\n");
	return 0;
}

int guessGame(){
	srand(time(NULL));
	int guess = 0, i = 0;
	bool found = false;
	int randNum = rand() % 100 + 1;
	printf("Guess a number between 1 and 100!\n");
	while (!found){
		i++;
		printf("Guess %d: ",i);
		scanf("%d",&guess);
		if (guess == randNum){
			found = true;
			printf("Congrats, you guessed the correct number!\n");
		} else if (guess > randNum){
			printf("Too high!\n");
		} else if (guess < randNum){
			printf("Too Low!\n");
		}
	}
	return i;
}
