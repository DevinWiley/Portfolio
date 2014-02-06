#include <iostream>
#include <string>
#include "SeatSelection.h"
using namespace std;

int main(){
	SeatSelection premium(1, 5);
	SeatSelection regular(6, 15);
	
	int input1, input2;
	bool assigned = true;
	while (input1 != -1 && assigned){
		cout << "Premium(1) or Regular(2): ";
		cin >> input1;
		cout << "# of tickets: ";
		cin >> input2;
		if (input1 == 1){
			assigned = premium.findSeatsTogether(input2);
		} else if (input1 == 2){
			assigned = regular.findSeatsTogether(input2);
		} else {
			cout << "Invalid input." << endl;
		}
	}
	return 0;
}
