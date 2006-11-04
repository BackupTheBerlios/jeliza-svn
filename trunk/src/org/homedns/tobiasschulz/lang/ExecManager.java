package org.homedns.tobiasschulz.lang;

import org.homedns.tobiasschulz.io.*;
import java.io.*;

public class ExecManager {


	public static int exec(String cmd) throws IOException, InterruptedException { 
		return exec(cmd, new OutputStreamWriter(System.out));
	}

	public static int exec(String cmd, Writer out) throws IOException, InterruptedException { 
		Process p = Runtime.getRuntime().exec(cmd);
		FileManager.copyFile(new InputStreamReader(p.getInputStream()), out, true);
		p.waitFor();	
		return p.exitValue();
	}

	public static int exec(String[] cmd) throws IOException, InterruptedException { 
		return exec(cmd, new OutputStreamWriter(System.out));
	}

	public static int exec(String[] cmd, Writer out) throws IOException, InterruptedException { 
		Process p = Runtime.getRuntime().exec(cmd);
		FileManager.copyFile(new InputStreamReader(p.getInputStream()), out, true);
			p.waitFor();
		return p.exitValue();
	}
}

