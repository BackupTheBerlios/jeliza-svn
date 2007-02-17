/*
 * This is part of JEliza 2.0.
 * Copyright 2006 by Tobias Schulz
 * WWW: http://jeliza.ch.to/
 * 
 * JEliza is free software; you can redistribute it and/or      
 * modify it under the terms of the GNU Lesser General Public    
 * License as published by the Free Software Foundation; either  
 * version 2.1 of the License, or (at your option) any later     
 * version.                                                      
 *                                                               
 * JEliza is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of    
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.          
 * See the GNU Lesser General Public License for more details.   
 *                                                               
 * You should have received a copy of the GNU LGPL               
 * along with JEliza (file "lgpl.txt") ; if not, write           
 * to the Free Software Foundation, Inc., 51 Franklin St,        
 * Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */

#include <iostream>
#include <fstream>
#include <string>
#include <map>

#include <string>
#include <list>

#include <time.h>
#include <vector>

#include "jeliza.cpp"
#include "util.cpp"
#include "arrays.cpp"

using namespace std;


int main() {
	JEliza jeliza;
	
	cout << "-> Program started" << endl;
	while (true) {
		cout << "-> Please enter a question:" << endl << "-> ";
		string fra = "";
		getline(cin, fra);
		long time1 = time(NULL);
		
		jeliza.init();
		
		string bestReply = jeliza.ask(fra);		
		jeliza.learn(fra, fra);
		
		long time2 = time(NULL);
		
		double unterschied = time2 - time1;
		// cout << time2 << endl << time1 << endl;
		double sec = unterschied;
		
		cout << "-> Time: " << sec << " Sekunden" << endl;
		cout << "-> Answer => " << bestReply << endl;
		cout << endl;
	}
	return 0;
}

