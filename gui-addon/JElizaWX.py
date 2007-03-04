#!/usr/bin/python
# -*- coding: utf-8 -*-

# -------- English: --------
# 
# This is part of JEliza 2.0.
# Copyright 2006 by Tobias Schulz
# WWW: http://jeliza.ch.to/
# 
# JEliza is free software; you can redistribute it and/or      
# modify it under the terms of the GNU General Public    
# License as published by the Free Software Foundation; either  
# version 2.1 of the License, or (at your option) any later     
# version.                                                      
#                                                               
# JEliza is distributed in the hope that it will be useful, but 
# WITHOUT ANY WARRANTY; without even the implied warranty of    
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.          
# See the GNU General Public License for more details.   
#                                                               
# You should have received a copy of the GNU GPL               
# along with JEliza (file "gpl.txt") ; if not, write           
# to the Free Software Foundation, Inc., 51 Franklin St,        
# Fifth Floor, Boston, MA  02110-1301  USA
# 
# 
# -------- Deutsch: --------
# 
# Dies ist ein Teil von  JEliza 2.0.
# Copyright 2006 by Tobias Schulz
# WWW: http://jeliza.ch.to/
# 
# JEliza ist freie Software. Sie können dieses Programm unter   
# den Bedingungen der GNU General Public License, wie    
# von der Free Software Foundation veroeffentlicht, weitergeben 
# und/oder modifizieren, entweder gemaess Version 2.1 der       
# Lizenz oder (nach Ihrer Option) jeder späteren Version.       
#                                                               
# Die Veröffentlichung JElizas erfolgt in der Hoffnung, dass es 
# Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE,    
# sogar OHNE die Garantie der MARKTREIFE oder der               
# VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK.                    
# Details finden Sie in der GNU General Public License.  
#                                                               
# Sie sollten ein Exemplar der GNU General Public        
# License zusammen mit JEliza (Datei gpl.txt) erhalten haben.  
# Falls nicht, schreiben Sie an die Free Software Foundation,   
# Inc., 51 Franklin St, 5. Stock, Boston, MA  02110-1301 USA


# Python Imports
import sys
import urllib
import re
from threading import Thread
import time

# GUI Imports
import wx

# My Imports
from Util import *
from JEliza import *
from JWWF import *
from JElizaTranslation import *

EVT_RESULT_ID = wx.NewId()

guiLang = "en"

class AppURLopener(urllib.FancyURLopener):
    version = "Mozilla/4.0 (compatible; MSIE 6.0; Windows 98)"
    
class OutputRedirect:
    
    def __init__(self, aWxTextCtrl):
        self.log = aWxTextCtrl
        
    def write(self, str):
        self.log.AppendText(str)

