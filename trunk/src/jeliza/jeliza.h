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
 
#include <iostream>
#include <fstream>
#include <string>
#include <map>

#include <string>
#include <list>

#include <time.h>
#include <vector>

using namespace std;


class JElizaData {
public:
	auto_ptr<vector<string> > m_sents;
	auto_ptr<vector<string> > m_SVs;
	auto_ptr<vector<string> > m_VOs;
	auto_ptr<vector<vector<string> > > m_SVs_words;
	auto_ptr<vector<vector<string> > > m_VOs_words;
	auto_ptr<vector<string> > m_sent_word;
	auto_ptr<vector<string> > m_sent_sent;
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
	
	JElizaData()
	: m_sents(new vector<string>()),
	  m_SVs(new vector<string>()),
	  m_VOs(new vector<string>()),
	  m_SVs_words(new vector<vector<string> >()),
	  m_VOs_words(new vector<vector<string> >()),
	  m_sent_word(new vector<string>()),
	  m_sent_sent(new vector<string>()),
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
	  m_last_answers_second(vector<string>())
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
	string m_file;
	static JElizaData m_jd;
	bool m_schonVorbereitet;
    
	JEliza() 
	: m_sentenceCount(0), m_file(searchConfigFile()), m_schonVorbereitet(false)
	{
		cout << "-> config file: " << m_file << endl;
		cout << endl;
	}
    
	JEliza(int dummy) 
	: m_sentenceCount(0), m_file(searchConfigFile()), m_schonVorbereitet(false)
	{
	}
	
	~JEliza() {
	}
	
	string searchConfigFile();
	void saveSentence (string savetype, string original, string newstring);
	void learn (string orig, string fra);
	void init ();
	void vorbereiteSentence(string sent, string art);
	void vorbereite();
	bool generiere(string sent);
	void vorbereiteSent(string bestStr);
	void generiereSentence(string& bestStr, vector<string>& ss, string& sFrageZeichen, string& last);
	void SentenceToSubVerbObj(string s, vector<string> verbs, ofstream& o1, ofstream& o2);
	string ask(string frage);
	double rechne(string s);
};
	
//JElizaData JEliza::m_jd = JElizaData();



#endif

