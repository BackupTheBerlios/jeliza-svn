#!/usr/bin/env python

# Python imports
import os
import sys
import urllib
import re
import random
import string
import pickle
import httplib
import mimetypes
from threading import Thread
import time
import popen2
import subprocess

# My imports
from Util import *
from DatabaseArt import *
from umwandlung_py import *

class ErrorcodeException(Exception):
	def __init__(self, errorcode):
		self.errorcode = errorcode

def systemArgs(*args, **kwargs):
	joinStdErr = kwargs.get("joinStdErr")
	returnTuple = kwargs.get("returnTuple")
	raiseException = kwargs.get("raiseException")
	
	result = ""
	if os.name == "posix":
		if(joinStdErr):
			process = popen2.Popen4(args)
			error = None # We lose this one from returnTuple
		else:
			# only capture stderr if returnTuple 
			capturestderr = returnTuple
			process = popen2.popen3(args, capturestderr)
		error = ""
		while process.poll() == -1:
			result += process.fromchild.read()
			if(returnTuple and not joinStdErr):
				error += process.childerr.read()
		# Would it work to poll() again after it's polled out?        
		errorcode = process.poll()
		if(returnTuple):
			return (errorcode, result, error)
		else:
			if errorcode:
				raise ErrorcodeException, errorcode
	else:
		import win32process
		p = win32process.popen3("'" + "' '".split(args) + "'")
		p[1].close()
		result = p[0].read()
	return result



class AppURLopener(urllib.FancyURLopener):
	version = "Mozilla/4.0 (compatible; MSIE 6.0; Windows 98)"

class AIDatabase:
	
	databaseArt = DatabaseArt()
	system = "net"
	file = "JEliza.txt"
	
	def __init__(self):
		self.lang = Lang()
	
	def learn(self, str):
		str = str.lower().strip()
		frage = toSimple(str) + ""
		frage = umwandlung(frage)
		frage = vereinfache(frage)

		fp = open("JEliza.txt", "a")
		fp.write(str + "\n")
		fp.close()

	def reply(self, fra):
		if re.search("->", self.language):
			l1, l2 = self.language.split(" -> ")
			if l1 != "deutsch":
				tr = Translate()
				fra = tr.translate(l1 + " -> deutsch", fra)
		
		reply = "?"
		fp = open("ques.txt", "w")
		fp.write(fra)
		fp.close()
		if os.name == "posix":
			reply = os.system('bin/jeliza --file')
		else:
			reply = os.system('jeliza.exe --file')
		reply = open("ans.txt", "r").read()
		
		if re.search("->", self.language):
			l1, l2 = self.language.split(" -> ")
			reply = self.lang.toNormal(reply)
			if l2 != "deutsch":
				tr = Translate()
				reply = tr.translate("deutsch -> " + l2, reply)
		return self.databaseArt.lookbetter(reply.lower())
		
	def clearDatabase(self):
		print "Leere Datenbank: Leere die alten Daten aus: " + "JEliza.txt"
		fd = open("JEliza.txt", "w")
		fd.write("Guten Tag\n")
		fd.close()

	def genDatabase(self):
		""" Nichts zu generieren """

class Lang:
	
	artikel = []
	vornebensatz = []
	grundformNormal = {}
	normalGrundform = {}
	
	def log(self, str):
		print str
	
	def toSimple(self, str):
		self.log("Uebersetze in JEliza-Sprache: " + str)
		ant = []
		markov = DatabaseArt()
		wordsNormaleFormen = markov.stripDot(markov.clean(str.lower())).split()
		words = []
		
		for word in wordsNormaleFormen:
			words.append(self.getGrundform(word))

		if len(words) == 1:
			return words[0]

		o = 0
		for word in words:
			
			print word
			
			if word == "aber":
				ant.append("und")
				
			elif ( word == "fast" or word == "vielleicht" or word == "oft" or word == "warscheinlich"
				  or word == "sicherlich" or word == "hoffentlich" or word == "bestimmt"
				  or word == "ganz" or word == "ziemlich" or word == "sicher"
				  or word == "jo" or word == "hey" or word == "also"):
				""" Das Wort wird ausgelassen """
				self.log("Lasse Wort aus: " + word)
				 
			else:
				ant.append(word)
			o = o + 1

		antstr = ' '.join(ant)
		self.log("Uebersetzung abgeschlossen: " + antstr)
		print
		return antstr

	def toNormal(self, str):
		ant = []
		words = str.lower().split()
		if len(words) == 1:
			return words[0]
		o = 0
		for word in words:
			pre = self.getPraepositionNomen(word, False)
			if pre != False:
				ant.append(pre)
			art = self.getArtikelNomen(word)
			if art != False:
				ant.append(art)
			ant.append(self.getNormalformOfGrundform(word))
			o = o + 1

		antstr = ' '.join(ant)
		return antstr
	
	def doVerb2Grundform(self, verb):
		return verb
	
	def toOnlyKeyword(self, str):
		str = self.toSimple(str)
		ant = []
		words = str.lower().split()
		if len(words) == 1:
			return words[0]
		o = 0
		for word in words:
			if ( word == "wer" or word == "was" or word == "wie" or word == "wessen"
				  or word == "warum" or word == "wieso" or word == "weshalb"
				   or word == "wann" or word == "wo" or word == "wieviel"
					or word == "wielange" or word == "wielang" ):
				""" Das Wort wird ausgelassen """
			else:
				ant.append(word)
			o = o + 1

		antstr = ' '.join(ant)
		return antstr
	
	def getGrundform(self, word):
		return word
	
	def getNormalformOfGrundform(self, grund):
		return grund
	
	def getArtikelNomen(self, nomen):
		return False
	
	def getPraepositionNomen(self, normal, raten=True):
		return False

	def getGrundformOfNormalform(self, normal, raten=True):
		return False

	def dump(self, hash, f):
		return
		
	
	def dumpPraepositionNomen(self, art, nomen):
		return
				
	def dumpArtikelNomen(self, art, nomen):
		return
				
	def dumpGrundformNormal(self, grund, norm):
		return
			