class JElizaWX:
        
    def __init__(self):
       self.app = wx.App(redirect = False)
      
    def gui(self):
        global guiLang
        
        """ Initialisation """
        self.language = "roh"
        self.util = Util()
        self.util.chProfile("default")
        self.dirname = "."
        
        """ Create new JEliza """
        self.jel = JEliza()
        
        """ Create Frame """
        self.frame = wx.Frame(None, wx.ID_ANY, GetString(guiLang, "0001"), 
        wx.DefaultPosition, wx.Size(800, 700))
        self.frame.CreateStatusBar()
        
        """ Create Sizers """
        self.sizer = wx.GridBagSizer()
        self.topsizer = wx.BoxSizer(wx.VERTICAL)
        self.control = wx.GridBagSizer()
        self.langs = wx.GridBagSizer()
        
        """ Create some Comboboxes """
        self.langin = wx.ComboBox(self.frame, 30, "Deutsch", 
                                  choices = ["Deutsch", "Englisch", "Spanisch", "Italienisch", "Franzoesisch", "Portugiesisch", "andere Sprache", "Finnisch"], 
                                  style=wx.CB_DROPDOWN)
        self.lang = wx.ComboBox(self.frame, 31, "Roh", 
                                choices = ["Deutsch", "Englisch", "Spanisch", "Italienisch", "Franzoesisch", "Portugiesisch", "Finnisch"], 
                                style=wx.CB_DROPDOWN)
        wx.EVT_COMBOBOX(self.frame, 30, self.chLang)
        wx.EVT_COMBOBOX(self.frame, 31, self.chLang)
        
        """ Create some "Statictext"s """
        statictext = wx.StaticText(self.frame, 50, 
                                "\n" + " " + GetString(guiLang, "0002") + ":" + "\n")
        statictext.SetFont(wx.Font(9, wx.SWISS, wx.NORMAL, wx.BOLD, False, 'Verdana'))
        self.control.Add(statictext, (0, 0), wx.DefaultSpan, wx.ALIGN_LEFT)
        self.control.Add(wx.StaticText(self.frame, 50, GetString(guiLang, "0003") + ":"), (1, 0), wx.DefaultSpan, wx.ALIGN_LEFT)
        self.control.Add(self.langin, (2, 0), wx.DefaultSpan, wx.EXPAND)
        self.control.Add(wx.StaticText(self.frame, 50, GetString(guiLang, "0004") + ":"), (3, 0), wx.DefaultSpan, wx.ALIGN_LEFT)
        self.control.Add(self.lang, (4, 0), wx.DefaultSpan, wx.EXPAND)
 
        statictext = wx.StaticText(self.frame, 50, 
                                   "\n " + GetString(guiLang, "0005") + ":" + "\n")
        statictext.SetFont(wx.Font(9, wx.SWISS, wx.NORMAL, wx.BOLD, False, 'Verdana'))
 
        self.control.Add(statictext, (6, 0), wx.DefaultSpan, wx.ALIGN_LEFT)
        self.gendata = wx.Button(self.frame, 70, GetString(guiLang, "0006"))
        self.control.Add(self.gendata, (7, 0), wx.DefaultSpan, wx.EXPAND)
        self.cleardata = wx.Button(self.frame, 71, GetString(guiLang, "0007"))
        self.control.Add(self.cleardata, (8, 0), wx.DefaultSpan, wx.EXPAND)
        self.loadonlinewiss = wx.Button(self.frame, 72, GetString(guiLang, "0008"))
        self.control.Add(self.loadonlinewiss, (9, 0), wx.DefaultSpan, wx.EXPAND)
        self.loadstdwiss = wx.Button(self.frame, 73, GetString(guiLang, "0009"))
        self.control.Add(self.loadstdwiss, (10, 0), wx.DefaultSpan, wx.EXPAND)
 
        """ Create some Events """
        wx.EVT_BUTTON(self.frame, 70, self.genDatabase)
        wx.EVT_BUTTON(self.frame, 71, self.clearDatabase)
        wx.EVT_BUTTON(self.frame, 72, self.loadonlinewissen)
        wx.EVT_BUTTON(self.frame, 73, self.loadstandardwissen)
        
        """ Add Menubar """
        self.menubar = wx.MenuBar()
        self.datei = wx.Menu()
        self.help = wx.Menu()
        
        """ Create some Widgets """
        self.button = wx.Button(self.frame, 10, GetString(guiLang, "0010"))
        wx.EVT_BUTTON(self.frame, 10, self.say)
        self.entry = wx.TextCtrl(self.frame, 555, "", style=wx.TE_PROCESS_ENTER | wx.TE_DONTWRAP)
        wx.EVT_TEXT_ENTER(self.frame, 555, self.say)
        self.log = wx.TextCtrl(self.frame, 9, style=wx.TE_MULTILINE) 
        self.gauge = wx.Gauge(self.frame, -1, 100, (10, 70), (390, 25))#, wx.VERTICAL)
        
        """ Create Menu entries """
        self.datei.Append(101, GetString(guiLang, "0011"), '')
        self.datei.Append(102, GetString(guiLang, "0012"), '')
        self.datei.AppendSeparator()
        self.datei.Append(103, GetString(guiLang, "0013"), '')
        self.datei.Append(104, GetString(guiLang, "0014"), '')
        
        self.help.Append(201, GetString(guiLang, "0015"), '')
       
        self.frame.Bind(wx.EVT_MENU, self.openDialog, id=101)
        self.frame.Bind(wx.EVT_MENU, self.saveDialog, id=102)
        self.frame.Bind(wx.EVT_MENU, self.openWissen, id=103)
        self.frame.Bind(wx.EVT_MENU, self.saveWissen, id=104)
        
        self.frame.Bind(wx.EVT_MENU, self.about, id=201)
        
        self.menubar.Append(self.datei, GetString(guiLang, "0016"))
        self.menubar.Append(self.help, GetString(guiLang, "0017"))
        self.frame.SetMenuBar(self.menubar)
        
        """ Add some widgets to a sizer """
        self.sizer.Add(self.log, (0, 0), (1, 2), wx.EXPAND)
        self.sizer.Add(self.control, (0, 2), (1, 2), wx.ALIGN_LEFT)
        self.sizer.Add(self.entry, (1, 0), (1, 3), wx.ALIGN_CENTER | wx.EXPAND)
        self.sizer.Add(self.gauge, (2, 0), (1, 4), wx.ALIGN_CENTER | wx.EXPAND)
        self.sizer.Add(self.button, (1, 3), wx.DefaultSpan, wx.ALIGN_RIGHT)
        self.sizer.AddGrowableRow(0)
        self.sizer.AddGrowableCol(1)
        self.sizer.AddGrowableCol(0)
        
        """ Set the Sizer """
        self.frame.SetSizer(self.sizer)
        
        """ Print some infos """
        print ""
        print "+---------------------------------------------------------------+"
        print "| JEliza                                            Version 2.0 |"
        print "| Copyright 2006 - 2007 by Tobias Schulz                        |"
        print "| License: GNU Gerneral Public License, www.gnu.org      |"
        print "+---------------------------------------------------------------+"
        print GetString(guiLang, "0018")
        print "+---------------------------------------------------------------+"
        print ""
        print "Application Starter : I 'm going to start the application now"

        """ Show the Window """
        self.frame.Center()       
        self.frame.Show(True)
        
        """ Check for updates """
        self.checkForUpdates()       
        print "Application Starter : OK ; I checked for updates"
       
        gau = GaugeThread(self.gauge, self.frame)
        gau.start()
        
        self.setGaugeValue(0)
        
        EVT_RESULT(self.frame, self.OnResult) 
        
        print "Application Starter : OK"

        self.app.MainLoop()
        
    def say(self, x):
        self.jel.language = self.language
        self.jel.system = "markov"
        self.util = Util()
        self.jel.file = "JEliza.txt"
        self.jel.debug = 1
        input = self.entry.GetLineText(0) 

        self.input = input
        out = self.jel.getAnswer(input)
        self.ready = True
        self.win.entry.Clear()
        self.win.log.AppendText("Mensch: " + input.decode("iso-8859-1").encode("utf-8") + "\n")
        self.win.log.AppendText("JEliza: " + out.decode("iso-8859-1").encode("utf-8") + "\n")
        print "Benutzte Sprache: " + self.win.jel.language
        if out == ".":
            dlg2 = wx.MessageDialog(self.win.frame, "Es kann sein, dass JEliza sich nicht in "
                               "der Ausgabesprache ausdruecken kann, da " 
                               "der notwendige Programmteil noch nicht implementiert wurde."
                               , "Language Error", wx.OK)
            dlg2.ShowModal()
            dlg2.Destroy()
        

            
    def setGaugeValue(self, x):
        fp = open("gauge.txt", "w")
        fp.write(str(x))
        fp.close()
 
    def checkForUpdates(self):
        up = UpdateChecker()
        up.run()

    def genDatabase(self, x):
        self.jel.language = self.language
        self.util = Util()
        self.jel.file = self.util.getProfilePath(self.util.getCurrentProfile()) + "/input.txt"
        self.jel.debug = 0
        self.jel.genDatabase()
 
    def clearDatabase(self, x):
        self.jel.language = self.language
        self.util = Util()
        self.jel.file = self.util.getProfilePath(self.util.getCurrentProfile()) + "/input.txt"
        self.jel.debug = 0
        self.jel.clearDatabase()
        
    def chLang(self, event):
        inl = self.langin.GetValue().lower().strip().replace("andere sprache", "roh")
        outl = self.lang.GetValue().lower().strip()
        if inl == "deutsch" and outl == "deutsch":
            self.language = "roh"
        else:
            self.language = inl + " -> " + outl
        print self.language
        
    def chSys(self, event):
        sys = self.useSystem.GetValue().lower().strip()
        sys = sys.replace("neuronales netz", "net")
        sys = sys.replace("hidden markov modell", "markov")
        self.system = sys
        print self.system
        
    def saveDialog(self, x):
        dlg = wx.FileDialog(self.frame, "Gespaech speichern", self.dirname, "JEliza-Gespraech.gsp", "*.gsp", wx.SAVE)
        if dlg.ShowModal() == wx.ID_OK:
            self.filename=dlg.GetFilename()
            self.dirname=dlg.GetDirectory()
            f = os.path.join(self.dirname, self.filename)
            if f == 0 or f == "":
                return
            if not f.endswith(".gsp"):
                f = f + ".gsp"
            fp=open(f, 'w')
            try:
                fp.writelines(self.log.GetValue())
            except IOError:
                dlg2 = wx.MessageDialog(self.frame, "Konnte Gespraech nicht speichern", "Error", wx.OK)
                dlg2.ShowModal()
                dlg2.Destroy()
                return
            dlg2 = wx.MessageDialog(self.frame, "Gespraech wurde gespeichert", "Nachricht", wx.OK)
            dlg2.ShowModal()
            dlg2.Destroy()
        dlg.Destroy()        
        
    def openDialog(self, x):
        dlg = wx.FileDialog(self.frame, "Gespaech laden", self.dirname, "", "*.gsp", wx.OPEN)
        if dlg.ShowModal() == wx.ID_OK:
            self.filename=dlg.GetFilename()
            self.dirname=dlg.GetDirectory()
            f = os.path.join(self.dirname, self.filename)
            if f == 0 or f == "":
                return
            fp=open(f, 'r')
            try:
                self.log.SetValue(fp.read())
            except IOError:
                dlg2 = wx.MessageDialog(self.frame, "Konnte Gespraech nicht laden", "Error", wx.OK)
                dlg2.ShowModal()
                dlg2.Destroy()
                return
            dlg2 = wx.MessageDialog(self.frame, "Gespraech wurde geladen", "Nachricht", wx.OK)
            dlg2.ShowModal()
            dlg2.Destroy()
        dlg.Destroy()        
        
    def saveWissen(self, x):
        dlg = wx.FileDialog(self.frame, "Wissen speichern", self.dirname, "JEliza-Wissen.jwwf", "*.jwwf", wx.SAVE)
        if dlg.ShowModal() == wx.ID_OK:
            self.filename=dlg.GetFilename()
            self.dirname=dlg.GetDirectory()
            f = os.path.join(self.dirname, self.filename)
            if f == 0 or f == "":
                return
            if not f.endswith(".jwwf"):
                f = f + ".jwwf"
            try:
                jwwf = JWWF()
                jwwf.save(self.util.getCurrentProfile(), f)
            except IOError:
                dlg2 = wx.MessageDialog(self.frame, "Konnte Wissen nicht speichern", "Error", wx.OK)
                dlg2.ShowModal()
                dlg2.Destroy()
                return 
 
            dlg2 = wx.MessageDialog(self.frame, "Wissen wurde gespeichert", "Nachricht", wx.OK)
            dlg2.ShowModal()
            dlg2.Destroy()
        dlg.Destroy()        
        
    def openWissen(self, x):
        dlg = wx.FileDialog(self.frame, "Wissen laden", self.dirname, "", "*.*", wx.OPEN)
        if dlg.ShowModal() == wx.ID_OK:
            self.filename=dlg.GetFilename()
            self.dirname=dlg.GetDirectory()
            f = os.path.join(self.dirname, self.filename)
            if f == 0 or f == "":
                return
            fp=open(f, 'r')
            try:
                jwwf = JWWF()
                jwwf.load(self.util.getCurrentProfile(), f)
            except IOError:
                dlg2 = wx.MessageDialog(self.frame, "Konnte Wissen nicht laden", "Error", wx.OK)
                dlg2.ShowModal()
                dlg2.Destroy()
                return
            dlg2 = wx.MessageDialog(self.frame, "Wissen wurde geladen", "Nachricht", wx.OK)
            dlg2.ShowModal()
            dlg2.Destroy()
        dlg.Destroy()     
       
    def about(self, x):
        fp = open("version.ver", "r")
        try:
             ver = fp.read().strip()
        except IOError:
             ver = "?"
        
        txt = ""
        
        txt += "JEliza " + ver + "\n"
        txt += "Copyright 2006 - 2007 by Tobias Schulz, http://jeliza.berlios.de" + "\n"
        txt += "License: GNU Gerneral Public License, www.gnu.org" + "\n"
        txt += "\n" + "\n"
        txt += GetString(guiLang, "0018").replace(" |\n| ", "\n").replace("|", "").strip() + "\n"
