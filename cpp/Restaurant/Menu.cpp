#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include "Menu.h"
using namespace std;

Menu::Menu(int items){
	maxItems = items;
	numItems = 0;
	menup = new MenuItem[items];
}

void Menu::addItem(MenuItem item){
	menup[numItems] = item;
	numItems++;
}

MenuItem* Menu::findItem(string mcode){
	for(int i = 0; i < maxItems; i++){
		if((menup[i].getCode()).compare(mcode) == 0){
			return &menup[i];
		}
	}
	return NULL;
}

Menu::~Menu(){
	delete[] this->menup;
	this->menup = NULL;
}
