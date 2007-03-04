/*
 * This is part of JEliza 2.0.
 * Copyright 2006 by Tobias Schulz
 * WWW: http://jeliza.ch.to/
 * 
 * JEliza is free software; you can redistribute it and/or      
 * modify it under the terms of the GNU General Public    
 * License as published by the Free Software Foundation; either  
 * version 2.1 of the License, or (at your option) any later     
 * version.                                                      
 *                                                               
 * JEliza is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of    
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.          
 * See the GNU General Public License for more details.   
 *                                                               
 * You should have received a copy of the GNU GPL               
 * along with JEliza (file "gpl.txt") ; if not, write           
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
#include <unistd.h>
#include <getopt.h>

#include "jeliza.cpp"
#include "util.cpp"
#include "arrays.cpp"
#include "server.cpp"

#include "SocketHandler.h"
#include "ListenSocket.h"
#include "TcpSocket.h"

using namespace std;


int helpText() {
	cout 	<< "Usage: jeliza [OPTION]... " << endl
		<< endl
		<< "--server ...                 Runs the http server on port ... (default 9001)" << endl
		<< "--console                    Runs the command line version" << endl
		<< "--gentext ...                Generates a text to the specific topic" << endl
		<< "--quickanswer ...            Replies and exits" << endl
		<< "--file                       Replies to the question in the file 'ques.txt' and writes the answer to 'ans.txt'" << endl
		<< "--learn ...                  Learns and exits" << endl
		<< "--help                       Shows this help and exits" << endl
		<< "--version                    Shows version information and exits" << endl;

	return 0;
}
int version() {
	cout 	<< "JEliza 2.0" << endl;
	cout 	<< "Copyright 2006-2007 Tobias Schulz" << endl;
	cout 	<< "JEliza is free software; you can redistribute it and/or      " << endl
		<< "modify it under the terms of the GNU General Public    " << endl
		<< "License as published by the Free Software Foundation; either  " << endl
		<< "version 2.1 of the License, or (at your option) any later     " << endl
		<< "version.                                                      " << endl
		<< "                                                             " << endl
		<< "JEliza is distributed in the hope that it will be useful, but " << endl
		<< "WITHOUT ANY WARRANTY; without even the implied warranty of    " << endl
		<< "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.          " << endl
		<< "See the GNU General Public License for more details.   " << endl
		<< "                                                              " << endl
		<< "You should have received a copy of the GNU GPL               " << endl
		<< "along with JEliza (file \"gpl.txt\") ; if not, write           " << endl
		<< "to the Free Software Foundation, Inc., 51 Franklin St,        " << endl
		<< "Fifth Floor, Boston, MA  02110-1301  USA" << endl;

	
	return 0;
}

static	bool quit = false;

int serverJEliza(int port)
{
	SocketHandler h;
	ListenSocket<MySocket> l(h);

	if (l.Bind(port))
	{
		exit(-1);
	}
	h.Add(&l);
	h.Select(1,0);
	while (!quit)
	{
		h.Select(1,0);
	}
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

int quickJEliza(string fra) {
	JEliza jeliza(1);
	
	jeliza.init();
		
	string bestReply = jeliza.ask(fra);
		
	cout << bestReply << endl;
	return 0;
}

int fileJEliza() {
	JEliza jeliza(1);
	
	jeliza.init();
	
	string ques = "";
	ifstream in("ques.txt");
	getline(in, ques);
		
	string bestReply = jeliza.ask(ques);
	
	ofstream o("ans.txt");
	o << bestReply << endl;
	o.close();
		
	cout << bestReply << endl;
	return 0;
}

int learnJEliza(string fra) {
	JEliza jeliza(1);
	
	jeliza.init();
		
	string bestReply = jeliza.ask(fra);
	jeliza.learn(fra, fra);
		
	cout << bestReply << endl;
	return 0;
}

int gentextJEliza(string thema) {
	thema = Util::toLower(thema);
	
	JEliza jeliza;
	jeliza.init();
	
	string out = "";
	
	StringArray* sentences = jeliza.getSentences("file");
	
	bool lastadded = 0;
	
	cout << "Generating a text if topic \"" << thema << "\"" << endl << endl;
	cout << "+-- Log:  -------------------------------------------------------->" << endl;
	
	int times = 0;
	for (int x = 0; x < jeliza.m_sentenceCount; x++) {
		string s = (*sentences)[x];
		s = Util::toLower(s);
		
		if (Util::contains(s, thema)) {
			cout << "\"" << Util::strip(s) << "\" contains \"" << thema << "\"" << endl;
			if (!lastadded && x > 0) {
				if ((*sentences)[x - 1].size() > s.size() and s.size() > 22) {
					out += (*sentences)[x - 1] + ". ";
				}
			}
			if (!Util::contains(out, s) and s.size() > 22) {
				out += s + ". ";
			}
			if (times > 7) {
				times = 0;
				out += "\n\n";
			}				
			
			lastadded = 1;
		} else {
			cout << "\"" << Util::strip(s) << "\" does not contain \"" << thema << "\"" << endl;
			lastadded = 0;
		}
		
////		cout << "Sentence: " << s << endl;
//		m_sents->set(x, s);
	}
		
	delete(sentences);
	
	cout << "+----------------------------------------------------------------->" << endl << endl << endl;
	cout << thema << ":" << endl;
	cout << "--------------------------------------------------------------" << endl;
	

	cout << out << endl;
	return 0;
}

int main(int argc, char** argv) {
	static struct option long_options[] = {
		{ "help", no_argument,       0, 'h' },
		{ "server", required_argument,       0, 's' },
		{ "console", no_argument, 0, 'c' },
		{ "gentext", required_argument, 0, 'g' },
		{ "file", no_argument, 0, 'f' },
		{ "quickanswer", required_argument, 0, 'a' },
		{ "learn", required_argument, 0, 'l' },
		{ "version", no_argument,       0, 'v' },
		{0, 0, 0, 0}
			};

	int result = 0;
	int console = 2;
	int server = 3;
	int help = 4;
	int ver = 5;
	int gentext = 6;
	int quickanswer = 7;
	int learn = 8;
	int fileanswer = 9;
	int port = 9001;
	string word = "";
	int index = 0;
	
	
	int method = server;
	
	while (1) {
		result = getopt_long(argc, argv, "hs:cg:fq:v", long_options, &index);
		
//		cout << result << endl;
		if (result == -1) {
			break;
		}
		
		switch (result) {
		case 'h':
			method = help;
			break;
		case 's':
			method = server;
			port = atoi(optarg);
			break;
		case 'c':
			method = console;
			break;
		case 'v':
			method = ver;
			break;
		case 'g':
			method = gentext;
			word =  optarg;
			break;
		case 'a':
			method = quickanswer;
			word =  optarg;
			break;
		case 'f':
			method = fileanswer;
			break;
		case 'l':
			method = learn;
			word =  optarg;
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
	if (method == gentext) {
	  return gentextJEliza(word);
	}
	if (method == fileanswer) {
	  return fileJEliza();
	}
	if (method == quickanswer) {
	  return quickJEliza(word);
	}
	if (method == learn) {
	  return learnJEliza(word);
	}
	
	return 0;	
}







