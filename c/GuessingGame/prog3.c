#include <stdio.h>
#include <stdlib.h>

main()
{
	srand(getpid());
	printf("%d\n", rand() % 100 + 1);
	printf("%d\n", rand());
	printf("%d\n", rand());
}	
