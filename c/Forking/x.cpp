#include <iostream>
#include <fstream>

using namespace std;

main()
{
	for(int i=100 ; i<110 ; i++) 
		cout.write((char *)(&i), sizeof(int));
}	
