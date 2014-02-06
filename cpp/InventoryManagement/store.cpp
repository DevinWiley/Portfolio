#include <iostream>
#include <fstream>
#include <iomanip>
#include <string>
#include <sstream>

#include "Product.h"
#include "Tokenizer.h"
#include "LookupTable.h"

using namespace std;

class Tokenizer;

void checkout(LookupTable<Product *> &table){
	int units;
	double weight, total = 0;
	int i;
	
	const double DISCOUNT_RATE = .05;

	do {
		cout << "\nEnter PLU code or 0 to complete checkout: ";
		cin >> i;
		if (!i)
			break;
		if (table[i] != NULL){
			if ((*table[i]).sold_by_weight()){
				cout << "\nEnter the weight for " << (*table[i]).get_name() << ": ";
				cin >> weight;
				total += (*table[i]).compute_cost_by_weight(weight);
			} else {
				cout << "\nEnter # of units for " << (*table[i]).get_name() << ": ";
				cin >> units;
				total += (*table[i]).compute_cost_by_units(units);
			}
			cout << fixed << setprecision(2) << "Total so far: $ " << total << endl;
		} else 
			cout << "Invalid selection. Try again.\n";
	} while (1);
	if(total > 50) {
		cout << "   Total: $ " << total << endl;
		cout << "Discount: $ " << total * DISCOUNT_RATE << endl;
		total -= total * DISCOUNT_RATE;
	}
	cout << "Amount due: $ " << total << endl;    
}

int main()
{
	LookupTable<Product *> table;
	
	table.addRange(0, 9999);
	table.addRange(90000, 99999);
	
	ifstream invent("inventory.csv");
	int maxplu = -1;
	//int alloc = 0;
	if (invent.is_open()){
		while(invent.good()){
			string line;
			getline(invent, line);
			Tokenizer str(line);
			str.setDelimiter(",");
			string token;
			int plu, i = 0, saleInt;
			bool saleType, valid = false;
			string name;
			double price, inventLevel;
			while ((token = str.next()) != ""){
				switch(i){
					case 0:
						{
							istringstream convertplu(token);
							convertplu >> plu;
							if (plu > maxplu){
								maxplu = plu;
							}
							//cout << "Plu code is: " << plu << endl;
						}
						break;
					case 1:
						name = token;
						//cout << "Name is: " << name << endl;
						break;
					case 2:
						{
							istringstream convertst(token);
							convertst >> saleInt;
							if (saleInt == 1){
								saleType = true;
							} else {
								saleType = false;
							}
							//cout << "Saletype is: " << saleType << endl;
						}
						break;
					case 3:
						{
							istringstream convertprice(token);
							convertprice >> price;
							//cout << "Price per unit: " << price << endl;
						}
						break;
					case 4:
						{
							istringstream convertilevel(token);
							convertilevel >> inventLevel;
							valid = true;
							//cout << "Inventory level is: " << inventLevel << endl;
						}
						break;
				}
				i++;
			}
			//cout << "Assigning product to table: " << plu << " " << name << endl;
			if (valid) {
				table[plu] = new Product(plu, name, saleType, price, inventLevel);
			}
			//cout << "Allocated: " << alloc << endl;
			//alloc++;
		}
	}
	invent.close();
	
	checkout(table);


	ofstream outputfile;
	outputfile.open("output.csv");
	
	//int freed = 0;
	for (int i = 0; i <= 99999; i++){
		if (table[i] != NULL){
			int saleType;
			if ((*table[i]).sold_by_weight()){
				saleType = 1;
			}
			//cout << "Name value at " << i << " " << (*table[i]).get_name() << endl;
			outputfile << (*table[i]).get_plu_code() << "," << (*table[i]).get_name() << ","
				<< saleType << "," << (*table[i]).get_price() << "," << (*table[i]).get_inventory() << endl;
			delete table[i];
			//freed++;
		}
		//delete table[i];
		//cout << "Freed: " << freed << endl;
	}
	outputfile.close();
	//cout << "Alloc: " << alloc << endl;
	//cout << "Freed: " << freed << endl;
	/*
	in a loop:
		read a product info from file
		// add product to lookup table
		table[plu] = new Product(...);

	later, in a loop to write output for each product, also do
	delete table[plu];
	*/
}
