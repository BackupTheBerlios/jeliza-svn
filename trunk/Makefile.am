CFLAGS=-g -Wall -fPIC -DPIC -Isrc/socket
AM_CPPFLAGS=-g -Wall -fPIC -DPIC -Isrc/socket
bin_PROGRAMS = jeliza grammartest bytelang jelizagtk test
jeliza_LDADD =  -lm -luuid -lpthread
jeliza_CFLAGS = -g -Wall -fPIC -DPIC -Isrc/socket
jeliza_CPPFLAGS = -g -Wall -fPIC -DPIC -Isrc/socket
jeliza_SOURCES = ./src/jeliza/jeliza.cpp ./src/jeliza/arrays.cpp ./src/jeliza/main.cpp ./src/jeliza/server.cpp ./src/jeliza/string_compare.cpp ./src/jeliza/util.cpp ./src/socket/HttpdCookies.h ./src/socket/ListenSocket.h ./src/socket/ICrypt.h ./src/socket/MinderHandler.h ./src/socket/MemFile.h ./src/socket/SocketThread.h ./src/socket/Event.h ./src/socket/PoolSocket.h ./src/socket/HttpClientSocket.h ./src/socket/Ipv4Address.h ./src/socket/HTTPSocket.h ./src/socket/NullCrypt.h ./src/socket/StdLog.h ./src/socket/CircularBuffer.h ./src/socket/SocketAddress.h ./src/socket/Mutex.h ./src/socket/IEventOwner.h ./src/socket/HttpdForm.h ./src/socket/SocketHandler.h ./src/socket/MinderSocket.h ./src/socket/Lock.h ./src/socket/HttpPostSocket.h ./src/socket/EventTime.h ./src/socket/UdpSocket.h ./src/socket/MinionSocket.h ./src/socket/CTcpSocket.h ./src/socket/Utility.h ./src/socket/Thread.h ./src/socket/Parse.h ./src/socket/File.h ./src/socket/EventHandler.h ./src/socket/HttpPutSocket.h ./src/socket/Uid.h ./src/socket/SctpSocket.h ./src/socket/Base64.h ./src/socket/IFile.h ./src/socket/IEventHandler.h ./src/socket/Socket.h ./src/socket/Ipv6Address.h ./src/socket/socket_include.h ./src/socket/HttpdSocket.h ./src/socket/ISocketHandler.h ./src/socket/HttpDebugSocket.h ./src/socket/TcpSocket.h ./src/socket/ResolvServer.h ./src/socket/ResolvSocket.h ./src/socket/StdoutLog.h ./src/socket/HttpGetSocket.h ./src/jeliza/SocketHandler.h ./src/jeliza/TcpSocket.h ./src/jeliza/ListenSocket.h ./src/socket/CircularBuffer.cpp ./src/socket/ISocketHandler.cpp ./src/socket/HttpDebugSocket.cpp ./src/socket/TcpSocket.cpp ./src/socket/ResolvServer.cpp ./src/socket/Mutex.cpp ./src/socket/ResolvSocket.cpp ./src/socket/SocketHandler.cpp ./src/socket/Lock.cpp ./src/socket/HttpPostSocket.cpp ./src/socket/EventTime.cpp ./src/socket/MinionSocket.cpp ./src/socket/CTcpSocket.cpp ./src/socket/Parse.cpp ./src/socket/Event.cpp ./src/socket/SctpSocket.cpp ./src/socket/Base64.cpp ./src/socket/HttpClientSocket.cpp ./src/socket/Socket.cpp ./src/socket/Ipv6Address.cpp ./src/socket/HttpdSocket.cpp ./src/socket/IEventOwner.cpp ./src/socket/HttpdForm.cpp ./src/socket/StdoutLog.cpp ./src/socket/MinderSocket.cpp ./src/socket/HttpGetSocket.cpp ./src/socket/HttpdCookies.cpp ./src/socket/UdpSocket.cpp ./src/socket/Utility.cpp ./src/socket/Thread.cpp ./src/socket/MinderHandler.cpp ./src/socket/MemFile.cpp ./src/socket/File.cpp ./src/socket/EventHandler.cpp ./src/socket/SocketThread.cpp ./src/socket/HttpPutSocket.cpp ./src/socket/Uid.cpp ./src/socket/PoolSocket.cpp ./src/socket/Ipv4Address.cpp ./src/socket/HTTPSocket.cpp ./src/socket/socket_include.cpp ./src/socket/NullCrypt.cpp

grammartest_LDADD =  -lm
grammartest_CFLAGS = -g -Wall -fPIC -DPIC
grammartest_CPPFLAGS = -g -Wall -fPIC -DPIC
grammartest_SOURCES = ./src/jeliza/jeliza.cpp ./src/jeliza/speechsyntax.cpp ./src/jeliza/arrays.cpp ./src/jeliza/string_compare.cpp ./src/jeliza/util.cpp 

