#ifndef JELIZA_TEST
#define JELIZA_TEST 1
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
#include "jeliza.cpp"

using namespace std;

namespace test {
	void generiere() {
		ifstream j("subject-verb.txt");
		vector<string> xy;
		string s;
		
		while (j) {
			getline(j, s);
			vector<string> s_v;
			
			Util::SplitString(s, string(" -> "), s_v, false);
			
			ifstream v("verb-object.txt");
			string b;
			long double best = 0;
			string bestStr = "";
			
			while (v) {
				getline(v, b);
				vector<string> v_o;
				
				Util::SplitString(b, string(" -> "), v_o, false);
				if (v_o.size() != 2 || s_v.size() != 2) {
					continue;
				}
				if (v_o[0].size() < 1 || s_v[1].size() < 1) {
					continue;
				}
				
				StringCompare sc(s_v[1], v_o[0]);
				long double x = sc.getPoints();
				if (x > 99) {
					best = x;
					bestStr = s_v[0] + " " + s_v[1] + " " + v_o[1];
					bestStr = Util::strip(bestStr);
					if (bestStr.size() > 1) {
						xy.push_back(bestStr);
						cout << bestStr << endl;
					}

				}
			}
			v.close();
		}
		j.close();
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
	
	void JElizaTxtToDic () {
		JEliza jel(1);
		jel.init();
		
		StringArray* sentences = jel.getSentences("file");
		
		ofstream o1("subject-verb.txt");
		ofstream o2("verb-object.txt");
		
		string buffer;
		
		ifstream in("verbs.txt");
		vector<string> verbs;
		while (in) {
			getline(in, buffer);
			buffer = Util::strip(buffer);
			verbs.push_back(buffer);
		}
		
		for (int x = 0; x < jel.m_sentenceCount; x++) {
			string s = (*sentences)[x];
			s = Util::strip(s);
			
			if (s.size() < 3) {
				continue;
			}
			
			SentenceToSubVerbObj(s, verbs, o1, o2);
		}
		delete (sentences);
		
		o1.close();
		o2.close();
	}
}

int main() {
	test::JElizaTxtToDic();
//	test::generiere();
}

#endif

