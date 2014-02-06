#ifndef SIMPLEVECTOR_H
#define SIMPLEVECTOR_H

#include <iostream>
#include <cstdlib>
using namespace std;

template <class T>
class SimpleVector
{
private:
   T *aptr;
   int arraySize;  
   void subError();       // Handles subscripts out of range
public:
   SimpleVector(int);                   // Constructor
   SimpleVector(const SimpleVector &);  // Copy constructor
   ~SimpleVector(); 	                   // Destructor
   int size()
     { return arraySize; }
   T &operator[](int);    // Overloaded [] operator
   void print();          // outputs the array elements.
};

//*******************************************************
// Constructor for SimpleVector class. Sets the size    *
// of the array and allocates memory for it.            *
//*******************************************************
template <class T>
SimpleVector<T>::SimpleVector(int s)
{
    arraySize = s;
    aptr = new T [s];
    for (int count = 0; count < arraySize; count++)
       aptr[count] = T();
}
//******************************************************
// Copy Constructor for SimpleVector class.            *
//******************************************************
template <class T>
SimpleVector<T>::SimpleVector(const SimpleVector &obj)
{
   arraySize = obj.arraySize;
   aptr = new T [arraySize];
   for(int count = 0; count < arraySize; count++)
      aptr[count] = obj[count];
}
//*****************************************************
// Destructor for SimpleVector class.                 *
//*****************************************************
template <class T>
SimpleVector<T>::~SimpleVector()
{
    if (arraySize > 0)
       delete [] aptr;
}

//******************************************************
// subError function. Displays an error message and    *
// terminates the program when a subscript is out of   *
// range.                                              *
//******************************************************
template <class T>
void SimpleVector<T>::subError()
{
    cout << "ERROR: Subscript out of range.\n";
    exit(0);
}
//*******************************************************
// Overloaded [] operator. The argument is a subscript. *
// This function returns a reference to the element     *
// in the array indexed by the subscript.               *
//*******************************************************
template <class T>
T &SimpleVector<T>::operator[](int sub)
{
    if (sub < 0 || sub >= arraySize)
      subError();
    return aptr[sub];
}
//********************************************************
// prints all the entries is the array.                  *
//********************************************************
template <class T>
void SimpleVector<T>::print( )
{
   for (int k = 0; k < arraySize; k++ )
     cout << aptr[k] << "  ";
   cout << endl;  
}
#endif