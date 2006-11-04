package org.homedns.tobiasschulz.net.myserver;

import java.io.*;

class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    String jt;
    
    StreamGobbler(InputStream is, String type, String jt)
    {
        this.is = is;
        this.jt = jt = "";
        this.type = type;
    }
    
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line="";
            while ( (line = br.readLine()) != null)
                 jt=jt+line+"\r\n";
            this.jt=jt+"";
            } catch (IOException ioe)
              {
                ioe.printStackTrace();  
              }
    }
    public String getString()
    {
        return this.jt;

    }
}