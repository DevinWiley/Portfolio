objects = divide.o main.o multiply.o subtract.o sum.o
sources = divide.cpp main.cpp multiply.cpp subtract.cpp sum.cpp

$(objects): $(sources)
	g++ -c $(sources)

all: $(objects)
	g++ -o divide divide.o -o main main.o -o multiply multiply.o -o subtract subtract.o -o sum sum.o
