#!/bin/sh

./compile.sh
java -Djava.library.path=lib/linux_gtk2/ -cp .:./lib/jaimbot-lib-1.4.jar:lib/linux_gtk2/swt.jar:lib/swingwt.jar:./lib/rdf-1.0.jar:jeliza.jar:lib/bin_extra.jar:ext/speech/lib/freetts.jar $@ jeliza.core.JElizaGui
