from stdlib import *
from random import randint
import sys
VAR = {}
#: begin SplitWords: _text
def SplitWords ( _text ) :
    """BEGIN OF FUNCTION SplitWords: """
    global VAR
#: split _text, ' ' as __words
    __words = _text.split ( ' ' )
#: 
    
#: _text = None
    _text = None
#: 
    
#: return __words
    return __words
#: end
#: 

#: begin contains: _text _pattern
def contains ( _text , _pattern ) :
    """BEGIN OF FUNCTION contains: """
    global VAR
#: split _text, _pattern as __tmp
    __tmp = _text.split ( _pattern )
#: 
    
#: if len ( __tmp ) > 1
    if len ( __tmp ) > 1 :
#: return True
        return True
#: end
#: return False
    return False
#: end
#: 

#: begin main:
def mainSTR_PR (  ) :
    """BEGIN OF FUNCTION mainSTR_PR: """
    global VAR
#: end
#: 

#: 

mainSTR_PR (  )
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
