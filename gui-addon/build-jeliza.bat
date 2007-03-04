Y:
cd Y:\pyinstaller_1.2

C:\python2.4\python.exe Configure.py
C:\python2.4\python.exe Makespec.py -c --upx --onefile Y:\workspace\JEliza\gui-addon\JElizaWX.py Y:\workspace\JEliza\gui-addon\DatabaseArt.py Y:\workspace\JEliza\gui-addon\JEliza.py Y:\workspace\JEliza\gui-addon\JElizaTranslation.py Y:\workspace\JEliza\gui-addon\JWWF.py Y:\workspace\JEliza\gui-addon\Save_py.py Y:\workspace\JEliza\gui-addon\str_py.py Y:\workspace\JEliza\gui-addon\Util.py Y:\workspace\JEliza\gui-addon\io_py.py Y:\workspace\JEliza\gui-addon\JEliza_py.py  Y:\workspace\JEliza\gui-addon\mysql_native.py Y:\workspace\JEliza\gui-addon\stdlib.py Y:\workspace\JEliza\gui-addon\umwandlung_py.py
C:\python2.4\python.exe Build.py JElizaWX\JElizaWX.spec

del Y:\workspace\JEliza\gui-addon\JElizaWX.exe
copy Y:\pyinstaller_1.2\JElizaWX\JElizaWX.exe Y:\workspace\JEliza\gui-addon

pause
