package org.homedns.tobiasschulz.apps.jeliza.hirn;

import java.io.*;
import java.util.*;

import module.megajeliza.*;

import org.homedns.tobiasschulz.apps.jeliza.Util;
import org.homedns.tobiasschulz.io.FileManager;

/**
 * Hilfsklasse des Java-Servlets JEliza, die einen Wrapper fuer die
 * Gehirn-Dateien von JEliza bereitstellt.
 * 
 * @author Tobias Schulz
 * @version 0.3
 */
public class Gehirn {

	String absoluteUrl = "";

	public Gefuehl gefuehlHeute = new Gefuehl();

	public AnswerWrapper fragenAntworter = new AnswerWrapper();

	public Answerer re = new Answerer(absoluteUrl);

	public String[] erSieEsGedaechtnis = { "Er", "Sie", "Es" };

	public UltraJEliza mj = new UltraJEliza();
	
	/**
	 * Privater Konstruktor
	 * 
	 * @param tmp
	 *            Die Absolute URL zum JEliza-Stammordner
	 */
	private Gehirn(String tmp) {
		absoluteUrl = tmp;
		re.hirn = this;
	}

	/**
	 * Neue Instanz der Klasse
	 * 
	 * @param tmp
	 *            Die Absolute URL zum JEliza-Stammordner
	 */
	public static Gehirn newGehirn(String tmp) {
		return new Gehirn(tmp);
	}