#        print "+---------------------------------------------------------------+"

        dlg2 = wx.MessageDialog(self.frame, txt, 
        "JEliza - Version " + ver, wx.OK)
        dlg2.ShowModal()
        dlg2.Destroy()
        
    def loadonlinewissen(self, x):
        try:
            con = urllib.urlopen("http://jeliza.berlios.de/jeliza-applet/jeliza.sat.brn.php").read()
        except IOError:
            dlg2 = wx.MessageDialog(self.frame, "Herunterladen war nicht moeglich"
                             "\nKlicken sie auf OK!", 
                             "Herunterladen", wx.OK)
            dlg2.ShowModal()
        fp = open("tmp.tmp", "w")
        fp.write(con)
        fp.close()
        jwwf = JWWF()
        jwwf.load(self.util.getCurrentProfile(), "tmp.tmp")
        self.genDatabase("")
        dlg2 = wx.MessageDialog(self.frame, "Wissen der online-Version geladen", 
                         "Erfolg", wx.OK)
        dlg2.ShowModal()
       
    def loadstandardwissen(self, x):
        try:
            con = urllib.urlopen("http://jeliza.berlios.de/addons/jeliza.standard.jwwf").read()
        except IOError:
            dlg2 = wx.MessageDialog(self.frame, "Herunterladen war nicht moeglich"
                             "\nKlicken sie auf OK!", 
                             "Herunterladen", wx.OK)
            dlg2.ShowModal()
        fp = open("tmp.tmp", "w")
        fp.write(con)
        fp.close()
        jwwf = JWWF()
        jwwf.load(self.util.getCurrentProfile(), "tmp.tmp")
        self.genDatabase("")
        dlg2 = wx.MessageDialog(self.frame, "Wissen der Standard-Version geladen", 
                         "Erfolg", wx.OK)
        dlg2.ShowModal()
       
    def initWortschatz(self, x):
        """ """
       
    def OnResult(self, event):
        self.win = self
        if event.data.startswith("gauge"):
            self.gauge.SetValue(int(event.data.split("=")[1]))
            return
        elif event.data.startswith("endOfAsk"):
            #            input = self.win.entry.GetLineText(0)
        #self.win.input = input
        #out = self.jel.getAnswer(input)
        #wx.PostEvent(self._notify_window, ResultEvent("endOfAsk=" + out.replace("=", " ")))
            input = self.input
            out = event.data.split("=")[1]
            self.ready = True
            self.win.entry.Clear()
            self.win.log.AppendText("Mensch: " + input.decode("iso-8859-1").encode("utf-8") + "\n")
            self.win.log.AppendText("JEliza: " + out.decode("iso-8859-1").encode("utf-8") + "\n")
            print "Benutzte Sprache: " + self.win.jel.language
            if out == ".":
                dlg2 = wx.MessageDialog(self.win.frame, "Es kann sein, dass JEliza sich nicht in "
                                    "der Ausgabesprache ausdruecken kann, da " 
                                    "der notwendige Programmteil noch nicht implementiert wurde."
                                    , "Language Error", wx.OK)
                dlg2.ShowModal()
                dlg2.Destroy()
        
            time.sleep(2)
            self.win.setGaugeValue(0)
          
       
