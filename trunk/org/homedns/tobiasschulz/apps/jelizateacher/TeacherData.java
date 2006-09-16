package org.homedns.tobiasschulz.apps.jelizateacher;

import java.io.*;
import java.net.*;

/**
 * JElizaTeacher ist ein Grafischer Wrapper für die Methoden in TeacherData.
 * 
 * @author Tobias Schulz
 * @version 0.2
 */
public class TeacherData {

	public String getRemoteFileData(String file) {
		java.lang.String strDatei = file;
		URL url = null;
		java.io.BufferedReader br = null;
		try {
			url = new URL("http://jeliza.tobiasschulz.homedns.org/intelligenz/jeliza/" + strDatei);
			br = new BufferedReader(new InputStreamReader(url.openStream()));
		} catch (FileNotFoundException e1) {
			return e1.getMessage();
	} catch (MalformedURLException e2) {
		e2.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}

		String dasIst = "";
		try {
			while (true) {
				java.lang.String line = br.readLine();
				if (null == line)
					break;
				dasIst += line + "\n";
			}
		} catch (IOException e) {
			System.out.println("Achtung! Konnte Datei nicht von Server holen. (" + file + ") \n"
					+ e.getMessage());
		}

		return dasIst;
	}

}
