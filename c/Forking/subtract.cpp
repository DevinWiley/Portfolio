#include <iostream>
#include <fstream>
#include <cstdio>
#include <cstdlib>

using namespace std;

main(int argc, char *argv[])
{
	int x, y, z;
	while (read(0, (char *)&x, sizeof(int)) &&
		   read(3, (char *)&y, sizeof(int))) {
		z = x - y;
		if (argc > 1)
			cerr << "subtract: " << x << " - " << y << " = " << z << endl;
		write(1, (char *)&z, sizeof(int));
	}
}	
