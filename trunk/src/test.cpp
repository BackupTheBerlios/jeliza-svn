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

namespace bytelang {
	string JElizaTxtToDic () {
		JEliza jel(1);
		jel.init();
		
		StringArray* sentences = jel.getSentences("file");
		
		for (int x = 0; x < m_sentenceCount; x++) {
			string s = (*sentences)[x];
			
			ifstream in("verbs.txt");
			
			while (in) {
				getline(in, buffer);
				buffer = Util::strip(buffer);
				if (buffer.size() > 1) {
					if (!Util::contains(s, buffer)) {
						cout << s << endl;
					}
				}
			}
		}
		delete (sentences);
			
	}
	
}

int main() {
	bytelang::JElizaTxtToDic();
}

#endif

