echo "$1" > reply.fra
for x in 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1; do
	if [ -f "canGetAnswer.reply" ] ; then
		echo "$(cat reply.ant)"
		rm canGetAnswer.reply
		exit
	fi
	
	sleep 1;
done