test_LDADD =  -lm
test_CFLAGS = -g -Wall -fPIC -DPIC
test_CPPFLAGS = -g -Wall -fPIC -DPIC
test_SOURCES = ./src/jeliza/jeliza.cpp ./src/jeliza/test.cpp ./src/jeliza/arrays.cpp ./src/jeliza/string_compare.cpp ./src/jeliza/util.cpp 

jelizagtk_LDADD = -lm -lgtkmm-2.4  -lgdkmm-2.4  -latkmm-1.6  -lpangomm-1.4  -lcairomm-1.0  -lglibmm-2.4  -lsigc-2.0   -latk-1.0  -lgdk_pixbuf-2.0  -lpangocairo-1.0  -lcairo  -lpangoft2-1.0  -lfontconfig  -lfreetype  -lz  -lpango-1.0  -lm  -lgobject-2.0  -lgmodule-2.0  -lglib-2.0  -lglade-2.0 -lglademm-2.4
jelizagtk_CFLAGS = -g -Wall -fPIC -DPIC 
jelizagtk_CPPFLAGS = -g -Wall -fPIC -DPIC -I/usr/include/gtkmm-2.4  -I/usr/lib/gtkmm-2.4/include  -I/usr/include/glibmm-2.4  -I/usr/lib/glibmm-2.4/include  -I/usr/include/gdkmm-2.4  -I/usr/lib/gdkmm-2.4/include  -I/usr/include/pangomm-1.4  -I/usr/include/atkmm-1.6  -I/usr/include/gtk-2.0  -I/usr/include/sigc++-2.0  -I/usr/lib/sigc++-2.0/include  -I/usr/include/glib-2.0  -I/usr/lib/glib-2.0/include  -I/usr/lib/gtk-2.0/include  -I/usr/include/cairomm-1.0  -I/usr/include/pango-1.0  -I/usr/include/cairo  -I/usr/include/freetype2  -I/usr/include/atk-1.0  -I/usr/include/libglademm-2.4
jelizagtk_SOURCES = ./src/jeliza/jeliza.cpp ./src/gtkgui/jelizagtk.cpp ./src/jeliza/arrays.cpp ./src/jeliza/string_compare.cpp ./src/jeliza/util.cpp 

bytelang_LDADD =  -lm
bytelang_CFLAGS = -g -Wall -fPIC -DPIC
bytelang_CPPFLAGS = -g -Wall -fPIC -DPIC
bytelang_SOURCES = ./src/jeliza/jeliza.cpp ./src/jeliza/bytelanguage.cpp ./src/jeliza/arrays.cpp ./src/jeliza/string_compare.cpp ./src/jeliza/util.cpp 


LD_LIBRARY_PATH=.:./lib
WIN_LD_LIBRARY_PATH=.:./lib:/media/hdb5/data/Dev-Cpp/lib

win:	clean winsocket winjeliza 

linuxupdate:	clean linuxjeliza
winupdate:	clean winjeliza

