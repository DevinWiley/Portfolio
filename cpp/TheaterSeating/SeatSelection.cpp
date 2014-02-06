#include <iostream>
#include <string>
#include <sstream>
using namespace std;

#include "SeatSelection.h" 

SeatSelection::SeatSelection(int start, int rows){
	sectionStart = start;
	sectionNum = rows - (start - 1);
	sectionRows = new bool*[sectionNum];
	for (int i = 0; i < sectionNum; i++){
		sectionRows[i] = new bool[SEATS];
	}
	for (int i = 0; i < sectionNum; i++){
		for (int j = 0; j < SEATS; j++){
			sectionRows[i][j] = false;
		}
	}
}

SeatSelection::~SeatSelection(){
	for (int i = 0; i < sectionNum; i++){
		sectionRows[i] = NULL;
		delete [] sectionRows[i];
	}
	delete [] sectionRows;

}

bool SeatSelection::findSeatsTogether(int number){
	bool assigned = false;
	string* assignedSeats = new string[number];
	int remainingSeats = 0;
	for (int i = 0; !assigned && i < sectionNum; i++){
		int emptySeats = 0;
		int start = 11;
		for (int j = 0; j < SEATS; j++){
			if (!(sectionRows[i][j])){
				if ((j + 1) < start){
					start = j;
				}
				emptySeats++;
				remainingSeats++;
				if (number == emptySeats){
					assignedSeats = assignSeats(i , start, number);
					assigned = true;
					break;
				}
			}
		}
	}
	if (!assigned && number < remainingSeats){
		assignedSeats = findSeatsApart(number);
		assigned = true;
	}
	if (assigned){
		cout << "Your seats are: ";
		for (int i = 0; i < number; i++){
			cout << assignedSeats[i] << " ";
		}
		cout << endl << endl;
	} else if (number > remainingSeats){
		cout << "Sorry, only " << remainingSeats << " seats are available." << endl;
	}
	return assigned;
}

string* SeatSelection::assignSeats(int row, int start, int number){
	string* seated = new string[number];
	char letters[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
	for (int i = 0; i < number; i++){
		stringstream ss;
		string result;
		ss << ((row) + sectionStart) << letters[start];
		result = ss.str();
		seated[i] = result;
		sectionRows[row][start] = true;
		start++;
	}
	return seated;
}

string* SeatSelection::findSeatsApart(int number){
	bool assigned = false;
	string* assignedSeats = new string[number];
	int numAssigned = 0;
	for (int i = 0; !assigned && i < number; i++){
		int emptySeats = 0;
		int start = 11;
		for (int j = 1; j <= SEATS; j++){
			if (!sectionRows[i][j - 1]){
				if (j + 1< start){
					start = j;
				}
				emptySeats++;
				if (j == SEATS || number == emptySeats && number != 0){
					string* seats = assignSeats(i, start, emptySeats);
					for (int seated = 0; seated < emptySeats; seated++){
						assignedSeats[numAssigned] = seats[seated];
						numAssigned++;
					}
					number = number - emptySeats;
				} else if (number == 0){
					assigned = true;
					break;
				}
			}
		}
	}
	return assignedSeats;
}