class GaugeThread(Thread):
    
    def __init__(self, gau, win):
        Thread.__init__(self)
        self._notify_window = win
        self.gauge = gau
        
    def run(self):
        while True:
            k = int(open("gauge.txt", "r").read().strip())
            wx.PostEvent(self._notify_window, ResultEvent("gauge=" + str(k)))
            time.sleep(1.0)
       
class SayThread(Thread):
    
    def __init__(self, win, notify_win):
        Thread.__init__(self)
        self._notify_window = notify_win
        self.win = win
        self.jel = self.win.jel
        self.ready = False
        
    def run(self):
        input = self.win.entry.GetLineText(0)
        self.win.input = input
        out = self.jel.getAnswer(input)
        wx.PostEvent(self._notify_window, ResultEvent("endOfAsk=" + out.replace("=", " ")))
       
class UpdateChecker:

    def run(self):
        print "Checking for Updates"
        try:
            page = urllib.urlopen("http://jeliza.berlios.de/aktuelle-version.php").read().split("\n")
        except IOError:
            return
        for line in page:
            line = line.strip()
            if re.search("<b>Aktuelle Version: ", line):
                l2 = line.split("<b>Aktuelle Version: ")[1]
                l3 = l2.split("</b>")[0]
                newversion = l3.strip().lower()
                print newversion
                oldversion = open("version.ver", "r").read().strip().lower()
                if newversion != oldversion:
                    dlg2 = wx.MessageDialog(None, "Die neue Version " + newversion +
                                               " ist verfuegbar. Es wird dringend empfohlen, "
                                               "die Internetseite \n"
                                               "http://jeliza.berlios.de/\n"
                                               "zu besuchen und die aktuelle Version herunterzuladen.\n\n"
                                               "Neue Versionen stellen Verbesserungen, Sicherheitsupdates"
                                               " und neue Funktionen bereit und korrigieren Fehler.\n"
                                               "Falls ihnen in der ihnen vorliegenden Version von JEliza "
                                               "ein Fehler aufgefallen ist, ist es ratsam, "
                                               "die neue Version zu installieren. "
                                               "Auch wenn das nicht der Fall ist:"
                                               "\n\n\n"
                                               "Gehen sie nun bitte auf "
                                               "\nhttp://jeliza.berlios.de/\n"
                                               "und laden sie die neue Version herunter.", 
                                               "Herunterladen", wx.OK)
                    dlg2.ShowModal()
                    dlg2.Destroy()
                break

