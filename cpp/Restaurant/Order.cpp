#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include "Order.h"
using namespace std;

Order::Order(int count){
	itemsp = new MenuItem*[count];
	maxItems = count;
	numItems = 0;
}

int Order::addItem(MenuItem &itemp){
	itemsp[numItems] = &itemp;
	numItems++;
}

Order::~Order(){
	/*
	for (int i = 0; i < maxItems; i++){
		delete itemsp[i];
		this->itemsp[i] = NULL;
	}
	*/
	delete[] this->itemsp;

}

void Order::printItems(void){
	for (int i = 0; i < numItems; i++){
		string name = itemsp[i]->getName();
		double price = itemsp[i]->getPrice();
		cout << (i + 1) << " " << name << " "<< price << endl;
	}
}

double Order::getTotal(void){
	double total = 0;
	for (int i = 0; i < numItems; i++){
		total += itemsp[i]->getPrice();
	}
	return total;
}