linuxjeliza: linuxjeliza.o
	mv main.o main.o.bak ; true
	mv server.o server.o.bak ; true
	g++ -shared -o libjeliza.so -Wl,-soname,libjeliza.so *.o 
	strip libjeliza.so
	mv libjeliza.so ./lib
	mv server.o.bak server.o ; true
	mv main.o.bak main.o ; true
	
	g++ $(CFLAGS) -L$(LD_LIBRARY_PATH) -o jeliza main.o compile_tmp/*.o -lm --library jeliza -luuid -lpthread $(DEBUG)
	
	strip jeliza
	mv jeliza bin
	rm -f *.o *~
	rm -rf dist/linux
	mkdir -p dist/linux
	cp -r bin dist/linux
	rm dist/windows/bin/jeliza.exe ; true
	cp -r lib dist/linux
	cp jeliza.inc.html dist/linux
	cp JEliza.txt dist/linux
	@echo "jeliza is up to date"

linuxjeliza.o:
	c++ -I. -L. $(CFLAGS) -c src/jeliza/arrays.cpp
	c++ -I. -L. $(CFLAGS) -c src/jeliza/jeliza.cpp
	c++ -I. -L. $(CFLAGS) -c src/jeliza/main.cpp
	c++ -I. -L. $(CFLAGS) -c src/jeliza/server.cpp
	c++ -I. -L. $(CFLAGS) -c src/jeliza/string_compare.cpp
	c++ -I. -L. $(CFLAGS) -c src/jeliza/util.cpp

socket:
	cd src/socket ; c++ -I. -L. $(CFLAGS) -c *.cpp ; mv *.o ../.. ; cd ../..
	rm -rf compile_tmp
	mkdir compile_tmp
	mv *.o compile_tmp
	rm -f *.o

winjeliza: winjeliza.o
#	mv main.o main.o.bak ; true
#	mv server.o server.o.bak ; true
#	i586-mingw32msvc-g++ -shared -o libjeliza.so -Wl,-soname,libjeliza.so *.o 
#strip libjeliza.so
#	mv libjeliza.so ./lib
#	mv server.o.bak server.o
#	mv main.o.bak main.o
	
	i586-mingw32msvc-g++ $(CFLAGS) -L$(WIN_LD_LIBRARY_PATH) -o jeliza.exe main.o compile_tmp_win/*.o -lm -luuid --library pthread -lwsock32 -lrpcrt4 -lole32 $(DEBUG)
	
	i586-mingw32msvc-strip jeliza.exe
	mv jeliza.exe bin
	rm -f *.o *~
	rm -rf dist/windows
	mkdir -p dist/windows
	cp -r bin dist/windows
	rm dist/windows/bin/jeliza ; true
	cp jeliza.inc.html dist/windows
	cp JEliza.txt dist/windows
	@echo "jeliza is up to date"

winjeliza.o:
	i586-mingw32msvc-c++ -I. -L. $(CFLAGS) -c src/jeliza/arrays.cpp
	i586-mingw32msvc-c++ -I. -L. $(CFLAGS) -c src/jeliza/jeliza.cpp
	i586-mingw32msvc-c++ -I. -L. $(CFLAGS) -c src/jeliza/main.cpp
	i586-mingw32msvc-c++ -I. -L. $(CFLAGS) -c src/jeliza/server.cpp
	i586-mingw32msvc-c++ -I. -L. $(CFLAGS) -c src/jeliza/string_compare.cpp
	i586-mingw32msvc-c++ -I. -L. $(CFLAGS) -c src/jeliza/util.cpp

winsocket:
	rm -f *.o
	cd src/socket ; i586-mingw32msvc-c++ -I. -L. $(CFLAGS) -c *.cpp ; mv *.o ../.. ; cd ../..
	rm -rf compile_tmp_win
	mkdir compile_tmp_win
	mv *.o compile_tmp_win
	rm -f *.o

clean:
	rm -f *.o *.so *~ *.a

packlinux:
	strip jeliza ; true
	mv jeliza bin ; true
	rm -f *.o *~
	rm -rf dist/linux
	mkdir -p dist/linux
	cp -r bin dist/linux
	rm dist/linux/bin/jeliza.exe ; true
	cp -r lib dist/linux
	cp jeliza.inc.html dist/linux
	cp JEliza.txt dist/linux

distribution: packlinux clean
	mkdir tmp_dist ; true
	rm -rf tmp_dist
	mkdir tmp_dist

	cp -r bin/gtk-jeliza tmp_dist/windows
#	mkdir tmp_dist/windows
#	mv tmp_dist/windows/bin/jeliza.exe tmp_dist/windows/ ; true
#	i586-mingw32msvc-strip tmp_dist/windows/jeliza.exe
#	cp ../gui-addon/JElizaWX.exe tmp_dist/windows
#	cp ../gui-addon/transla* tmp_dist/windows
#	cp ../gui-addon/version.ver tmp_dist/windows
	cp JEliza.txt tmp_dist/windows
	cp gpl.txt tmp_dist/windows
	cp Benutzerhandbuch.*  tmp_dist/windows
#	cp jeliza.inc.html tmp_dist/windows

	mkdir tmp_dist/linux
	cp dist/linux/bin/jeliza tmp_dist/linux
	strip tmp_dist/linux/jeliza
	cp jelizagtk tmp_dist/linux/
	cp JEliza.txt tmp_dist/linux
	cp *.txt tmp_dist/linux
	cp gpl.txt tmp_dist/linux
	cp jeliza.inc.html tmp_dist/linux

	mkdir tmp_dist/src
	cp -r * tmp_dist/src ; true
	rm -rf tmp_dist/src/compile_tmp* ; true
	rm -rf tmp_dist/src/tmp_dist ; true
	rm -rf tmp_dist/src/dist/* ; true
	rm -rf tmp_dist/src/gtk/* ; true
	rm -rf tmp_dist/src/distribution ; true
	rm -rf tmp_dist/src/lib/* ; true
	rm -rf tmp_dist/src/bin/* ; true

	find tmp_dist/ -name "*svn*" -exec rm -rf '{}' \; ; true
	find tmp_dist/ -name "*svn*" -exec rm -rf '{}' \; ; true
	find tmp_dist/ -name "*svn*" -exec rm -rf '{}' \; ; true

	find tmp_dist/ -name "*~*" -exec rm -rf '{}' \; ; true
	find tmp_dist/ -name "*~*" -exec rm -rf '{}' \; ; true
	
	rm -rf ../distribution/*.zip ; true
	rm -rf ../distribution/*.gz ; true
	mkdir ../distribution ; true
	
	cd tmp_dist ; cd windows ; zip -r ../../../distribution/win.zip *
	cd tmp_dist ; cd linux ; tar cplzSf ../../../distribution/linux.tar.gz *
	cd tmp_dist ; cd src ; zip -r ../../../distribution/src.zip *
	
java:
	find javagui -name "*.java" -exec javac '{}' \;
