#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <algorithm>

#include "Menu.h"
#include "MenuItem.h"
#include "Order.h"
#include "Payment.h"
#include "Table.h"
#include "Waiter.h"
#include "Tokenizer.h"
using namespace std;

class Menu;
class MenuItem;
class Order;
class Payment;
class Table;
class Waiter;
class Tokenizer;
void trim(string& str);
void setup(void);
void activities(void);

Waiter **waiter;
int numWaiter = 0;
int numTables = 0;
Table **tables;
Menu menu(100);
Order **order;

int main(){
	setup();
	//cout << "Program finished.\n";
	/*
	for (int i = 0; i < numWaiter; i++){
		delete waiter[i];
	}
	*/
	waiter = NULL;
	delete[] waiter;
	/*
	for (int i = 0; i < numTables; i++){
		delete tables[i];
	}
	*/
	tables = NULL;
	delete[] tables;
	return 0;
}

void activities(void){
	ifstream activity("activity.txt");
	if (activity.is_open()){
		int i = 0, orders = 0;
		while (activity.good()){
			string line, foodCode;
			int tableId = -1, people = -1;
			getline(activity, line);
			trim(line);
			Tokenizer str(line, " ");
			string token = str.next();
			bool error = false;
			bool seating = false, serve = false, orderb = false, check = false;
			do{
				char first = token[0];
				if (first == 'T' && !orderb){
					int id = -1;
					string num = token.erase(0, 1);
					istringstream number(num);
					number >> id;
					tableId = id - 1;
					if (tables[tableId]->getTableWaiter() == NULL){
						error = true;
						cout << "Table " << (tableId + 1) << " does not have a waiter assigned.\n";
					}
				}
				if (first == 'O' && !error && !orderb){
					if (tables[tableId]->getTableStatus() == SEATED){
						cout << "Table " << (tableId + 1) << " has order placed.\n";
						orderb = true;
					} else {
						error = true;
						cout << "Order cannot be placed at empty table.\n";
					}
				}
				if (first == 'P' && !error && !orderb){
					if (tables[tableId]->getTableStatus() == SEATED){
						error = true;
						cout << "Party cannot be seated at occupied table.\n";
					} else {
						string num = token.erase(0, 1);
						istringstream number(num);
						number >> people;
						tables[tableId]->partySeated(people);
						cout << "Party of " << people << " has been seated.\n";
						seating = true;
					}
				}
				if (first == 'S' && !error && !orderb){
					if (tables[tableId]->getTableStatus() == ORDERED){
						tables[tableId]->partyServed();
						cout << "Table " << (tableId + 1) << "  has been served.\n";
						serve = true;
					} else {
						error = true;
						cout << "Table " << (tableId + 1) << "  cannot be served before ordering.\n";
					}
				}
				if (first == 'C' && !error && !orderb){
					if (tables[tableId]->getTableStatus() == SERVED){
						tables[tableId]->partyCheckout();
						check = true;
					} else {
						error = true;
						cout << "Table " << tableId << " cannot checkout before being served.\n";
					}
				}
			} while (!error && !(seating || serve || orderb || check) && (token = str.next()) != "");
			if (orderb && !error){
				order[orders] = new Order(20);
				token = str.next();
				do {
					foodCode = token;
					order[orders]->addItem(*menu.findItem(foodCode));
				} while ((token = str.next()) != "");
				tables[tableId]->partyOrdered(order[orders]);
				orders++;
			}
			i++;
		}
		activity.close();
		for (int j = 0; j < orders; j++){
			delete order[j];
			order[j] = NULL;
		}
		delete[] order;
		order = NULL;
	}
	return;
}