def EVT_RESULT(win, func):
    """Define Result Event."""
    win.Connect(-1, -1, EVT_RESULT_ID, func)
 
class ResultEvent(wx.PyEvent):
    """Simple event to carry arbitrary result data."""
    def __init__(self, data):
        """Init Result Event."""
        wx.PyEvent.__init__(self)
        self.SetEventType(EVT_RESULT_ID)
        self.data = data

try:
    sys.winver
    """ Laeuft auf Windows """
    import psycowin as psyco
    psyco.log()
    psyco.full(memory=1000)
except:
    """ Laeuft auf anderem OS """
    import psycolinux as psyco
    psyco.log()
    psyco.full(memory=1000)

app = wx.App(redirect = False)
frame = None
      
def gui(x):
    global frame
    try:
        frame.Show(False)
    except AttributeError:
        """ """

    print "Application Starter : Application Starter initialized "
    
    print "Application Starter : Please choose your language"
    
    """ Create Frame """
    frame = wx.Frame(None, wx.ID_ANY, GetString(guiLang, "0019"), 
    wx.DefaultPosition, wx.Size(400, 220))
    
    """ Create Sizers """
    sizer = wx.GridBagSizer()
    
    """ Create some Comboboxes """
    statictextNull = wx.StaticText(frame, 50, "")
    statictextNull2 = wx.StaticText(frame, 50, "")
    statictext = wx.StaticText(frame, 50, "Waehlen Sie bitte ihre Sprache aus:")
    statictext2 = wx.StaticText(frame, 50, "Please choose your language:")
        
    """ Create some Widgets """
    buttonDE = wx.Button(frame, 10, "Weiter (Deutsch)")
    wx.EVT_BUTTON(frame, 10, chooseDE)
    buttonEN = wx.Button(frame, 11, "Next (English)")
    wx.EVT_BUTTON(frame, 11, chooseEN)
   
    """ Add some widgets to a sizer """
    sizer.Add(statictextNull, (0, 0), (1, 2), wx.EXPAND)
    sizer.Add(statictext, (1, 0), (1, 2), wx.EXPAND)
    sizer.Add(statictext2, (2, 0), (1, 2), wx.EXPAND)
    sizer.Add(statictextNull2, (3, 0), (1, 2), wx.EXPAND)
    sizer.Add(buttonDE, (4, 0), wx.DefaultSpan, wx.EXPAND)
    sizer.Add(buttonEN, (4, 1), wx.DefaultSpan, wx.EXPAND)
   
    sizer.AddGrowableRow(0)
    sizer.AddGrowableRow(1)
    sizer.AddGrowableRow(2)
    sizer.AddGrowableCol(0)
    sizer.AddGrowableCol(1)

        
    """ Set the Sizer """
    frame.SetSizer(sizer)
    
    """ Show the Window """
    frame.Center()       
    frame.Show(True)
    
    app.MainLoop()

