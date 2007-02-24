#ifndef JELIZA_SERVER
#define JELIZA_SERVER 1

/*
 * This is part of JEliza 2.0.
 * Copyright 2006 by Tobias Schulz
 * WWW: http://jeliza.ch.to/
 * 
 * JEliza is free software; you can redistribute it and/or      
 * modify it under the terms of the GNU Lesser General Public    
 * License as published by the Free Software Foundation; either  
 * version 2.1 of the License, or (at your option) any later     
 * version.                                                      
 *                                                               
 * JEliza is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of    
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.          
 * See the GNU Lesser General Public License for more details.   
 *                                                               
 * You should have received a copy of the GNU LGPL               
 * along with JEliza (file "lgpl.txt") ; if not, write           
 * to the Free Software Foundation, Inc., 51 Franklin St,        
 * Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */

#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <map>

#include <string>
#include <list>

#include <time.h>
#include <vector>

#include "jeliza.cpp"
#include "util.cpp"
#include "arrays.cpp"

#include "SocketHandler.h"
#include "ListenSocket.h"
#include "TcpSocket.h"

using namespace std;

class MySocket : public TcpSocket {
public:
	string m_url;
	string m_headers;

	MySocket(ISocketHandler& );

//	void OnRead();

	void OnLine(const std::string& lin) {
		string line = lin;
		if (Util::toLower(line).substr(0, 3) == "get") {
			line = Util::toLower(line);
			string url = Util::replace(line, "get ", "");
			url = Util::replace(url, " http/", "");
			url = Util::replace(url, "1.1", "");
			url = Util::replace(url, "1.0 ", "");
			cout << "Request (GET): " << url << endl;
			m_url = url;
			m_headers = "";
			
			return;
		}
		cout << "Incoming: " << line << endl;
		m_headers += line;
		m_headers += "\n";
		
		if (m_headers.size() > 0 && Util::strip(line).size() == 0) {
			answer();
		}
	}
	
	void answer() {
		string bestReply = "";
		if (m_url == "/") {
		}
		else {
			JEliza jeliza;
			
			string fra = m_url.substr(5, m_url.size());
			fra = Util::replace(fra, "00", " ");
			
			jeliza.init();
			
			bestReply = jeliza.ask(fra);
			jeliza.learn(fra, fra);
		}	
		ifstream in("jeliza.inc.html");
		string content = "";
        while (in) {
        	string buffer;
            getline(in, buffer);
            content += Util::replace(buffer, "${ANSWER}", bestReply);
            content += "\n";
//            Close();
        }
        Send("HTTP/1.0 200 OK\n");
        Send("Server: JEliza\n");
        Send("Content-Type: text/html\n");
        ostringstream s;
        s << static_cast<int>(content.size());
        Send("Content-Length: " + s.str());
        Send(string("\n") + string("\n"));
        Send(content);
	}
};


MySocket::MySocket(ISocketHandler& h) : TcpSocket(h)
{
	SetLineProtocol();
}





static	bool quit = false;

int serverJEliza(int port)
{
	SocketHandler h;
	ListenSocket<MySocket> l(h);

	if (l.Bind(port))
	{
		exit(-1);
	}
	h.Add(&l);
	h.Select(1,0);
	while (!quit)
	{
		h.Select(1,0);
	}
	return 0;
}

#endif
