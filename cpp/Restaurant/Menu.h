#ifndef MENU_H
#define MENU_H
#include "MenuItem.h"

class MenuItem;

class Menu 
{
private:
	int maxItems; // MAX capacity 
	int numItems; // current # of items in menu
	MenuItem *menup;  

public:

	Menu(int items = 100); //constructor to allocate memory
	void addItem(MenuItem item); //add one menu item at a time
	MenuItem* findItem(string mcode); //lookup operation
	~Menu(); //destructor
};

// Usage:
// Menu menu;
// MenuItem *ip = menu.findItem(code);

// IMPLEMENTATION:
// MenuItem* Menu::findItem(string code) CORRECT
// MenuItem Menu::*findItem(string code) WRONG
#endif
