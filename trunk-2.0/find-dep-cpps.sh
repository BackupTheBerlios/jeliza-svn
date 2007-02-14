#!/bin/sh

for x in $(
for f in $(cat src/jeliza/server.cpp | grep '#include "Poco/' | cut -d'"' -f2); do
echo $f | grep -v -i WIN
for f2 in $(cat ${f} | grep '#include "Poco/' | cut -d'"' -f2); do
echo $f2 | grep -v -i WIN
for f3 in $(cat ${f2} | grep '#include "Poco/' | cut -d'"' -f2); do
echo $f3 | grep -v -i WIN
for f4 in $(cat ${f3} | grep '#include "Poco/' | cut -d'"' -f2); do
echo $f4 | grep -v -i WIN
for f5 in $(cat ${f4} | grep '#include "Poco/' | cut -d'"' -f2); do
echo $f5 | grep -v -i WIN
for f6 in $(cat ${f5} | grep '#include "Poco/' | cut -d'"' -f2); do
echo $f6 | grep -v -i WIN
for f7 in $(cat ${f6} | grep '#include "Poco/' | cut -d'"' -f2); do
echo $f7 | grep -v -i WIN

done
done
done
done
done
done
done
); do

echo -n "cp "
echo -n "$(find /media/hdb5/workspace/JEliza/poco/poco-1.2.8 | grep "$x")"
echo " /media/hdb5/workspace/JEliza/c++/src/Poco/"
done



