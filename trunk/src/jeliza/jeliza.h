#ifndef JELIZA_H
#define JELIZA_H 1

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



using namespace std;

namespace verbs {

vector<string> getVerbs();

}

class Answer {
public:
    string m_ans;

    Answer (string ans)
    : m_ans(ans)
    {
    }

    Answer ()
    : m_ans("")
    {
    }
};


class Question {
public:
    string m_ques;

    Question (string ques)
    : m_ques(ques)
    {
    }

    Question ()
    : m_ques("")
    {
    }
};

class LearnableSentence {
public:
    string m_ques;

    LearnableSentence (string ques)
    : m_ques(ques)
    {
    }

    LearnableSentence ()
    : m_ques("")
    {
    }
};


namespace jdb {
    typedef vector<string> answers;

    class DBSentence {
    public:
        string subject;
        string verb;
        string object;
        string prefix;
        string suffix;
        string feeling;
        string category;
        unsigned int priority;

        DBSentence ()
        : subject(string("")), verb(string("")), object(string("")), prefix(string("")), suffix(string("")),
          feeling(string("normal")), category("normal"), priority(50)
        {
        }

        void print();
        string toXML();
        void toXMLPrint();
        string printPart (string str, string temp, string wert);
        void strip();
        answers genSentences(bool);
    };

    typedef vector<jdb::DBSentence> DB;

    jdb::DB parseJDB (string file);
}


class JElizaData {
public:
	auto_ptr<jdb::DB> m_sents;
	bool m_sents_is;
	bool m_SVs_is;
	bool m_VOs_is;
	bool m_SVs_words_is;
	bool m_VOs_words_is;
	bool m_initOk;
	bool m_destructorOk;
	int m_countAbbau;
	vector<string> m_last_questions;
	vector<string> m_last_answers;
	vector<string> m_last_answers_second;
	vector<string> m_last_sentence_words;

	JElizaData()
	: m_sents(new jdb::DB()),
	  m_sents_is(true),
	  m_SVs_is(true),
	  m_VOs_is(true),
	  m_SVs_words_is(true),
	  m_VOs_words_is(true),
	  m_initOk(true),
	  m_destructorOk(false),
	  m_countAbbau(0),
	  m_last_questions(vector<string>()),
	  m_last_answers(vector<string>()),
	  m_last_answers_second(vector<string>()),
	  m_last_sentence_words(vector<string>())
	{
//		cout << "JElizaData" << " initialisiert!" << endl;
	}

	~JElizaData()
	{
//		cout << "JElizaData" << " Destruktor... (OK?=" << m_initOk << ")" << endl;
		abbau();
	}

	void abbau() {
		m_countAbbau++;
//		cout << "Abbau Beginn No" << m_countAbbau << endl;
		if (m_initOk && !m_destructorOk) {
//			cout << "JElizaData wird zerstoert:" << endl;
			m_sents_is = false;
			m_SVs_is = false;
			m_VOs_is = false;
			m_SVs_words_is = false;
			m_VOs_words_is = false;
//			cout << "JElizaData" << " zerstoert" << endl;
		} else {
//			cout << "Komischer Destruktoraufruf" << endl;
		}
		m_initOk = false;
		m_destructorOk = true;
//		cout << "Abbau Ende" << endl;
	}
};



/*
 * The JEliza class
 *
 * JEliza is a conversation simulator.
 *
 * This class is the main class of jeliza. To use it, you have to create an instance of
 * "JEliza".
 */
class JEliza {
public:
	int m_sentenceCount;
	static JElizaData m_jd;
	bool m_schonVorbereitet;
	Answer m_aktuell_answer;

	JEliza()
	: m_sentenceCount(0), m_schonVorbereitet(false)
	{
	}

	JEliza(int dummy)
	: m_sentenceCount(0), m_schonVorbereitet(false)
	{
	}

	~JEliza() {
	}

	string searchConfigFile();
	void saveSentence (jdb::answers anss);
	void learn (string fra);
	void init ();
	void vorbereiteSentence (string sent, string art);
	void vorbereite ();
//	bool generiere (string sent);
//	void generiereSentence (string& bestStr, vector<string>& ss, string& sFrageZeichen, string& last);
//	void SentenceToSubVerbObj (string s, vector<string> verbs, ofstream& o1, ofstream& o2);
	vector<string> trenne_SubVerbObj (string s, vector<string> verbs);
	string ohne_muell (string frage);
	Answer answer_logical (string frage, string);
	Answer answer_logical_question_type_1 (string, string);
	unsigned int isQuestion (string ques);
	Answer ask (string frage);
	double rechne (string s);
};

//JElizaData JEliza::m_jd = JElizaData();

/*
 * Die << Operatoren
 */
JEliza& operator<< (JEliza& jel, const Question& fra);
JEliza& operator<< (JEliza& jel, const LearnableSentence& fra);

/*
 * Die >> Operatoren
 */
JEliza& operator>> (JEliza& jel, string& ans);

namespace Util {
    void split (string& text, string separators, vector<string>& words);
    void SplitString (string text, string separators, vector<string>& words, bool includeEmpties);
    string toLower (string text);
    string toUpper (string text);
    string toLower_const (const string text);
    void trim(string& str);
    string strip (string text);
    string replace (string& in, const string rep, const string wit);
    string replace_save (string in, const string rep, const string wit);
	string replace_nocase (string& in, string rep, const string wit);
	bool contains (string in, string rep);
	string tausche (string str, string s1, string s2);
	string umwandlung (string str);
    int max (int a, int b);
    int min (int a, int b);
}




class StringCompare {
	long double points;
	int x;
	long double acc;
	long double accBest;
	int y;
	int s1_size;
	int s2_size;
	long double res;

public:
	StringCompare (string s1, string s2)
	: points((compare(s1, s2) + compare(s2, s1)) / 2)
	{
	}

	long double getPoints();
	long double compare (string s1, string s2);
};

/*
 * Die Downloadfunktion aus socketload.cpp
 */
string download (string);
string download_with_pbar (string);

namespace jdb {
    jdb::DBSentence toDBSentence (string buffer, JEliza& jel, vector<string> verbs);
    void saveDB(string file, JEliza& jel, jdb::DB db);
}

string search_in_wikipedia (string);
string search_in_wikipedia_with_newlines (string);

extern double* JELIZA_PROGRESS;

#endif

