from stdlib import *
from random import randint
import sys
VAR = {}
#: 

#: import io
from io_py import *
#: import str
from str_py import *
#: 

#: import native mysql
from mysql_native import *
#: 

#: 

#: begin SaveSentence: type ostri stri isques
def SaveSentence ( type , ostri , stri , isques ) :
    """BEGIN OF FUNCTION SaveSentence: """
    global VAR
#: if type == 'file'
    if type == 'file' :
#: open 'JEliza.txt' in mode 'a' as fp
        fp = open ( 'JEliza.txt' , "a" )
#: writeline stri into fp
        fp.write ( stri + "\n" )
#: close fp
        fp.close (  )
#: end if
#: 
    
#: if type == 'db'
    if type == 'db' :
#: host = strip ( ReadAll ( 'conf/mysql/host.txt' )  )
        host = strip ( ReadAll ( 'conf/mysql/host.txt' )  )
#: user = strip ( ReadAll ( 'conf/mysql/user.txt' )  )
        user = strip ( ReadAll ( 'conf/mysql/user.txt' )  )
#: passw = strip ( ReadAll ( 'conf/mysql/pass.txt' )  )
        passw = strip ( ReadAll ( 'conf/mysql/pass.txt' )  )
#: db = strip ( ReadAll ( 'conf/mysql/db.txt' )  )
        db = strip ( ReadAll ( 'conf/mysql/db.txt' )  )
#: table = strip ( ReadAll ( 'conf/mysql/table.txt' )  )
        table = strip ( ReadAll ( 'conf/mysql/table.txt' )  )
#: 
        
#: conn = NativeMysqlConnect ( host, user, passw )
        conn = NativeMysqlConnect ( host , user , passw )
#: 
        
#: query = 'INSERT INTO ' . table . ' ( original, umgewandelt, question ) '
        query = 'INSERT INTO ' + table + ' ( original , umgewandelt , question ) '
#: query = query . 'VALUES ( \'' . ostri . '\', \'' . stri . '\', \'' . isques . '\' ) '
        query = query + 'VALUES ( \'' + ostri + '\' , \'' + stri + '\' , \'' + isques + '\' ) '
#: 
        
#: NativeMysqlQuery ( conn, db, query )
        NativeMysqlQuery ( conn , db , query )
#: 
        
#: NativeMysqlClose ( conn )
        NativeMysqlClose ( conn )
#: end if
#: end
#: 

#: begin GetSentences: type
def GetSentences ( type ) :
    """BEGIN OF FUNCTION GetSentences: """
    global VAR
#: if type == 'file'
    if type == 'file' :
#: lines2 = ReadLines ( 'JEliza.txt' )
        lines2 = ReadLines ( 'JEliza.txt' )
#: lines = new list
        lines = [ ]
#: 
        
#: t = 0
        t = 0
#: while t < len ( lines2 )
        while t < len ( lines2 ) :
#: line = get ( lines2, t )
            line = get ( lines2 , t )
#: 
            
#: if len ( line ) < 2
            if len ( line ) < 2 :
#: t = t + 1
                t = t + 1
#: continue
                continue
#: end if
#: 
            
#: isQues = False
            isQues = False
#: if charAt ( line, len ( line ) - 1 ) == '?'
            if charAt ( line , len ( line ) - 1 ) == '?' :
#: isQues = True
                isQues = True
#: end if
#: 
            
#: if not isQues
            if not isQues :
#: add line to lines
                lines.append ( line )
#: end if
#: 
            
#: t = t + 1
            t = t + 1
#: end while
#: 
        
#: return lines
        return lines
#: end if
#: 
    
#: if type == 'db'
    if type == 'db' :
#: host = strip ( ReadAll ( 'conf/mysql/host.txt' )  )
        host = strip ( ReadAll ( 'conf/mysql/host.txt' )  )
#: user = strip ( ReadAll ( 'conf/mysql/user.txt' )  )
        user = strip ( ReadAll ( 'conf/mysql/user.txt' )  )
#: passw = strip ( ReadAll ( 'conf/mysql/pass.txt' )  )
        passw = strip ( ReadAll ( 'conf/mysql/pass.txt' )  )
#: db = strip ( ReadAll ( 'conf/mysql/db.txt' )  )
        db = strip ( ReadAll ( 'conf/mysql/db.txt' )  )
#: table = strip ( ReadAll ( 'conf/mysql/table.txt' )  )
        table = strip ( ReadAll ( 'conf/mysql/table.txt' )  )
#: 
        
#: conn = NativeMysqlConnect ( host, user, passw )
        conn = NativeMysqlConnect ( host , user , passw )
#: 
        
#: query = 'SELECT umgewandelt, original FROM ' . table . ' WHERE question = \'N\''
        query = 'SELECT umgewandelt , original FROM ' + table + ' WHERE question = \'N\''
#: 
        
#: arr = NativeMysqlQuery ( conn, db, query )
        arr = NativeMysqlQuery ( conn , db , query )
#: 
        
#: query2 = 'SELECT * FROM ' . table . ''
        query2 = 'SELECT * FROM ' + table + ''
#: 
        
#: arr2 = NativeMysqlQuery ( conn, db, query2 )
        arr2 = NativeMysqlQuery ( conn , db , query2 )
#: 
        
#: NativeMysqlClose ( conn )
        NativeMysqlClose ( conn )
#: 
        
#: 
        
#: return get ( arr, 0 )
        return get ( arr , 0 )
#: end if
#: end
#: 

#: begin main:
def mainSAVE_PR (  ) :
    """BEGIN OF FUNCTION mainSAVE_PR: """
    global VAR
#: end
#: 

mainSAVE_PR (  )
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
