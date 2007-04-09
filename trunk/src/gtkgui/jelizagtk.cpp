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

//#include <gtkmm.h>
#include <gtkmm/main.h>
#include <gtkmm/menubar.h>
#include <gtkmm/menu.h>
#include <gtkmm/menuitem.h>
#include <gtkmm/entry.h>
#include <gtkmm/textview.h>
#include <gtkmm/button.h>
#include <gtkmm/window.h>
#include <gtkmm/filechooserdialog.h>
#include <gtkmm/messagedialog.h>
#include <gtkmm/stock.h>
#include <gtkmm/progressbar.h>
#include <gtkmm/statusbar.h>
//#include <gtkmm/.h>
#include <libglademm.h>

#include <glib/gthread.h>
#include <glibmm/thread.h>
//#include <glibmm.h>
#include <glibmm/unicode.h>
#include <glibmm/ustring.h>

#include "defs.h"

using namespace std;
using namespace Gtk;
using namespace jdb;

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

class MainWindow : public Gtk::Window {
public:
    //zugriff auf glade-datei
    Glib::RefPtr<Gnome::Glade::Xml> &refXml;
    Gtk::TextView* m_log;
    Gtk::Statusbar* m_status;

	MainWindow(GtkWindow* base, Glib::RefPtr<Gnome::Glade::Xml> &refXml);
	void thread_worker();
	void thread_work_helper();
	void answer();
	void launch_threads();
	void jeliza_dispatcher_pulse_method();
	void jeliza_dispatcher_log();
};

int main(int argc, char *argv[]) {
    Glib::thread_init();

    if (!Glib::thread_supported()) {
        g_thread_init(NULL);
    }
    if (!Glib::thread_supported()) {
        Glib::thread_init();
    }

    double JELIZA_PROGRESS2 = 0.0;
    JELIZA_PROGRESS = &JELIZA_PROGRESS2;



//    cout << download("http://de.wikipedia.org/wiki/Spezial:Zuf%C3%A4llige_Seite") << endl;
//    return 0;

    main_2(argc, argv);
}


JElizaManager global_jeliza = JElizaManager();
bool vorbereitet = false;

Glib::Thread* jeliza_thread;
Glib::Thread* jeliza_helper_thread;
Request jeliza_request;
Request temp_req;
bool readyForNewRequests = true;

double* JELIZA_PROGRESS;
double JELIZA_PROGRESS_AKTUELL = 0;

answers log_meldungen;

MainWindow* mainWindow(0);

int count_loop;

int verbrauchte_seconds;

Glib::StaticMutex mutex = GLIBMM_STATIC_MUTEX_INIT;
Glib::Dispatcher* jeliza_dispatcher;
Glib::Dispatcher* jeliza_dispatcher_pulse;
Glib::Dispatcher* jeliza_dispatcher_log;

CheckMenuItem* offline_item;

CheckMenuItem* www_surf_item;

void log(string str) {
    {
        Glib::Mutex::Lock lock(mutex);

        if (log_meldungen.size() > 200) {
            log_meldungen.erase(log_meldungen.begin(), log_meldungen.begin() + 10);
        }
        log_meldungen.push_back(str);

        string all;
        for (answers::iterator it = log_meldungen.begin(); it != log_meldungen.end(); it++) {
            all += *it;
            all += "\n";
        }
        log_all = all;

        (*jeliza_dispatcher_log)();
    }
//    r.textview->get_buffer()->set_text(r.textview->get_buffer()->get_text()
//                + Glib::ustring("Mensch: " + r.m_ques.m_ques) + Glib::ustring("\nJEliza: ") + msg + Glib::ustring("\n"));
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


	std::string filename;
	if (data.dlg->run() == Gtk::RESPONSE_OK) { // RESPONSE_ACCEPT
		filename = data.dlg->get_filename();
		Gtk::MessageDialog dia3(*data.win, Glib::ustring(filename + "\nWird nun in die Datenbank geladen"));
		dia3.run();

		jdb::DB tmpDB = jdb::parseJDB(filename);

		for (jdb::DB::iterator it = tmpDB.begin(); it != tmpDB.end(); it++) {
		    JEliza::m_jd.m_sents->push_back(*it);
		}

		jdb::saveDB("jeliza-standard.xml", *global_jeliza, *JEliza::m_jd.m_sents);

		Gtk::MessageDialog dia4(*data.win, Glib::ustring(filename + ":\nErfolgreich geladen!"));
		dia4.run();
	}

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

		jdb::saveDB(filename, *global_jeliza, *JEliza::m_jd.m_sents);

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
};