class Translate:
	
	def post_multipart(self, host, selector, fields, files):
		content_type, body = self.encode_multipart_formdata(fields, files)
		h = httplib.HTTP(host)
		h.putrequest('POST', selector)
		h.putheader('content-type', content_type)
		h.putheader('content-length', str(len(body)))
		h.putheader('User-Agent', 'Mozilla/4.0 (compatible; MSIE 6.0; Windows 98)')
		h.endheaders()
		h.send(body)
		errcode, errmsg, headers = h.getreply()
		return h.file.read()

	def encode_multipart_formdata(self, fields, files):
		CRLF = '\r\n'
		L = ""
		for (key, value) in fields:
			L = L + "&" + urllib.quote(key) + "=" + urllib.quote(value)
		L = L[1:]
		body = L
		content_type = 'application/x-www-form-urlencoded'
		return content_type, body
	
	def post_multipart2(self, host, selector, fields, files):
		content_type, body = self.encode_multipart_formdata2(fields, files)
		h = httplib.HTTP(host)
		h.putrequest('POST', selector)
		h.putheader('content-type', content_type)
		h.putheader("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5")
		h.putheader('content-length', str(len(body)))
		h.putheader('Host', host)
		h.putheader('User-Agent', 'Mozilla/5.0 (X11; U; Linux i686; de; rv:1.8.1) Gecko/20060601 Firefox/2.0 (Ubuntu-edgy)')
		h.endheaders()
		h.send(body)
		errcode, errmsg, headers = h.getreply()
		return h.file.read()

	def encode_multipart_formdata2(self, fields, files):
		BOUNDARY = '----------ThIs_Is_tHe_bouNdaRY_$'
		CRLF = '\r\n'
		L = ""
		for (key, value) in fields:
			L = L + "&" + urllib.quote(key) + "=" + urllib.quote(value)
		L = L[1:]
		body = L
		content_type = 'application/x-www-form-urlencoded'
		return content_type, body

	def translate(self, lang, text):
		lang = lang.replace(" -> ", "|")
		lang = lang.replace("deutsch", "de")
		lang = lang.replace("englisch", "en")
		lang = lang.replace("english", "en")
		lang = lang.replace("spanisch", "es")
		lang = lang.replace("italienisch", "it")
		lang = lang.replace("franzoesisch", "fr")
		lang = lang.replace("portugiesisch", "pt")
		lang = lang.replace("finnisch", "fi")
		
		if lang == "de|it":
			text = self.translate("de|en", text)
			lang = "en|it"
		if lang == "it|de":
			text = self.translate("it|en", text)
			lang = "en|de"
		if lang == "de|es":
			text = self.translate("de|en", text)
			lang = "en|es"
		if lang == "es|de":
			text = self.translate("es|en", text)
			lang = "en|de"
		if lang == "de|pt":
			text = self.translate("de|en", text)
			lang = "en|pt"
		if lang == "pt|de":
			text = self.translate("pt|en", text)
			lang = "en|de"
		
		if lang == "de|fi" or lang == "fi|de":
			""" Finnisch """
			finn = Finnisch()
			fi = finn.translate(lang, text)
			return fi

		
		""" Altavista-Babelfish: """

		i = 0
		while True:
			i = i + 1
			con = self.post_multipart2("babelfish.altavista.com", "/tr", 
							 ( ("trtext", text), ("lp", lang.replace("|", "_")) ),
							 ())
			con = con.strip()
			print con
			if len(con) > 1 and con != ".":
				break
			if i > 8:
				break

		if i < 8:
			try:
				con2 = con.split("<td bgcolor=white class=s><div style=padding:10px;>")[1]
				con3 = con2.split("</div></td>")[0]
				tra = con3.strip()
				if len(tra) > 1 and tra != ".":
					print lang
					return tra
			except IndexError:
				""" Fahre mit Google fort """
		
		""" Google Uebersetzer: """
		
		con = self.post_multipart("translate.google.com", "/translate_t", 
							 ( ("text", text), ("langpair", lang), 
							   ("hl", "de"), ("ie", "UTF-8"), ("oe", "UTF-8"), 
							   ("u", "http://"), ("prev", "/language_tools") ), 
							 ())
		con2 = con.split("</textarea></td><td id=gap>&nbsp;</td><td id=result_cell><div id=result_box dir=ltr>")[1]
		print con2
		con3 = con2.split("</div></td></tr><tr><td id=submitcell><table><td id=selectcell><select name=langpair>")[0]
		con3 = con3.lower().strip()
		if con3 == ".":
			con3 = "?"
		tra = con3
		print lang

		return tra

