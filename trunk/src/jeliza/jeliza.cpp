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

using namespace std;
using namespace jdb;

#include "modules/ques_empty.cpp"
#include "modules/ques_logical.cpp"
#include "modules/ques_math.cpp"
#include "modules/ques_similar.cpp"

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
void JEliza::saveSentence (answers anss) {
	vorbereite();

    for (answers::iterator it = anss.begin(); it != anss.end(); it++) {
        if (isQuestion(*it) != 0) {
            continue;
        }

        vector<string> temp;
        Util::split(*it, string(" "), temp);

        if (temp.size() < 3) {
            cerr << "- Satz hat zu wenig Woerter (< 10): " << *it << endl;
            return;
        }

        DBSentence dbs = jdb::toDBSentence (*it, *this, verbs::getVerbs());

        JEliza::m_jd.m_sents->push_back(dbs);

        saveDB("jeliza-standard.xml", *this, *JEliza::m_jd.m_sents);
    }
	vorbereite();
}

/*
 * Lernt einen Satz, dh. formt ihn um (ich->du) und ruft saveSentence() auf
 */
void JEliza::learn (string fra) {
	fra = Util::umwandlung(fra);
	answers x;
	x.push_back(fra);
	saveSentence (x);
}

/*
 * Ehemalige initialisierungsmethode von JEliza
 */
void JEliza::init () {
	cout << "init()" << endl;
	cout << "Initialisiert" << endl;
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

    (*JEliza::m_jd.m_sents) = jdb::parseJDB("jeliza-standard.xml");

	cout << "- Datenbank erfolgreich geladen!" << endl;
	cout << "- Vorbereitung abgeschlossen!" << endl << endl;
}



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
			for (unsigned int g = 0; g < woerter.size(); g++) {
				string wort = woerter[g];
				string wort_lower = Util::toLower(wort);
				StringCompare sc(wort, buffer);
				points2 += sc.getPoints();
//				if (wort == buffer) {
//                    cout << sc.getPoints() << " " << wort << " " << buffer << endl;
//				}

				if (sc.getPoints() > bestVerb) {
                    bestVerb = sc.getPoints();
                    verb = wort;
                    verb2 = buffer;

                    if (bestVerb > 75) {
                        break;
                    }
				}
			}

