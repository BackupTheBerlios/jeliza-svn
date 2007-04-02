#ifndef JELIZA_SOCKET_LOAD
#define JELIZA_SOCKET_LOAD 1


#include <iostream>
#include <fstream>
#include <stdexcept> // runtime_error
#include <sstream>
#ifdef linux
#include <sys/socket.h> // socket(), connect()
#include <arpa/inet.h> // sockaddr_in
#include <netdb.h> // gethostbyname(), hostent
#include <errno.h> // errno
#else
#include <winsock2.h>
#endif

#include <string>
#include <map>

#include <string>
#include <list>

#include <time.h>
#include <vector>

#include "util.cpp"

using namespace std;

std::runtime_error CreateSocketError()
{
	std::ostringstream temp;
#ifdef linux
	temp << "Socket-Fehler #" << errno << ": " << strerror(errno);
#else
	int error = WSAGetLastError();
	char* msg;
	FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM,
				  NULL, error, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
				  reinterpret_cast<char*>(&msg), 0, NULL);
	temp << "Socket-Fehler #" << error << ": " << msg;
#endif
	return std::runtime_error(temp.str());
}

void SendAll(int socket, const char* const buf, const size_t size)
{
	size_t bytesSent = 0; // Anzahl Bytes die wir bereits vom Buffer gesendet haben
	do
	{
		bytesSent += send(socket, buf + bytesSent, size, 0);
		if(bytesSent <= 0) // Wenn send einen Wert <= 0 zurück gibt deutet dies auf einen Fehler hin.
		{
			throw CreateSocketError();
		}
	} while(bytesSent < size);
}

// Liest eine Zeile des Sockets in einen stringstream
void GetLine(int socket, std::stringstream& line)
{
	for(char c; recv(socket, &c, 1, 0) > 0; line << c)
	{
		if(c == '\n')
		{
			return;
		}
	}
	throw CreateSocketError();
}

// Entfernt das http:// vor dem URL
void RemoveHttp(std::string& URL)
{
	size_t pos = URL.find("http://");
	if(pos != std::string::npos)
	{
		URL.erase(0, 7);
	}
}

// Gibt die Dateiendung im URL zurück
std::string GetFileEnding(std::string& URL)
{
	using namespace std;
	size_t pos = URL.rfind(".");
	if(pos == string::npos)
	{
		return "";
	}
	URL.erase(0, pos);
	string ending = ".";
	// Algorithmus um Sachen wie ?index=home nicht zuzulassen
	for(string::iterator it = URL.begin() + 1; it != URL.end(); ++it)
	{
		if(isalpha(*it))
		{
			ending += *it;
		}
		else
		{
			break;
		}
	}
	return ending;
}

// Gibt den Hostnamen zurück und entfernt ihn aus der URL, sodass nur noch der Pfad übrigbleibt
std::string RemoveHostname(std::string& URL)
{
	size_t pos = URL.find("/");
	if(pos == std::string::npos)
	{
		std::string temp = URL;
		URL = "/";
		return temp;
	}
	std::string temp = URL.substr(0, pos);
	URL.erase(0, pos);
	return temp;
}



static string replace_aufz (string& in, const string rep) {
	int wit = 1;
	int pos;
	while (true) {
		pos = in.find(rep);
		if (pos == -1) {
			break;
		} else {
			in.erase(pos, rep.length());
			stringstream ss;
			ss << wit;
			string tmp;
			ss >> tmp;
			in.insert(pos, " " + tmp + ". ");
			wit++;
		}
	}
	return in;
}


string ohneHtml(string all) {
	string allAscii = "";
	all = Util::replace(all, string("<br>"), string("\n"));
	all = Util::replace(all, string("<br/>"), string("\n"));
	all = Util::replace(all, string("<br />"), string("\n"));
	int aufz = 1;
	bool inHtml = false;
	for (int x = 0; x < all.size(); x++) {
		char array[2];
		array[0] = all[x];
		array[1] = '\0';
		string y(array);

		if (y == string("<")) {
			inHtml = true;
		}
		if (!inHtml) {
			allAscii += y;
		}
		if (y == string(">")) {
			inHtml = false;
		}
	}

	allAscii = replace_aufz(allAscii, string(" - "));

	return allAscii;
}


