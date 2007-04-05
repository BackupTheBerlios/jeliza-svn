#ifndef JELIZA_util
#define JELIZA_util 1

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


#include "defs.h"


using namespace std;

namespace Util {

void split (string& text, string separators, vector<string>& words) {
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

void SplitString (string text, string separators, vector<string>& words, bool includeEmpties) {
	vector<string> ret;
	text = " " + text + " ";
	separators = strip(separators);
	text = replace(text, " " + separators + " ", " #escxds#dscddwe#acs#ewaWAQaqA ");
	separators = strip(" #escxds#dscddwe#acs#ewaWAQaqA ");

	vector<string> h;
	split(text, " ", h);
	string temp = "";

	for (unsigned int x = 0; x < h.size(); x++) {
		string u = h[x];

		if (u == separators) {
			ret.push_back(temp);
			temp = "";
		} else if (temp.size() == 0) {
			temp += u;
		} else {
			temp += " " + u;
		}
	}
	ret.push_back(temp);

	words = ret;
//		return ret;
}

 string toLower (string text) {
	string str = "";

	for (string::size_type x = 0; x < text.size(); x++) {
		str += tolower(text[x]);
	}
	return str;
}

 string toUpper (string text) {
	string str = "";

	for (string::size_type x = 0; x < text.size(); x++) {
		str += toupper(text[x]);
	}
	return str;
}

 string toLower_const (const string text) {
	string str = "";

	for (string::size_type x = 0; x < text.size(); x++) {
		str += tolower(text[x]);
	}
	return str;
}


 void trim(string& str) {
	string::size_type pos = str.find_last_not_of(' ');
	if(pos != string::npos) {
		str.erase(pos + 1);
		pos = str.find_first_not_of(' ');
		if(pos != string::npos)  {
			str.erase(0, pos);
		}
	}
	else {
		str.erase(str.begin(), str.end());
	}
}

/*string strip (string text) {
	string txt = replace(text, string("\n"), string(""));
	txt = replace(txt, string("\r"), string(""));

	string txt2 = txt;
	trim(txt);
	while (txt != txt2) {
		txt2 = txt;
		trim(txt);
	}

	return txt;
}*/

string strip (string text) {
	text = replace(text, string("\r"), string(""));
    do {
        text = "rkewfdneokwjdseowlj" + text + "zhrtzhftfhvcderthjhgf";

        text = replace(text, string("rkewfdneokwjdseowlj\n"), string(""));
        text = replace(text, string("rkewfdneokwjdseowlj "), string(""));

        text = replace(text, string("\nzhrtzhftfhvcderthjhgf"), string(""));
        text = replace(text, string(" zhrtzhftfhvcderthjhgf"), string(""));
    } while (!Util::contains(text, "rkewfdneokwjdseowlj") && !Util::contains(text, "zhrtzhftfhvcderthjhgf"));

    text = replace(text, string("zhrtzhftfhvcderthjhgf"), string(""));
    text = replace(text, string("rkewfdneokwjdseowlj"), string(""));

    string txt = text;

    string txt2;
    do {
		txt2 = txt;
		trim(txt);
	} while (txt != txt2);

	return txt;
}

 string replace (string& in, const string rep, const string wit) {
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

 string replace_save (string in, const string rep, const string wit) {
	in = replace(in, rep, string("iknlkmgncdrkhjikmpljwuxgbeikjenxcurnj"));
	in = replace(in, string("iknlkmgncdrkhjikmpljwuxgbeikjenxcurnj"), wit);
	return in;
}

 string replace_nocase (string& in, string rep, const string wit) {
	int pos;
	rep = toLower(rep);
	while (true) {
		pos = toLower_const(in).find(rep);
		if (pos == -1) {
			break;
		} else {
			in.erase(pos, rep.length());
			in.insert(pos, wit);
		}
	}
	return in;
}

 bool contains (string in, string rep) {
	in = string(in.c_str());
	string rep2 = string(rep.c_str());

	string input = string(in.c_str());
	string input2 = string(input.c_str());
	string in2 = replace(input, rep2, "");
//		cout << endl << "input" << input << endl << "in2" << in2 << endl << endl;
	if (input2.size() == in2.size()) {
		return 0;
	}
	return 1;
}



 string tausche (string str, string s1, string s2) {
	str = " " + str + " ";
	s1 = " " + s1 + " ";
	s2 = " " + s2 + " ";
	str = replace_nocase(str, s1, "6352652630,5,64,4636");
	str = replace_nocase(str, s2, s1);
	str = replace_nocase(str, "6352652630,5,64,4636", s2);

	return str.substr(1, str.size() - 1);
}

 string umwandlung (string str) {
	str = replace(str, ".", "");
	str = replace(str, ",", "");
	str = replace(str, "-", "");
	str = replace(str, ";", "");
	str = replace(str, "!", "");

	str = tausche(str, "ich", "du");
	str = tausche(str, "mein", "dein");
	str = tausche(str, "meine", "deine");
	str = tausche(str, "meins", "deins");
	str = tausche(str, "meiner", "deiner");
	str = tausche(str, "meinem", "deinem");
	str = tausche(str, "mir", "dir");
	str = tausche(str, "mich", "dich");
	str = tausche(str, "bin", "bist");

		//str = replace(str, "habe", "hab");
//		str = replace(str, "habee", "habe");

	str = tausche(str, "habe", "hast");
		//str = replace(str, "hab", "habe");

	str = replace(str, "wieso", "warum");
	str = replace(str, "weshalb", "warum");

	str = tausche(str, "warum", "weil");
	str = tausche(str, "i", "you");
	str = tausche(str, "my", "your");
	str = tausche(str, "mine", "yours");
	str = tausche(str, "am", "are");

	ifstream in("verbs.txt");
	vector<string> verbs;
	string buffer;
	while (in) {
		getline(in, buffer);
		buffer = strip(buffer);
		verbs.push_back(buffer);
	}

	vector<string> ss;
	split(str, string(" "), ss);
	string rep = "";

	for (unsigned int y = 0; y < ss.size(); y++) {
		string word = "-" + ss[y] + "-";
		string lower = word;
		lower = toLower(lower);

		if (contains(word, string("st-")) && word.size() > 7 && word == lower) {
			string verb2 = replace(word, string("st-"), string("e-"));
			verb2 = replace(word, string("ee-"), string("hoire-"));
			verb2 = replace(word, string("hoire-"), string("e-"));
			verb2 = replace(verb2, string("-"), string(""));
			rep += verb2;
			rep += " ";
		}
		else if (contains(word, string("e-")) && word.size() > 7 && word == lower) {
			string verb2 = replace(word, string("e-"), string("st-"));
			verb2 = replace(verb2, string("-"), string(""));
			rep += verb2;
			rep += " ";
		}
		else {
			rep += word;
			rep += " ";
		}
	}

	str = replace(rep, string("-"), string(""));
	str = replace(str, string("eise"), string("eisse"));

	return str;
}

 int max (int a, int b) {
    return ((a>b ? a : b));
}

 int min (int a, int b) {
    return ((a<b ? a : b));
}

}

#endif

