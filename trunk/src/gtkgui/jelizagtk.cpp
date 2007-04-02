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
#include <queue>

#include <string>
#include <list>

#include <time.h>
#include <vector>
#include <unistd.h>
#include <getopt.h>

#include "../jeliza/socketload.cpp"
#include "jeliza.h"
#include "../jeliza/util.cpp"
#include "../jeliza/arrays.cpp"

#include <gtkmm.h>
#include <libglademm.h>

#include <glib/gthread.h>
#include <glibmm/thread.h>
#include <glibmm.h>

using namespace std;
using namespace Gtk;

int main(int argc, char *argv[]);
int main_2(int argc, char *argv[]);

class Request {
public:

    Question m_ques;
    LearnableSentence m_learn;
    Gtk::Entry* entry;
    Gtk::TextView* textview;
    Glib::RefPtr<Gtk::TextBuffer::Tag> refTagMatch;
    string m_ans;
    string todo;

    Request()
    : m_ans(string("")), todo("")
    {
    }
};

string toASCIIreally(string all);
string toASCII_2(string all);
string toASCII(string all);


class MainWindow : public Gtk::Window {
public:
    //zugriff auf glade-datei
    Glib::RefPtr<Gnome::Glade::Xml> &refXml;

	MainWindow(GtkWindow* base, Glib::RefPtr<Gnome::Glade::Xml> &refXml);
	void thread_worker();
	void answer();
	void launch_threads();
};

int main(int argc, char *argv[]) {
    g_thread_init(NULL);
    if (!Glib::thread_supported()) {
        Glib::thread_init();
    }

    main_2(argc, argv);
}


auto_ptr<JEliza> global_jeliza(new JEliza());
bool vorbereitet = false;

Glib::Thread* jeliza_thread;
Request jeliza_request;
Request temp_req;


Glib::StaticMutex mutex = GLIBMM_STATIC_MUTEX_INIT;
Glib::Dispatcher* jeliza_dispatcher;
//unsigned int verarbeitete_requests = 0;

string toASCIIreally(string all);

string toASCII(string all) {
	Glib::ustring utf(Glib::convert(all, "UTF-8", "ISO-8859-1"));
	return utf;
}

string toASCII_2(string all) {
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

    all = toASCIIreally(all);

	return all;
}

