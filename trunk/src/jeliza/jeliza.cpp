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
#include "jeliza.h"

using namespace std;

JElizaData jd2;
JElizaData JEliza::m_jd = jd2;


/*
 * Eine Klasse, die die Antwort beinhaltet
 * 
 * answer: Die Antwort
 * success: Ob die Ántwort erfolgreich bestimmt wurde oder nur geraten war
 */
struct Answer {
	string answer;
	bool success;
};


/*
 * Sucht nach der Datei JEliza.txt
 */
string JEliza::searchConfigFile() {
	ifstream tmpin1("JEliza.txt");
	ifstream tmpin2("/etc/jeliza/JEliza.txt");
	if (!tmpin1) {
		return "/etc/jeliza/JEliza.txt";
	}
	return "JEliza.txt";
}
	
/*
 * Speichert einen Satz in div. Dateien und im RAM
 */
void JEliza::saveSentence (string savetype, string original, string newstring) {
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
	vorbereiteSent(newstring);
}

/*
 * Lernt einen Satz, dh. formt ihn um (ich->du) und ruft saveSentence() auf
 */
void JEliza::learn (string orig, string fra) {
	fra = Util::umwandlung(fra);
	saveSentence ("file", orig, fra);
}
	
/*
 * Ehemalige initialisierungsmethode von JEliza
 */
void JEliza::init () {
	cout << "init()" << endl;
	cout << "Initialisiert" << endl;
}
	
/*
 * Wird von vorbereite() aufgerufen
 *
 * Trennt einen String auf (|) und speichert die beiden Teile (Subjekt-Verb bzw. Verb-Objekt) in dem Entsprechenden Variablen
 */
void JEliza::vorbereiteSentence(string sent, string art) {
	vector<string> temp;
	Util::split(sent, string("|"), temp);
	if (temp.size() != 2 || temp.size() != 2) {
		return;
	}
	if (temp[0].size() < 1 || temp[1].size() < 1) {
		return;
	}
	if (art == "sv") {
		JEliza::m_jd.m_SVs->push_back(sent);
		JEliza::m_jd.m_SVs_words->push_back(temp);
	}
	if (art == "vo") {
		JEliza::m_jd.m_VOs->push_back(sent);
		JEliza::m_jd.m_VOs_words->push_back(temp);
	}
}

/*
 * Liest alle Subjekt-Verb bzw. Verb-Objekt Paare ein und ruft vorbereiteSentence() und vorbereiteSent() auf
 */
void JEliza::vorbereite() {
	cout << endl << "- Vorbereite..." << endl;
	string b;
	ifstream j("subject-verb.txt");
	while (j) {
		getline(j, b);
		
		vorbereiteSentence(b, "sv");
	}

	ifstream v("verb-object.txt");
	while (v) {
		getline(v, b);
		
		vorbereiteSentence(b, "vo");
	}
	
	string s;
	string bestStr = "";
	
	cout << "- Lade Datenbank ins RAM... " << endl;

	for (unsigned int a = 0; a < JEliza::m_jd.m_SVs->size(); a++) {
		s = (*JEliza::m_jd.m_SVs)[a];
		vector<string> s_v = (*JEliza::m_jd.m_SVs_words)[a];
		
		for (unsigned int c = 0; c < JEliza::m_jd.m_VOs->size(); c++) {
			s = (*JEliza::m_jd.m_VOs)[c];
			vector<string> v_o = (*JEliza::m_jd.m_VOs_words)[c];
			
			if (s_v[1] == v_o[0]) {
				bestStr = s_v[0] + " " + s_v[1] + " " + v_o[1];
				
				vorbereiteSent(bestStr);
			}
		}
	}
	
	cout << "- Datenbank erfolgreich geladen!" << endl;
	cout << "- Vorbereitung abgeschlossen!" << endl << endl;
}

/*
 * Speichert jedes Wort in einem satz zusammen mit dem Satz in Variablen
 */
void JEliza::vorbereiteSent(string bestStr) {
	bestStr = Util::strip(bestStr);
	if (bestStr.size() < 2) {
		return;
	}
	
	vector<string> temp;
	Util::split(bestStr, string(" "), temp);
	
	for (vector<string>::iterator it = temp.begin(); it != temp.end(); it++) {
		JEliza::m_jd.m_sent_word->push_back(*it);
		JEliza::m_jd.m_sent_sent->push_back(bestStr);
	}
}
	
/*
 * Generiert die Datenbank, die dann von ask() benutzt wird
 */
