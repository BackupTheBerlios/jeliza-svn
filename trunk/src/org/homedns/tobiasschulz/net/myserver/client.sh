clear

cd /hdb5/workspace/ClientServer_v2

export CLASSPATH=.:./swt/org.eclipse.core.runtime.compatibility_3.1.0.jar:./swt/org.eclipse.core.runtime_3.1.1.jar:./swt/org.eclipse.swt_3.1.0.jar:./swt/org.eclipse.swt.gtk.linux.x86_3.1.1.jar:./swt/org.eclipse.jface_3.1.1.jar:./swt/org.eclipse.jface.text_3.1.1.jar:$CLASSPATH
export LD_LIBRARY_PATH=$CLASSPATH:./swt/libswt-pi-gtk-3139.so:$LD_LIBRARY_PATH
export PATH=$PATH:$CLASSPATH


java -Djava.library.path=.:org.eclipse.swt.gtk.linux.x86_3.1.1.jar:./swt:./swt/libswt-pi-gtk-3139.so Client
