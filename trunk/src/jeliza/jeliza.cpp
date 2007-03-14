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

struct Answer {
	string answer;
	bool success;
};

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
	vector<string>* m_sents;
	string m_file;
    
	JEliza() 
	: m_sentenceCount(0), m_sents(new vector<string>(StringArray(m_sentenceCount).m_vector)), m_file(searchConfigFile()) 
	{
		cout << "-> config file: " << m_file << endl;
		cout << endl;
	}
    
	JEliza(int dummy) 
	: m_sentenceCount(0), m_sents(new vector<string>(StringArray(m_sentenceCount).m_vector)), m_file(searchConfigFile()) 
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
		
		ofstream o1("subject-verb.txt", ios::app | ios::ate);
		ofstream o2("verb-object.txt", ios::app | ios::ate);
		
		string buffer;
		
		ifstream in("verbs.txt");
		vector<string> verbs;
		while (in) {
			getline(in, buffer);
			buffer = Util::strip(buffer);
			verbs.push_back(buffer);
		}
		
		SentenceToSubVerbObj(newstring, verbs, o1, o2);
	}
	
	void learn (string orig, string fra) {
		fra = Util::umwandlung(fra);
		saveSentence ("file", orig, fra);
	}
	
	void init () {
		StringArray* sentences = getSentences("file");
		
		delete(m_sents);
		m_sents = new vector<string>(m_sentenceCount);
		
		for (int x = 0; x < m_sentenceCount; x++) {
			string s = (*sentences)[x];
			s = Util::toLower(s);
////			cout << "Sentence: " << s << endl;
			m_sents->push_back(s);
		}
		
		delete(sentences);
	}
	
	Answer askDynamic(string frage) {
		vector<string> woerter;
		Util::split(frage, " ", woerter);
		long double points2;
		unsigned int y;
		ifstream u("subject-verb.txt");
		ifstream v("verb-object.txt");
		vector<string> s_v;
		vector<string> v_o;
		string buffer;
		vector<string> tmp;
		vector<string> tmp1;
		
		while (u) {
			getline(u, buffer);
			buffer = Util::strip(buffer);
			vector<string> woerter2;
			Util::split(buffer, " ", woerter2);
			
			points2 = 0;
			
//			for (y = 0; y < woerter2.size(); y++) {
//				string wort2 = woerter2[y];
//
//				for (y = 0; y < woerter.size(); y++) {
//					string wort = woerter[y];
//			
//					StringCompare sc(wort, wort2);
//					points2 += sc.getPoints();
//				}
//			}
//			
//			points2 = points2 / (woerter.size() * woerter2.size());
//			
//			if (points2 < 70) {
//				continue;
//			}
			
			bool drin = false;
			for (y = 0; y < woerter2.size(); y++) {
				string wort2 = woerter2[y];
				
				if (Util::contains(buffer, wort2)) {
					drin = true;
					break;
				}
			}
			
			if (drin) {
				s_v.push_back(buffer);
				Util::SplitString(buffer, string(" -> "), tmp, false);
				tmp1.push_back(tmp[1]);
			}
		}

		while (v) {
			getline(v, buffer);
			buffer = Util::strip(buffer);
			vector<string> woerter2;
			Util::split(buffer, " ", woerter2);
			
			bool drin = false;
			for (y = 0; y < woerter2.size(); y++) {
				string wort2 = woerter2[y];
				
				if (Util::contains(buffer, wort2)) {
					drin = true;
					break;
				}
			}
			
			for (y = 0; y < tmp1.size(); y++) {
				string wort2 = tmp1[y];
				
				if (Util::contains(buffer, wort2)) {
					drin = true;
					break;
				}
			}
			
			if (drin) {
				v_o.push_back(buffer);
			}
		}
		
		string bestStr;
		long double best;
		vector<string> tmp2;
		for (y = 0; y < s_v.size(); y++) {
			string wort = s_v[y];
			
			Util::SplitString(buffer, string(" -> "), tmp, false);
			
			bestStr = "";
			best = 0;
			for (y = 0; y < v_o.size(); y++) {
				string wort2 = v_o[y];
				
				Util::SplitString(bestStr, string(" -> "), tmp2, false);
				
				StringCompare sc(tmp[1], tmp2[0]);
				if (sc.getPoints() > best) {
					best = sc.getPoints();
					bestStr = wort2;
				}
			}
			
			cout << tmp[0] << " " << tmp[1] << " " << tmp2[1] << endl;
			
			
		}
		
		Answer a;
		a.answer = tmp[0] + " " + tmp[1] + " " + tmp2[1];
		a.success = true;
		return a;
	}
	
	void generiere(string sent) {
		ifstream j("subject-verb.txt");
		vector<string> xy;
		string s;
		string last = "";
		
		vector<string> ss;
		Util::split(sent, string(" "), ss);
		
		delete(m_sents);
		m_sents = new vector<string>();
		
		while (j) {
			getline(j, s);
			vector<string> s_v;
			
			Util::split(s, string("|"), s_v);
			
			ifstream v("verb-object.txt");
			string b;
			long double best = 0;
			string bestStr = "";
			
			while (v) {
				getline(v, b);
				vector<string> v_o;
				
				Util::split(b, string("|"), v_o);
				if (v_o.size() != 2 || s_v.size() != 2) {
					continue;
				}
				if (v_o[0].size() < 1 || s_v[1].size() < 1) {
					continue;
				}
				
//				StringCompare sc(s_v[1], v_o[0]);
//				long double x = sc.getPoints();
				if (s_v[1] == v_o[0]) {
					best = 100;
					bestStr = s_v[0] + " " + s_v[1] + " " + v_o[1];
					bestStr = Util::strip(bestStr);
					bestStr = " " + bestStr + " ";
					if (bestStr.size() > 1) {
						bool ok = false;
						for (int x = 0; x < ss.size(); x++) {
							string sss = " " + ss[x] + " ";
							
							if (Util::contains(bestStr, sss)) {
								ok = true;
								break;
							}
						}
						if (ok && bestStr != last) {
							xy.push_back(bestStr);
							last = bestStr;
							m_sents->push_back(Util::strip(bestStr));
//							cout << bestStr << endl;
						}
					}

				}
			}
			v.close();
		}
		j.close();
		
		m_sentenceCount = m_sents->size();
	}
	
	void SentenceToSubVerbObj(string s, vector<string> verbs, ofstream& o1, ofstream& o2) {
		vector<string> woerter;
		Util::split(s, " ", woerter);
		
		ifstream in("verbs.txt");
		
		string verb = "";
		long double bestVerb = 0;
		long double points2 = 0;
		long double points3 = 0;

		for (int x = 0; x < verbs.size(); x++) {
			string buffer = verbs[x];
			if (buffer.size() > 1) {
				points2 = 0;
				points3 = 0;
				string tempverb = "";
				for (int g = 0; g < woerter.size(); g++) {
					string wort = woerter[g];
					StringCompare sc(wort, buffer);
					points2 += sc.getPoints();
					
					if (sc.getPoints() > points3) {
						points3 = sc.getPoints();
						tempverb = wort;
					}
				}
				
				points2 = points2 / woerter.size();
				
				if (points2 > bestVerb) {
					bestVerb = points2;
					verb = tempverb;
				}
			}
		}
	
		if (woerter.size() < 15 && bestVerb > 40) {
			vector<string> k;
			Util::SplitString(s, verb, k, false);
			
			k[0] = Util::strip(k[0]);
			k[1] = Util::strip(k[1]);
			s = Util::strip(s);
			verb = Util::strip(verb);
			
			k[0] = Util::replace(k[0], "  ", " ");
			k[1] = Util::replace(k[1], "  ", " ");
			s = Util::replace(s, "  ", " ");
			verb = Util::replace(verb, "  ", " ");
			
			if (k[0].size() > 1) {
				o1 << k[0] << "|" << verb << endl;
				cout << k[0] << "|" << verb << endl;
			}
			if (k[1].size() > 1) {
				o2 << verb << "|" << k[1] << endl;
				cout << verb << "|" << k[1] << endl;
			}
		}

	}
	
	/*
	 * The answering algorithm
	 */
	string ask(string frage) {
		frage = Util::umwandlung(frage);
		frage = Util::replace(frage, string("?"), string(""));
		frage = Util::strip(frage);
		
		generiere(frage);
		
		vector<string> woerter;
		Util::split(frage, " ", woerter);
		
		long double best = -1;
		vector<string>* replies = new vector<string>();
		replies->push_back("");

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
					points2 += sc.getPoints() * wort.size();
				}
				
				points2 = points2 / (woerter.size() * frage.size());
				
				if (points2 > best) {
					best = points2 / 100 * 98;
					
					for (int f = 0; f < points2 / 20; f++) {
						(*replies)[0] = sentence;
					}
				}
				
					
			}
			
		}
		
		string answer = "";
		if (replies->size() > 0) {
			cout << "Rate nicht..." << endl;
			srand((unsigned) time(NULL));
			int ran = rand() % replies->size();
			answer = (*replies)[ran];
		}
		else if (m_sentenceCount > 0) {
			cout << "Rate..." << endl;
			srand((unsigned) time(NULL));
			int ran = rand() % m_sentenceCount;
			answer = (*m_sents)[ran];
			cout << endl;
		}
		else {
			answer = "Erzähl mir mehr darüber!";
			cout << endl;
		}
		
		//delete(replies);
		
		return answer;
	}
	
	/*
	 * The old answering algorithm
	 */
	string askOLD(string frage) { // ####################################### => OLD
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
					best = points2;
					
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