class Finnisch:
	
	def translate(self, lang, text):
		urllib._urlopener = AppURLopener()
		words = text.split()
		ant = []
		antc = []
		for word in words:
			ok = False
			wiki = urllib.urlopen ("http://rio.pauker.at/p.php/DE/FI?s=" + word)
			content = wiki.read().replace("\n<!-- show_entry -->\n", "|-|")
			lines = content.split("\n")
			for line in lines:
				line = line.lower()
				if re.search("class\\='pl' tabindex\\='2' >", line) and not re.search("gefunden", line):
					line = re.sub("<(.*?)>", "", line)
					line = re.sub("\((.*?)\)", "", line)
					line = re.sub("^\\|-\\|", "", line).strip()
					try:
						if lang == "de|fi":
							line = line.strip()
							de = line.split("|-|")[0].strip()
							if de == word or ( line.split("|-|")[0].split()[1].lower() == word.lower() and 
											   len(line.split("|-|")[0].split()) == 2 ):
								fi = line.split("|-|")[1].strip()
								print de + " -> " + fi
								if len(fi.split()) == 1:
									ant.append(fi)
									antc.append(fi)
									ok = True
									break
								else:
									antc.append(fi)
						elif lang == "fi|de":
							line = line.strip()
							de = line.split("|-|")[1].strip()
							if de == word or ( line.split("|-|")[1].split()[1].lower() == word.lower() and 
											   len(line.split("|-|")[1].split()) == 2 ):
								fi = line.split("|-|")[0].strip()
								print de + " -> " + fi
								if len(fi.split()) < 2:
									ant.append(fi)
									antc.append(fi)
									ok = True
									break
								else:
									antc.append(fi)
					except IndexError:
						""" Na und? """
			if not ok:
				ant.append(word)
				antc.append(word)
		return " ".join(ant)


class JEliza:
	
	debug = 0
	file = "JEliza.txt"
	language = "roh"
	system = "net"
	
	def __init__(self):
		self.util = Util()
		self.file = self.util.getProfilePath(self.util.getCurrentProfile()) + "/input.txt";
		self.createSwapFiles()
		self.markov = AIDatabase()
	
	def getAnswer(self, fra):
		fra = fra.strip().lower()
		fra = fra.replace(u"\xe4", "ae");
		fra = fra.replace(u"\xf6", "oe");
		fra = fra.replace(u"\xfc", "ue");
		
		self.markov.system = self.system
		self.markov.language = self.language
#		self.markov.file = self.file
		self.markov.debug = self.debug
		output = self.markov.reply(fra)
		self.markov.learn(fra)
		print self.markov.language
		print self.language
		print output
		return output
	
	def createSwapFiles(self):
		""" No Swapfiles to create """
		
	def genDatabase(self):
		self.markov.system = self.system
		self.markov.language = self.language
#		self.markov.file = self.file
		self.markov.debug = self.debug
		self.markov.genDatabase()
		
	def clearDatabase(self):
		self.markov.system = self.system
		self.markov.language = self.language
#		self.markov.file = self.file
		self.markov.debug = self.debug
		self.markov.clearDatabase()
		
class JElizaConsole:
	
	debug = 0
	
	def main(self):
		u = Util()
		u.chProfile("default")
		self.loop()
			
	def loop(self):
		i = 0
		while i<1:
			print "Mensch:"
			jel = JEliza()
			jel.debug = self.debug
			input = sys.stdin.readline().strip()
			print "JEliza:"
			print jel.getAnswer(input)

#if __name__=="__main__":
#	jelizaconsole = JElizaConsole()
#	jelizaconsole.main()
