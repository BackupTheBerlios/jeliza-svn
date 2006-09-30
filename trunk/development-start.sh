cd /media/hdb5/workspace/Eliza/trunk
echo Starting Program
java -cp .:./tsclasses.jar:./org/homedns/tobiasschulz/apps/jeliza JElizaGui | tee tmp
echo Program halted
