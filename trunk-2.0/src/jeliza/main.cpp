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
#include <getopt.h>

#include "jeliza.cpp"
#include "util.cpp"
#include "arrays.cpp"
#include "server.cpp"

using namespace std;


int helpText() 
{
  return 0;
  
}
int version() 
{
  return 0;
}



int consoleJEliza() {
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

int main(int argc, char** argv) {
const struct option long_options[] = {
	{ "help", no_argument,       0, 'h' },
	{ "server", require_argument,       0, 's' },
	{ "console", no_argument, 0, 'c' },
	{ "version", no_argument,       0, 'v' },
	0
		};

	int result = 0;
	int console = 2;
	int server = 3;
	int help = 4;
	int ver = 5;
	int port = 9001;
	int index = 0;
	
	
	int method = server;
	
	while (result = getopt_long(argc, argv, "hs:cv", long_options, &index)) {
		switch (result) {
		case 'h':
		  method = help;
		  break;
		case 's':
		  method = server;
		  port = index;
		  break;
		case 'c':
		  method = console;
		  break;
		case 'v':
		  method = ver;
		  break;
		default:
		  method = server;
		}
	}
	
	if (method == server) {
	  return serverJEliza(port);
	}
	if (method == help) {
	  return helpText();
	}
	if (method == ver) {
	  return version();
	}
	if (method == console) {
	  return consoleJEliza();
	}
	
	
	
	return 0;	
}



