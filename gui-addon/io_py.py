from stdlib import *
from random import randint
import sys
VAR = {}
#: begin ReadLines: _file
def ReadLines ( _file ) :
    """BEGIN OF FUNCTION ReadLines: """
    global VAR
#: open _file in mode 'r' as __fp
    __fp = open ( _file , "r" )
#: read from __fp into __text
    __text = __fp.read (  )
#: close __fp
    __fp.close (  )
#: splitlines __text as __lines
    __lines = __text.split ( "\n" )
#: 
    
#: __fp = None
    __fp = None
#: __text = None
    __text = None
#: _file = None
    _file = None
#: 
    
#: return __lines
    return __lines
#: end
#: 

#: begin ReadAll: _file
def ReadAll ( _file ) :
    """BEGIN OF FUNCTION ReadAll: """
    global VAR
#: open _file in mode 'r' as __fp
    __fp = open ( _file , "r" )
#: read from __fp into __text
    __text = __fp.read (  )
#: close __fp
    __fp.close (  )
#: 
    
#: __fp = None
    __fp = None
#: _file = None
    _file = None
#: 
    
#: return __text
    return __text
#: end
#: 

#: begin Create: _file
def Create ( _file ) :
    """BEGIN OF FUNCTION Create: """
    global VAR
#: open _file in mode 'w' as __fp
    __fp = open ( _file , "w" )
#: close __fp
    __fp.close (  )
#: 
    
#: __fp = None
    __fp = None
#: end
#: 

#: begin main:
def mainIO_PR (  ) :
    """BEGIN OF FUNCTION mainIO_PR: """
    global VAR
#: end
#: 

#: 

mainIO_PR (  )
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