void on_einstellungen_activate(Data3& data) {
	ifstream in("jeliza-standard.xml");
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
	all = toASCII_2(all);

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

	ofstream o("jeliza-standard.xml");

	string all;
	Gtk::TextBuffer::iterator iter = data.buf->begin();
	while (iter != data.buf->end()) {
		all += *iter;
		++iter;
	}

	all = toASCII_2(all);
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
	Gtk::MessageDialog dia3(*data.win, Glib::ustring(toASCII_2("Soll die aktuelle Datenbank wirklich gelöscht und eine neue angelegt werden?")), true, Gtk::MESSAGE_QUESTION, Gtk::BUTTONS_YES_NO, true);

	if (dia3.run() == Gtk::RESPONSE_YES) {
		ofstream o("jeliza-standard.xml");
		o << "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" << endl << endl << "<jdb>" << endl << "</jdb>" << endl;
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
	msg = toASCII_2(fra);
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

        (*JELIZA_PROGRESS) = 2.0;
        (*jeliza_dispatcher_pulse)();
    }

    jeliza_helper_thread = Glib::Thread::create (sigc::mem_fun(*mainWindow, &MainWindow::thread_work_helper), true);
}

void fs_on_Ask_clicked(Data4& data) {
	Glib::ustring msg;

	if(!data.entry) {
		Gtk::MessageDialog dia4(*data.win, Glib::ustring("JEliza Error 2"));
		dia4.run();
	}
	string fra = data.entry->get_text();
	msg = toASCII_2(fra);
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

        (*JELIZA_PROGRESS) = 2.0;
        (*jeliza_dispatcher_pulse)();
    }

    jeliza_helper_thread = Glib::Thread::create (sigc::mem_fun(*mainWindow, &MainWindow::thread_work_helper), true);
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
    string all1 = download_with_pbar("http://svn.berlios.de/svnroot/repos/jeliza/trunk/jeliza-standard-online.xml");

    ofstream o1("jeliza-standard.xml");
    o1 << all1 << endl;
    o1.close();
}

void MainWindow::answer() {
    while (temp_req.m_ans.size() < 1) {
        Glib::usleep(1000000);
    }

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

        Gtk::TextBuffer::iterator itee = r.textview->get_buffer()->end();
        Glib::RefPtr<TextBuffer::Mark> ma = r.textview->get_buffer()->create_mark (Glib::ustring("endmark"), itee, false);
        r.textview->scroll_to(ma);

        stringstream sst;
        sst << verbrauchte_seconds;
        string z;
        sst >> z;

        log ("Es wurden " + z + " Sekunden für das Antworten gebraucht.");
        log ("");

        (*JELIZA_PROGRESS) = 0.0;
        (*(::jeliza_dispatcher_pulse))();
    }
}

void jeliza_pulse() {
//    mainWindow->jeliza_dispatcher_pulse();
}

void MainWindow::thread_worker() {
    count_loop = 0;

    Glib::usleep(5000000);

    while (true) {
        {
            Request r;

            {
                Glib::Mutex::Lock lock(mutex);

                r = jeliza_request;
            }
            if (count_loop > 2 && !offline_mode && www_surf_mode) {
                {
                    Glib::Mutex::Lock lock(mutex);

                    (*JELIZA_PROGRESS) = 1.0;
                    (*jeliza_dispatcher_pulse)();
                }

                answers ans;

                if (wikipedia_words.size() > 0) {
                    string word = wikipedia_words[0];
                    answers wikipedia_words2;

                    ans = search_in_wikipedia_acticle(word);

                    for (int g = 1; g < wikipedia_words.size(); g++) {
                        wikipedia_words2.push_back(wikipedia_words[g]);
                    }
                    wikipedia_words = wikipedia_words2;

                } else {
                    //ans = search_in_wikipedia_random();
                }

                for (int g = 0; g < ans.size(); g++) {
                    clogger << "- Neuer Satz: " << ans[g] << endl;
                }

                global_jeliza << ans;

                {
                    Glib::Mutex::Lock lock(mutex);

                    (*JELIZA_PROGRESS) = 0.0;
                    (*jeliza_dispatcher_pulse)();
                }
            }
        }
        if (count_loop >= 0) {
            count_loop++;
        }
        Glib::usleep(7000000);
    }
}

void offline_mode_toggled() {
    offline_mode = offline_item->get_active();
}

void www_surf_toggled() {
    www_surf_mode = www_surf_item->get_active();
}

void save_arin_activated() {
    ofstream o("wikipedia_words.tmp");
    for (int x = 0; x < wikipedia_words.size(); x++) {
        o << wikipedia_words[x] << " ";
    }
}

void load_arin_activated() {
    ifstream i("wikipedia_words.tmp");
    wikipedia_words = answers();
    string s;
    while (i) {
        i >> s;
        if (s.size() > 1) {
            wikipedia_words.push_back(s);
        }
    }
}

