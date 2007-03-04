from stdlib import *
from random import randint
import sys
VAR = {}
VAR["JELIZA_PR_frage"] = None
VAR["JELIZA_PR_frage"] = None
VAR["JELIZA_PR_ofrage"] = None
VAR["JELIZA_PR_ofrage"] = None
VAR["JELIZA_PR_bestReply"] = None
VAR["JELIZA_PR_bestReply"] = None
#: 

#: 

#: import io
from io_py import *
#: import str
from str_py import *
#: 

#: import umwandlung
from umwandlung_py import *
#: import JElizaNeuro
from JElizaNeuro_py import *
#: import JElizaMarkov
from JElizaMarkov_py import *
#: 

#: begin GetReplyMarkov: frag
def GetReplyMarkov ( frag ) :
    """BEGIN OF FUNCTION GetReplyMarkov: """
    global VAR
#: frage = frag
    VAR["JELIZA_PR_frage"] = frag
#: frage = strip ( frage )
    VAR["JELIZA_PR_frage"] = strip ( VAR["JELIZA_PR_frage"] )
#: frage = toSimple ( frage )
    VAR["JELIZA_PR_frage"] = toSimple ( VAR["JELIZA_PR_frage"] )
#: frage = umwandlung ( frage )
    VAR["JELIZA_PR_frage"] = umwandlung ( VAR["JELIZA_PR_frage"] )
#: ofrage = frage . ''
    VAR["JELIZA_PR_ofrage"] = VAR["JELIZA_PR_frage"] + ''
#: frage = vereinfache ( frage )
    VAR["JELIZA_PR_frage"] = vereinfache ( VAR["JELIZA_PR_frage"] )
#: 
    
#: MarkovInit (  )
    MarkovInit (  )
#: 
    
#: tmp = new list
    tmp = [ ]
#: add MarkovAsk ( frage . '' ) to tmp
    tmp.append ( MarkovAsk ( VAR["JELIZA_PR_frage"] + '' )  )
#: print_str ( tmp )
    print_str ( tmp )
#: randint 0 ... len ( tmp ) - 1 as y
    y = randint ( 0 , len ( tmp ) - 1 )
#: bestReply = get ( tmp, y )
    VAR["JELIZA_PR_bestReply"] = get ( tmp , y )
#: 
    
#: 
    
#: return bestReply
    return VAR["JELIZA_PR_bestReply"]
#: end
#: 

#: begin GetReplyNeuro: frage
def GetReplyNeuro ( frage ) :
    """BEGIN OF FUNCTION GetReplyNeuro: """
    global VAR
#: frage = strip ( frage )
    VAR["JELIZA_PR_frage"] = strip ( VAR["JELIZA_PR_frage"] )
#: frage = toSimple ( frage )
    VAR["JELIZA_PR_frage"] = toSimple ( VAR["JELIZA_PR_frage"] )
#: frage = umwandlung ( frage )
    VAR["JELIZA_PR_frage"] = umwandlung ( VAR["JELIZA_PR_frage"] )
#: ofrage = frage . ''
    VAR["JELIZA_PR_ofrage"] = VAR["JELIZA_PR_frage"] + ''
#: frage = vereinfache ( frage )
    VAR["JELIZA_PR_frage"] = vereinfache ( VAR["JELIZA_PR_frage"] )
#: 
    
#: NeuroInit (  )
    NeuroInit (  )
#: 
    
#: bestReply = NeuroAsk ( frage . '' )
    VAR["JELIZA_PR_bestReply"] = NeuroAsk ( VAR["JELIZA_PR_frage"] + '' )
#: return bestReply
    return VAR["JELIZA_PR_bestReply"]
#: end
#: 

#: begin loop:
def loop (  ) :
    """BEGIN OF FUNCTION loop: """
    global VAR
#: print '--- START -> ok'
    print '--- START -> ok'
#: MarkovStart (  )
    MarkovStart (  )
#: 
    
#: frage = ''
    VAR["JELIZA_PR_frage"] = ''
#: while 1 == 1
    while 1 == 1:
#: printdata '--- QUEST -> '
        sys.stdout.write ( str ( '--- QUEST -> ' )  )
#: readline as fra
        fra = sys.stdin.readline (  )
#: 
        
#: bestReply = request ( fra, 'file' )
        VAR["JELIZA_PR_bestReply"] = request ( fra , 'file' )
#: print '--- REPLY -> ' . bestReply
        print '--- REPLY -> ' + VAR["JELIZA_PR_bestReply"]
#: end while
#: 
    
#: print '--- ENDED -> ok'
    print '--- ENDED -> ok'
#: end
#: 

#: begin request: fra saveType
def request ( fra , saveType ) :
    """BEGIN OF FUNCTION request: """
    global VAR
#: open 'conf/saveType.txt' in mode 'w' as fp
    fp = open ( 'conf/saveType.txt' , "w" )
#: write strip ( saveType ) into fp
    fp.write ( strip ( saveType )  )
#: close fp
    fp.close (  )
#: 
    
#: bestReply = GetReplyMarkov ( fra . '' )
    VAR["JELIZA_PR_bestReply"] = GetReplyMarkov ( fra + '' )
#: MarkovLearn ( fra . '', ofrage . '' )
    MarkovLearn ( fra + '' , VAR["JELIZA_PR_ofrage"] + '' )
#: 
    
#: return bestReply
    return VAR["JELIZA_PR_bestReply"]
#: end
#: 

#: begin main:
def mainJELIZA_PR (  ) :
    """BEGIN OF FUNCTION mainJELIZA_PR: """
    global VAR
#: public frage = ''
    VAR["JELIZA_PR_frage"] = ''
#: public ofrage = ''
    VAR["JELIZA_PR_ofrage"] = ''
#: public bestReply = ''
    VAR["JELIZA_PR_bestReply"] = ''
#: 
    
#: loop (  )
    loop (  )
#: 
    
#: end
#: 

#: 

mainJELIZA_PR (  )
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