string download (string url) {
	using namespace std;

	string URL = url;

#ifndef linux
	WSADATA w;
	if(WSAStartup(MAKEWORD(2,2), &w) != 0)
	{
		cout << "Winsock 2 konnte nicht gestartet werden! Error #" << WSAGetLastError() << endl;
		return "";
	}
#endif

	RemoveHttp(URL);

	string filename;

	string hostname = RemoveHostname(URL);

	hostent* phe = gethostbyname(hostname.c_str());

	if(phe == NULL)
	{
		cout << "Host konnte nicht aufgeloest werden!" << endl;
		return "";
	}

	if(phe->h_addrtype != AF_INET)
	{
		cout << "Ungueltiger Adresstyp!" << endl;
		return "";
	}

	if(phe->h_length != 4)
	{
		cout << "Ungueltiger IP-Typ!" << endl;
		return "";
	}

	int Socket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
	if(Socket == -1)
	{
		cout << "Socket konnte nicht erstellt werden!" << endl;
		return "";
	}

	sockaddr_in service;
	service.sin_family = AF_INET;
	service.sin_port = htons(80); // Das HTTP-Protokoll benutzt Port 80

	char** p = phe->h_addr_list; // p mit erstem Listenelement initialisieren
	int result; // Ergebnis von connect
	do
	{
		if(*p == NULL) // Ende der Liste
		{
			cout << "Verbindung fehlgschlagen!" << endl;
			return "";
		}

//		char xyz[4];
//		xyz[0] = 66;
//		xyz[1] = 230;
//		xyz[2] = 200;
//		xyz[3] = 100;

//		service.sin_addr.s_addr = *reinterpret_cast<unsigned long*>(xyz);
		service.sin_addr.s_addr = *reinterpret_cast<unsigned long*>(*p);
		++p;
		result = connect(Socket, reinterpret_cast<sockaddr*>(&service), sizeof(service));
	}
	while(result == -1);

	cout << "Verbindung erfolgreich!" << endl;

	string request = "GET ";
	request += URL;	// z.B. /faq/index.html
	request += " HTTP/1.1\n";
	request += "Host: " + hostname + "\n";
	request += "User-Agent: Mozilla/5.0 (X11; U; Linux i686; de; rv:1.8.1.2) Gecko/20060601 Firefox/2.0.0.2 (Ubuntu-edgy)\n";
	request += "Connection: close\n\n";

	try
	{
		SendAll(Socket, request.c_str(), request.size());

		int code = 100; // 100 = Continue
		string Protokoll;
		stringstream firstLine; // Die erste Linie ist anders aufgebaut als der Rest
		while(code == 100)
		{
			GetLine(Socket, firstLine);
			firstLine >> Protokoll;
			firstLine >> code;
			if(code == 100)
			{
				GetLine(Socket, firstLine); // Leere Zeile nach Continue ignorieren
			}
		}
		cout << "Protokoll: " << Protokoll << endl;

		if(code != 200)
		{
			firstLine.ignore(); // Leerzeichen nach dem Statuscode ignorieren
			string msg;
			getline(firstLine, msg);
			cout << "Error #" << code << " - " << msg << endl;
			return "";
		}

		bool chunked = false;
		const int noSizeGiven = -1;
		int size = noSizeGiven;

		while(true)
		{
			stringstream sstream;
			GetLine(Socket, sstream);
			if(sstream.str() == "\r") // Header zu Ende?
			{
				break;
			}
			string left; // Das was links steht
			sstream >> left;
			sstream.ignore(); // ignoriert Leerzeichen
			if(left == "Content-Length:")
			{
				sstream >> size;
			}
			if(left == "Transfer-Encoding:")
			{
				string transferEncoding;
				sstream >> transferEncoding;
				if(transferEncoding == "chunked")
				{
					chunked = true;
				}
			}
		}

		filename = "download" + GetFileEnding(URL);
		cout << "Filename: " << filename << endl;
		fstream fout(filename.c_str(), ios::binary | ios::out);
		if(!fout)
		{
			cout << "Could Not Create File!" << endl;
			return "";
		}
		int recvSize = 0; // Empfangene Bytes insgesamt
		char buf[1024];
		int bytesRecv = -1; // Empfangene Bytes des letzten recv

		if(size != noSizeGiven) // Wenn die Größe über Content-length gegeben wurde
		{
			cout << "0%";
			while(recvSize < size)
			{
				if((bytesRecv = recv(Socket, buf, sizeof(buf), 0)) <= 0)
				{
					throw CreateSocketError();
				}
				recvSize += bytesRecv;
				fout.write(buf, bytesRecv);
				cout << "\r" << recvSize * 100 / size << "%" << flush; // Mit \r springen wir an den Anfang der Zeile
			}
		}
		else
		{
			if(!chunked)
			{
				cout << "Downloading... (Unknown Filesize)" << endl;
				while(bytesRecv != 0) // Wenn recv 0 zurück gibt, wurde die Verbindung beendet
				{
					if((bytesRecv = recv(Socket, buf, sizeof(buf), 0)) < 0)
					{
						throw CreateSocketError();
					}
					fout.write(buf, bytesRecv);
				}
			}
			else
			{
				cout << "Downloading... (Chunked)" << endl;
				while(true)
				{
					stringstream sstream;
					GetLine(Socket, sstream);
					int chunkSize = -1;
					sstream >> hex >> chunkSize; // Größe des nächsten Parts einlesen
					if(chunkSize <= 0)
					{
						break;
					}
					cout << "Downloading Part (" << chunkSize << " Bytes)... " << endl;
					recvSize = 0; // Vor jeder Schleife wieder auf 0 setzen
					while(recvSize < chunkSize)
					{
						int bytesToRecv = chunkSize - recvSize;
						if((bytesRecv = recv(Socket, buf, bytesToRecv > sizeof(buf) ? sizeof(buf) : bytesToRecv, 0)) <= 0)
						{
							throw CreateSocketError();
						}
						recvSize += bytesRecv;
						fout.write(buf, bytesRecv);
						cout << "\r" << recvSize * 100 / chunkSize << "%" << flush;
					}
					cout << endl;
					for(int i = 0; i < 2; ++i)
					{
						char temp;
						recv(Socket, &temp, 1, 0);
					}
				}
			}
		}
		cout << endl << "Finished!" << endl;

/*		ifstream ifstr(filename.c_str());
		string all;
		string temp;
		int inTable = 0;
		bool anfangOk = false;
		int inImg = false;
		while (ifstr) {
			getline(ifstr, temp);

			if ((Util::contains(temp, string("table")) && !Util::contains(temp, string("/table")))) {
				inTable++;
			}
			if (!inImg && Util::contains(temp, string("div")) && Util::contains(temp, string("img"))) {
			    inImg = true;
			}
//			cout << inTable << endl;
			if (inTable < 1 && anfangOk && !inImg) {
				string tmp = Util::replace(temp, string("<li"), string("\n - <l22i"));
				tmp = Util::replace(tmp, string("<l22i"), string("<li"));

                tmp = Util::strip(tmp);
                tmp = ohneHtml(tmp);
                tmp = Util::strip(tmp);
                if (tmp.size() > 1) {
                    all += tmp;
                    all += "#";
                }
			} else {
			    cout << 1 << inImg << " " << temp << endl;
			}
			if (Util::contains(temp, string("/table"))) {
                inTable--;
			}
			if (Util::contains(temp, string("/div")) && inImg && !Util::contains(temp, string("img"))) {
                inImg = false;
			    cout << 2 << inImg << " " << temp << endl;
			}

			if (Util::contains(temp, string("<!-- start content -->"))) {
				anfangOk = true;
			}
		}

		string noHtml = all;
//		cout << all << endl << endl;
//		cout << noHtml << endl << endl;
		noHtml = Util::strip(noHtml);
		vector<string> parts;
		Util::split(noHtml, string("#"), parts);

//		cout << parts[0] << endl;

        for (vector<string>::iterator it = parts.begin(); it != parts.end(); it++) {
            string p = Util::strip(*it);
            cout << "  " << p << endl;
            if (p.size() > 5) {
                cout << p << endl;
                break;
            }
        }*/
	}
	catch(exception& e)
	{
		cout << endl;
		cerr << e.what() << endl;
	}

#ifdef linux
	close(Socket); // Verbindung beenden
#else
	closesocket(Socket); // Windows-Variante
#endif

    ifstream ifstr(filename.c_str());
    string all;
    string temp;
    while (ifstr) {
		getline(ifstr, temp);
//		temp = Util::strip(temp);

		all += temp;
		all += "\n";
    }

    return all;
}



#endif

