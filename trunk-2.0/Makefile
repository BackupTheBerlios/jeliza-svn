# DEBUG=-DDEBUG
CFLAGS=-g -Wall -fPIC -DPIC -O3 -Os  -Isrc/socket
LD_LIBRARY_PATH=.:./lib

##	g++ $(CFLAGS) -o server server.o lib/* -lm --library PocoUtil --library PocoNet --library PocoXML --library PocoFoundation --library PocoUtild --library PocoNetd --library PocoXMLd --library PocoFoundationd --library uuid --library pthread $(DEBUG)

all:	clean jeliza 

jeliza: socket jeliza.o
#	g++ $(CFLAGS) -o jeliza jeliza.o main.o util.o arrays.o string_compare.o -lm $(DEBUG)

	mv main.o main.o.bak
	mv server.o server.o.bak
	g++ -shared -o libjeliza.so -Wl,-soname,libjeliza.so *.o 
	strip libjeliza.so
	mv libjeliza.so ./lib
	mv server.o.bak server.o
	mv main.o.bak main.o
	
#	mv server.o server.o.bak
##	g++ $(CFLAGS) -o jeliza main.o -lm --library jeliza $(DEBUG)
	g++ $(CFLAGS) -L$(LD_LIBRARY_PATH) -o jeliza main.o compile_tmp/*.o -lm --library jeliza -luuid -lpthread $(DEBUG)
#	mv server.o.bak server.o
	
#	mv main.o main.o.bak
##	g++ $(CFLAGS) -L$(LD_LIBRARY_PATH) -o server server.o compile_tmp/*.o -lm --library jeliza -luuid -lpthread $(DEBUG)
#	mv main.o.bak main.o
	
	strip jeliza ; mv jeliza bin
##	strip server ; mv server bin
	rm -f *.o *~
	rm -rf dist
	mkdir -p dist/linux
	cp -r bin dist/linux
	cp -r lib dist/linux
	cp jeliza.inc.html dist/linux
	@echo "jeliza is up to date"

jeliza.o:
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

clean:
	rm -f *.o *.so *~ *.a