void setup(void){
	int tableid = -1, maxSeat = -1;
	int numItems = 0;
	ifstream count("config.txt");
	if (count.is_open()){
		while (count.good()){
			tableid = maxSeat = -1;
			string line;
			string paramsItems[] = {"", "", ""};
			getline(count, line);
			trim(line);
			Tokenizer item(line, " ");
			Tokenizer waiter(line, " ");
			Tokenizer empty(line);
			empty.setDelimiter(" ");
			if(empty.next() != ""){
				string token1, token2;
				istringstream input(line);
				bool mTable = false;
				if ((input >> tableid >> maxSeat) && !mTable){
					numTables++;
					mTable = true;
				}
				int i = 0, j = 0;
				bool mItem = false;
				bool invalidItem = false;
				while((token1 = item.next()) != "" && !invalidItem && !mTable && !mItem){
					if (i >= 3){
						invalidItem = true;
					} else {
						paramsItems[i] = token1;
					}
					i++;
				}
				if (!invalidItem && !mTable && !mItem && paramsItems[2] != ""){
					double price;
					istringstream parse(paramsItems[2]);
					parse >> price;
					MenuItem itemp(paramsItems[0], paramsItems[1], price);
					menu.addItem(itemp);
					numItems++;
					mItem = true;
				}
				bool invalidWaiter = false;
				while((token2 = waiter.next()) != "" && !invalidWaiter){
					if (j >= 2){
						invalidWaiter = true;
					}
					j++;
				}
				if (!invalidWaiter && !mTable && !mItem){
					numWaiter++;
				}
			} else {}
		}
		count.close();
		tables = new Table*[numTables];
		order = new Order*[numTables];
		waiter = new Waiter*[numWaiter];
		ifstream config("config.txt");
		if (config.is_open()){
			int i = 0, k = 0;
			while (config.good()){
				tableid = maxSeat = -1;
				string line;
				getline(config, line);
				trim(line);
				Tokenizer str(line, " ");
				Tokenizer str2(line);
				str2.setDelimiter(" ");
				if (str2.next() != ""){
					string token1;
					string params[] = {"", ""};
					istringstream input(line);
					bool mItem = false;
					if ((input >> tableid >> maxSeat) && !mItem){
						tables[i] = new Table(tableid, maxSeat);
						i++;
						mItem = true;
					}
					int j = 0;
					bool invalidLine = false;
					while ((token1 = str.next()) != "" && !invalidLine){
						if (j >= 2){
							invalidLine = true;
						} else {
							params[j] = token1;
						}
						j++;
					}
					if (!invalidLine && !mItem){
						waiter[k] = new Waiter(params[0], params[1], tables, numTables);
						k++;
					}
				} else {}
			}
			config.close();
		} else {
			cout << "Something happend.\n";
		}
		activities();
		for (int i = 0; i < numWaiter; i++){
			delete waiter[i];
			waiter[i] = NULL;
		}
		delete[] waiter;
		waiter = NULL;
		//cout << "Order num: " << order[1]->getTotal() << endl;
		/*
		for (int i = 0; i < numTables; i++){
			cout << i << endl;
			delete order[i];
			order[i] = NULL;
		}*/
		for (int i = 0; i < numTables; i++){	
			delete tables[i];
			tables[i] = NULL;
		}
		delete[] tables;
		tables = NULL;

	} else {
		cout << "Unable to open config file.\n";
	}
	/*
	activities();
	cout << "Finished activities()\n";
	for (int i = 0; i < numWaiter; i++){
		delete waiter[i];
		cout << "Deleted waiter[" << i << "]\n";
	}
	delete[] waiter;
	cout << "Deleted waiter pointer array.\n";
	for (int i = 0; i < numTables; i++){
		delete tables[i];
		cout << "Deleted tables[" << i << "]\n";
	}
	delete[] tables;
	cout << "Deleted tables pointer array.\n";
	*/
	return;
}

void trim(string& str)
{
	string::size_type pos1 = str.find_first_not_of(' ');
	string::size_type pos2 = str.find_last_not_of(' ');
	str = str.substr(pos1 == string::npos ? 0 : pos1, 
	pos2 == string::npos ? str.length() - 1 : pos2 - pos1 + 1);
}
