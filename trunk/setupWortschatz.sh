echo Lade Wortschatz-Installationprogramm
wget http://tobiasschulz.homedns.org/intelligenz/jeliza/getWortschatz.sh
chmod 0777 *.sh
echo Lade Wortschatz
./getWortschatz.sh 2>&1 | tee getWortschatz.log
echo Wortschatz geladen
echo Wortschatz ist unter ./gehirn/ zu finden