string toASCIIreally(string all) {
	Glib::ustring utf(Glib::convert(" \n\r!\"#$%&'()*+,-./0123456789:;<=>?    @ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_    `abcdefghijklmnopqrstuvwxyz{|}~", "UTF-8", "ISO-8859-1"));
	string ascii(utf);
//	string ascii = ;

	string allAscii = "";
	for (int x = 0; x < all.size(); x++) {
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

string wikipedia (string wort, bool rec = false) {
    wort = Util::replace(wort, string(" "), string("_"));

    string url = "http://de.wikipedia.org/w/index.php?title=" + wort + "&action=edit";
    cout << "Url: \"" << url << "\"" << endl;
    download(url);

    ifstream ifstr("download.php");
    string all;
    string temp;
    while (ifstr) {
		getline(ifstr, temp);
//		temp = Util::strip(temp);

		all += toASCII_2(temp);
		all += "\n";
    }

    vector<string> lines;
    Util::split(all, string("\n"), lines);

    bool inRichtigemBereich = false;
    int inKlammer = 0;
    string satz = "";
    for (int x = 0; x < lines.size(); x++) {
        string line = lines[x];
        line = Util::strip(line);

        if (Util::contains(line, "cols='80'")) {
//            cout << "inRichtigemBereich = true; " << line << endl;
            inRichtigemBereich = true;
            line = line.substr(line.find(">") + 1, line.size() - line.find(">") - 1);
        }

        if (inRichtigemBereich && Util::contains(line, string("{{"))) {
            inKlammer++;
        }

        if (inRichtigemBereich && Util::contains(line, string("{|"))) {
            inKlammer++;
        }

        if (inRichtigemBereich && inKlammer == 0 && line.size() > 0) {
//            cout << "cout << satz << endl; " << line << endl;
            satz = "";

            line = Util::replace(line, string("'"), string(""));

            int inKlammer2 = 0;
            for (int y = 0; y < line.size(); y++) {
                char ch = line[y];

                if (ch == '(') {
                    inKlammer2++;
                }

                if (inKlammer2 == 0) {
                    satz += ch;
                }

                if (ch == ')') {
                    inKlammer2--;
                }
            }

            inKlammer2 = 0;
            string tempStr = "";
            string satz2 = satz + " ";
            satz = "";
            for (int y = 0; y < satz2.size(); y++) {
                char ch = satz2[y];

                if (ch == '[' && satz2[y+1] == '[') {
                    inKlammer2++;
                    y++;
                }

                if (inKlammer2 == 0) {
                    satz += ch;
                }

                if (ch == '|') {
                    tempStr = "";
                }

                if (inKlammer2 > 0 && ch != '|' && ch != '[' && ch != ']') {
                    tempStr += ch;
                }

                if (ch == ']' && satz2[y+1] == ']') {
                    inKlammer2--;
                    satz += tempStr;
                    tempStr = "";
                    y++;
                }
            }

            satz = "-" + Util::replace(satz, string("  "), string(" ")) + "-";
            satz = Util::replace(satz, string("."), string("ekdnkecolesl"));
            satz = Util::replace(satz, string("ekdnkecolesl"), string("-.-"));
            if (Util::contains(satz, "-</textarea> -")) {
                satz = "";
                break;
            }

            string satzlower = Util::toLower(satz);
            if (Util::contains(satzlower, "#redirect") && !rec) {
                satz = Util::replace_nocase(satz, string("#redirect"), string(""));
                satz = Util::strip(satz);
                satz = Util::replace(satz, string("-"), string(""));
                satz = Util::replace(satz, string("  "), string(" "));
                satz = Util::strip(satz);
                return wikipedia(satz, true);
            }
            else if (Util::contains(satzlower, "#redirect") && !rec) {
                return "";
            }

            vector<string> temp;
            Util::split(satz, string("."), temp);

            vector<string> temp2;
            Util::split(satz, string(" "), temp2);

//            cout << temp[0] << endl;

            if ((temp[0].size() < 15 || Util::contains(temp[0], string("bzw-"))) && temp.size() > 1) {
                satz = Util::strip(temp[0]) + ". " + Util::strip(temp[1]) + ".";
            } else if (temp[0].size() < 12) {
                satz = "";
            } else if (temp2.size() < 7) {
                satz = "";
            } else if (Util::contains(satz, string(":"))) {
                satz = "";
            } else {
                satz = Util::strip(temp[0]) + ".";
            }

            satz = Util::replace(satz, string("-"), string(""));
            satz = Util::replace(satz, string("  "), string(" "));

            break;
        }

        if (inRichtigemBereich && inKlammer > 0 && Util::contains(line, string("}}"))) {
            inKlammer--;
        }

        if (inRichtigemBereich && inKlammer > 0 && Util::contains(line, string("|}"))) {
            inKlammer--;
        }
    }

    return satz;
}

string search_in_wikipedia(string wort) {
    wort = Util::strip(wort);
    wort = Util::toLower(wort);
    string orig_wort = wort;
    string firstchar = string(Util::toUpper(wort.substr(0, 1)));
    wort = wort.substr(1, wort.size());
    wort = firstchar + wort;
    wort = Util::strip(wort);

    string satz;

    if (wort.size() < 3) {
        return "";
    }

    cout << "- Wort zum Nachschlagen: " << wort << endl;
    satz = wikipedia(wort);
    if (satz.size() < 1) {
        cout << "- Wort zum Nachschlagen: " << orig_wort << endl;
        satz = wikipedia(orig_wort);
        if (satz.size() < 1) {
            cout << "- Wort zum Nachschlagen: " << Util::toUpper(orig_wort) << endl;
            satz = wikipedia(Util::toUpper(orig_wort));
        }
    }
    return satz;
}


void durchsuche_nach_unbekanntem (string all) {
    cout << "- Entferne Muell..." << endl;
    all = Util::replace(all, string("."), string(" "));
    all = Util::replace(all, string(","), string(" "));
    all = Util::replace(all, string(";"), string(" "));
    all = Util::replace(all, string("-"), string(" "));
    all = Util::replace(all, string("+"), string(" "));
    all = Util::replace(all, string("-"), string(" "));
    all = Util::replace(all, string(")"), string(" "));
    all = Util::replace(all, string("("), string(" "));
    all = Util::replace(all, string("?"), string(" "));
    all = Util::replace(all, string("!"), string(" "));
    all = Util::replace(all, string("  "), string(" "));
//        all = Util::toLower(all);

    vector<string> woerter;
    Util::split(all, string(" "), woerter);

    cout << "- Lade alle Woerter die nicht in der Wikipedia sind..." << endl;
    ifstream in4("not_in_wikipedia.txt");
	vector<string> not_in_wikipedia;
	string buffer;
    while (in4) {
        getline(in4, buffer);
        buffer = Util::strip(buffer);
        if (buffer.size() < 1) {
            continue;
        }
        buffer = Util::toLower(buffer);
        not_in_wikipedia.push_back(buffer);
    }
    in4.close();

    cout << "- Lade alle Woerter die schon aus der Wikipedia geholt wurden..." << endl;
    ifstream in5("schon_aus_wikipedia.txt");
	vector<string> schon_aus_wikipedia;
    while (in5) {
        getline(in5, buffer);
        buffer = Util::strip(buffer);
        if (buffer.size() < 1) {
            continue;
        }
        buffer = Util::toLower(buffer);
        schon_aus_wikipedia.push_back(buffer);
    }
    in5.close();

    cout << "- Suche in Wikipedia nach unbekannten Woertern..." << endl;
    for (vector<string>::iterator it = woerter.begin(); it != woerter.end(); it++) {
        if ((*it) == Util::toLower((*it)) || (*it) == Util::toUpper((*it))) {
            continue;
        }

        bool schon = false;
        for (vector<string>::iterator ite = not_in_wikipedia.begin(); ite != not_in_wikipedia.end(); ite++) {
            if (Util::toLower(*it) == Util::toLower(*ite)) {
                schon = true;
                break;
            }
        }
        if (!schon) {
            for (vector<string>::iterator ite = schon_aus_wikipedia.begin(); ite != schon_aus_wikipedia.end(); ite++) {
                if (Util::toLower(*it) == Util::toLower(*ite)) {
                    schon = true;
                    break;
                }
            }
        }
        if (!schon) {
            cout << "- Suche nach einer Definition fuer \"" << (*it) << "\" in der Wikipedia" << endl;
            string definition = search_in_wikipedia((*it));
            if (definition.size() < 2) {
                ofstream of("not_in_wikipedia.txt", ios::app | ios::ate);
                of << (*it) << endl;
                of.close();
                not_in_wikipedia.push_back((*it));
            } else {
                ofstream of("schon_aus_wikipedia.txt", ios::app | ios::ate);
                of << (*it) << endl;
                of.close();
                schon_aus_wikipedia.push_back((*it));
                JEliza jel(1);
                jel.saveSentence("file", definition, definition);
            }
//            } else if ( (*it) != Util::toLower((*it)) && (*it) != Util::toUpper((*it))) {
//                ofstream of("not_in_wikipedia.txt", ios::app | ios::ate);
//                of << (*it) << endl;
//                of.close();
//                not_in_wikipedia.push_back((*it));
        }
    }
}



class Data2 {
public:
	Gtk::FileChooserDialog* dlg;
	MainWindow* win;
};

void on_open_activate(Data2& data) {
	Gtk::FileChooserDialog dialog("Wissen importieren", Gtk::FILE_CHOOSER_ACTION_OPEN);
	dialog.set_transient_for(*data.win);

	dialog.add_button(Gtk::Stock::CANCEL, Gtk::RESPONSE_CANCEL);
	dialog.add_button(Gtk::Stock::OPEN, Gtk::RESPONSE_OK);


	Gtk::FileFilter filter_any;
	filter_any.set_name("Alle Dateien");
	filter_any.add_pattern("*");
	dialog.add_filter(filter_any);
	data.dlg = &dialog;

	JEliza jeliza(1);
	jeliza.init();
	string buffer;
	ofstream o("JEliza.txt", ios::app | ios::ate);

	std::string filename;
	if (data.dlg->run() == Gtk::RESPONSE_OK) { // RESPONSE_ACCEPT
		filename = data.dlg->get_filename();
		Gtk::MessageDialog dia3(*data.win, Glib::ustring(filename + "\nWird nun in die Datenbank geladen"));
		dia3.run();

		ifstream in(filename.c_str());

		while (in) {
			getline(in, buffer);
			o << Util::strip(buffer) << "\n" << endl;
		}

		in.close();
		Gtk::MessageDialog dia4(*data.win, Glib::ustring(filename + ":\nErfolgreich geladen!"));
		dia4.run();
	}

	o.close();

    {
        Glib::Mutex::Lock lock(mutex);
        Request r;
        r.todo = "vorbereiten";
        jeliza_request = r;
    }
}

void on_save_activate(Data2& data) {
	Gtk::FileChooserDialog dialog("Wissen exportieren", Gtk::FILE_CHOOSER_ACTION_SAVE);
	dialog.set_transient_for(*data.win);

	dialog.add_button(Gtk::Stock::CANCEL, Gtk::RESPONSE_CANCEL);
	dialog.add_button(Gtk::Stock::OPEN, Gtk::RESPONSE_OK);


	Gtk::FileFilter filter_any;
	filter_any.set_name("Alle Dateien");
	filter_any.add_pattern("*");
	dialog.add_filter(filter_any);
	data.dlg = &dialog;

	JEliza jeliza(1);
	jeliza.init();
	string buffer;


	std::string filename;
	if (data.dlg->run() == Gtk::RESPONSE_OK) { // RESPONSE_ACCEPT
		filename = data.dlg->get_filename();
		Gtk::MessageDialog dia3(*data.win, Glib::ustring("Die Datenbank wird nun nach \n" + filename + "\nexportiert"));
		dia3.run();

		ifstream in("JEliza.txt");
		ofstream o(filename.c_str());

		while (in) {
			getline(in, buffer);
			o << Util::strip(buffer) << "\n" << endl;
		}

		in.close();
		o.close();
		Gtk::MessageDialog dia4(*data.win, Glib::ustring(filename + ":\nErfolgreich exportiert!"));
		dia4.run();
	}

//	o.close();
}

void on_close_activate(Data2& data) {
	data.win->hide();
}

void on_cut_activate() {
}

void on_copy_activate() {
}

void on_paste_activate() {
}

void on_delete_activate() {
}

class Data3 {
public:
	Gtk::Dialog* dialog;
	MainWindow* win;
	Gtk::TextView* tv;
	Gtk::TextView* talk;
	Glib::RefPtr<Gtk::TextBuffer> buf;
};

class Data5 {
public:
	Gtk::Window* win;
};

class Data4 {
public:
	Gtk::Window* win;
	Gtk::TextView* tv;
	Gtk::TextView* textview;
	Gtk::Entry* entry;
	Glib::RefPtr<Gtk::TextBuffer::Tag> refTagMatch;
//	Glib::RefPtr<Gtk::TextBuffer> buf;
//	auto_ptr<JEliza> jeliza;
//
//	Data4()
//	: jeliza(new JEliza())
//	{
//	}
//
//	void init() {
//		jeliza->init();
//	}
};

void on_einstellungen_activate(Data3& data) {
	ifstream in("JEliza.txt");
	string buffer;
	string all = "";
	string sFrageZeichen("?");
	while (in) {
		getline(in, buffer);
		buffer = Util::strip(buffer);
		buffer = Util::replace(buffer, string("\r"), string(""));
		buffer = Util::replace(buffer, string("\n"), string(""));
		all += buffer;
		all += "\n";
	}
	in.close();
	all = toASCII(all);

	data.buf = data.tv->get_buffer();
	data.buf->set_text(all);
	data.tv->set_buffer(data.buf);

	data.dialog->show();
	data.dialog->show_all_children();

//	if (data.dialog->run() == Gtk::RESPONSE_OK) {
//	}
}

void on_okbutton1_clicked(Data3& data) {
	data.dialog->hide();

	data.buf = data.tv->get_buffer();

	ofstream o("JEliza.txt");

	string all;
	Gtk::TextBuffer::iterator iter = data.buf->begin();
	while (iter != data.buf->end()) {
		all += *iter;
		++iter;
	}

	all = toASCII(all);
	o << all << endl;
	o.close();

    {
        Glib::Mutex::Lock lock(mutex);
        Request r;
        r.todo = "vorbereiten";
        jeliza_request = r;
    }
}

void on_cancelbutton1_clicked(Data3& data) {
	data.dialog->hide();
}

void on_info_activate(Data5& data) {
	data.win->show();
	data.win->show_all_children();
}

void on_about_ok_clicked(Data5& data) {
	data.win->hide();
}

void on_new_database_activate(Data3& data) {
	Gtk::MessageDialog dia3(*data.win, Glib::ustring(toASCII("Soll die aktuelle Datenbank wirklich gelöscht und eine neue angelegt werden?")), true, Gtk::MESSAGE_QUESTION, Gtk::BUTTONS_YES_NO, true);

	if (dia3.run() == Gtk::RESPONSE_YES) {
		ofstream o("JEliza.txt");
		o << "" << endl;
		o.close();
	}

    {
        Glib::Mutex::Lock lock(mutex);
        Request r;
        r.todo = "vorbereiten";
        jeliza_request = r;
    }
}


void on_new_talk_activate(Data3& data) {
	data.talk->get_buffer()->set_text("");
}

class Data1 {
public:
	Gtk::Entry* entry;
	Gtk::TextView* textview;
	MainWindow* win;
	Glib::RefPtr<Gtk::TextBuffer::Tag> refTagMatch;
//	auto_ptr<JEliza> jeliza;
//
//	Data1()
//	: jeliza(new JEliza())
//	{
//	}
//
//	void init() {
//		jeliza->init();
//	}
};

void on_Ask_clicked(Data1& data) {
	Glib::ustring msg;

	if(!data.entry) {
		Gtk::MessageDialog dia4(*data.win, Glib::ustring("JEliza Error 1"));
		dia4.run();
	}
	string fra = data.entry->get_text();
	msg = toASCII(fra);
	string bestReply;

	Request r;
	r.m_ques = Question(fra);
	r.m_learn = LearnableSentence(fra);
	r.entry = data.entry;
	r.refTagMatch = data.refTagMatch;
	r.textview = data.textview;
    r.todo = "ask";

    {
        Glib::Mutex::Lock lock(mutex);
        jeliza_request = r;
    }

	/*(*global_jeliza) << Question(fra);
	(*global_jeliza) >> bestReply;

	bestReply = toASCII(bestReply);
	msg = Glib::ustring(bestReply);
	(*global_jeliza) << LearnableSentence(fra);

	data.textview->get_buffer()->set_text(data.textview->get_buffer()->get_text() + Glib::ustring("Mensch: " + fra) + Glib::ustring("\nJEliza: ") + msg + Glib::ustring("\n"));
	data.entry->set_text("");

	data.textview->get_buffer()->apply_tag(data.refTagMatch, data.textview->get_buffer()->begin(), data.textview->get_buffer()->end());
	TextBuffer::iterator it = data.textview->get_buffer()->end();*/

	//cout << "asked1" << endl;
}

void fs_on_Ask_clicked(Data4& data) {
	Glib::ustring msg;

	if(!data.entry) {
		Gtk::MessageDialog dia4(*data.win, Glib::ustring("JEliza Error 2"));
		dia4.run();
	}
	string fra = data.entry->get_text();
	msg = toASCII(fra);
	string bestReply;

	Request r;
	r.m_ques = Question(fra);
	r.m_learn = LearnableSentence(fra);
	r.entry = data.entry;
	r.refTagMatch = data.refTagMatch;
	r.textview = data.textview;
    r.todo = "ask";

    {
        Glib::Mutex::Lock lock(mutex);
        jeliza_request = r;
    }



	/*durchsuche_nach_unbekanntem (fra);

	(*global_jeliza) << Question(fra);
	(*global_jeliza) >> bestReply;

	bestReply = toASCII(bestReply);
	msg = Glib::ustring(bestReply);
	(*global_jeliza) << LearnableSentence(fra);

	data.textview->get_buffer()->set_text(Glib::ustring(msg) + Glib::ustring("\n")); // data.textview->get_buffer()->get_text() + "Mensch: " + fra + "\nJEliza: " +
	data.entry->set_text("");

	data.textview->get_buffer()->apply_tag(data.refTagMatch, data.textview->get_buffer()->begin(), data.textview->get_buffer()->end());
	TextBuffer::iterator it = data.textview->get_buffer()->end();*/
	//data.textview->scroll_to(it, 0);

	//cout << "asked2" << endl;
}

void fs_end_clicked(Data4& data) {
	data.win->hide();
	data.tv->get_buffer()->set_text("");
}

void on_fullscreen_mode_activate(Data4& data) {
	data.win->fullscreen();
	data.win->show();
	data.win->show_all_children();
}

void on_load_online_activate(Data3 data) {
    string all1 = download("http://svn.berlios.de/svnroot/repos/jeliza/trunk/JEliza-online.txt");
    string all2 = download("http://svn.berlios.de/svnroot/repos/jeliza/trunk/subject-verb-online.txt");
    string all3 = download("http://svn.berlios.de/svnroot/repos/jeliza/trunk/verb-object-online.txt");

    ofstream o1("JEliza.txt");
    o1 << all1 << endl;
    o1.close();

    ofstream o2("subject-verb.txt");
    o2 << all2 << endl;
    o2.close();

    ofstream o3("verb-object.txt");
    o3 << all3 << endl;
    o3.close();

    {
        Glib::Mutex::Lock lock(mutex);
        Request r;
        r.todo = "vorbereiten";
        jeliza_request = r;
    }
}

void MainWindow::answer() {
    {
        Glib::Mutex::Lock lock(mutex);

        Request r = temp_req;

        if (r.todo == "vorbereiten") {
            cerr << "JEliza Error 2" << endl;
        }

        Glib::ustring msg = Glib::ustring(r.m_ans);

        r.textview->get_buffer()->set_text(r.textview->get_buffer()->get_text()
                + Glib::ustring("Mensch: " + r.m_ques.m_ques) + Glib::ustring("\nJEliza: ") + msg + Glib::ustring("\n"));
        r.entry->set_text("");

        r.textview->get_buffer()->apply_tag(r.refTagMatch, r.textview->get_buffer()->begin(),
                r.textview->get_buffer()->end());
    }
}

void MainWindow::thread_worker() {
    while (true) {
        {
            Glib::Mutex::Lock lock(mutex);
            Request r = jeliza_request;
            if (r.todo.size() > 0) {
                cout << "Neuer Request" << endl;
                jeliza_request.todo = string("");

                if (r.todo.size() > 5) {
                    global_jeliza->vorbereite();

                    cout << "- Datenbank wurde vorbereitet " << endl;
                } else {
                    Question ques = r.m_ques;
                    LearnableSentence learn = r.m_learn;
                    string bestReply;

                    durchsuche_nach_unbekanntem (ques.m_ques);

                    (*global_jeliza) << ques;
                    r.m_ans = "";
                    (*global_jeliza) >> r.m_ans;

                    r.m_ans = toASCII_2(r.m_ans);
                    (*global_jeliza) << learn;

                    temp_req = r;
                    (*jeliza_dispatcher)();
                }
            }
        }
        Glib::usleep(1000000);
    }
}

MainWindow::MainWindow(GtkWindow* base, Glib::RefPtr<Gnome::Glade::Xml> &ref)
: Window(base), refXml(ref)
{
	Gtk::Button* btn_on_Ask_clicked;
	refXml->get_widget("Ask", btn_on_Ask_clicked);

	Gtk::Entry* entry;
	refXml->get_widget("entry", entry);

	Gtk::TextView* textview;
	refXml->get_widget("textview2", textview);


	Glib::RefPtr<Gtk::TextBuffer::Tag> refTagMatch2 = Gtk::TextBuffer::Tag::create();
	refTagMatch2->property_size_points() = 13;
	refTagMatch2->property_indent() = true;
	Glib::RefPtr<Gtk::TextBuffer::TagTable> refTagTable2 = Gtk::TextBuffer::TagTable::create();
	refTagTable2->add(refTagMatch2);
	Glib::RefPtr<Gtk::TextBuffer> refBuffer2 = Gtk::TextBuffer::create(refTagTable2);
	textview->set_buffer(refBuffer2);
	textview->set_wrap_mode(Gtk::WRAP_WORD);

	Data1 d1;
	d1.entry = entry;
	d1.textview = textview;
	d1.win = this;
	d1.refTagMatch = refTagMatch2;
//	d1.init();

	Gtk::MenuItem* mi;
	refXml->get_widget("open", mi);

	Gtk::MenuItem* mi2;
	refXml->get_widget("save", mi2);

	Gtk::MenuItem* mi3;
	refXml->get_widget("close", mi3);

	Gtk::MenuItem* mi4;
	refXml->get_widget("einstellungen-menuitem", mi4);

	Gtk::MenuItem* mi5;
	refXml->get_widget("info", mi5);

	Gtk::MenuItem* mi6;
	refXml->get_widget("new_database", mi6);

	Gtk::MenuItem* mi7;
	refXml->get_widget("new_talk", mi7);

	Gtk::MenuItem* mi8;
	refXml->get_widget("fullscreen_mode", mi8);

	Gtk::MenuItem* mi9;
	refXml->get_widget("new_talk2", mi9);

	Gtk::MenuItem* load_online;
	refXml->get_widget("load_online", load_online);

	Data2 d2;
	d2.win = this;


	Gtk::Dialog* dialog;
	refXml->get_widget("einstellungen", dialog);

	Gtk::TextView* tv2;
	refXml->get_widget("jelizafilecontent", tv2);

	Data3 d3;
	d3.win = this;
	d3.dialog = dialog;
	d3.tv = tv2;
	d3.talk = textview;

	Gtk::Button* okbutton1;
	refXml->get_widget("okbutton1", okbutton1);

	Gtk::Button* cancelbutton1;
	refXml->get_widget("cancelbutton1", cancelbutton1);



	Gtk::Window* fullscreen;
	refXml->get_widget("fullscreen", fullscreen);

	Gtk::TextView* fs_tv;
	refXml->get_widget("fs_tv", fs_tv);

	Glib::RefPtr<Gtk::TextBuffer::Tag> refTagMatch = Gtk::TextBuffer::Tag::create();
	refTagMatch->property_size_points() = 30;
	Glib::RefPtr<Gtk::TextBuffer::TagTable> refTagTable = Gtk::TextBuffer::TagTable::create();
	refTagTable->add(refTagMatch);
	Glib::RefPtr<Gtk::TextBuffer> refBuffer = Gtk::TextBuffer::create(refTagTable);
	fs_tv->set_buffer(refBuffer);
	fs_tv->set_wrap_mode(Gtk::WRAP_WORD);

	Gtk::Entry* fs_entry;
	refXml->get_widget("fs_entry", fs_entry);

	Gtk::Button* fs_button;
	refXml->get_widget("fs_button", fs_button);

	Gtk::Button* fs_end;
	refXml->get_widget("fs_end", fs_end);

	Data4 d4;
	d4.win = fullscreen;
	d4.tv = fs_tv;
	d4.textview = fs_tv;
	d4.entry = fs_entry;
	d4.refTagMatch = refTagMatch;
//	d4.init();



	Gtk::Window* about_dia;
	refXml->get_widget("about_dia", about_dia);

	Gtk::Button* about_ok;
	refXml->get_widget("about_ok", about_ok);


	Data5 d5;
	d5.win = about_dia;



	if(btn_on_Ask_clicked) {
		btn_on_Ask_clicked->signal_clicked().connect(sigc::bind<Data1>(sigc::ptr_fun(&on_Ask_clicked), d1));
	}

	if(entry) {
		entry->signal_activate().connect(sigc::bind<Data1>(sigc::ptr_fun(&on_Ask_clicked), d1));
	}

	if(mi) {
		mi->signal_activate().connect(sigc::bind<Data2>(sigc::ptr_fun(&on_open_activate), d2));
	}

	if(mi2) {
		mi2->signal_activate().connect(sigc::bind<Data2>(sigc::ptr_fun(&on_save_activate), d2));
	}

	if(mi3) {
		mi3->signal_activate().connect(sigc::bind<Data2>(sigc::ptr_fun(&on_close_activate), d2));
	}

	if(mi4) {
		mi4->signal_activate().connect(sigc::bind<Data3>(sigc::ptr_fun(&on_einstellungen_activate), d3));
	}

	if(load_online) {
		load_online->signal_activate().connect(sigc::bind<Data3>(sigc::ptr_fun(&on_load_online_activate), d3));
	}

	if(mi5) {
		mi5->signal_activate().connect(sigc::bind<Data5>(sigc::ptr_fun(&on_info_activate), d5));
	}

	if(mi6) {
		mi6->signal_activate().connect(sigc::bind<Data3>(sigc::ptr_fun(&on_new_database_activate), d3));
	}

	if(mi7) {
		mi7->signal_activate().connect(sigc::bind<Data3>(sigc::ptr_fun(&on_new_talk_activate), d3));
	}

	if(mi8) {
		mi8->signal_activate().connect(sigc::bind<Data4>(sigc::ptr_fun(&on_fullscreen_mode_activate), d4));
	}

	if(mi9) {
		mi9->signal_activate().connect(sigc::bind<Data3>(sigc::ptr_fun(&on_new_talk_activate), d3));
	}

	if(okbutton1) {
		okbutton1->signal_clicked().connect(sigc::bind<Data3>(sigc::ptr_fun(&on_okbutton1_clicked), d3));
	}

	if(cancelbutton1) {
		cancelbutton1->signal_clicked().connect(sigc::bind<Data3>(sigc::ptr_fun(&on_cancelbutton1_clicked), d3));
	}


	if(fs_button) {
		fs_button->signal_clicked().connect(sigc::bind<Data4>(sigc::ptr_fun(&fs_on_Ask_clicked), d4));
	}

	if(fs_entry) {
		fs_entry->signal_activate().connect(sigc::bind<Data4>(sigc::ptr_fun(&fs_on_Ask_clicked), d4));
	}

	if(fs_end) {
		fs_end->signal_clicked().connect(sigc::bind<Data4>(sigc::ptr_fun(&fs_end_clicked), d4));
	}

//	if(fs_entry) {
//		fs_entry->signal_activate().connect(sigc::bind<Data4>(sigc::ptr_fun(&fs_on_Ask_clicked), d4));
//	}

	if(about_ok) {
		about_ok->signal_clicked().connect(sigc::bind<Data5>(sigc::ptr_fun(&on_about_ok_clicked), d5));
	}




	const Glib::ustring jelizaxpm("jeliza16.xpm");
	this->get_window()->set_icon_name(jelizaxpm);

    launch_threads();
}

void MainWindow::launch_threads () {
    jeliza_thread = Glib::Thread::create (sigc::mem_fun(*this, &MainWindow::thread_worker), true);

    {
        Glib::Mutex::Lock lock(mutex);
        Request r;
        r.todo = "vorbereiten";
        jeliza_request = r;
    }
}

void saveSentence_online (JEliza jel, string newstring) {
    ofstream o("JEliza.txt", ios::app | ios::ate);
    if (!o) {
        cerr << "Fehler beim Oeffnen einer JEliza-Datei (jeliza.cpp)" << endl;
    } else if (newstring.size() < 10) {
        cerr << "Satz hat zu wenig Woerter (< 10): " << newstring << endl;
    } else {
        o << newstring << endl;
        o.close();
    }

	ofstream o1("subject-verb-online.txt", ios::app | ios::ate);
	ofstream o2("verb-object-online.txt", ios::app | ios::ate);

	string buffer;

	ifstream in("verbs.txt");
	vector<string> verbs;
	while (in) {
		getline(in, buffer);
		buffer = Util::strip(buffer);
		verbs.push_back(buffer);
	}

	jel.SentenceToSubVerbObj(newstring, verbs, o1, o2);
}


int main_2(int argc, char *argv[]) {
    if (argc > 1) {
        JEliza jel(1);

        ofstream o1("JEliza.txt");
        o1.close();
        ofstream o2("subject-verb-online.txt");
        o2.close();
        ofstream o3("verb-object-online.txt");
        o3.close();


        vector<string> allVec;
        ifstream ifstr("JEliza-online.txt");
        string temp;
        while (ifstr) {
            getline(ifstr, temp);
            temp = Util::strip(temp);

            allVec.push_back(temp);
        }

        for (int x = 0; x < allVec.size(); x++) {
            string line = allVec[x];
            line = Util::strip(line);

            saveSentence_online (jel, line);
            ofstream o(global_jeliza->m_file.c_str(), ios::app | ios::ate);

            if (!o) {
                cerr << "Fehler beim Oeffnen einer JEliza-Datei (gtkgui.cpp)" << endl;
            } else {
                o << line << endl;
                o.close();
            }
            cout << "- Learned " << line << endl;
        }
        return 0;
    }



	Main mainApplication(argc, argv);

    try {
        Glib::RefPtr<Gnome::Glade::Xml> refXml = Gnome::Glade::Xml::create("jeliza-glade.glade"); // , "MainWindow"
        MainWindow* mainWindow(0);

        refXml->get_widget_derived("MainWindow", mainWindow);

        Glib::Dispatcher jeliza_dispatcher2;
        jeliza_dispatcher = &jeliza_dispatcher2;
        (*jeliza_dispatcher).connect (sigc::mem_fun(*mainWindow, &MainWindow::answer));

		//wenn es eine entsprechende instanz gibt...
		if(mainWindow) {
//			mainWindow->fullscreen();
			Main::run(*mainWindow); //...dann koennen wir das fenster nun anzeigen
		}
		else {
			cout << "Hauptfenster konnte nicht geladen werden!" << endl;
			return 1;
		}
	} catch(Gnome::Glade::Xml::Error& xmlError) {
		cout << xmlError.what() << endl;
		cin.get();
		return 1;
	}
    return 0;


}
