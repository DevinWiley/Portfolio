#ifndef LOOKUPTABLE_H
#define LOOKUPTABLE_H

#include <iostream>

using namespace std;

#define MAXRANGE 10

template <class T>
class LookupTable
{
private:
   T *aptr[MAXRANGE];
   int rangeStart[MAXRANGE];
   int rangeEnd[MAXRANGE];
   int numRanges;
   T defaultValue; //needed to return when [] operator does not find valid product

public:
   LookupTable();      
   
   //for each range, following method needs to be called.
   void addRange(int start, int end);	  
   ~LookupTable(); 	      
   T &operator[](int value);    
   // find the appropriate range based on value, then
   // use index = value-start into corresponding array
};

template <class T>
LookupTable<T>::LookupTable()
{
	numRanges = 0;
	defaultValue = NULL;
}

template <class T>
void LookupTable<T>::addRange(int start, int end)
{
	//cout << "Number of ranges: " << numRanges << endl;
	rangeStart[numRanges] = start;
	rangeEnd[numRanges] = end;
	//cout << "ranges set.\n";
	aptr[numRanges] = new T[(end - start) + 1];
	numRanges++;
}


template <class T>
T &LookupTable<T>::operator[](int value)
{
	bool found = false;
	int i;
	for (i = 0; i < numRanges && !found; i++){
		if (value >= rangeStart[i]){
			if (value <= rangeEnd[i]){
				value = value - rangeStart[i];
				found = true;
			}
		}
	}
	if (found){
		//cout << "Returning address of " << value << " from Range: " << rangeStart[i - 1] << " - " << rangeEnd[i - 1] << endl;
		return aptr[i - 1][value];
	} else {
		//cout << "Returning default value.\n";
		return defaultValue;
	}
}

template <class T>
LookupTable<T>::~LookupTable()
{
	for (int i = 0; i < numRanges; i++){
		delete [] aptr[i];
	}
}

#endif
