#ifndef JELIZA
#define JELIZA 1
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

#include "util.cpp"
#include "arrays.cpp"
#include "string_compare.cpp"

using namespace std;


class JEliza {
public:
	int m_sentenceCount;
    StringArray* m_sents;
    string m_file;
    
    JEliza() 
    : m_sentenceCount(0), m_sents(new StringArray(m_sentenceCount)), m_file(searchConfigFile()) 
    {
    	cout << "-> Konfigurationsdatei: " << m_file << endl;
    	cout << endl;
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
        StringArray* arr = new StringArray(5000);
        string buffer;
        
        ifstream in(m_file.c_str());
        if (!in) {
   	        cerr << "Fehler beim Oeffnen einer JEliza-Datei" << endl;
       	    return arr;
        }
        while (in) {
            getline(in, buffer);
            if (buffer.size() > 1) {
	            if (buffer.at(buffer.size() - 1) != static_cast<char>('?')) {
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
	
	string ask(string frage) {
		frage = Util::umwandlung(frage);
		frage = Util::replace(frage, string("?"), string(""));
		
		vector<string> woerter;
		Util::split(frage, " ", woerter);
		
		int best = -1;
		list<string> ants;
		
		// for (int x = 0; x < 351; x++) {
		// 	ants[x] = StringArray(20);
		// }

		for (int z = 0; z < m_sentenceCount; z++) {
			string sentence = (*m_sents)[z];
			
			vector<string> woerter2;
			Util::split(sentence, " ", woerter2);
			string last = "";
			
			for (unsigned int a = 0; a < woerter2.size(); a++) {
				string wort2 = woerter2[a];
					
				for (unsigned int y = 0; y < woerter.size(); y++) {
					string wort = woerter[y];
//					cout << "Wort: " << wort << endl;
			
					StringCompare sc(wort, wort2);
					int points = sc.getPoints();
					
					// cout << "Points: " << points << "Wort1: " << wort << " Wort2: " << wort2 << endl;

//						cout << ants << endl;
					if (points >= best && last != sentence) {
						best = points;
						ants.push_front(sentence);
						last = sentence;
						// cout << "Points: " << points << "Wort1: " << wort << " Wort2: " << wort2 << endl;
//						cout << ants << endl;
					}
					else if (best == -1 && last != sentence) {
						ants.push_back(sentence);
						last = sentence;
					}
					// ants[points].add(sentence);
					
				}
			}
		}
		
		string answer = "";
		if (best > 0) {
//			cout << ants << endl;
			list<string>& answers = ants;
//			Util::split(ants, "~~#-#-#~~", answers);
			
//			cout << endl;
//			cout << "   Moegliche Antworten, nach Warscheinlichkeit geordnet:" << endl;
//			cout << "   -----------------------------------------------------" << endl;
			list<string>::iterator theIterator;
			int b = 0;
			for (theIterator = answers.begin(); theIterator != answers.end(); theIterator++) {
				string it = *theIterator;
				if (b == 0) {
					answer = it;
				}
				if (b >= 20) {
					break;
				}
//				cout << "   " << b + 1 << ". Moegliche Antwort: " << it << endl;
				b++;
			}
//			cout << endl;
		}
		else {
			answer = "Ich weiss noch nicht genug, um sinnvoll zu antworten!";
			cout << endl;
		}
		
		return answer;
	}
};

#endif

