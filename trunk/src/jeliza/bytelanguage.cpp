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

namespace bytelang {
	class Object {
	public:
		static string PERSON_JELIZA = "JEliza";
		static string PERSON_USER = "User";
		
		string m_obj;
		vector<string> m_adj(50);
		
		Object(string p, vector<string> adj)
		: m_obj(p), m_adj(adj)
		{ }
	}
	
	class Verb {
	public:
		string m_verb;
		
		Verb(string v)
		: m_verb(v)
		{ }
	}
	
	class Sentence {
	public:
		Object m_subject;
		Verb m_verb;
		Object m_object;
		
		string m_quesword;
		bool m_isQues;
		
		Sentence(Object sub, Verb v, Object ob, bool isq, string quesw)
		: m_subject(sub), m_verb(v), m_object(ob), m_quesword(quesw), m_isQues(isq)
		{ }
	}
	
	string TranslateToByte (string str) {
		str = Util::replace(str, ".", "");
		str = Util::replace(str, ",", "");
		str = Util::replace(str, "-", "");
		str = Util::replace(str, ";", "");
		str = Util::replace(str, "!", "");
		
		str = Util::tausche(str, "ich", "du");
		str = Util::tausche(str, "mein", "dein");
		str = Util::tausche(str, "meine", "deine");
		str = Util::tausche(str, "meins", "deins");
		str = Util::tausche(str, "meiner", "deiner");
		str = Util::tausche(str, "meinem", "deinem");
		str = Util::tausche(str, "mir", "dir");
		str = Util::tausche(str, "mich", "dich");
		str = Util::tausche(str, "bin", "bist");
		
		str = Util::replace(str, "hab", "habe");
		str = Util::replace(str, "habee", "habe");
		
		str = Util::tausche(str, "habe", "hast");
		
		str = Util::replace(str, "wieso", "warum");
		str = Util::replace(str, "weshalb", "warum");
		
		str = Util::tausche(str, "warum", "weil");
		str = Util::tausche(str, "i", "you");
		str = Util::tausche(str, "my", "your");
		str = Util::tausche(str, "mine", "yours");
		str = Util::tausche(str, "am", "are");
		
		
	}
	
}

int main() {
	
}

#endif

