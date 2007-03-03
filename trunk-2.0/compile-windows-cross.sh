# DEBUG=-DDEBUG
#CFLAGS=-g -Wall -fPIC -DPIC -O3 -Os  -Isrc/socket
CFLAGS=-g -Wall -fPIC -DPIC -Isrc/socket
LD_LIBRARY_PATH=.:./lib
WIN_LD_LIBRARY_PATH=.:./lib:/media/hdb5/data/Dev-Cpp/lib

##	g++ ${CFLAGS} -o server server.o lib/* -lm --library PocoUtil --library PocoNet --library PocoXML --library PocoFoundation --library PocoUtild --library PocoNetd --library PocoXMLd --library PocoFoundationd --library uuid --library pthread $(DEBUG)

rm -f *.o *.so *~ *.a
rm -f *.o
cd src/socket ; i586-mingw32msvc-c++ -I. -L. ${CFLAGS} -c *.cpp ; mv *.o ../.. ; cd ../..
rm -rf compile_tmp_win
mkdir compile_tmp_win
mv *.o compile_tmp_win
rm -f *.o

i586-mingw32msvc-c++ -I. -L. ${CFLAGS} -c src/jeliza/arrays.cpp
i586-mingw32msvc-c++ -I. -L. ${CFLAGS} -c src/jeliza/jeliza.cpp
i586-mingw32msvc-c++ -I. -L. ${CFLAGS} -c src/jeliza/main.cpp
i586-mingw32msvc-c++ -I. -L. ${CFLAGS} -c src/jeliza/server.cpp
i586-mingw32msvc-c++ -I. -L. ${CFLAGS} -c src/jeliza/string_compare.cpp
i586-mingw32msvc-c++ -I. -L. ${CFLAGS} -c src/jeliza/util.cpp

i586-mingw32msvc-g++ ${CFLAGS} -L${WIN_LD_LIBRARY_PATH} -o jeliza.exe main.o compile_tmp_win/*.o -lm -luuid --library pthread -lwsock32 -lrpcrt4 -lole32 ${DEBUG}

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


rm -rf tmp_dist; true
mkdir tmp_dist
cp -r dist/windows tmp_dist
cp JEliza.txt tmp_dist/windows
cp gpl.txt tmp_dist/windows
cp jeliza.inc.html tmp_dist/windows
cp -r dist/linux tmp_dist
cp JEliza.txt tmp_dist/linux
cp gpl.txt tmp_dist/linux
cp jeliza.inc.html tmp_dist/linux

mkdir tmp_dist/src
cp Makefile tmp_dist/src
cp JEliza.txt tmp_dist/src
cp gpl.txt tmp_dist/src
cp jeliza.inc.html tmp_dist/src
cp -r src/ tmp_dist/src

find tmp_dist/ -name "*svn*" -exec rm -rf '{}' \; ; true
find tmp_dist/ -name "*svn*" -exec rm -rf '{}' \; ; true
find tmp_dist/ -name "*svn*" -exec rm -rf '{}' \; ; true

find tmp_dist/ -name "*~*" -exec rm -rf '{}' \; ; true
find tmp_dist/ -name "*~*" -exec rm -rf '{}' \; ; true

cd tmp_dist ; cd windows ; zip -r ../win.zip *
cd tmp_dist ; cd linux ; tar cplzSf ../linux.tar.gz *
cd tmp_dist ; cd src ; zip -r ../src.zip *

