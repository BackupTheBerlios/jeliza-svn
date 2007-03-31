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
#include <sstream>

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


JEliza& operator<< (JEliza& jel, const Question& fra) {
    jel.m_aktuell_answer = jel.ask(fra.m_ques);
    return (jel);
}
JEliza& operator<< (JEliza& jel, const LearnableSentence& fra) {
    jel.learn(fra.m_ques);
    return (jel);
}

/*
 * Die >> Operatoren
 */
JEliza& operator>> (JEliza& jel, string& ans) {
    ans = jel.m_aktuell_answer.m_ans;
    return (jel);
}


/*
 * Sucht nach der Datei JEliza.txt
 */
string JEliza::searchConfigFile() {
	return "JEliza.txt";
}

/*
 * Speichert einen Satz in div. Dateien und im RAM
 */
void JEliza::saveSentence (string savetype, string original, string newstring) {
    ofstream o(m_file.c_str(), ios::app | ios::ate);
    if (!o) {
        cerr << "Fehler beim Oeffnen einer JEliza-Datei (jeliza.cpp)" << endl;
    } else if (newstring.size() < 10) {
        cerr << "Satz hat zu wenig Woerter (< 10): " << newstring << endl;
    } else {
        o << newstring << endl;
        o.close();
    }

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
	if (newstring.size() > 9) {
        vorbereiteSent(newstring);
	}
}

/*
 * Lernt einen Satz, dh. formt ihn um (ich->du) und ruft saveSentence() auf
 */