void JEliza::generiere(string sent) {
	cout << "- Generiere Moegliche Antworten auf \"" << sent << "\":" << endl;
	
	string last = "";
	
	string sFrageZeichen("?");
	
	vector<string> ss;
	Util::split(sent, string(" "), ss);
	
	JElizaData jd;
	JEliza::m_jd.m_sents = jd.m_sents;
	
	string s;
	long double best = 0;
	string bestStr = "";
	bool ok;
	string sss;
	
	for (unsigned int a = 0; a < JEliza::m_jd.m_sent_word->size(); a++) {
		s = (*JEliza::m_jd.m_sent_word)[a];
		
		ok = false;
		for (unsigned int x = 0; x < ss.size(); x++) {
			if (s == ss[x]) {
				ok = true;
				break;
			}
		}
		
		if (ok) {
			generiereSentence((*JEliza::m_jd.m_sent_sent)[a], ss, sFrageZeichen, last);
		}
	}
	
	m_sentenceCount = JEliza::m_jd.m_sents->size();
	
	cout << "- " << m_sentenceCount << " Antworten auf \"" << sent << "\" gefunden!" << endl;
}

/*
 * Fügt einen von generiere() generierten Satz zur Datenbank hinzu
 */
void JEliza::generiereSentence(string& bestStr, vector<string>& ss, string& sFrageZeichen, string& last) {
	bestStr = Util::strip(bestStr);
	if (bestStr.size() > 1) {
		bool ok = true;
		if (ok && bestStr != last && !Util::contains(bestStr, sFrageZeichen)) {
			last = bestStr;
			JEliza::m_jd.m_sents->push_back(Util::strip(bestStr));
		}
	}
}

/*
 * Trennt einen Satz in Subjekt, Verb und Objekt auf
 */
void JEliza::SentenceToSubVerbObj(string s, vector<string> verbs, ofstream& o1, ofstream& o2) {
	vector<string> woerter;
	Util::split(s, " ", woerter);
	
	ifstream in("verbs.txt");
	
	string verb = "";
	long double bestVerb = 0;
	long double points2 = 0;
	long double points3 = 0;
	for (unsigned int x = 0; x < verbs.size(); x++) {
		string buffer = verbs[x];
		if (buffer.size() > 1) {
			points2 = 0;
			points3 = 0;
			string tempverb = "";
			for (unsigned int g = 0; g < woerter.size(); g++) {
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
			vorbereiteSentence(k[0] + "|" + verb, "sv");
		}
		if (k[1].size() > 1) {
			o2 << verb << "|" << k[1] << endl;
			cout << verb << "|" << k[1] << endl;
			vorbereiteSentence(verb + "|" + k[1], "vo");
		}
	}
}
	
/*
 * Antwortet auf eine Frage
 */
string JEliza::ask(string frage) {
	frage = Util::umwandlung(frage);
	frage = Util::replace(frage, string("?"), string(""));
	frage = Util::strip(frage);
	
	generiere(frage);
	
	cout << "- Suche eine passende Antwort: " << endl;
	
	vector<string> woerter;
	Util::split(frage, " ", woerter);
	
	long double best = -1;
	vector<string>* replies = new vector<string>();

	for (int z = 0; z < m_sentenceCount; z++) {
		string sentence = (*JEliza::m_jd.m_sents)[z];
		sentence = Util::strip(sentence);
		if (sentence.size() < 1) {
			continue;
		}
		
		vector<string> woerter2;
		Util::split(sentence, " ", woerter2);
		string last = "";
		
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
					if (replies->size() == 0) {
						replies->push_back(sentence);
					} else {
						(*replies)[0] = sentence;
					}
				}
			}
			
				
		}
		
	}
	
	string answer = "";
	if (replies->size() > 0) {
		srand((unsigned) time(NULL));
		int ran = rand() % replies->size();
		answer = (*replies)[ran];
		cout << "- Eine passende Antwort wurde gefunden, und zwar: \"" << answer << "\"" << endl;
	}
	else if (m_sentenceCount > 0) {
		srand((unsigned) time(NULL));
		int ran = rand() % m_sentenceCount;
		answer = (*JEliza::m_jd.m_sents)[ran];
		cout << "- Keine passende Antwort wurde gefunden => Raten: \"" << answer << "\"" << endl;
	}
	else {
		answer = "Erzähl mir mehr darüber!";
		cout << "- Die Datenbank ist leer, irgendwas stimmt da nicht. Antwort: \"" << answer << "\"" << endl;
	}
	
	cout << endl;

	return answer;
}
	


#endif

