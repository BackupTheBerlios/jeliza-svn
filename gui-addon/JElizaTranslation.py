#!/usr/bin/python
# -*- coding: utf-8 -*-

# Python Imports
import sys
import re
import time

def GetString(lang, num):
    """ Laed den Text mit der Nummer 'num' in der Sprache 'lang' aus einer Datei """
    
    fp = open("translation_" + lang + ".jtr")
    lines = fp.readlines()
    fp.close()
    
    txt = ""
    
    for line in lines:
#        line = line.strip()
        
        tmp = str(num) + " -> "
        if line.startswith(tmp):
            txt += line[len(tmp):]
    
    return txt.strip()
            
    
    
    