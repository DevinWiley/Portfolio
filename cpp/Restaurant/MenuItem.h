#ifndef MENUITEM_H
#define MENUITEM_H
#include <iostream>
#include <string>

using namespace std;

class Menu;

class MenuItem
{
//friend class Menu;
private:
	string code; // See sample codes in config.txt
	string name; // Full name of the entry
	double price; // price of the item

public:
	~MenuItem();
	MenuItem(string mcode = "", string mname= "", double mprice = 0);
	double getPrice(void);
	string getName(void);
	string getCode(void);
};
#endif