void MainWindow::jeliza_dispatcher_pulse_method() {
    /*double x = // (*JELIZA_PROGRESS) / 100.0;
    if (x > 1.05) {
        x = 0.95;
    }
    if ((x > JELIZA_PROGRESS_AKTUELL || x < 0.5) && x != 0) {
        m_pbar->set_fraction(x);
        Glib::usleep(300000);
        m_pbar->set_fraction(x + 0.025);
        Glib::usleep(300000);
        m_pbar->set_fraction(x + 0.05);
        JELIZA_PROGRESS_AKTUELL = x + 0.05;
    } else if (x == 0) {
        m_pbar->set_fraction(x);
    }*/

    {
        Glib::Mutex::Lock lock(mutex);

        if ((*JELIZA_PROGRESS) == 0.0) {
            m_status->push("JEliza hört zu...");
        } else if ((*JELIZA_PROGRESS) == 1.0) {
            m_status->push("JEliza surft im WWW...");
        } else {
            m_status->push("JEliza denkt nach...");
        }
    }
}

void MainWindow::jeliza_dispatcher_log() {
    {
        Glib::Mutex::Lock lock(mutex);

        m_log->get_buffer()->set_text(log_all);

        Gtk::TextBuffer::iterator itee = m_log->get_buffer()->end();
        Glib::RefPtr<TextBuffer::Mark> ma = m_log->get_buffer()->create_mark (Glib::ustring("endmark2"), itee, false);
        m_log->scroll_to(ma);
    }
}

void MainWindow::thread_work_helper() {
    clock_t start;
    clock_t end;
    start = clock();

    count_loop = -1;
    clogger << "Neuer Request" << endl;
    count_loop = 0;
    temp_req = jeliza_request;
    clogger << "Frage (1): " << temp_req.m_ques.m_ques;

    global_jeliza << temp_req.m_ques.m_ques;
    temp_req.m_ans = "";
    global_jeliza >> temp_req.m_ans;
    temp_req.m_ans = toASCII_2(temp_req.m_ans);

    end = clock();
    end -= start;
    long double x;
    x = end;

    verbrauchte_seconds = x / CLOCKS_PER_SEC;

    (*jeliza_dispatcher)();
    count_loop = 0;
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
	refTagMatch->property_size_points() = 28;
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

	refXml->get_widget("statusbar", m_status);
	refXml->get_widget("logview", m_log);



	refXml->get_widget("off_mode", offline_item);
	refXml->get_widget("www_surf", www_surf_item);


	Gtk::MenuItem* save_arin;
	Gtk::MenuItem* load_arin;
	refXml->get_widget("save_arin", save_arin);
	refXml->get_widget("load_arin", load_arin);



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

	if(offline_item) {
		offline_item->signal_toggled().connect(sigc::ptr_fun(&offline_mode_toggled));
	}
	if(www_surf_item) {
		www_surf_item->signal_toggled().connect(sigc::ptr_fun(&www_surf_toggled));
	}

	if(save_arin) {
		save_arin->signal_activate().connect(sigc::ptr_fun(&save_arin_activated));
	}
	if(load_arin) {
		load_arin->signal_activate().connect(sigc::ptr_fun(&load_arin_activated));
	}


    d1.textview->get_buffer()->set_text(Glib::ustring("JEliza: ") + global_jeliza.greet() + Glib::ustring("\n"));
    d1.textview->get_buffer()->apply_tag(d1.refTagMatch, d1.textview->get_buffer()->begin(),
            d1.textview->get_buffer()->end());


	const Glib::ustring jelizaxpm("jeliza16.xpm");
	this->get_window()->set_icon_name(jelizaxpm);

    launch_threads();
}

void MainWindow::launch_threads () {
    jeliza_thread = Glib::Thread::create (sigc::mem_fun(*this, &MainWindow::thread_worker), true);
}

int main_2(int argc, char *argv[]) {


	Main mainApplication(argc, argv);

    try {
        Glib::RefPtr<Gnome::Glade::Xml> refXml = Gnome::Glade::Xml::create("jeliza-glade.glade"); // , "MainWindow"

        refXml->get_widget_derived("MainWindow", mainWindow);

        Glib::Dispatcher jeliza_dispatcher2;
        jeliza_dispatcher = &jeliza_dispatcher2;
        (*jeliza_dispatcher).connect (sigc::mem_fun(*mainWindow, &MainWindow::answer));

        Glib::Dispatcher jeliza_dispatcher_pulse2;
        jeliza_dispatcher_pulse = &jeliza_dispatcher_pulse2;
        (*jeliza_dispatcher_pulse).connect (sigc::mem_fun(*mainWindow, &MainWindow::jeliza_dispatcher_pulse_method));

        Glib::Dispatcher jeliza_dispatcher_log2;
        jeliza_dispatcher_log = &jeliza_dispatcher_log2;
        (*jeliza_dispatcher_log).connect (sigc::mem_fun(*mainWindow, &MainWindow::jeliza_dispatcher_log));



		//wenn es eine entsprechende instanz gibt...
		if(mainWindow) {
//			mainWindow->fullscreen();
			Main::run(*mainWindow); //...dann koennen wir das fenster nun anzeigen
		}
		else {
			clogger << "Hauptfenster konnte nicht geladen werden!" << endl;
			return 1;
		}
	} catch(Gnome::Glade::Xml::Error& xmlError) {
		clogger << xmlError.what() << endl;
		cin.get();
		return 1;
	}
    return 0;


}
