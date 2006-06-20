package org.homedns.tobiasschulz.apps.jeliza;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Hilfsklasse des Java-Servlets JEliza, die einen Wrapper fuer die
 * Gehirn-Dateien von JEliza bereitstellt.
 * 
 * @author Tobias Schulz
 * @version 0.2
 */
public class Gehirn {

	String absoluteUrl = "";

	/**
	 * Konstruktor
	 * 
	 * @param tmp
	 *            Die Absolute URL zum JEliza-Stammordner
	 */
	public Gehirn(String tmp) {
		absoluteUrl = tmp;
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
		String dasIst = null;
		String all = "";
		String tabu = "";
		@SuppressWarnings("unused")
		boolean tabi = false;
		@SuppressWarnings("unused")
		String obj2 = obj.trim().toLowerCase();
		int xy = 0;
		try {
			while (true) {
				java.lang.String line = br.readLine();
				if (null == line)
					break;
				String line2 = line.trim().toLowerCase();

				if (line2.contains("Portal".toLowerCase())) {
					continue;
				}
				if (line2.contains("<div class=\"sideBox\"".toLowerCase())) {
					tabu = "</div>";
					tabi = true;
				}

				if (line2.contains("<b>Diese Seite existiert nicht.</b>"
						.toLowerCase())) {
					return null;
				}

				if (tabu != "") {
					if (line2.contains(tabu)) {
						tabi = false;
						tabu = "";
					} else {
						continue;
					}
				}

				all += line;

				if (xy > 60)
					break;

				xy++;
			}
		} catch (IOException e) {
			println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n"
					+ e.getMessage());
		}

		int p1 = all.indexOf("<p>");
		int p2 = all.indexOf("</p>");

		if (p1 > -1 && p2 > -1) {
			dasIst = all.substring(p1, p2);
			dasIst = dasIst.replace("<p>", "");
			dasIst = dasIst.replace("</p>", "");
			dasIst = dasIst.replace("„", "\"");
			dasIst = dasIst.replace("“", "\"");
			dasIst = dasIst.replace("<a ", "<!-- <a");
			dasIst = dasIst.replace("\">", "\"> -->");
			dasIst = dasIst.replace("</a>", "<!--</a> -->");
			return dasIst;
		}

		return null;
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
		java.lang.String strDatei = absoluteUrl + "gehirn/kategorien/software.ghn";
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
			return "\n\n<br><br>Konnte Hirn \"" + strDatei +
					"\" nicht laden. Bitte benachrichtige meinem Entwickler " +
					"unter tobischulz@arcor.de !<br><br> \n\n";
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
				if ("-" == line)
					break;

				if (line.startsWith("#")
						&& obj.toLowerCase().indexOf(line.substring(1)) > -1) {
					Object[] anws;
					Vector<String> anws2 = new Vector<String>();
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
		java.lang.String strDatei = absoluteUrl + "gehirn/allgemein.ghn";
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
				if ("-" == line)
					break;

				if (line.startsWith("#")
						&& obj.toLowerCase().indexOf(line.substring(1)) > -1) {
					Object[] anws;
					Vector<String> anws2 = new Vector<String>();
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
	 * Schreib str in den Ausgabestrom.
	 * 
	 * @param str
	 *            Der String, der geschrieben werden soll
	 */
	void println(String str) {
		System.out.println(str);
	}
} // class Gehirn