def gui2(x):
    global frame
    frame.Show(False)

    fp = open("version.ver", "r")
    ver = "?"
    try:
         ver = fp.read().strip()
    except IOError:
         ver = "?"

    print "Application Starter : Please read this carefully"

    """ Create Frame """
    frame = wx.Frame(None, wx.ID_ANY, "JEliza " + ver, 
    wx.DefaultPosition, wx.Size(500, 500))
    
    """ Create Sizers """
    sizer = wx.GridBagSizer()
    
    """ Create some Comboboxes """
    statictextNull = wx.StaticText(frame, 50, "")
    statictextNull2 = wx.StaticText(frame, 50, "")
    statictext = wx.StaticText(frame, 50, GetString(guiLang, "0020"))
    statictext2 = GetString(guiLang, "0018").replace(" |\n| ", "\n").replace("|", "").strip() + "\n"
    statictext2 += "\n\n"
    statictext2 += "JEliza " + ver + "\n"
    statictext2 += "Copyright 2006 - 2007 by Tobias Schulz, http://jeliza.berlios.de" + "\n"
    statictext2 = wx.StaticText(frame, 50, statictext2)
        
    """ Create some Widgets """
    button = wx.Button(frame, 10, "Zurueck")
    wx.EVT_BUTTON(frame, 10, gui)
    button2 = wx.Button(frame, 11, "Weiter")
    wx.EVT_BUTTON(frame, 11, runJEliza)
   
    """ Add some widgets to a sizer """
    sizer.Add(statictextNull, (0, 0), (1, 2), wx.EXPAND)
    sizer.Add(statictext, (1, 0), (1, 2), wx.EXPAND)
    sizer.Add(statictext2, (2, 0), (1, 2), wx.EXPAND)
    sizer.Add(statictextNull2, (3, 0), (1, 2), wx.EXPAND)
    sizer.Add(button, (4, 0), wx.DefaultSpan, wx.EXPAND)
    sizer.Add(button2, (4, 1), wx.DefaultSpan, wx.EXPAND)
   
    sizer.AddGrowableRow(0)
    sizer.AddGrowableRow(1)
    sizer.AddGrowableRow(2)
    sizer.AddGrowableCol(0)
    sizer.AddGrowableCol(1)
        
    """ Set the Sizer """
    frame.SetSizer(sizer)
    
    """ Show the Window """
    frame.Center()       
    frame.Show(True)
    
    app.MainLoop()

def chooseDE(x):
    global guiLang
    global frame

    guiLang = "de"
    frame.Show(False)
    
    runGui2()
#    runJEliza()
    
def chooseEN(x):
    global guiLang
    global frame
    
    guiLang = "en"
    frame.Show(False)

    runGui2()
#    runJEliza()
        
def runJEliza(x):
    global frame
    frame.Show(False)
#    frame.Dispose()
    
    instance = JElizaWX()
    instance.gui()
        
def runGui2():
    gui2(0)
        
#instance = JElizaChooseLanguage()
gui(0)


