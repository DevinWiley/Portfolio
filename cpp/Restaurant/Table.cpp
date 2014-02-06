#include "Table.h"
using namespace std;

Table::Table(int tblid, int mseats){
	tableId = tblid;
	maxSeats = mseats;
	waiter = NULL;
	numPeople = 0;
	status = IDLE;
}

Table::~Table(){
}

void Table::assignWaiter(Waiter &person){
	this->waiter = &person;
}

void Table::partySeated(int npeople){
	numPeople = npeople;
	status = SEATED;
}

void Table::partyOrdered(Order *order){
	this->order = order;
	status = ORDERED;
}

void Table::partyServed(void){
	status = SERVED;
}

void Table::partyCheckout(void){
	cout << "\nCheckout\n";
	cout << "Table Id: " << this->getTableId() << endl;
	cout << "Customers: " << this->getNumPeople() << endl;
	order->printItems();
	cout << "Total Price: " << order->getTotal() << "\n\n";
	numPeople = 0;
	status = IDLE;
}

int Table::getTableId(void){
	return tableId;
}

int Table::getMaxSeats(void){
	return maxSeats;
}

int Table::getNumPeople(void){
	return numPeople;
}

Waiter* Table::getTableWaiter(void){
	return waiter;
}

TableStatus Table::getTableStatus(void){
	return status;
}