//			points2 = points2 / woerter.size();

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
		else if (k[0].size() > 1) {
		    vector<string> ret;
		    ret.push_back(k[0]);
		    ret.push_back(verb);
		    ret.push_back(verb2);
		    ret.push_back("");
		    return ret;
		}
		else if (k[1].size() > 1) {
		    vector<string> ret;
		    ret.push_back("");
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

	for (unsigned int x = 0; x < unuseful_words.size(); x++) {
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

    if (s1.size() < 1 && s2.size() > 0) {
        return false;
    }
    if (s2.size() < 1 && s1.size() > 0) {
        return false;
    }

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

Answer JEliza::answer_logical(string frage, string orig_fra) {
    frage = Util::strip(frage);
    cout << "- Suche nach einer logischen Antwort auf \"" << frage << "\"" << endl;

    DBSentence dbs = toDBSentence (frage, *this, verbs::getVerbs());
    if (dbs.verb.size() < 1) {
        cout << "- Kein Verb gefunden in: \"" << frage << "\"" << endl;
        cout << dbs.toXML() << endl;
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
    for (DB::iterator it = JEliza::m_jd.m_sents->begin(); it != JEliza::m_jd.m_sents->end(); it++) {
        DBSentence orig = *it;
		DBSentence sent = *it;

		if (sent.genSentences(true)[0].size() > 60) {
		    continue;
		}

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

        if ((dbs.subject == sent.subject || dbs.subject == sent.object)
                && (dbs.object == sent.object || dbs.object == sent.subject)) {
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

    for (unsigned int x = 0; x < meinungen.size(); x++) {
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

Answer JEliza::answer_logical_question_type_1 (string frage, string orig_fra) {
    frage = Util::strip(frage);
    cout << "- Suche nach einer logischen Antwort auf die Frage \"" << frage << "\"" << endl;

    DBSentence dbs = toDBSentence (frage, *this, verbs::getVerbs());
    DBSentence dbs_orig = toDBSentence (orig_fra, *this, verbs::getVerbs());
    if (dbs_orig.verb.size() < 1) {
        cout << "- Kein Verb gefunden in: \"" << frage << "\"" << endl;
        cout << dbs.toXML() << endl;
        return Answer("");
    }

    dbs.subject = ohne_muell(dbs.subject);
    dbs.subject = Util::toLower(dbs.subject);
    dbs.object = ohne_muell(dbs.object);
    dbs.object = Util::toLower(dbs.object);

    dbs.verb = ohne_muell(dbs.verb);
    dbs.verb = Util::toLower(dbs.verb);


    (*JELIZA_PROGRESS) = 26;
    if (dbs.object.size() > 0 && !Util::contains(Util::toLower(dbs_orig.verb), "bin")
            && (Util::contains(dbs.subject, "was") || Util::contains(dbs.subject, "wer"))) {
        string definition = search_in_wikipedia_with_newlines(dbs.object);
        if (definition.size() > 2) {
            return Answer(definition);
        }
    }

    if (Util::contains(Util::toLower(orig_fra), "bin") || Util::contains(Util::toLower(orig_fra), "bist")) {
        return Answer("");
    }

    cout << "- Verb ist " << dbs.verb << endl;

    dbs.print();
    dbs_orig.print();

    (*JELIZA_PROGRESS) = 46;

    if (dbs_orig.subject.size() < 1 || dbs_orig.object.size() < 1 || dbs_orig.verb.size() < 1) {
        cout << "- Unvollstaendiger Satzteil in: \"" << frage << "\"" << endl;
        return Answer("");
    }

    answers meinungen;

    string s;
    int ssize = JEliza::m_jd.m_sents->size();
    int x = 0;
    for (DB::iterator it = JEliza::m_jd.m_sents->begin(); it != JEliza::m_jd.m_sents->end(); it++) {
        DBSentence orig = *it;
		DBSentence sent = *it;

		x++;

		(*JELIZA_PROGRESS) += 8.0 / ssize * x;

		if (sent.genSentences(true)[0].size() > 60) {
		    continue;
		}

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

        cout << sent.genSentences(true)[0] << endl;

        if (!is_similar(dbs.subject, sent.subject) && !is_similar(dbs.object, sent.object)
                && !is_similar(dbs.subject, sent.object) && !is_similar(dbs.object, sent.subject)) {
            continue;
        }

        if (!is_similar(dbs.verb, sent.verb)) {
            continue;
        }

        answers anss = orig.genSentences(true);
        for (answers::iterator it = anss.begin(); it != anss.end(); it++) {
            meinungen.push_back(*it);
        }
	}

    for (unsigned int x = 0; x < meinungen.size(); x++) {
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

string JEliza::getGreeting () {
    DB db = jdb::parseJDB("jeliza-standard.xml");

    answers greetings;
    for (DB::iterator it = db.begin(); it != db.end(); it++) {
        if (Util::contains(it->category, "greeting")) {
            greetings.push_back(it->genSentences(true)[0]);
        }
    }

    srand((unsigned) time(NULL));
	int random = rand() % greetings.size();
	string answer = greetings[random];
	return answer;
}

unsigned int JEliza::isQuestion (string ques) {
    ques = Util::toLower(ques);
    if (Util::contains(ques, string("was"))) {
        return 1;
    }
    if (Util::contains(ques, string("wie"))) {
        return 1;
    }
    if (Util::contains(ques, string("wer"))) {
        return 1;
    }
    if (Util::contains(ques, string("wie"))) {
        return 1;
    }
    if (Util::contains(ques, string("wo"))) {
        return 1;
    }
    if (Util::contains(ques, string("wann"))) {
        return 1;
    }
    if (Util::contains(ques, string("warum"))) {
        return 1;
    }
    if (Util::contains(ques, string("wieso"))) {
        return 1;
    }
    if (Util::contains(ques, string("weshalb"))) {
        return 1;
    }

    if (Util::contains(ques, string("?"))) {
        return 2;
    }

    return 0;
}


/*
 * Antwortet auf eine Frage
 */
Answer JEliza::ask(string frage) {
    string orig_fra = frage;
    (*JELIZA_PROGRESS) = 2;
    frage = ohne_muell(frage);
    (*JELIZA_PROGRESS) = 3;
	frage = Util::umwandlung(frage);

	cout << "- Suche eine passende Antwort: " << endl;

    Answer ans;

    (*JELIZA_PROGRESS) = 5;

    // Modul Empty
    ModQues_Empty mod_empty(*this, *JEliza::m_jd.m_sents);
    ans = mod_empty.get(frage);
    if (ans.m_ans.size() > 0) {
        return ans;
    }

    (*JELIZA_PROGRESS) = 10;

    // Modul Math
    ModQues_Math mod_math(*this, *JEliza::m_jd.m_sents);
    ans = mod_math.get(frage);
    if (ans.m_ans.size() > 0) {
        return ans;
    }

    (*JELIZA_PROGRESS) = 15;

    // Modul Logical
    ModQues_Logical mod_logical(*this, *JEliza::m_jd.m_sents);
    ans = mod_logical.get(frage, orig_fra);
    if (ans.m_ans.size() > 0) {
        return ans;
    }

    (*JELIZA_PROGRESS) = 60;

    // Modul Similar
    ModQues_Similar mod_similar(*this, *JEliza::m_jd.m_sents);
    ans = mod_similar.get(frage);

    (*JELIZA_PROGRESS) = 95;

    return ans;
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

	for (unsigned int x = 0; x < ss.size(); x++) {
		string sym = ss[x];
		string temp = sym;

		for (unsigned int y = 0; y < digits.size(); y++) {
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

