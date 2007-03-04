# My imports
from Util import Util
from JEliza import *
import Save_py

class JWWF:
    
    def save(self, profile, file):
        self.util = Util()
        
        bw = open(file, 'w')
        fp = open("conf/saveType.txt", "r")
        saveType = fp.read().strip().lower()
        fp.close()
        
        try:
            lines = GetSentences(saveType)
            for line in lines:
                bw.writeline(line)
            bw.close()
        except IOError:
            return False
        
        return True
    
    def load(self, profile, file):
        self.util = Util()

        br = open(file, 'r')
        fp = open("conf/saveType.txt", "r")
        saveType = fp.read().strip().lower()
        fp.close()

        try:
            str = br.read()
            lines = str.split("\n")
            for line in lines:
                line = line.strip()
                if len(line) < 2:
                    continue
                
                isQues = "N"
                if line[-1] == "?":
                    isQues = "Y"
                SaveSentence(saveType, line, line.lower(), isQues)
#                bw.writelines(jel.duToIch(line, False) + "\n")
        except IOError:
            return False
                
        return True
