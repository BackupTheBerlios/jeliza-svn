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

#include "../jeliza/jeliza.cpp"
#include "../jeliza/util.cpp"
#include "../jeliza/arrays.cpp"

#include <gtkmm.h>

using namespace std;

class MyWindow : public Gtk::Window {
public:
	
	Gtk::VBox   	m_vbox;
	Gtk::HBox   	m_hbox;
//	Gtk::Label  	m_label1;
//	Gtk::Label  	m_label2;
//	Gtk::Label  	m_label3;
	Gtk::Entry  	m_entry; 
	Gtk::TextView  	m_textview; 
	Gtk::Button  	m_ask; 
	
	MyWindow();
};    

void AskJEliza(MyWindow* mw) {
	JEliza jeliza(1);
	jeliza.init();
	string fra = mw->m_entry.get_text();
	string bestReply = jeliza.ask(fra);
	jeliza.learn(fra, fra);
	
	mw->m_textview.get_buffer()->set_text(mw->m_textview.get_buffer()->get_text() + "Mensch: " + fra + "\nJEliza: " + bestReply + "\n");
	mw->m_entry.set_text("");
	
	cout << "asked" << endl;
}

MyWindow::MyWindow()
: Gtk::Window(), m_vbox(false, 1), m_hbox(false, 5), m_ask("  Fragen  ")
{
	// Text der Titelleiste setzen
	set_title("JEliza 2.1");
	
	m_hbox.pack_start(m_entry, Gtk::PACK_EXPAND_WIDGET);
	m_hbox.pack_start(m_ask, Gtk::PACK_SHRINK);
	
	m_vbox.pack_start(m_textview, Gtk::PACK_EXPAND_WIDGET);
	m_vbox.pack_start(m_hbox, Gtk::PACK_SHRINK);
	
	// die vertikale Box an das Fenster uebergeben
	add(m_vbox);
	
	m_ask.signal_clicked().connect(sigc::bind<MyWindow*>(sigc::ptr_fun(&AskJEliza), this)); 
	
	// sorgt dafuer, dass alle Widgets angezeigt werden
	show_all_children();
}





int main(int argc, char *argv[])
{
    Gtk::Main main_obj(argc, argv);
    MyWindow window_obj;
    main_obj.run(window_obj);
    return 0;
}

