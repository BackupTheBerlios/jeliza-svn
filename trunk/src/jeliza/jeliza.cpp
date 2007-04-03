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

#include "defs.h"
#include "util.cpp"
#include "arrays.cpp"
#include "string_compare.cpp"
#include "jeliza.h"
#include "jdb.cpp"

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
void JEliza::saveSentence (string savetype, string str) {
    vector<string> temp;
    Util::split(str, string(" "), temp);

    if (temp.size() < 3) {
        cerr << "- Satz hat zu wenig Woerter (< 10): " << str << endl;
        return;
    }

    DBSentence dbs = jdb::toDBSentence (str, *this, verbs::getVerbs()) {

    JEliza::m_jd.m_sents.push_back(dbs);

    saveDB("jeliza-standard.xml", *this, JEliza::m_jd.m_sents);

	vorbereite();
}

/*
 * Lernt einen Satz, dh. formt ihn um (ich->du) und ruft saveSentence() auf
 */
void JEliza::learn (string fra) {
	fra = Util::umwandlung(fra);
	saveSentence ("file", fra);
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
	cout << "- Lade Datenbank ins RAM... " << endl;

    JEliza::m_jd.m_sents = jdb::parseJDB("jeliza-standard.xml");

	cout << "- Datenbank erfolgreich geladen!" << endl;
	cout << "- Vorbereitung abgeschlossen!" << endl << endl;
}

/*
 * Generiert die Datenbank, die dann von ask() benutzt wird
 */
/*bool JEliza::generiere(string sent) {
	cout << "- Generiere Moegliche Antworten auf \"" << sent << "\":" << endl;

	sent = Util::replace(sent, string("?"), string(""));
	sent = Util::toLower(sent);

	string last = "";

	string sFrageZeichen("?");
	string sWas("was");
	string sWer("wer");
	string sWie("wie");

	Util::split(sent, string(" "), JEliza::m_jd.m_last_sentence_words);
	if (JEliza::m_jd.m_last_sentence_words.size() > 60) {
	    JEliza::m_jd.m_last_sentence_words.erase(
                JEliza::m_jd.m_last_sentence_words.begin(),
                JEliza::m_jd.m_last_sentence_words.begin() + (JEliza::m_jd.m_last_sentence_words.size() - 59)
                );
	}
	for (int x = 0; x < JEliza::m_jd.m_last_sentence_words.size(); x++) {
	    cout << "- Wort: " << JEliza::m_jd.m_last_sentence_words[x] << endl;
	}

	JElizaData jd;
	JEliza::m_jd.m_sents = jd.m_sents;

	string s;
	long double best = 0;
	string bestStr = "";
	bool ok;
	string sss;

    // Hier muss noch die Geschwindigkiet optimiert werden:
*/
	/*for (unsigned int a = 0; a < JEliza::m_jd.m_sent_word->size(); a++) {
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
	}*/
/*
	bool found = (JEliza::m_jd.m_sents->size() > 0) ? true : false;

	string b;
	ifstream j("JEliza.txt");
	while (j) {
		getline(j, b);
		b = Util::strip(b);
		if (b.size() > 0) {
			string b_orig = b;
			b = Util::toLower(b);
			if (!Util::contains(b, sFrageZeichen) && !Util::contains(b, sWas) && !Util::contains(b, sWer) && !Util::contains(b, sWie)) {
				JEliza::m_jd.m_sents->push_back(b_orig);
			}
		}
	}

	m_sentenceCount = JEliza::m_jd.m_sents->size();

	cout << "- " << m_sentenceCount << " Antworten auf \"" << sent << "\" gefunden!" << endl;

	return found;
}*/

/*
 * Fügt einen von generiere() generierten Satz zur Datenbank hinzu
 */
/*void JEliza::generiereSentence(string& bestStr, vector<string>& ss, string& sFrageZeichen, string& last) {
	bestStr = Util::strip(bestStr);
	if (bestStr.size() > 1) {
		bool ok = true;
		if (ok && bestStr != last && !Util::contains(bestStr, sFrageZeichen)) {
			last = bestStr;
			JEliza::m_jd.m_sents->push_back(Util::strip(bestStr));
		}
	}
}*/

/*
 * Trennt einen Satz in Subjekt, Verb und Objekt auf
 */
/*void JEliza::SentenceToSubVerbObj(string s, vector<string> verbs, ofstream& o1, ofstream& o2) {
	vector<string> woerter;
	Util::split(s, " ", woerter);

//	ifstream in("verbs.txt");

	string verb = "";
	string verb2 = "";
	long double bestVerb = 0;
	long double points2 = 0;
	long double points3 = 0;
	for (unsigned int x = 0; x < verbs.size(); x++) {
		string buffer = verbs[x];
		if (buffer.size() > 1) {
			points2 = 0;
			points3 = 0;
			string tempverb = "";
			string tempverb2 = "";
			bool brea = false;
			for (unsigned int g = 0; g < woerter.size(); g++) {
				string wort = woerter[g];
				StringCompare sc(wort, buffer);
				points2 += sc.getPoints();

				if (sc.getPoints() > points3) {
					points3 = sc.getPoints();
					tempverb = wort;
					tempverb2 = buffer;
				}
			}

			points2 = points2 / woerter.size();

			if (points2 > bestVerb) {
				bestVerb = points2;
				verb = tempverb;
				verb2 = tempverb2;
				if (bestVerb > 75) {
				    break;
				}
			}
		}
	}

	if (verb.size() > 1 && verb == Util::toLower(verb)) { // && bestVerb > 40
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
			o1 << k[0] << "|" << verb2 << endl;
			cout << k[0] << "|" << verb << endl;
			cout << k[0] << "|" << verb2 << endl;
			vorbereiteSentence(k[0] + "|" + verb, "sv");
		}
		if (k[1].size() > 1) {
			o2 << verb << "|" << k[1] << endl;
			o2 << verb << "|" << k[1] << endl;
			cout << verb2 << "|" << k[1] << endl;
			cout << verb2 << "|" << k[1] << endl;
			vorbereiteSentence(verb + "|" + k[1], "vo");
		}
	}
}*/

/*
 * Trennt einen Satz in Subjekt, Verb und Objekt auf
 */
vector<string> JEliza::trenne_SubVerbObj(string s, vector<string> verbs) {
	vector<string> woerter;
	Util::split(s, " ", woerter);

//	ifstream in("verbs.txt");

	string verb = "";
	string verb2 = "";
	long double bestVerb = 0;
	long double points2 = 0;
	long double points3 = 0;
	for (unsigned int x = 0; x < verbs.size(); x++) {
		string buffer = verbs[x];
		if (buffer.size() > 1) {
		    string buffer_lower = Util::toLower(buffer);
			points2 = 0;
			points3 = 0;
			string tempverb = "";
			string tempverb2 = "";
			bool brea = false;
			for (unsigned int g = 0; g < woerter.size(); g++) {
				string wort = woerter[g];
				string wort_lower = Util::toLower(wort);
				StringCompare sc(wort, buffer);
				points2 += sc.getPoints();

				if (sc.getPoints() > points3) {
					points3 = sc.getPoints();
					tempverb = wort;
					tempverb2 = buffer;
				}
			}

			points2 = points2 / woerter.size();

			if (points2 > bestVerb) {
				bestVerb = points2;
				verb = tempverb;
				verb2 = tempverb2;

				if (bestVerb > 75) {
				    break;
				}
			}
		}
	}

	if (verb.size() > 1 && verb == Util::toLower(verb)) { // && bestVerb > 40
		vector<string> k;
		Util::SplitString(s, verb, k, false);

		k[0] = Util::strip(k[0]);
		k[1] = Util::strip(k[1]);
		s = Util::strip(s);
		verb = Util::strip(verb);
		verb2 = Util::strip(verb2);

		k[0] = Util::replace(k[0], "  ", " ");
		k[1] = Util::replace(k[1], "  ", " ");
		s = Util::replace(s, "  ", " ");
		verb = Util::replace(verb, "  ", " ");

		if (k[0].size() > 1 && k[1].size() > 1) {
		    vector<string> ret;
		    ret.push_back(k[0]);
		    ret.push_back(verb);
		    ret.push_back(verb2);
		    ret.push_back(k[1]);
		    return ret;
		}
	}
	vector<string> ret;
	return ret;
}

string JEliza::ohne_muell(string frage) {
    frage = Util::replace(frage, string("-"), string("refdrefdrefzthgred4t4"));
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

	unuseful_words.push_back("fast");
	unuseful_words.push_back("bald");
	unuseful_words.push_back("sehr");
	unuseful_words.push_back("na");
	unuseful_words.push_back("bisschen");
	unuseful_words.push_back("bischen");
	unuseful_words.push_back("hald");
	unuseful_words.push_back("halt");
	unuseful_words.push_back("eben");
	unuseful_words.push_back("oder");
	unuseful_words.push_back("und");

	unuseful_words.push_back("of");
	unuseful_words.push_back("from");
	unuseful_words.push_back("by");
	unuseful_words.push_back("between");
	unuseful_words.push_back("what");
	unuseful_words.push_back("when");

	unuseful_words.push_back("ein");
	unuseful_words.push_back("eine");
	unuseful_words.push_back("einer");
	unuseful_words.push_back("eines");
	unuseful_words.push_back("einem");

	unuseful_words.push_back("fr");
	unuseful_words.push_back("für");
	unuseful_words.push_back("fuer");

	for (int x = 0; x < unuseful_words.size(); x++) {
		string unuseful_word = " " + unuseful_words[x] + " ";

		string better_frage = Util::replace(frage, unuseful_word, string(" "));
//		cout << better_frage << endl;

		if (Util::strip(better_frage).size() > 0) {
			frage = better_frage;
		}
	}

	frage = Util::strip(frage);
	return frage;
}

bool is_similar(string s1, string s2) {
    if (s1 == s2) {
        return true;
    }

    s1 = Util::toLower(s1);
    s1 = Util::strip(s1);

    s2 = Util::toLower(s2);
    s2 = Util::strip(s2);

    if (s1 == s2) {
        return true;
    }
    if (Util::contains(s1, s2)) {
        return true;
    }
    if (Util::contains(s2, s1)) {
        return true;
    }
    return false;
}

Answer JEliza::answer_logical(string frage) {
    frage = Util::strip(frage);
    cout << "- Suche nach einer logischen Antwort auf \"" << frage << "\"" << endl;

    DBSentence dbs = toDBSentence (frage, *this, verbs::getVerbs())
    if (dbs.verb.size() < 1) {
        cout << "- Kein Verb gefunden in: \"" << frage << "\"" << endl;
        return Answer("");
    }

    dbs.subject = ohne_muell(dbs.subject);
    dbs.subject = Util::toLower(dbs.subject);
    dbs.object = ohne_muell(dbs.object);
    dbs.object = Util::toLower(dbs.object);

    dbs.verb = ohne_muell(dbs.verb);
    dbs.verb = Util::toLower(dbs.verb);

    cout << "- Verb ist " << dbs.verb << endl;

    if (dbs.subject.size() < 1 || dbs.object.size() < 1 || dbs.verb.size() < 1) {
        cout << "- Unvollstaendiger Satzteil in: \"" << frage << "\"" << endl;
        return Answer("");
    }

    answers meinungen;

    string s;
    for (DB::iterator it = JEliza::m_jd.m_sents.begin(); it != JEliza::m_jd.m_sents.end(); it++) {
			DBSentence orig = *it;
			DBSentence sent = *it;

            if (sent.subject.size() < 1 || sent.object.size() < 1 || sent.verb.size() < 1) {
                continue;
            }

            sent.subject = ohne_muell(sent.subject);
            sent.subject = Util::toLower(sent.subject);
            sent.object = ohne_muell(sent.object);
            sent.object = Util::toLower(sent.object);

            sent.verb = ohne_muell(sent.verb);
            sent.verb = Util::toLower(sent.verb);

            if (sent.subject.size() < 1 || sent.object.size() < 1 || sent.verb.size() < 1) {
                continue;
            }

            if (!is_similar(dbs.subject, sent.subject) || !is_similar(dbs.object, sent.object)) {
                continue;
            }

            meinungen.push_back("Ich dachte immer, " + orig.subject + " " + orig.verb + " " + orig.object + "??");
            meinungen.push_back(orig.verb + " " + orig.subject + " nicht " + orig.object + "?");
            meinungen.push_back("Nein, " + orig.subject + " " + orig.verb + " " + orig.object + ".");
            meinungen.push_back(orig.subject + " " + orig.verb + " doch " + orig.object + ", oder?");
            meinungen.push_back(orig.subject + " " + orig.verb + " doch " + orig.object + ", nicht wahr?");
            meinungen.push_back("Ich ging immer davon aus, dass " + orig.subject + " " + orig.object + " " + orig.verb + "!");
            meinungen.push_back(orig.subject + " " + orig.verb + " " + orig.object + "! Stimmt das etwa nicht?");
            meinungen.push_back(orig.subject + " " + orig.verb + " " + orig.object + "! Das stimmt doch, oder?");
            meinungen.push_back(orig.verb + " " + orig.subject + " wirklich " + orig.object + "?");
            meinungen.push_back("Ich weiss nur, dass " + orig.subject + " " + orig.object + " " + orig.verb + ".");
            meinungen.push_back("Meine Berechnungen ergaben, dass " + orig.subject + " " + orig.object + " " + orig.verb + "!!");
            meinungen.push_back("Kann durchaus sein.");
            meinungen.push_back("Das glaube ich nicht.");
		}
	}

    for (int x = 0; x < meinungen.size(); x++) {
        cout << "- Meinung: " << meinungen[x] << endl;
    }

    if (meinungen.size() < 1) {
        return Answer("");
    }

    srand((unsigned) time(NULL));
	int random = rand() % meinungen.size();
	Answer answer = Answer(meinungen[random]);
	return answer;
}



