#include "MenuItem.h"
using namespace std;

MenuItem::MenuItem(string mcode, string mname, double mprice){
	this->code = mcode;
	this->name = mname;
	this->price = mprice;
}

MenuItem::~MenuItem(){
	
}

double MenuItem::getPrice(void){
	return price;
}

string MenuItem::getName(void){
	return name;
}

string MenuItem::getCode(void){
	return code;
}
