#ifndef JELIZA
#define JELIZA 1
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

#include "util.cpp"
#include "arrays.cpp"
#include "string_compare.cpp"

using namespace std;

/*
 * The JEliza class
 * 
 * JEliza is a conversation simulator.
 * 
 * This class is the main class of jeliza. To use it, you have to create an instance of
 * "JEliza".
 */
class JEliza {
public:
	int m_sentenceCount;
	StringArray* m_sents;
	string m_file;
    
	JEliza() 
	: m_sentenceCount(0), m_sents(new StringArray(m_sentenceCount)), m_file(searchConfigFile()) 
	{
		cout << "-> config file: " << m_file << endl;
		cout << endl;
	}
    
	JEliza(int dummy) 
	: m_sentenceCount(0), m_sents(new StringArray(m_sentenceCount)), m_file(searchConfigFile()) 
	{
//		cout << "-> config file: " << m_file << endl;
//		cout << endl;
	}
    
	~JEliza() {
		delete(m_sents);
	}
	
	string searchConfigFile() {
		ifstream tmpin1("JEliza.txt");
		ifstream tmpin2("/etc/jeliza/JEliza.txt");
		if (!tmpin1) {
			return "/etc/jeliza/JEliza.txt";
		}
		return "JEliza.txt";
	}
    
	StringArray* getSentences (string savetype) {
		m_sentenceCount = 0;
		string buffer;
		int r = 0;
		ifstream in2(m_file.c_str());
		while (in2) {
			getline(in2, buffer);
			r++;
		}
		StringArray* arr = new StringArray(r);
		
		ifstream in(m_file.c_str());
		if (!in) {
			cerr << "Fehler beim Oeffnen einer JEliza-Datei" << endl;
			return arr;
		}
		while (in) {
			getline(in, buffer);
			if (buffer.size() > 1) {
				string s("?");
				if (!Util::contains(buffer, s)) {
					(*arr)[m_sentenceCount] = Util::strip(buffer);
					m_sentenceCount++;
				}
			}
		}
		
		StringArray* arr2 = new StringArray(m_sentenceCount);
		for (int x = 0; x < m_sentenceCount; x++) {
			(*arr2)[x] = (*arr)[x];
		}
		
		delete(arr);
		return arr2;
	}
	
	void saveSentence (string savetype, string original, string newstring) {
		ofstream o(m_file.c_str(), ios::app | ios::ate);
		
		if (!o) {
			cerr << "Fehler beim Oeffnen einer JEliza-Datei" << endl;
			return;
		}
		
		o << newstring << endl;
		
		o.close();
	}
	
	void learn (string orig, string fra) {
		fra = Util::umwandlung(fra);
		saveSentence ("file", orig, fra);
	}
	
	void init () {
		StringArray* sentences = getSentences("file");
		
		delete(m_sents);
		m_sents = new StringArray(m_sentenceCount);
		
		for (int x = 0; x < m_sentenceCount; x++) {
			string s = (*sentences)[x];
			s = Util::toLower(s);
////			cout << "Sentence: " << s << endl;
			m_sents->set(x, s);
		}
		
		delete(sentences);
	}
	
	/*
	 * The answering algorithm
	 */
	string ask(string frage) {
		frage = Util::umwandlung(frage);
		frage = Util::replace(frage, string("?"), string(""));
		frage = Util::strip(frage);
		
		vector<string> woerter;
		Util::split(frage, " ", woerter);
		
		long double best = -1;
		vector<string>* replies = new vector<string>();

		for (int z = 0; z < m_sentenceCount; z++) {
			string sentence = (*m_sents)[z];
			sentence = Util::strip(sentence);
			if (sentence.size() == 0) {
				continue;
			}
			
			vector<string> woerter2;
			Util::split(sentence, " ", woerter2);
			string last = "";
			
			long double points = 0.0;
			long double hatWasGebracht = 0.0;
					
			for (unsigned int a = 0; a < woerter2.size(); a++) {
				string wort2 = woerter2[a];
				
				long double points2 = 0;
				
				for (unsigned int y = 0; y < woerter.size(); y++) {
					string wort = woerter[y];
			
					StringCompare sc(wort, wort2);
					points2 += sc.getPoints();
				}
				
				points2 = points2 / woerter.size();
				
				if (points2 > best) {
					best = points2 / 100 * 99;
					
					for (int f = 0; f < points2 / 20; f++) {
						replies->push_back(sentence);
					}
				}
				
					
			}
			
		}
		
		string answer = "";
		if (replies->size() > 0) {
			srand((unsigned) time(NULL));
			int ran = rand() % replies->size();
			answer = (*replies)[ran];
		}
		else if (m_sentenceCount > 0) {
			srand((unsigned) time(NULL));
			int ran = rand() % m_sentenceCount;
			answer = (*m_sents)[ran];
			cout << endl;
		}
		else {
			answer = "Erzähl mir mehr darüber!";
			cout << endl;
		}
		
		return answer;
	}
};

#endif

