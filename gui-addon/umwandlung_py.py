from stdlib import *
from random import randint
import sys
VAR = {}
#: 

#: import str
from str_py import *
#: import io
from io_py import *
#: 

#: begin toSimple: sent
def toSimple ( sent ) :
    """BEGIN OF FUNCTION toSimple: """
    global VAR
#: replace '!' with '' in sent as sent
    sent = sent.replace ( '!' , '' )
#: replace '.' with '' in sent as sent
    sent = sent.replace ( '.' , '' )
#: replace ',' with '' in sent as sent
    sent = sent.replace ( ',' , '' )
#: replace '-' with '' in sent as sent
    sent = sent.replace ( '-' , '' )
#: replace '' with '' in sent as sent
    sent = sent.replace ( '' , '' )
#: return sent
    return sent
#: end
#: 

#: begin umwandlungWord: word
def umwandlungWord ( word ) :
    """BEGIN OF FUNCTION umwandlungWord: """
    global VAR
#: if word == 'du'
    if word == 'du' :
#: return 'ich'
        return 'ich'
#: end if
#: if word == 'ich'
    if word == 'ich' :
#: return 'du'
        return 'du'
#: end if
#: if word == 'mein'
    if word == 'mein' :
#: return 'dein'
        return 'dein'
#: end if
#: if word == 'meine'
    if word == 'meine' :
#: return 'deine'
        return 'deine'
#: end if
#: if word == 'meins'
    if word == 'meins' :
#: return 'deins'
        return 'deins'
#: end if
#: if word == 'meiner'
    if word == 'meiner' :
#: return 'deiner'
        return 'deiner'
#: end if
#: if word == 'dein'
    if word == 'dein' :
#: return 'mein'
        return 'mein'
#: end if
#: if word == 'deine'
    if word == 'deine' :
#: return 'meine'
        return 'meine'
#: end if
#: if word == 'deins'
    if word == 'deins' :
#: return 'meins'
        return 'meins'
#: end if
#: if word == 'deiner'
    if word == 'deiner' :
#: return 'meiner'
        return 'meiner'
#: end if
#: if word == 'deinem'
    if word == 'deinem' :
#: return 'meinem'
        return 'meinem'
#: end if
#: if word == 'meinem'
    if word == 'meinem' :
#: return 'deinem'
        return 'deinem'
#: end if
#: if word == 'mir'
    if word == 'mir' :
#: return 'dir'
        return 'dir'
#: end if
#: if word == 'dir'
    if word == 'dir' :
#: return 'mir'
        return 'mir'
#: end if
#: if word == 'mich'
    if word == 'mich' :
#: return 'dich'
        return 'dich'
#: end if
#: if word == 'dich'
    if word == 'dich' :
#: return 'mich'
        return 'mich'
#: end if
#: if word == 'bin'
    if word == 'bin' :
#: return 'bist'
        return 'bist'
#: end if
#: if word == 'bist'
    if word == 'bist' :
#: return 'bin'
        return 'bin'
#: end if
#: if word == 'hab'
    if word == 'hab' :
#: return 'hast'
        return 'hast'
#: end if
#: if word == 'habe'
    if word == 'habe' :
#: return 'hast'
        return 'hast'
#: end if
#: if word == 'hast'
    if word == 'hast' :
#: return 'habe'
        return 'habe'
#: end if
#: 
    
#: if word == 'warum' or word == 'wieso' or word == 'weshalb'
    if word == 'warum' or word == 'wieso' or word == 'weshalb' :
#: return 'weil'
        return 'weil'
#: end if
#: if word == 'weil'
    if word == 'weil' :
#: return 'warum'
        return 'warum'
#: end if
#: 
    
#: if word == 'i'
    if word == 'i' :
#: return 'you'
        return 'you'
#: end if
#: if word == 'you'
    if word == 'you' :
#: return 'i'
        return 'i'
#: end if
#: if word == 'my'
    if word == 'my' :
#: return 'your'
        return 'your'
#: end if
#: if word == 'mine'
    if word == 'mine' :
#: return 'yours'
        return 'yours'
#: end if
#: if word == 'your'
    if word == 'your' :
#: return 'my'
        return 'my'
#: end if
#: if word == 'yours'
    if word == 'yours' :
#: return 'mine'
        return 'mine'
#: end if
#: if word == 'am'
    if word == 'am' :
#: return 'are'
        return 'are'
#: end if
#: if word == 'are'
    if word == 'are' :
#: return 'am'
        return 'am'
#: end if
#: if word == 'me'
    if word == 'me' :
#: return 'you'
        return 'you'
#: end if
#: return word
    return word
#: end
#: 

#: begin umwandlung: sent
def umwandlung ( sent ) :
    """BEGIN OF FUNCTION umwandlung: """
    global VAR
#: sent = lower ( sent )
    sent = lower ( sent )
#: split sent, ' ' as list
    list = sent.split ( ' ' )
#: v = 0
    v = 0
#: antw = new list
    antw = [ ]
#: while v < len ( list )
    while v < len ( list ) :
#: word = get ( list, v )
        word = get ( list , v )
#: ers = umwandlungWord ( word )
        ers = umwandlungWord ( word )
#: add ers to antw
        antw.append ( ers )
#: v = v + 1
        v = v + 1
#: end while
#: join antw, ' ' as antww
    antww = ' '.join ( antw )
#: return antww
    return antww
#: end
#: 

#: begin vereinfache: sent
def vereinfache ( sent ) :
    """BEGIN OF FUNCTION vereinfache: """
    global VAR
#: sent2 = sent . ''
    sent2 = sent + ''
#: replace '?' with '' in sent2 as sent2
    sent2 = sent2.replace ( '?' , '' )
#: replace '!' with '' in sent2 as sent2
    sent2 = sent2.replace ( '!' , '' )
#: replace '.' with '' in sent2 as sent2
    sent2 = sent2.replace ( '.' , '' )
#: replace ',' with ' , ' in sent2 as sent2
    sent2 = sent2.replace ( ',' , '  , ' )
#: replace '-' with ' - ' in sent2 as sent2
    sent2 = sent2.replace ( '-' , ' - ' )
#: 
    
#: replace '  ' with ' ' in sent2 as sent2
    sent2 = sent2.replace ( '  ' , ' ' )
#: replace '  ' with ' ' in sent2 as sent2
    sent2 = sent2.replace ( '  ' , ' ' )
#: 
    
#: return sent2
    return sent2
#: end
#: 

#: begin main:
def mainUMWANDLUNG_PR (  ) :
    """BEGIN OF FUNCTION mainUMWANDLUNG_PR: """
    global VAR
#: 
    
#: end
#: 

mainUMWANDLUNG_PR (  )
try:
    sys.winver
    """ Laeuft auf Windows """
    import psycowin as psyco
    psyco.log (  ) 
    psyco.full ( memory=1000 ) 
except:
    """ Laeuft auf anderem OS """
    import psycolinux as psyco
    psyco.log (  ) 
    psyco.full ( memory=1000 )
