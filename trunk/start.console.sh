#!/bin/sh

./compile.sh 

/opt/java/jdk-1.5/bin/java -Djava.library.path=lib/linux_gtk2/ -cp .:./lib/jaimbot-lib-1.4.jar:lib/linux_gtk2/swt.jar:lib/swingwt.jar:./lib/rdf-1.0.jar:jeliza.jar:lib/bin_extra.jar jeliza.core.JElizaConsole $@
