#ifndef JELIZA_JDB
#define JELIZA_JDB 1

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

namespace jdb {

string toASCIIreally_2(string all) {
	string ascii(" \n\r!\"#$%&'()*+,-./0123456789:;<=>?    @ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_    `abcdefghijklmnopqrstuvwxyz{|}~");
//	string ascii = ;

	string allAscii = "";
	for (unsigned int x = 0; x < all.size(); x++) {
		char array[2];
		array[0] = all[x];
		array[1] = '\0';
		string y(array);
		if (Util::contains(ascii, y)) {
			allAscii += y;
		}
	}

	return allAscii;
}

string toASCII_2_2(string all) {
	all = Util::replace(all, "Ã¼", "ue");
	all = Util::replace(all, "Ã", "ss");
	all = Util::replace(all, "Ã¤", "ae");
	all = Util::replace(all, "ü", "ue");
	all = Util::replace(all, "ß", "ss");
	all = Util::replace(all, "ä", "ae");
	all = Util::replace(all, "ö", "oe");
	all = Util::replace(all, "Ü", "Ue");
	all = Util::replace(all, "Ä", "Ae");
	all = Util::replace(all, "Ö", "Oe");
	all = Util::replace(all, "Ã¢ÂÂ", "\"");
	all = Util::replace(all, "Ã¢ÂÂ", "\"");
	all = Util::replace(all, "&lt;/br&gt;", " ");

    all = toASCIIreally_2(all);

	return all;
}


void jdb::DBSentence::print() {
    cout << "Subject:   " << subject << endl;
    cout << "Verb:      " << verb << endl;
    cout << "Object:    " << object << endl;
    cout << "Prefix:    " << prefix << endl;
    cout << "Suffix:    " << suffix << endl;
    cout << "Feeling:   " << feeling << endl;
    cout << "Priority:  " << priority << endl;
    cout << "Category:  " << category << endl;
    cout << endl;
}

string jdb::DBSentence::toXML() {
    string temp = "";

    stringstream sst;
    sst << priority;
    string tempprio;
    sst >> tempprio;

    string all = "";

    all += " <fact>\n";
    all += printPart ("prefix>   ", temp, prefix);
    all += printPart ("subject>  ", temp, subject);
    all += printPart ("verb>     ", temp, verb);
    all += printPart ("object>   ", temp, object);
    all += printPart ("suffix>   ", temp, suffix);
    all += printPart ("feeling>  ", temp, feeling);
    all += printPart ("priority> ", temp, tempprio);
    all += printPart ("category> ", temp, category);
    all += " </fact>\n\n";

    return all;
}

void jdb::DBSentence::toXMLPrint() {
    string temp = "";

    stringstream sst;
    sst << priority;
    string tempprio;
    sst >> tempprio;

    cout << " <fact>" << endl;
    printPart ("prefix>   ", temp, prefix);
    printPart ("subject>  ", temp, subject);
    printPart ("verb>     ", temp, verb);
    printPart ("object>   ", temp, object);
    printPart ("suffix>   ", temp, suffix);
    printPart ("feeling>  ", temp, feeling);
    printPart ("priority> ", temp, tempprio);
    printPart ("category> ", temp, category);

    cout << " </fact>" << endl;
    cout << endl;
}

string jdb::DBSentence::printPart (string str, string temp, string wert) {
//    cout << "  <" << str << wert << "  " << "</" << str << endl;
    return "  <" + str + wert + "  " + "</" + str + "\n";
}

void jdb::DBSentence::strip() {
    prefix = Util::strip(prefix);
    subject = Util::strip(subject);
    verb = Util::strip(verb);
    object = Util::strip(object);
    suffix = Util::strip(suffix);
    feeling = Util::strip(feeling);
    category = Util::strip(category);
}

answers jdb::DBSentence::genSentences(bool withFix = false) {
    strip();

    answers ans;

    if (subject.size() > 2 && verb.size() > 2 && object.size() > 1) {
        ans.push_back(subject + " " + verb + " " + object);
    } else if (withFix) {
        ans.push_back(prefix + " " + subject + " " + verb + " " + object + " " + suffix);
    }
    ans.push_back(prefix + " " + subject + " " + verb + " " + object);
    ans.push_back(subject + " " + verb + " " + object + " " + suffix);

    return ans;
}


jdb::DB parseJDB (string file) {
    ifstream i(file.c_str());

    string buffer;
    string all;
    while (i) {
        getline (i, buffer);

        if (Util::contains(buffer, "<?xml")) {
            continue;
        }

        all += buffer + "\n";
    }

//    cout << "!" << endl;
    /*all = Util::replace(all, string("\r"), string(""));
    all = Util::replace_save(all, string("<"), string("\n<"));
    all = Util::replace_save(all, string(">"), string(">\n"));
    all = Util::replace_save(all, string("\n\n"), string("\n"));
    cout << "?" << endl;*/

    vector<string> lines;
    Util::split(all, string("\n\r<>"), lines);


    jdb::DB sents;

    for (vector<string>::iterator it = lines.begin(); it != lines.end(); it++) {
        *it = Util::strip(*it);
//        cout << ",";
    }
    cout << endl;

    jdb::DBSentence act_sent;
    for (vector<string>::iterator it = lines.begin(); it != lines.end(); it++) {
        string line = *it;

        if (line == "priority") {
            stringstream sst;
            sst << *(it + 1);
            int t;
            sst >> t;
            act_sent.priority = t;
            it += 2;
        }

        else if (line == "subject") {
            act_sent.subject = *(it + 1);
            it += 2;
        }

        else if (line == "verb") {
            act_sent.verb = *(it + 1);
            it += 2;
        }

        else if (line == "object") {
            act_sent.object = *(it + 1);
            it += 2;
        }

        else if (line == "feeling") {
            act_sent.feeling = *(it + 1);
            it += 2;
        }

        else if (line == "prefix") {
            act_sent.prefix = *(it + 1);
            it += 2;
        }

        else if (line == "suffix") {
            act_sent.suffix = *(it + 1);
            it += 2;
        }

        else if (line == "category") {
            act_sent.category = *(it + 1);
            it += 2;
        }

        else if (line == "/fact") {
            act_sent.strip();
            sents.push_back(act_sent);
            act_sent = jdb::DBSentence();
            cout << ".";
        }
    }
    cout << endl << endl;

    return sents;
}

jdb::DBSentence toDBSentence (string buffer, JEliza& jel, vector<string> verbs) {
    vector<string> parts = jel.trenne_SubVerbObj(buffer, verbs);

    jdb::DBSentence act_sent;

    if (parts.size() > 1) {
        cout << "parts.size() > 1" << parts[0] << "|"  << parts[1] << "|"  << parts[2] << "|"  << parts[2] << endl;
        string prefix = "";
        if (Util::contains(parts[0], ",")) {
            int n = parts[0].find(",");
            prefix = parts[0].substr(0, n);
            parts[0] = parts[0].substr(n+1, parts[0].size());
        }
        string suffix = "";
        if (Util::contains(parts[3], ",")) {
            int n = parts[3].find(",");
            suffix = parts[3].substr(n+1, parts[3].size());
            parts[3] = parts[3].substr(0, n);
        }
        act_sent.subject = Util::strip(parts[0]);
        act_sent.verb = Util::strip(parts[1]);
        act_sent.object = Util::strip(parts[3]);
        act_sent.prefix = Util::strip(prefix);
        act_sent.suffix = Util::strip(suffix);
    }
    else {
        cout << "parts.size() == 0" << endl;
        string prefix = buffer;
        string suffix = "";
        if (Util::contains(buffer, ",")) {
            int n = buffer.find(",");
            prefix = buffer.substr(0, n);
            suffix = buffer.substr(n+1, buffer.size());
        }
        act_sent.prefix = Util::strip(prefix);
        act_sent.suffix = Util::strip(suffix);
    }
    return act_sent;
}

void convertOldDB() {
    ifstream i("JEliza-online.txt");

    JEliza jel(1);

    vector<string> verbs = verbs::getVerbs();
	string buffer;

    cout << "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" << endl << endl << "<jdb>" << endl;

    while (i) {
        getline (i, buffer);
        buffer = Util::strip(buffer);

        if (buffer.size() < 2) {
            continue;
        }

        toDBSentence(toASCII_2_2(buffer), jel, verbs).toXML();
    }

    cout << "</jdb>" << endl;
}

void saveDB(string file, JEliza& jel, jdb::DB db) {
    ofstream o(file.c_str());

    vector<string> verbs = verbs::getVerbs();
	string buffer;

    o << "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" << endl << endl << "<jdb>" << endl;

    o << " <feelings>" << endl
      << "  <feeling>happy</happy>" << endl
      << "  <feeling>angry</angry>" << endl
      << "  <feeling>bored</bored>" << endl
      << "  <feeling>scared</scared>" << endl
      << "  <feeling>curiously</curiously>" << endl
      << "  <feeling>normal</normal>" << endl
      << " </feelings>" << endl
      << endl;


    for (DB::iterator it = db.begin(); it != db.end(); it++) {
        o << toASCII_2_2((*it).toXML()) << endl;
    }

    o << "</jdb>" << endl;
}

}

#endif