void JEliza::learn (string fra) {
	fra = Util::umwandlung(fra);
	saveSentence ("file", fra, fra);
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
	if (m_schonVorbereitet) {
		cout << "- Schon vorbereitet" << endl;
		return;
	}
	m_schonVorbereitet = true;

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
	string sFrageZeichen("?");
	string sWas("was");
	string sWer("was");
	string sWie("was");

	if (Util::contains(bestStr, sFrageZeichen) || Util::contains(bestStr, sWas) || Util::contains(bestStr, sWer) || Util::contains(bestStr, sWie)) {
		return;
	}

	bestStr = Util::strip(bestStr);
	if (bestStr.size() < 2) {
		return;
	}

//	cout << 3 << bestStr << endl;

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
bool JEliza::generiere(string sent) {
	cout << "- Generiere Moegliche Antworten auf \"" << sent << "\":" << endl;

	sent = Util::replace(sent, string("?"), string(""));
	sent = Util::toLower(sent);

	string last = "";

	string sFrageZeichen("?");
	string sWas("was");
	string sWer("was");
	string sWie("was");

	Util::split(sent, string(" "), JEliza::m_jd.m_last_sentence_words);
	if (JEliza::m_jd.m_last_sentence_words.size() > 60) {
	    JEliza::m_jd.m_last_sentence_words.erase(
                JEliza::m_jd.m_last_sentence_words.begin(),
                JEliza::m_jd.m_last_sentence_words.begin() + (JEliza::m_jd.m_last_sentence_words.size() - 59)
                );
	}
	for (int x = 0; x < JEliza::m_jd.m_last_sentence_words.size(); x++) {
	    cout << " - Wort: " << JEliza::m_jd.m_last_sentence_words[x] << endl;
	}

	JElizaData jd;
	JEliza::m_jd.m_sents = jd.m_sents;

	string s;
	long double best = 0;
	string bestStr = "";
	bool ok;
	string sss;

	for (unsigned int a = 0; a < JEliza::m_jd.m_sent_word->size(); a++) {
		s = (*JEliza::m_jd.m_sent_word)[a];
		s = Util::toLower(s);

//		if (Util::contains(s, sWas) || Util::contains(s, sWer) || Util::contains(s, sWie)) {
//			cout << 2 << s << endl;
//			continue;
//		}

		ok = false;
		for (unsigned int x = 0; x < JEliza::m_jd.m_last_sentence_words.size(); x++) {
			if (s == JEliza::m_jd.m_last_sentence_words[x]) {
				ok = true;
				break;
			}
		}

		if (ok) {
//			cout << 1 << (*JEliza::m_jd.m_sent_sent)[a] << endl;
			generiereSentence((*JEliza::m_jd.m_sent_sent)[a], JEliza::m_jd.m_last_sentence_words, sFrageZeichen, last);
		}
	}

	bool found = (JEliza::m_jd.m_sents->size() > 0) ? true : false;

	string b;
	ifstream j("JEliza.txt");
	while (j) {
		getline(j, b);
		if (Util::strip(b).size() > 0) {
			b = Util::strip(b);
			string b_orig = b;
			b = Util::toLower(b);
			if (!Util::contains(b, sFrageZeichen) && !Util::contains(b, sWas) && !Util::contains(b, sWer) && !Util::contains(b, sWie)) {
				JEliza::m_jd.m_sents->push_back(Util::strip(b_orig));
			}
		}
	}

	m_sentenceCount = JEliza::m_jd.m_sents->size();

	cout << "- " << m_sentenceCount << " Antworten auf \"" << sent << "\" gefunden!" << endl;

	return found;
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

//	ifstream in("verbs.txt");

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
			bool brea = false;
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

	if (verb.size() > 1) { // && bestVerb > 40
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
Answer JEliza::ask(string frage) {
	frage = Util::replace(frage, string("-"), string("refdrefdrefzthgred4t4"));
	frage = Util::umwandlung(frage);
	frage = Util::replace(frage, string("refdrefdrefzthgred4t4"), string("-"));
	frage = Util::replace(frage, string("?"), string(""));
	frage = Util::replace(frage, string("!"), string(""));
	frage = Util::replace(frage, string("."), string(""));
	frage = Util::replace(frage, string(","), string(""));
	frage = Util::replace(frage, string(";"), string(""));
	frage = Util::replace(frage, string("+"), string(" ztrgftredrefd "));
	frage = Util::replace(frage, string("ztrgftredrefd"), string("+"));
	frage = Util::replace(frage, string("-"), string(" ztrgftredrefd "));
	frage = Util::replace(frage, string("ztrgftredrefd"), string("-"));
	frage = Util::replace(frage, string("*"), string(" ztrgftredrefd "));
	frage = Util::replace(frage, string("ztrgftredrefd"), string("*"));
	frage = Util::replace(frage, string("/"), string(" ztrgftredrefd "));
	frage = Util::replace(frage, string("ztrgftredrefd"), string("/"));
	frage = Util::replace(frage, string("  "), string(" "));
	frage = " " + frage + " ";

	vector<string> unuseful_words;
	unuseful_words.push_back("der");
	unuseful_words.push_back("die");
	unuseful_words.push_back("das");
	unuseful_words.push_back("dem");
	unuseful_words.push_back("des");
	unuseful_words.push_back("dessen");
	unuseful_words.push_back("was");
	unuseful_words.push_back("wer");
	unuseful_words.push_back("wie");
	unuseful_words.push_back("wo");
	unuseful_words.push_back("wann");
	unuseful_words.push_back("von");
	unuseful_words.push_back("vom");
	unuseful_words.push_back("in");
	unuseful_words.push_back("auf");
	unuseful_words.push_back("unter");
	unuseful_words.push_back("neben");
	unuseful_words.push_back("zwischen");
	unuseful_words.push_back("denn");
	unuseful_words.push_back("bloß");
	unuseful_words.push_back("bloss");
	unuseful_words.push_back("stimmts");
	unuseful_words.push_back("stimmt's");
	unuseful_words.push_back("hmm");
	unuseful_words.push_back("hm");
	unuseful_words.push_back("hmmm");
	unuseful_words.push_back("hmmmm");
	unuseful_words.push_back("sonst");
	unuseful_words.push_back("ansonsten");
	unuseful_words.push_back("noch");

	unuseful_words.push_back("of");
	unuseful_words.push_back("from");
	unuseful_words.push_back("by");
	unuseful_words.push_back("between");
	unuseful_words.push_back("what");
	unuseful_words.push_back("when");

	for (int x = 0; x < unuseful_words.size(); x++) {
		string unuseful_word = " " + unuseful_words[x] + " ";

		string better_frage = Util::replace(frage, unuseful_word, string(" "));
//		cout << better_frage << endl;

		if (Util::strip(better_frage).size() > 0) {
			frage = better_frage;
		}
	}

	frage = Util::strip(frage);

	// Ist die Frage "leer"?
	if (frage.size() == 0) {
		cout << "- Leere Frage!" << endl;
		return Answer("Hmmm.");
	}

	// Ist es eine Rechenaufgabe?
	try {
		stringstream sst;
		string str;

		string divNull = string("/ 0");
		if (Util::contains(frage, divNull)) {
			cout << "- Division durch Null!" << endl;
			return Answer("Tut mir leid, aber durch 0 kann ich nicht teilen!");
		}

		sst << rechne(frage);
		sst >> str;

		cout << "- Es war eine Rechenaufgabe!" << endl;
		cout << "- " << frage << " = " << str << endl;
		return str;
	} catch (const string) {
		cout << "- Es war keine Rechenaufgabe!" << endl;
	}

	bool found = generiere(frage);
	cout << "- Suche eine passende Antwort: " << endl;

	vector<string> woerter;
	Util::split(frage, " ", woerter);

	// Unbekannte Woerter?
	ifstream ifstr("JEliza.txt");
	vector<string> bekannt;
	while (ifstr) {
		string bek;
		ifstr >> bek;
		bek = Util::replace(bek, string("?"), string(""));
		bek = Util::replace(bek, string("!"), string(""));
		bek = Util::replace(bek, string("."), string(""));
		bek = Util::replace(bek, string(","), string(""));
		bek = Util::replace(bek, string(";"), string(""));
		bek = Util::strip(bek);
		bek = Util::toLower(bek);
		bekannt.push_back(bek);
	}
	ifstream ifstr2("subject-verb.txt");
	while (ifstr2) {
		string bek;
		ifstr2 >> bek;
		vector<string> temp;
		if (Util::contains(bek, string("|"))) {
			Util::split(bek, "|", temp);
		} else {
			temp.push_back(bek);
		}
		for (int x = 0; x < temp.size(); x++) {
			bek = temp[x];

			bek = Util::replace(bek, string("?"), string(""));
			bek = Util::replace(bek, string("!"), string(""));
			bek = Util::replace(bek, string("."), string(""));
			bek = Util::replace(bek, string(","), string(""));
			bek = Util::replace(bek, string(";"), string(""));
			bek = Util::strip(bek);
			bek = Util::toLower(bek);
			bekannt.push_back(bek);
		}
	}
	ifstream ifstr3("verb-object.txt");
	while (ifstr2) {
		string bek;
		ifstr3 >> bek;
		vector<string> temp;
		if (Util::contains(bek, string("|"))) {
			Util::split(bek, "|", temp);
		} else {
			temp.push_back(bek);
		}
		for (int x = 0; x < temp.size(); x++) {
			bek = temp[x];

			bek = Util::replace(bek, string("?"), string(""));
			bek = Util::replace(bek, string("!"), string(""));
			bek = Util::replace(bek, string("."), string(""));
			bek = Util::replace(bek, string(","), string(""));
			bek = Util::replace(bek, string(";"), string(""));
			bek = Util::strip(bek);
			bek = Util::toLower(bek);
			bekannt.push_back(bek);
		}
	}
	for (int x = 0; x < woerter.size(); x++) {
		string wort = woerter[x];
		wort = Util::toLower(wort);

		bool isBeka = false;

		for (int y = 0; y < bekannt.size(); y++) {
			string beka = bekannt[y];

			if (wort == beka) {
				isBeka = true;
				break;
			}
		}

		if (woerter[x] == wort) {
		    isBeka = true;
		}

		if (!isBeka) {
			cout << "- Unbekanntes Wort: " << wort << endl;
			return Answer("Was bedeutet " + wort + "??");
		}
	}


	long double best = -1;
	string allanswers = "";

	string reply = "";
	string second_reply = "";

	string last_answer = "";
	if (JEliza::m_jd.m_last_answers.size() > 0) {
		last_answer = JEliza::m_jd.m_last_answers[JEliza::m_jd.m_last_answers.size() - 1];
	}

	for (int z = 0; z < m_sentenceCount; z++) {
		string sentence = (*JEliza::m_jd.m_sents)[z];
		sentence = Util::strip(sentence);
		if (sentence.size() < 1) {
			continue;
		}
		if (reply == sentence) {
			continue;
		}

		vector<string> woerter2;
		Util::split(sentence, " ", woerter2);
		string last = "";

		long double points2 = 0;

		for (unsigned int a = 0; a < woerter2.size(); a++) {
			string wort2 = woerter2[a];
			wort2 = Util::toLower(wort2);

			for (unsigned int y = 0; y < woerter.size(); y++) {
				string wort = woerter[y];
				wort = Util::toLower(wort);

				StringCompare sc(wort, wort2);
				points2 += sc.getPoints() * wort.size() * 1.2;
			}

		}

		points2 = points2 / (woerter.size() * frage.size() * woerter2.size());

		cout << points2 << " " << frage << " " << sentence << endl;

        if (points2 > best && last_answer != sentence) {

			best = points2 / 100 * 98;

			second_reply = reply;
			reply = sentence;

		}

	}

	if (JEliza::m_jd.m_last_answers_second.size() > 1 && best < 5) {
		vector<string>::iterator it = JEliza::m_jd.m_last_answers_second.end();
		it--;
		if ((*it).size() > 0) {
			cout << "- Nehme zweitbeste Antwort der letzten Frage " << *it << endl;
			JEliza::m_jd.m_last_answers.push_back("");
			JEliza::m_jd.m_last_answers_second.push_back("");
			JEliza::m_jd.m_last_questions.push_back("");
			return Answer(*it);
		}
	}

	vector<string> prefixes;
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("Also, ");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("Hmmm, ");
	prefixes.push_back("Ich glaube ");
	prefixes.push_back("");

	vector<string> suffixes;
	suffixes.push_back("");
	suffixes.push_back("");
	suffixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	prefixes.push_back("");
	suffixes.push_back("");
	suffixes.push_back("");
	suffixes.push_back(", oder?");
	suffixes.push_back("");
	suffixes.push_back("");
	suffixes.push_back(", stimmt's?");
	suffixes.push_back("");
	suffixes.push_back("");
	suffixes.push_back("");
	suffixes.push_back("");
	suffixes.push_back("");
	suffixes.push_back("");

	srand((unsigned) time(NULL));
	int random = rand() % prefixes.size();
	string answer = prefixes[random];

	srand((unsigned) time(NULL));
	random = rand() % suffixes.size();
	string suff = suffixes[random];

	if (reply.size() > 0) {
		if (Util::contains(reply, "was") || Util::strip(reply).size() > 15) {
			answer = "";
			suff = "";
		}
		answer += reply + suff;
		cout << "- Eine passende Antwort wurde gefunden, und zwar: \"" << answer << "\"" << endl;
		JEliza::m_jd.m_last_answers.push_back(reply);
		cout << "- Die zweitbeste Antwort war: \"" << second_reply << "\"" << endl;
		JEliza::m_jd.m_last_answers_second.push_back(second_reply);
	}
	else if (m_sentenceCount > 0) {
		srand((unsigned) time(NULL));
		int ran = rand() % m_sentenceCount;
		if (Util::contains(Util::strip((*JEliza::m_jd.m_sents)[ran]), "was") || Util::strip((*JEliza::m_jd.m_sents)[ran]).size() < 12) {
			answer = "";
			suff = "";
		}
		answer += (*JEliza::m_jd.m_sents)[ran] + suff;
		cout << "- Keine passende Antwort wurde gefunden => Raten: \"" << answer << "\"" << endl;
	}
	else {
		answer = "Erzähl mir mehr darüber!";
		cout << "- Die Datenbank ist leer, irgendwas stimmt da nicht. Antwort: \"" << answer << "\"" << endl;
	}

	JEliza::m_jd.m_last_questions.push_back(frage);

	cout << endl;

	return Answer(answer);
}

double JEliza::rechne(string s) {
	vector<string> ss;
	s = Util::strip(s);
	Util::split(s, string(" "), ss);

	vector<string> digits;
	digits.push_back("0");
	digits.push_back("1");
	digits.push_back("2");
	digits.push_back("3");
	digits.push_back("4");
	digits.push_back("5");
	digits.push_back("6");
	digits.push_back("7");
	digits.push_back("8");
	digits.push_back("9");
	digits.push_back(",");
	digits.push_back(".");

	double u = 0;
	double* base = &u;

	string zeichen = "";

	for (int x = 0; x < ss.size(); x++) {
		string sym = ss[x];
		string temp = sym;

		for (int y = 0; y < digits.size(); y++) {
			temp = Util::replace(temp, digits[y], string(""));
		}

		cout << "sym " << sym << "  temp.size() " << temp.size() << "  temp " << temp;

		if (temp.size() == 0) {
			stringstream sst;
			double integer;

			sst << sym;
			sst >> integer;

//				if (base == NULL) {
			if (zeichen.size() == 0) {
				double v = integer + 0;
				base = &v;
			} else {
				cout << "  zeichen.size() " << zeichen.size() << "  integer " << integer << "  base " << *base;
				if (zeichen == "+") {
					*base += integer;
				}
				if (zeichen == "-") {
					*base -= integer;
				}
				if (zeichen == "*") {
					*base *= integer;
				}
				if (zeichen == "/") {
					*base /= integer;
				}
				cout << "  zeichen.size() " << zeichen.size() << "  integer " << integer << "  base " << *base;
			}
//				}
		} else {
			if (Util::contains(sym, string("+"))) {
				zeichen = "+";
			}
			else if (Util::contains(sym, string("-"))) {
				zeichen = "-";
			}
			else if (Util::contains(sym, string("*"))) {
				zeichen = "*";
			}
			else if (Util::contains(sym, string("/"))) {
				zeichen = "/";
			}
			else {
				throw string("KeineRechnung");
			}
		}

		cout << endl;
	}

	return *base;
}


#endif

