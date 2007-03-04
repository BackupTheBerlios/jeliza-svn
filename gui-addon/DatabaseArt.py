#!/usr/bin/env python

class DatabaseArt:
    
    def clearDatabase(self):
        print "Leere Datenbank: Leere die alten Daten aus: " + self.file
        fd = open(self.file, "w")
        fd.write("Guten Tag\n")
        fd.close()
        print "Leere Datenbank: Leere die alten Daten aus: " + self.file.replace("input.txt", "roh.txt")
        fd=open(self.file.replace("input.txt", "roh.txt"), "w")
        fd.write("Guten Tag\n")
        fd.close()
        
    def duToIch(self, str, doStripDot = True):
        ant = ""
        if doStripDot:
            str = self.stripDot(self.clean(str)).lower()
        words = str.split()
        for word in words:
            if word == "du":
                word = "ich"
            elif word == "ich":
                word = "du"
            elif word == "mein":
                word = "dein"
            elif word == "meine":
                word = "deine"
            elif word == "meins":
                word = "deins"
            elif word == "meiner":
                word = "deiner"
            elif word == "dein":
                word = "mein"
            elif word == "deine":
                word = "meine"
            elif word == "deins":
                word = "meins"
            elif word == "deiner":
                word = "meiner"
            elif word == "deinem":
                word = "meinem"
            elif word == "meinem":
                word = "deinem"
            elif word == "mir":
                word = "dir"
            elif word == "dir":
                word = "mir"
            elif word == "mich":
                word = "dich"
            elif word == "dich":
                word = "mich"
            elif word == "bin":
                word = "bist"
            elif word == "bist":
                word = "bin"
            elif word == "hab":
                word = "hast"
            elif word == "habe":
                word = "hast"
            elif word == "hast":
                word = "habe"

            elif word == "warum" or word == "wieso" or word == "weshalb":
                word = "weil"
            elif word == "weil":
                word = "warum"

            elif word == "i":
                word = "you"
            elif word == "you":
                word = "i"
            elif word == "my":
                word = "your"
            elif word == "mine":
                word = "yours"
            elif word == "your":
                word = "my"
            elif word == "yours":
                word = "mine"
            elif word == "am":
                word = "are"
            elif word == "are":
                word = "am"
            elif word == "me":
                word = "you"

            ant = ant + " " + word
            ant = ant.strip()
        
        return ant
    
    def stripDot(self, str):
        ant = ""
        str = str.replace("..", " . ");
        str = str.replace("..", " . ");
        str = str.replace("..", " . ");
        str = str.replace("?", " ? ");
        str = str.replace("!", " ! ");
        str = str.replace(",", " , ")
        str = str.replace("  ", " ")
        str = str.strip()
        for x in str.split():
            if x != ".":
                ant = ant + " " + x
        ant = ant.strip()
        return ant
    
    def clean(self, str):
        str = str.replace("!", "")
        str = str.replace("?", "")
        str = str.replace(".", "")
        str = str.replace(";", "")
        str = str.replace(",", " , ")
        str = str.replace("  ", " ")
        str = str.replace("(", "")
        str = str.replace(")", "")
        return str
    
    def lookbetter(self, str):
        str = self.stripDot(str)
        str = str + "."
        str = str[0].upper() + str[1:]
        return str
    
    def setGaugeValue(self, x):
        fp = open("gauge.txt", "w")
        fp.write(str(x))
        fp.close()

