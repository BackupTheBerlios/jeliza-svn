echo Lade Hirn-Installationprogramm
wget http://tobiasschulz.homedns.org/intelligenz/jeliza/gethirn.sh
chmod 0777 *.sh
echo Lade Hirn
./gethirn.sh 2>&1 | tee gethirn.log
echo Hirn geladen
echo Hirn ist unter ./gehirn/ zu finden

