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
#include <unistd.h>
#include <getopt.h>

#include "jeliza.h"
#include "../jeliza/util.cpp"
#include "../jeliza/arrays.cpp"

#include <gtkmm.h>
#include <libglademm.h> 

using namespace std;
using namespace Gtk; 


auto_ptr<JEliza> global_jeliza(new JEliza());
bool vorbereitet = false;

/*void AskJEliza() {
	JEliza jeliza(1);
	jeliza.init();
	string fra = mw->m_entry.get_text();
	string bestReply = jeliza.ask(fra);
	jeliza.learn(fra, fra);
	
	mw->m_textview.get_buffer()->set_text(mw->m_textview.get_buffer()->get_text() + "Mensch: " + fra + "\nJEliza: " + bestReply + "\n");
	mw->m_entry.set_text("");
	
	cout << "asked" << endl;
}*/


class MainWindow : public Gtk::Window {
    public:
        //zugriff auf glade-datei
        Glib::RefPtr<Gnome::Glade::Xml> &refXml;
       
        /*
            wir wollen die fenster informationen aus einer glade-datei laden.
            mittels 'get_widget_derived' (s. main.cpp) kann auf eine abgeleitete
            klasse zugegriffen werden. zum instanziieren ruft 'get_widget_derived'
            einen CTor auf, welcher eben diese signatur haben muss. 'base' ist
            hierbei der c-typ (gtk+) und enthaelt bereits alle relervanten
            informationen ueber das fenster. 'refXml' ist hierbei wichtig, da
            wir damit die member der klasse aus der glade-datei herauslesen und
            den klassenmembern zuweisen koennen (siehe implementierung des ctor)
        */
        
	MainWindow(GtkWindow* base, Glib::RefPtr<Gnome::Glade::Xml> &refXml);
};


string toASCII(string all) {
	Glib::ustring utf(Glib::convert(all, "UTF-8", "ISO-8859-1"));
	string utf2(utf);
	return utf;
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
	global_jeliza->vorbereite();
	
	Glib::ustring msg;
	
	if(!data.entry) {
		Gtk::MessageDialog dia4(*data.win, Glib::ustring("JEliza Error 1"));
		dia4.run(); 
	}
	string fra = data.entry->get_text();
	msg = toASCII(fra);
	
	
	string bestReply = toASCII(global_jeliza->ask(fra));
	msg = Glib::ustring(bestReply);
	global_jeliza->learn(fra, fra);
	
	data.textview->get_buffer()->set_text(data.textview->get_buffer()->get_text() + Glib::ustring("Mensch: " + fra) + Glib::ustring("\nJEliza: ") + msg + Glib::ustring("\n"));
	data.entry->set_text("");
	
	data.textview->get_buffer()->apply_tag(data.refTagMatch, data.textview->get_buffer()->begin(), data.textview->get_buffer()->end());
	TextBuffer::iterator it = data.textview->get_buffer()->end();
	
	//cout << "asked1" << endl;
}

void fs_on_Ask_clicked(Data4& data) {
	global_jeliza->vorbereite();
	
	Glib::ustring msg;
	
	if(!data.entry) {
		Gtk::MessageDialog dia4(*data.win, Glib::ustring("JEliza Error 2"));
		dia4.run(); 
	}
	string fra = data.entry->get_text();
	msg = toASCII(fra);
	
	
	string bestReply = toASCII(global_jeliza->ask(fra));
	msg = Glib::ustring(bestReply);
	global_jeliza->learn(fra, fra);
	
	data.textview->get_buffer()->set_text(Glib::ustring(msg) + Glib::ustring("\n")); // data.textview->get_buffer()->get_text() + "Mensch: " + fra + "\nJEliza: " + 
	data.entry->set_text("");
	
	data.textview->get_buffer()->apply_tag(data.refTagMatch, data.textview->get_buffer()->begin(), data.textview->get_buffer()->end());
	TextBuffer::iterator it = data.textview->get_buffer()->end();
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

}

int main(int argc, char *argv[])
{
	ifstream in("JEliza.txt");
	string buffer;
	string all = "";
	while (in) {
		getline(in, buffer);
		buffer = Util::strip(buffer);
		all += Util::replace(buffer, string("\r"), string(""));
		all += "\n";
	}
	in.close();
	
	ofstream o("JEliza.txt");
	o << toASCII(all) << endl;
	o.close();

	
	Main mainApplication(argc, argv);
	
        try {
		/*
			gladedatei oeffnen, um fensterinformationen herauszulesen
			der zweite parameter gibt an, dass wir lediglich das fenster
			mit diesem namen instanziieren wollen. wuerden wir diesen
			parameter weglassen, so wuerden saemtliche, in der datei
			definierten, fenster instanziiert werden
		*/
		Glib::RefPtr<Gnome::Glade::Xml> refXml = Gnome::Glade::Xml::create("jeliza-glade.glade"); // , "MainWindow"
		MainWindow* mainWindow(0);
		
		/*
			um auf eine instanz zugreifen zu koennen, nutzt man die element-
			funktion 'get_widget'. Da wir jedoch eine von Gtk::Window
			abgeleitete Klasse nutzen, muessen wir 'get_widget_derived'
			aufrufen.
		*/
		refXml->get_widget_derived("MainWindow", mainWindow);
		
		//wenn es eine entsprechende instanz gibt...
		if(mainWindow) {
//			mainWindow->fullscreen();
			Main::run(*mainWindow); //...dann koennen wir das fenster nun anzeigen
		}
		else {
			/*
			...oder es konnte es aus irgendwelchen gruenden nicht
			nicht dem zeiger 'mainWindow' zugewiesen werden?
			*/
			cout << "Hauptfenster konnte nicht geladen werden!" << endl;
			return 1;
		}
	/*
		falls beim parsen der glade-datei ein problem gibt, dann wird
		eine exception geschmissen, die wir abfangen sollten
	*/
	} catch(Gnome::Glade::Xml::Error& xmlError) {
		cout << xmlError.what() << endl;
		cin.get();
		return 1;
	}
    return 0; 
	
	
}

