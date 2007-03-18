#ifndef JELIZA_SS
#define JELIZA_SS 1
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


class Word {
private:
	string m_word;
	string m_sym;
	
public:
	Word()
	: m_word(""), m_sym("")
	{ }
	
	Word& make (Word& wo) {
//		cout << 3 << wo.getWord() << wo.getSym() << endl;
		set(wo.getWord(), wo.getSym());
//		cout << 3 << "ok" << endl;
		return (*this);
	}

	Word& set (string& w, string& s) {
//		cout << 1 << w << s << endl;
		m_word = w;
		m_sym = s;
//		cout << 1 << "ok" << endl;
		return (*this);
	}
	Word& set (string& w) {
//		cout << 2 << w << endl;
		m_word = w;
		m_sym = "";
//		cout << 2 << "ok" << endl;
		return (*this);
	}
	
	string& getWord () {
		return m_word;
	}
	string& getSym () {
		return m_sym;
	}
};


class Speechsyntax {
public:
	int m_sentenceCount;
	
	Speechsyntax()
	: m_sentenceCount(0)
	{ }
	
	vector<Word> getSymWord () {
		m_sentenceCount = 0;
		string buffer;
		int r = 0;
		ifstream in2("dic/std.wb");
		while (in2) {
			getline(in2, buffer);
			r++;
		}
		cout << r << endl;
		vector<Word> arr(r);
		
		ifstream in("dic/std.wb");
		if (!in) {
			cerr << "Error while opening dictionary file" << endl;
			return arr;
		}
		cout << "dictionary file opened successful" << endl;
		while (in) {
			getline(in, buffer);
			if (buffer.size() > 1) {
				vector<string> woerter;
				buffer = Util::strip(buffer);
//				cout << buffer << endl;
				Util::split(buffer, string("|"), woerter);
				arr[m_sentenceCount] = Word().set(woerter[1], woerter[0]);
				m_sentenceCount++;
			}
		}
		
		vector<Word> arr2(m_sentenceCount);
		for (int x = 0; x < m_sentenceCount; x++) {
			arr2[x] = arr[x];
		}
		
//		delete(arr);
		return arr2;
	}
	
	vector<Word> getGramm () {
		m_sentenceCount = 0;
		string buffer;
		int r = 0;
		ifstream in2("dic/std.wb");
		while (in2) {
			getline(in2, buffer);
			r++;
		}
		cout << r << endl;
		vector<Word> arr(r);
		
		ifstream in("dic/std.wb");
		if (!in) {
			cerr << "Error while opening dictionary file" << endl;
			return arr;
		}
		cout << "dictionary file opened successful" << endl;
		while (in) {
			getline(in, buffer);
			if (buffer.size() > 1) {
				vector<string> woerter;
				buffer = Util::strip(buffer);
//				cout << buffer << endl;
				Util::split(buffer, string("|"), woerter);
				arr[m_sentenceCount] = Word().set(woerter[1], woerter[0]);
				m_sentenceCount++;
			}
		}
		
		vector<Word> arr2(m_sentenceCount);
		for (int x = 0; x < m_sentenceCount; x++) {
			arr2[x] = arr[x];
		}
		
//		delete(arr);
		return arr2;
	}
	
	vector<Word> parse (string txt) {		
		vector<string> woerter;
		Util::split(txt, " ", woerter);
		vector<Word> fra(woerter.size());
		for (unsigned int y = 0; y < woerter.size(); y++) {
			string word = woerter[y];
			Word x;
			x.set(word);
			fra[y] = x;
		}
		
		vector<Word> symbols(woerter.size());
		vector<Word> syms = getSymWord();
		for (int y = 0; y < fra.size(); y++) {
			Word word = fra[y];
			
			Word symbol = Word().make(word);
			for (int z = 0; z < syms.size(); z++) {
				Word s = syms[z];
				
				if (s.getWord().c_str() == word.getWord().c_str() || s.getWord() == word.getWord()) {
					cout << s.getWord() << "|" << word.getWord() << " " << s.getSym() << endl;
					
					symbol.set(s.getWord(), s.getSym());
				}
			}
			
			cout << symbol.getWord() << "..." << symbol.getSym() << endl;

//			cout << symbol.getWord() << symbol.getSym() << endl;
			
//			if (symbol.getSym().c_str() != word.getSym().c_str()) {
			symbols[y] = symbol;
//			}
//			else {
//				symbols[y] = word;
//			}
		}
		
//		delete(syms);
		return symbols;
	}
	
};


int main() {
	Speechsyntax* sp = new Speechsyntax();
	vector<Word> words = sp->parse("essen ist zahm");
	
	cout << endl << endl;
	
	for (int y = 0; y < words.size(); y++) {
		Word word = words[y];
		
		cout << word.getWord() << word.getSym() << endl;
	}
}

#endif

