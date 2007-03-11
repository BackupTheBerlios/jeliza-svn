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

#include "../../src/jeliza/jeliza.cpp"
#include "../../src/jeliza/util.cpp"
#include "../../src/jeliza/arrays.cpp"


#include "config.h"
#include "MainWindow.hh"

void MainWindow::on_new_activate()
{  
	ofstream o("JEliza.txt");
	o << "" << endl;
	o.close();
}

void MainWindow::on_open_activate()
{  
}

void MainWindow::on_save_activate()
{  
}

void MainWindow::on_close_activate()
{  
}

void MainWindow::on_cut_activate()
{  
}

void MainWindow::on_copy_activate()
{  
}

void MainWindow::on_paste_activate()
{  
}

void MainWindow::on_delete_activate()
{  
}

void MainWindow::on_einstellungen_activate()
{  
}

void MainWindow::on_info_activate()
{  
}
