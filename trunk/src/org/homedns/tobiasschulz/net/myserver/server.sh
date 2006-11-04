clear

cd /usr/share/tsclasses/

chmod 0777 org/homedns/tobiasschulz/net/myserver/*.sh

. org/homedns/tobiasschulz/net/myserver/ram.sh

function myrun {
echo "######### Tobias Webserver wird gestartet ... ##########"

chmod 0666 org/homedns/tobiasschulz/net/myserver/log/access_log.txt

chmod 0777 org/homedns/tobiasschulz/net/myserver/php
chmod 0777 org/homedns/tobiasschulz/net/myserver/*.sh

nice $(which java) -Xmx${RAM_JAVA}M -Xms${RAM_JAVA}M \
org.homedns.tobiasschulz.net.myserver.Server -XX:NewSize=$[$RAM_JAVA / 2]m -XX:MaxNewSize=$[$RAM_JAVA / 2]m -XX:SurvivorRatio=14 2> \
org/homedns/tobiasschulz/net/myserver/log/error_log.txt \
| tee -a org/homedns/tobiasschulz/net/myserver/log/access_log.txt 

if [ "$?" = "1" ]; then
	myrun
else
	echo $1
	myrun
fi

echo "######### Tobias Webserver wird beendet ... ##########"

}

myrun


