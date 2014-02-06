#include "Payment.h"
using namespace std;

Payment::Payment(int tblid, int npersons, Order &order, double total, Waiter &waiter){
	tableId = tblid;
	numPeople = npersons;
	this->waiterp = &waiter;
	this->orderp = &order;
	this->total = total;
}

Payment::~Payment(){
	//waiterp = NULL;
	//delete waiterp;
	//orderp = NULL;
	//delete orderp;
}

void Payment::calculateTotal(void){
	total = orderp->getTotal();
}

int Payment::getTableId(void){
	return tableId;
}

int Payment::getNumPeople(void){
	return numPeople;
}

double Payment::getTotal(void){
	return total;
}

Waiter* Payment::getWaiter(void){
	return waiterp;
}

Order* Payment::getOrder(void){
	return orderp;
}

void Payment::payCheck(void){
	waiterp->tableAccess(tableId - 1)->partyCheckout();
}
