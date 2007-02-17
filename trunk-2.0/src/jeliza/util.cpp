#ifndef JELIZA_util
#define JELIZA_util 1

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

using namespace std;

class Util {
public:
    static void split (string& text, string separators, vector<string>& words) {
        int n = text.length();
        int start, stop;
    
        start = text.find_first_not_of(separators);
        while ((start >= 0) && (start < n)) {
            stop = text.find_first_of(separators, start);
            if ((stop < 0) || (stop > n)) stop = n;
            words.push_back(text.substr(start, stop - start));
            start = text.find_first_not_of(separators, stop+1);
        }
    }
    
    static string toLower (string& text) {
        string str = "";
        
        for (string::size_type x = 0; x < text.size(); x++) {
            str += tolower(text[x]);
        }
        return str;
    }
    
//    static string toLower (const string text) {
//        toLower(static_cast<string>(text));
//    }
    
    static string strip (string text) {
        string txt = replace(text, string("\n"), string(""));
        return txt;
    }
    
//    static string strip (const string text) const {
//        strip(static_cast<string>(text));
//    }
    
    static string replace (string& in, const string rep, const string wit) {
		int pos;
		while (true) {
			pos = in.find(rep);
		    if (pos == -1) {
				break;
		    } else {
				in.erase(pos, rep.length());
				in.insert(pos, wit);
		    }
		}
		return in;
	}

//    static string replace (string& in, string rep, string wit) {
//		return replace(in, static_cast<const string>(rep), static_cast<const string>(wit));
//	}

/*
    	string text = string(txt);
		size_t pos = text.find(fnd);
		while (pos != string::npos) {
			text.replace(pos, pos + fnd.length(), rep);
			pos = text.find(fnd, pos);
		}
		return text;
		
*/

	
	static string tausche (string str, string s1, string s2) {
		str = " " + str + " ";
		s1 = " " + s1 + " ";
		s2 = " " + s2 + " ";
		str = Util::replace(str, s1, "6352652630,5,64,4636");
		str = Util::replace(str, s2, s1);
		str = Util::replace(str, "6352652630,5,64,4636", s2);
		
		return str.substr(1, str.size() - 1);
	}
	
	static string umwandlung (string str) {
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
		
		return str;
	}
    
};

#endif

