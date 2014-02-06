#include <string>
#include <sstream>
#include "Tokenizer.h"
#include "Waiter.h"
using namespace std;

Waiter::Waiter(string name, string TableList, Table *table[], int totalTables){
	this->name = name;
	Tokenizer count(TableList);
	count.setDelimiter(",");
	string token;
	numTables = 0;
	int validTables[totalTables];
	while ((token = count.next()) != ""){
		int tableid;
		istringstream i(token);
		i >> tableid;
		tableid -= 1;
		if (tableid < totalTables){
			int i = 0;
			bool repeat = false;
			while (i < numTables && !repeat){
				if (validTables[i] == tableid){
					repeat = true;
				}
				i++;
			}
			if (!repeat){
				validTables[numTables] = tableid;
				numTables++;
			}
		}
	}
	tables = new Table*[numTables];
	int i = 0;
	while (i < numTables){
		tables[i] =  table[(validTables[i])];
		table[validTables[i]]->assignWaiter(*this);
		//tables[i]->assignWaiter(*this);
		i++;
	}
}

Waiter::~Waiter(){
	for (int i = 0; i < numTables; i++){
		delete tables[i];
		tables[i] = NULL;
	}
	delete[] tables;
	tables = NULL;
}

string Waiter::getName(void){
	return name;
}

int Waiter::getNumTables(void){
	return numTables;
}

Table** Waiter::getTables(void){
	return tables;
}

Table* Waiter::tableAccess(int tableId){
	return tables[tableId];
}
