@echo off
cls

set CLASSPATH=.;./swt_win/*.jar;./swt_win/org.eclipse.jface.text_3.1.1.jar;./swt_win/org.eclipse.jface_3.1.1.jar;./swt_win/org.eclipse.ant.core_3.1.1.jar;./swt_win/org.eclipse.core.boot_3.1.0.jar;./swt_win/org.eclipse.core.commands_3.1.0.jar;./swt_win/org.eclipse.core.expressions_3.1.0.jar;./swt_win/org.eclipse.core.filebuffers_3.1.0.jar;./swt_win/org.eclipse.core.resources_3.1.0.jar;./swt_win/org.eclipse.core.resources.compatibility_3.1.0.jar;./swt_win/org.eclipse.core.resources.win32_3.1.0.jar;./swt_win/org.eclipse.core.runtime_3.1.1.jar;./swt_win/org.eclipse.core.runtime.compatibility_3.1.0.jar;./swt_win/org.eclipse.core.variables_3.1.0.jar;./swt_win/org.eclipse.debug.core_3.1.0.jar;./swt_win/org.eclipse.jdt.core_3.1.1.jar;./swt_win/org.eclipse.ltk.core.refactoring_3.1.0.jar;./swt_win/org.eclipse.pde.core_3.1.1.jar;./swt_win/org.eclipse.swt_3.1.0.jar;./swt_win/org.eclipse.swt.win32.win32.x86_3.1.1.jar;./swt_win/org.eclipse.team.core_3.1.1.jar;./swt_win/org.eclipse.team.cvs.core_3.1.1.jar;./swt_win/org.eclipse.update.core_3.1.1.jar;./swt_win/org.eclipse.update.core.win32_3.1.0.jar;%CLASSPATH%
set LD_LIBRARY_PATH=.;./swt_win/;%CLASSPATH%;/swt_win/swt-win32-3139.dll;;/swt_win/swt-win32-3139;%LD_LIBRARY_PATH%
set PATH=%PATH%;%CLASSPATH%

java -Djava.library.path=.;%LD_LIBRARY_PATH%;%CLASSPATH%;%PATH% ServerGUI

pause