/*
 * Antwortet auf eine Frage
 */
Answer JEliza::ask(string frage) {
    frage = ohne_muell(frage);
	frage = Util::umwandlung(frage);

	cout << "- Suche eine passende Antwort: " << endl;

    Answer ans;

    // Modul Empty
    ModQues_Empty mod_empty;
    ans = mod_empty.get();
    if (ans.size() > 0) {
        return ans;
    }

    // Modul Math
    ModQues_Math mod_math;
    ans = mod_math.get();
    if (ans.size() > 0) {
        return ans;
    }


    Answer logical_ans = answer_logical(frage);
    if (logical_ans.m_ans.size() > 2) {
        cout << "- Logische Antwort gefunden: " << logical_ans.m_ans << endl;
        return logical_ans;
    }

    cout << "- Keine logische Antwort gefunden, suche eine unlogische..." << endl;






/*
	long double best = -1;
	string allanswers = "";

	string reply = "";
	string second_reply = "";

	string last_answer = "";
	if (JEliza::m_jd.m_last_answers.size() > 0) {
		last_answer = JEliza::m_jd.m_last_answers[JEliza::m_jd.m_last_answers.size() - 1];
	}

*/

/*    ofstream jelizatraining("jeliza.training");
    for (int z = 0; z < m_sentenceCount; z++) {
		string sentence = (*JEliza::m_jd.m_sents)[z];
		sentence = Util::strip(sentence);

		jelizatraining << sentence << endl;
    }

    string fra;
    for (int x = 0; x < JEliza::m_jd.m_last_sentence_words.size(); x++) {
	    fra += JEliza::m_jd.m_last_sentence_words[x] + " ";
	    fra += JEliza::m_jd.m_last_sentence_words[x] + " ";
	}
    fra += frage;
    fra += frage;
    fra += frage;

    string buffer;
    string al_reply;

    Model *mod = new Model();
	mod->load_personality("");
	Dictionary *input_words = NULL;

	input_words = mod->make_greeting();
	al_reply = mod->generate_reply (input_words);
	Funcs::middle (al_reply);

	if (input_words != NULL) {
		delete input_words;
		input_words = NULL;
	}

    input_words = new Dictionary (fra);
    Dictionary *input_words_frage = new Dictionary (frage);

    if( input_words->size() != 0 ) {
        Funcs::upper (fra);
        Funcs::upper (frage);
		mod->learn (input_words_frage);
		al_reply = mod->generate_reply (input_words);
		Funcs::middle(al_reply);
		reply = al_reply;
    }
*/


    // ALter Algo

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
                points2 += sc.getPoints() * sc.getPoints();
			}
		}

        points2 = points2 / (woerter.size() * woerter2.size());

//		cout << points2 << " " << frage << " " << sentence << endl;

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

