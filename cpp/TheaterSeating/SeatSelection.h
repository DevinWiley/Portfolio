#include <iostream>
#include <string>
using namespace std;

#ifndef SeatSelection_H
#define SeatSelection_H

#define SEATS 10

class SeatSelection{
	public:
		SeatSelection(int, int);
		~SeatSelection();
		bool findSeatsTogether(int);
		string* findSeatsApart(int);
		string* assignSeats(int, int, int);
		bool** sectionMatrix(int);
	private:
		bool** sectionRows;
		int sectionStart, sectionNum;
};

#endif