	/**
	 * Sucht nach passenden Antworten in gehirn/fragen.ghn und liefert eine
	 * zurueck. Liefert, wenn keinen Antwort gefunden wurde, null zurueck!
	 * 
	 * @param obj
	 *            Die Frage
	 * @return Die Antwort
	 */
	public String getAnt(String obj) {
		java.lang.String strDatei = absoluteUrl + "gehirn/fragen.ghn";
		java.io.BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(strDatei));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String dasIst = null;
		int xy = 0;
		try {
			while (true) {
				java.lang.String line = br.readLine();
				if (null == line)
					break;
				String line2 = line.trim().toLowerCase();

				if (line2.substring(0, line.indexOf("====================="))
						.contains(obj)) {
					StringTokenizer stoken = new StringTokenizer(line,
							"=====================");
					String token = stoken.nextToken();
					token = stoken.nextToken();
					dasIst = token;
					return token;
				}
				xy++;
			}
		} catch (IOException e) {
			println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n"
					+ e.getMessage());
		}

		return dasIst;
	}

	/**
	 * Sucht nach passenden Antworten in der Wikipedia und liefert eine zurueck.
	 * Liefert, wenn keinen Antwort gefunden wurde, null zurueck!
	 * 
	 * @param obj
	 *            Die Frage
	 * @return Die Antwort
	 */
	public String getAntWWW(String obj) {
		java.lang.String strDatei = "http://de.wikipedia.org/wiki/"
				+ obj.replace(" ", "_");
		java.io.InputStream file = null;
		java.net.URL url = null;
		try {
			url = new java.net.URL(strDatei); //$NON-NLS-1$
			file = url.openStream();
		} catch (java.net.MalformedURLException mue) {
			System.out.println("Fehlerhafte URL-Adresse / Datei");
			return null;
		} catch (java.io.IOException e) {
			System.out
					.println("IOException beim Initialisieren der Verbindung zum Server");
			return null;
		}
		java.io.BufferedReader br = new java.io.BufferedReader(
				new java.io.InputStreamReader(file));
		String definition = "";
		String content = "";
		try {
			while (true) {
				String line = br.readLine();
				if (null == line)
					break;
				String line2 = line.trim().toLowerCase();
				if (line2.contains("Wechseln zu:".toLowerCase())) {
					break;
				}
			}
			boolean nextLineUeberspringen = false;
			int countOpenTables = 0;
			while (true) {
				String line = br.readLine();
				if (null == line)
					break;
				line = line.replace("  ", "");
				if (!nextLineUeberspringen && countOpenTables == 0) {
					content = content.concat(line).concat("\n");
				}
			}

			int i = 0;

			String[][] remove = { { "<table", "</table>" },
					{ "<div class=\"toccolours\"", "</div>" }, { "<img", "/>" } };
			for (int x = 0; x < remove.length; x++) {
				String[] re = remove[x];
				while ((i = content.indexOf(re[0])) > -1) {
					content = content.substring(0, i).concat(
							content.substring(content.indexOf(re[1], i),
									content.length() - 1));
					System.gc();
				}
				content = content.replace(re[1], "");
			}

			definition = content.substring(content.indexOf("<p>"), content
					.indexOf("</p>"));
			definition = Util.toASCII(definition);

		} catch (IOException e) {
			println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n"
					+ e.getMessage());
		}

		if (definition.toLowerCase().contains("artikel verschwunden")) {
			return "Das weiss ich nicht!";
		}

		return definition;
	}

	/**
	 * Sucht nach Software-Paketen in gehirn/kategorien/software.ghn und liefert
	 * einen String mit allen gefundenen Programmen zurueck. Sucht, wenn nichts
	 * gefunden, in der Wikipedia mit Hilfe von getAntWWW Liefert, wenn keinen
	 * Antwort gefunden wurde, "" zurueck!
	 * 
	 * @see getAntWWW
	 * @param obj
	 *            Die Frage
	 * @return Die Antwort
	 */
	public String getAntKategorie(String obj) {
		java.lang.String strDatei = absoluteUrl
				+ "gehirn/kategorien/software.ghn";
		java.io.BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(strDatei));
		} catch (FileNotFoundException e1) {
			return e1.getMessage();
		}

		String dasIst[] = new String[200];
		int xy = 0;
		int xyz = 0;
		try {
			while (true) {
				java.lang.String line = br.readLine();
				if (null == line)
					break;
				String line2 = line.trim().toLowerCase();

				if (line2.substring(0, line.indexOf("->")).contains(obj)
						|| obj.toLowerCase().contains(
								line2.substring(0, line.indexOf("->")))) {
					StringTokenizer stoken = new StringTokenizer(line, "->");
					String token = stoken.nextToken();
					token = stoken.nextToken();
					dasIst[xyz] = token;
					xyz++;
				}
				xy++;
			}
		} catch (IOException e) {
			println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n"
					+ e.getMessage());
		}

		String text = "";
		int abc = 0;
		for (int x = 0; x < dasIst.length; x++) {
			if (dasIst[x] == null)
				continue;
			abc++;
		}
		if (abc > 0) {
			text += "Ich kenne folgende " + obj + ": <ul>";
			for (int x = 0; x < dasIst.length; x++) {
				if (dasIst[x] == null)
					continue;
				text += "<li>" + dasIst[x] + "</li>";
			}
			text += "</ul>";
		} else {
			text = getAntWWW(obj);

			if (text == "" || text == null) {
				String myobj = obj;
				StringTokenizer stoken = new StringTokenizer(myobj, " ");
				myobj = "";
				while (stoken.hasMoreTokens()) {
					String token = stoken.nextToken();
					token = (token.charAt(0) + "").toUpperCase()
							+ token.substring(1);
					myobj += token + " ";
				}
				myobj = myobj.trim();
				text = getAntWWW(myobj);
				if (text == "")
					text = myobj;
				if (text == null)
					text = myobj;
			}
		}

		return text;
	}

	/**
	 * Sucht nach Aliasen in gehirn/alias.ghn und liefert als Antwort einen
	 * String zurueck, in dem steht, dass das andere Wort zu verwenden ist.
	 * Liefert, wenn keinen Antwort gefunden wurde, obj zurueck!
	 * 
	 * @see getAntWWW
	 * @param obj
	 *            Das Wort, bei dem gesucht werden soll, ob es einen Alias gibt.
	 * @return Der Alias
	 */
	public String getAlias(String obj) {
		java.lang.String strDatei = absoluteUrl + "gehirn/alias.ghn";
		java.io.BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(strDatei));
		} catch (FileNotFoundException e1) {
			return "\n\n<br><br>Konnte Hirn \"" + strDatei
					+ "\" nicht laden. Bitte benachrichtige meinem Entwickler "
					+ "unter tobischulz@arcor.de !<br><br> \n\n";
		}

		String dasIst[] = new String[200];
		int xy = 0;
		int xyz = 0;
		try {
			while (true) {
				java.lang.String line = br.readLine();
				if (null == line)
					break;
				String line2 = line.trim().toLowerCase();

				if (line2.substring(0, line.indexOf("->")).trim().equals(
						obj.trim().toLowerCase())) {
					StringTokenizer stoken = new StringTokenizer(line, "->");
					String token = stoken.nextToken();
					token = stoken.nextToken();
					dasIst[xyz] = token;
					return token;
				}
				xy++;
			}
		} catch (IOException e) {
			println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n"
					+ e.getMessage());
		}

		return obj;
	}

	/**
	 * Sucht nach Antworten in gehirn/base.ghn und liefert die Antwort zurueck.
	 * Liefert, wenn keinen Antwort gefunden wurde, ant zurueck!
	 * 
	 * @param obj
	 *            Die Frage
	 * @param ant
	 *            Die bisherige Antwort
	 * @return Die Antwort
	 */
	public String getAntBaseGehirn(String obj, String ant) {
		java.lang.String strDatei = absoluteUrl + "gehirn/base.ghn";
		java.io.BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(strDatei));
		} catch (FileNotFoundException e1) {
			return e1.getMessage();
		}

		String dasIst = ant;
		Random r = new java.util.Random();

		try {
			while (true) {
				java.lang.String line = br.readLine();
				if (null == line)
					break;
				line = line.trim().toLowerCase();
				if ("".hashCode() == line.hashCode())
					continue;
				if ("-" == line)
					break;

				if (line.startsWith("#")
						&& obj.toLowerCase().indexOf(line.substring(1)) > -1) {
					Object[] anws;
					Vector anws2 = new Vector();
					int z = 0;
					java.lang.String line2 = br.readLine();
					while (!line2.trim().startsWith("-")) {
						if (null == line2)
							break;
						line2 = line2.trim();
						anws2.add(line2);

						z++;
						line2 = br.readLine().trim();
					}
					if (z == 1000) {
						break;
					}

					anws = anws2.toArray();
					return (String) anws[r.nextInt(anws.length)];
				}
			}
		} catch (IOException e) {
			println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n"
					+ e.getMessage());
		}

		return dasIst;
	}

	/**
	 * Sucht nach Antworten in gehirn/allgemein.ghn und liefert die Antwort
	 * zurueck. Liefert, wenn keinen Antwort gefunden wurde, ant zurueck!
	 * "gehirn/allgemein.ghn" kann auf
	 * http://tobiasschulz.homedns.org/intelligenz/jeliza/hirnCenter.php von
	 * jedem abgeaendert werden.
	 * 
	 * @param obj
	 *            Die Frage
	 * @param ant
	 *            Die bisherige Antwort
	 * @return Die Antwort
	 */
	public String getAntPublicGehirn(String obj, String ant) {
		obj = obj.replace("?", "").replace("!", "").replace(".", "");
		java.lang.String strDatei = absoluteUrl + "gehirn/allgemein.ghn";
		java.io.BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(strDatei));
		} catch (FileNotFoundException e1) {
			return e1.getMessage();
		}

		String dasIst = ant;
		Random r = new java.util.Random();
		Vector anws2 = new Vector();

		try {
			while (true) {
				java.lang.String line = br.readLine();
				if (null == line)
					break;
				line = line.trim().toLowerCase();
				if ("".hashCode() == line.hashCode())
					continue;
				if ("-" == line)
					continue;

				if (line.startsWith("#")
						&& (" " + obj + " ").toLowerCase().indexOf(
								(" " + line.substring(1) + " ").toLowerCase()) > -1) {
					int z = 0;
					java.lang.String line2 = br.readLine();
					while (!line2.trim().startsWith("-")) {
						if (null == line2)
							break;
						line2 = line2.trim();
						if (line2.length() < 2) {
							line2 = br.readLine().trim();
							continue;
						}
						System.out.println(line2);
						anws2.add(line2);

						z++;
						line2 = br.readLine().trim();
					}
				}
			}
			Object[] anws;
			anws = anws2.toArray();
			if (anws.length > 0) {
				return (String) anws[r.nextInt(anws.length)];
			} else {
				return ant;
			}
		} catch (IOException e) {
			println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n"
					+ e.getMessage());
		}

		return dasIst;
	}

	/**
	 * Schreib str in den Ausgabestrom.
	 * 
	 * @param str
	 *            Der String, der geschrieben werden soll
	 */
	void println(String str) {
		System.out.println(str);
	}

	/**
	 * Fügt einen Fakt der Datenbank hinzu
	 */
	public void addFakt(String subjekt, String verben, String objekt,
			String fragw, String truefalse) {
		if (subjekt == null || subjekt == "") {
			subjekt = "null";
		}
		if (verben == null || verben == "") {
			verben = "null";
		}
		if (objekt == null || objekt == "") {
			objekt = "null";
		}
		subjekt = Util.toASCII(subjekt);
		verben = Util.toASCII(verben);
		objekt = Util.toASCII(objekt);
		String sub = subjekt.toLowerCase().trim();
		String verb = verben.toLowerCase().trim();
		String obj = objekt.toLowerCase().trim();
		String subj = sub.replace("nicht", "").replace("  ", "").trim();
		String obje = obj.replace("nicht", "").replace("  ", "").trim();

		// Hauptsatz Bsp.: Till macht Unsinn
		File f = new File("wortschatz/simple-sent/" + sub + "/" + verb + "/"
				+ obj);
		f.getParentFile().mkdirs();
		try {
			FileManager.writeStringIntoFile("true", f.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Einfache Frage Bsp.: Macht Till Unsinn?
		f = new File("wortschatz/simple-ques/" + sub + "/" + verb + "/" + obj);
		f.getParentFile().mkdirs();
		try {
			FileManager.writeStringIntoFile(truefalse, f.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Hauptsatz Bsp.: Till macht Unsinn (evtl. ohne "nicht")
		f = new File("wortschatz/simple-sent/" + subj + "/" + verb + "/" + obje);
		f.getParentFile().mkdirs();
		try {
			FileManager.writeStringIntoFile("true", f.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Einfache Frage Bsp.: Macht Till Unsinn? (evtl. ohne "nicht)
		f = new File("wortschatz/simple-ques/" + subj + "/" + verb + "/" + obje);
		f.getParentFile().mkdirs();
		try {
			FileManager.writeStringIntoFile(truefalse, f.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Was Frage Bsp.: Was macht Till?
		f = new File("wortschatz/ext-ques/was/" + sub + "/" + verb + "/null");
		f.getParentFile().mkdirs();
		try {
			FileManager.writeStringIntoFile(subjekt + " " + verb + " " + objekt
					+ "!", f.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Was Frage Bsp.: Wie ist Till?
		f = new File("wortschatz/ext-ques/wie/" + sub + "/" + verb + "/null");
		f.getParentFile().mkdirs();
		try {
			FileManager.writeStringIntoFile(subjekt + " " + verb + " " + objekt
					+ "!", f.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Was Frage Bsp.: Wer ist Till?
		f = new File("wortschatz/ext-ques/wer/" + sub + "/" + verb + "/null");
		f.getParentFile().mkdirs();
		try {
			FileManager.writeStringIntoFile(subjekt + " " + verb + " " + objekt
					+ "!", f.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Was Frage Bsp.: Wo ist Till?
		f = new File("wortschatz/ext-ques/wo/" + sub + "/" + verb + "/null");
		f.getParentFile().mkdirs();
		try {
			FileManager.writeStringIntoFile(subjekt + " " + verb + " " + objekt
					+ "!", f.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList subjObj = new ArrayList();
		for (int x = 0; x < subjekt.split(" ").length; x++) {
			String tmp = subjekt.split(" ")[x];
			subjObj.add(tmp);
		}
		for (int x = 0; x < objekt.split(" ").length; x++) {
			String tmp = objekt.split(" ")[x];
			subjObj.add(tmp);
		}
		for (int x = 0; x < subjObj.size(); x++) {
			String so = (String) subjObj.get(x);
			if (so.toLowerCase() == so) {
				continue;
			}
			String w = "";
			try {
				w = FileManager.readFileIntoString("stichwort/"
						+ so.toLowerCase());
			} catch (IOException e) {
				w = "";
			}
			try {
				FileManager.writeStringIntoFile(subjekt + " " + verb + " "
						+ objekt + "\n" + w, "stichwort/" + so.toLowerCase());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

} // class Gehirn
