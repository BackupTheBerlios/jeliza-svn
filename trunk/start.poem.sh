#cd bin
./compile.sh
java -cp .:./lib/jaimbot-lib-1.4.jar:./lib/rdf-1.0.jar:jeliza.jar:lib/bin_extra.jar  jeliza/poem/Gedichte $@
