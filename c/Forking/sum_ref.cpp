#include <iostream>
#include <fstream>

using namespace std;

main()
{
	int x, y, z;
	while (cin.read((char *)&x, sizeof(int)) &&
		   cin.read((char *)&y, sizeof(int))) {
		z = x + y;
		cout << x << " " << y << " " << z << endl;
	}
}	
