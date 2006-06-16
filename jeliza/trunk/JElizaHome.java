import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.homedns.tobiasschulz.io.*;

public class JElizaHome {

	public String name = "PC";

	public String nameCmd = "PC# ";

	public String user = "Du";

	public String userCmd = "Du# ";

	public String oldFra = "";

	public String oldAnt = "";

	public String oldObj = "";

	public String s = "";
	
	public String outAll = "";

	public boolean isQuesAnt = false;

	PrintWriter out;

	void println(String str) {
	}

	void userSayln(String str) {
	}

	public void doCli() {
		String fra = "";
		String ant = "";

		fra = fra.trim();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String l = "";
		try {
			while((l = br.readLine()) != null) {
				l = l.trim();
				if(l == "") ant="Bitte gib einen ganzen Satz ein!";
				fra = l;
				if(ant == "") ant = processQues(fra);
				System.out.println(ant);
				ant = "";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new JElizaHome().doCli();
	}
	
	public String processQues(String fra) {
		boolean fertig = false;
		String ant = "Uff! Da bin ich überfragt!\nKontaktieren sie doch bitte meinen Programmierer "
				+ "darüber,\n"
				+ "indem sie im Gästebuch dieser Seite einen Eintrag mit dieser Frage hinterlassen!";

		Random r = new java.util.Random();

		fra = replace(fra, "?", "");
		fra = replace(fra, ".", "");
		fra = replace(fra, "!", "");

		fra = fra.trim();

		String s = "";

		s = regel1(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = regel2(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = regel2_5(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = getAntFromGehirn(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = regel4(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = regel3(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		fertig = true;
		if (fertig) {

			ant = replace(ant, "  ", " ");
			oldFra = fra;
			oldAnt = ant;

			return ant;
		}
		return null;
	}


	public String wasIst(String obj) {
		java.lang.String strDatei = "http://tobiasschulz.homedns.org/WasIst.txt";
		java.lang.String strLs = System.getProperty("line.separator");
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
		int xy = 0;
		while (true) {
			try {
				java.lang.String line = br.readLine();
				if (null == line)
					break;
				String line2 = line.trim().toLowerCase();

				if (line2.substring(0, line.indexOf("====================="))
						.contains(obj)) {
					// if (line2.startsWith(obj + "=====================")) {
					StringTokenizer stoken = new StringTokenizer(line,
							"=====================");
					String token = stoken.nextToken();
					token = stoken.nextToken();
					dasIst = token;
					return token;
				}
			} catch (IOException e) {
				println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n"
						+ e.getMessage());
			}
			xy++;
		}

		return dasIst;
	}

	public String ichSucheSoftware(String obj) {
		java.lang.String strDatei = "http://tobiasschulz.homedns.org/"
				+ "intelligenz/jeliza/gehirn/kategorien/software.ghn";
		java.lang.String strLs = System.getProperty("line.separator");
		java.io.InputStream file = null;
		java.net.URL url = null;
		try {
			url = new java.net.URL(strDatei);
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
		String dasIst[] = new String[200];
		int xy = 0;
		int xyz = 0;
		while (true) {
			try {
				java.lang.String line = br.readLine();
				if (null == line)
					break;
				String line2 = line.trim().toLowerCase();

				if (line2.substring(0, line.indexOf("->")).contains(obj)) {
					StringTokenizer stoken = new StringTokenizer(line, "->");
					String token = stoken.nextToken();
					token = stoken.nextToken();
					dasIst[xyz] = token;
					xyz++;
				}
			} catch (IOException e) {
				println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n"
						+ e.getMessage());
			}
			xy++;
		}

		String text = "";
		text += "Ich kenne folgende " + obj + ": <ul>";
		for (int x = 0; x < dasIst.length; x++) {
			if (dasIst[x] == null)
				continue;
			text += "<li>" + dasIst[x] + "</li>";
		}
		text += "</ul>";

		return text;
	}

	public String getAlias(String obj) {
		java.lang.String strDatei = "http://tobiasschulz.homedns.org/"
				+ "intelligenz/jeliza/gehirn/alias.ghn";
		java.lang.String strLs = System.getProperty("line.separator");
		java.io.InputStream file = null;
		java.net.URL url = null;
		try {
			url = new java.net.URL(strDatei);
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
		String dasIst[] = new String[200];
		int xy = 0;
		int xyz = 0;
		while (true) {
			try {
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
			} catch (IOException e) {
				println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n"
						+ e.getMessage());
			}
			xy++;
		}

		return obj;
	}

	public String getAntFromGehirn(String obj, String ant) {
		java.lang.String strDatei = "http://tobiasschulz.homedns.org/intelligenz/jeliza/gehirn/base.ghn";
		java.lang.String strLs = System.getProperty("line.separator");
		java.io.InputStream file = null;
		java.net.URL url = null;
		try {
			url = new java.net.URL(strDatei); //$NON-NLS-1$
			file = url.openStream();
		} catch (java.net.MalformedURLException mue) {
			System.out.println("Fehlerhafte URL-Adresse / Datei");
		} catch (java.io.IOException e) {
			System.out
					.println("IOException beim Initialisieren der Verbindung zum Server");
		}
		java.io.BufferedReader br = new java.io.BufferedReader(
				new java.io.InputStreamReader(file));
		String dasIst = ant;
		while (true) {
			try {
				java.lang.String line = br.readLine();
				if (null == line)
					break;
				line = line.trim().toLowerCase();
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

					Random r = new java.util.Random();

					anws = anws2.toArray();
					return (String) anws[r.nextInt(anws.length)];
				}
			} catch (IOException e) {
				println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n"
						+ e.getMessage());
			}
		}

		return dasIst;
	}
	private String regel1(String _fra, String _ant) {
		String ant = _ant;
		String fra = _fra;

		String antExit = "Auf Wiedersehen. Du schuldest mit 500 Euro fuer die Beratung.";
		String[] antJa = { "Ach so.", "Gut.", "Warscheinlich hast du Recht!",
				"Wie du meinst!" };
		String[] antNein = { "Sicher?", "Wirklich nicht?", "Meinst du?",
				"Warum?" };
		Random r = new java.util.Random();

		Date dt = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss !");

		if (fra.indexOf("tschue") > -1)
			return antExit;
		if (fra.indexOf("auf wiedersehen") > -1)
			return antExit;
		if (fra.indexOf("bis bald") > -1)
			return antExit;
		if (fra.indexOf("bis nachher") > -1)
			return antExit;
		if (fra.indexOf("danke") > -1)
			return "Fuer was bekankst du dich?";
		if (fra.indexOf("bitte") > -1)
			return "Warum sagst du bitte? Ich bin eine Maschine.";
		if (fra.toLowerCase().indexOf("wieviel uhr") > -1)
			return "Es ist genau (!) " + df2.format(dt);
		if (fra.toLowerCase().indexOf("der wievielte") > -1)
			return "Wir haben den " + df.format(dt) + "!";
		if (fra.toLowerCase().indexOf("der wie vielte") > -1)
			return "Wir haben den " + df.format(dt) + "!";
		if (fra.toLowerCase().indexOf("den wie vielt") > -1)
			return "Wir haben den " + df.format(dt) + "!";
		if (fra.toLowerCase().indexOf("den wievielte") > -1)
			return "Wir haben den " + df.format(dt) + "!";

		if (fra.startsWith("ja") && fra.length() < 4)
			return antJa[r.nextInt(antJa.length)];

		if (fra.startsWith("nein") && fra.length() < 6)
			return antNein[r.nextInt(antJa.length)];

		return ant;
	}

	private String regel2(String _fra, String _ant) {
		String ant = _ant;
		String fra = _fra;

		fra = fra.toLowerCase();

		Random r = new java.util.Random();

		if (fra.indexOf("eliza") > -1)
			return "Ja ich bin JEliza. Oder was hast du gefragt?";

		return ant;

	}

	private String regel2_5(String _fra, String _ant) {
		String ant = _ant;
		String fra = _fra;

		fra = fra.toLowerCase();

		Random r = new java.util.Random();

		fra = replace(fra, "ä", "ae");
		fra = replace(fra, "ö", "oe");
		fra = replace(fra, "ü", "ue");
		fra = replace(fra, "ß", "ss");
		fra = replace(fra, "#", "");
		fra = replace(fra, "?", "");
		fra = replace(fra, "!", "");
		fra = replace(fra, "\n", "");
		fra = replace(fra, "\r", "");
		String ofra = fra;

		{
			StringTokenizer stoken = new StringTokenizer(fra, " ");
			String obj = "";
			while (stoken.hasMoreTokens()) {
				String tok = stoken.nextToken();
				if (tok.toLowerCase() != getAlias(tok.toLowerCase())
						.toLowerCase()
						&& !fra.toLowerCase().contains(
								getAlias(tok.toLowerCase()).toLowerCase())) {
					ant = "Meintest du vielleicht " + getAlias(tok)
							+ ", du hast nämlich " + tok + " eingegeben?";
					return ant;
				}
			}
		}

		fra = replace(fra, ".", "");
		fra = replace(fra, ",", "");
		fra = replace(fra, ";", "");
		fra = replace(fra, "-", "");
		fra = replace(fra, "$", "");
		fra = replace(fra, "§", "");
		fra = replace(fra, "/", " ");
		fra = replace(fra, "(", " ");
		fra = replace(fra, ")", " ");
		fra = replace(fra, "=", " ");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "  ", "");

		if (fra.startsWith("ich suche") || fra.startsWith("wo finde ich")
				|| fra.startsWith("kennst du")) {
			StringTokenizer stoken = new StringTokenizer(fra, " ");
			stoken.nextToken();
			stoken.nextToken();
			if (fra.startsWith("wo finde ich"))
				stoken.nextToken();
			String obj = "";
			while (stoken.hasMoreTokens()) {
				String tok = stoken.nextToken();
				if (tok.contains("gut"))
					break;
				if (tok.contains("ein"))
					break;
				obj += tok + " ";
			}
			String tok = stoken.nextToken();
			obj = tok;
			obj = obj.trim();
			obj = obj.toLowerCase();
			String dasIst = "";
			dasIst = ichSucheSoftware(obj);
			if (dasIst == null) {
				dasIst = ant;
			}

			return dasIst;
		}

		if (fra.startsWith("was") || fra.startsWith("wer")) {
			StringTokenizer stoken = new StringTokenizer(fra, " ");
			stoken.nextToken();
			String obj = "";
			while (stoken.hasMoreTokens()) {
				obj += stoken.nextToken() + " ";
			}
			obj = obj.trim();
			obj = obj.toLowerCase();
			String dasIst = "";
			dasIst = wasIst("was " + obj);
			if (dasIst == null) {
				outAll = "<script>"
						+ "window.setTimeout(self.location.href=\""
						+ "http://tobiasschulz.homedns.org/intelligenz/jeliza/addWord.php?was=was "
						+ obj + "\", 3000);" + "</script>";
				dasIst = ant;
			}

			return dasIst;
		}

		if (fra.startsWith("wieso") || fra.startsWith("warum")
				|| fra.startsWith("weshalb")) {
			StringTokenizer stoken = new StringTokenizer(fra, " ");
			stoken.nextToken();
			String obj = "";
			while (stoken.hasMoreTokens()) {
				obj += stoken.nextToken() + " ";
			}
			obj = obj.trim();
			obj = obj.toLowerCase();
			String dasIst = "";
			dasIst = wasIst("wieso " + obj);
			if (dasIst == null) {
				outAll = "<script>"
						+ "window.setTimeout(self.location.href=\""
						+ "http://tobiasschulz.homedns.org/intelligenz/jeliza/addWord.php?was=wieso "
						+ obj + "\", 3000);" + "</script>";
				dasIst = ant;
			}

			return dasIst;
		}

		if (fra.startsWith("wie")) {
			StringTokenizer stoken = new StringTokenizer(fra, " ");
			stoken.nextToken();
			String obj = "";
			while (stoken.hasMoreTokens()) {
				obj += stoken.nextToken() + " ";
			}
			obj = obj.trim();
			obj = obj.toLowerCase();
			String dasIst = "";
			dasIst = wasIst("wie " + obj);
			if (dasIst == null) {
				outAll = "<script>"
						+ "window.setTimeout(self.location.href=\""
						+ "http://tobiasschulz.homedns.org/intelligenz/jeliza/addWord.php?was=wie "
						+ obj + "\", 3000);" + "</script>";
				dasIst = ant;
			}

			return dasIst;
		}

		return ant;
	}

	private String regel3(String _fra, String _ant) {
		String ant = _ant;
		String fra = _fra;

		Random r = new java.util.Random();
		fra = replace(fra, "?", "");
		fra = replace(fra, "!", "");
		fra = replace(fra, ".", "");
		fra = replace(fra, ",", "");
		fra = replace(fra, ";", "");
		fra = replace(fra, "-", "");
		fra = replace(fra, "$", "");
		fra = replace(fra, "§", "");
		fra = replace(fra, "/", " ");
		fra = replace(fra, "(", " ");
		fra = replace(fra, ")", " ");
		fra = replace(fra, "=", " ");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "\n", "");
		fra = replace(fra, "\r", "");

		// String[] words = fra.split(" ");
		Vector nomen = new Vector();
		String tmpAdj = "";
		int tmpAdjNum = 0;
		int num = 0;
		StringTokenizer stoken = new StringTokenizer(fra, " ");
		while (stoken.hasMoreElements()) {
			String token = stoken.nextToken().trim();
			// for (int x = 0; x < words.length; x++) {
			if (token == "" || token == " " || token == null)
				continue;
			if (token.charAt(0) == (token.charAt(0) + "").toUpperCase().charAt(
					0)) {
				nomen.add(((tmpAdjNum == num - 1) ? tmpAdj : "") + " " + token);
			} else if (token != "und" || token != "oder" || token != "aber") {
				token = ((tmpAdjNum == num - 1) ? tmpAdj : "") + " " + token;
				tmpAdj = token;
				tmpAdjNum = num;
			}
			num++;
		}

		/*
		 * System.out.println(words[x]); if (words[x] == "" || words[x] == " " ||
		 * words[x] == null) continue; if (words[x].charAt(0) ==
		 * (words[x].charAt(0) + "").toUpperCase() .charAt(0)) {
		 * nomen.add(words[x]); }
		 */
		// }
		if (nomen.size() == 0) {
			ant = ant;
		} else if (nomen.size() < 2) {
			ant = nomen.get(0) + "? Erzaehle mir, was du darueber weisst !";
		} else if (nomen.size() < 3) {
			ant = nomen.get(0) + " und " + nomen.get(1) + "? Erzaehle mir, "
					+ "was du darueber weisst !";
		} else {
			ant = nomen.get(0) + ", " + nomen.get(1) + " und " + nomen.get(2)
					+ "? Was hat es damit auf sich ?";
		}

		return ant;
	}

	private String regel4(String _fra, String _ant) {
		String ant = _ant;
		String fra = _fra;

		String[] ichVerben = { "habe", "bin", "fuehle", "laufe", "renne",
				"gehe", "kann", "sehe", "hatte", "war", "fuehlte", "lief",
				"rannte", "ging", "konnte", "gebe", "gab", "nehme", "nahm",
				"spiele", "spielte", "arbeite", "arbeitete" };
		String[] ichVerben2 = { "hast", "bist", "fuehst", "laeufst", "rennst",
				"gehst", "kannst", "siehst", "hattest", "warst", "fuehltest",
				"liefst", "rannte", "gegangen bist", "konntest", "gibst",
				"gabst", "nimmst", "nahmst", "spielst", "spieltest",
				"arbeitest", "hattest gearbeitet" };
		String[] ichVerben3 = { "XhaXstX", "XbiXstX", "XfuXehXstX",
				"XlaXeuXfsXt", "XreXnnXstX", "gXehXstX", "XkaXnnXstX",
				"sXiehXstX", "hXatXteXstX", "wXarXstX", "fXueXhlXteXstX",
				"XliXefXstX", "XraXnnXteX", "XgeXgaXngXenXYbXisXt",
				"XkoXnnXteXstX", "XgiXbsXt", "gXabXstX", "XniXmmXstX",
				"XnaXhmXstX", "sXpiXelXstX", "sXpiXelXteXstX",
				"XarXbeXitXesXt", "XhaXttXesXtYXgeXarXbeXitXetX" };
		Random r = new java.util.Random();

		fra = replace(fra, "-", "");
		fra = replace(fra, "$", "");
		fra = replace(fra, "§", "");
		fra = replace(fra, "/", " ");
		fra = replace(fra, "(", " ");
		fra = replace(fra, ")", " ");
		fra = replace(fra, "=", " ");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "\n", "");
		fra = replace(fra, "\r", "");

		fra = replace(fra, "?", " ? ");
		fra = replace(fra, "!", " ! ");
		fra = replace(fra, ".", " . ");

		// String[] words = fra.split(" ");
		int y = 0;
		Vector nomen = new Vector();
		StringTokenizer stoken = new StringTokenizer(fra, " ");
		while (stoken.hasMoreElements()) {
			String token = stoken.nextToken();
			// for (int x = 0; x < words.length; x++) {
			if (token == "" || token == " " || token == null)
				continue;
			if (token == "?" || token == "!" || token == ".")
				break;
			nomen.add(token);
			y++;
		}

		fra = replace(fra, "?", "");
		fra = replace(fra, "!", "");
		fra = replace(fra, ".", "");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "  ", "");

		// if (y < 12) {
		ant = " ";
		for (int x = 0; x < nomen.size(); x++) {
			ant += nomen.get(x) + " ";
		}
		ant = replace(ant, " Du ", " XDXuX ");
		ant = replace(ant, " Dein", " XDXeXiXnX");
		ant = replace(ant, " Dir ", " XDXiXrX ");
		ant = replace(ant, " Dich ", " XDXiXcXhX ");

		ant = replace(ant, " du ", " XDXuX ");
		ant = replace(ant, " dein", " XDXeXiXnX");
		ant = replace(ant, " dir ", " XDXiXrX ");
		ant = replace(ant, " dich ", " XDXiXcXhX ");

		ant = replace(ant, " ich ", " Du ");
		ant = replace(ant, " mein", " Dein");
		ant = replace(ant, " mir ", " Dir ");
		ant = replace(ant, " mich ", " Dich ");

		ant = replace(ant, " Ich ", " du ");
		ant = replace(ant, " Mein", " dein");
		ant = replace(ant, " Mir ", " dir ");
		ant = replace(ant, " Mich ", " dich ");

		ant = replace(ant, " XDXuX ", " ich ");
		ant = replace(ant, " XDXeXiXnX", " mein");
		ant = replace(ant, " XDXiXrX ", " mir ");
		ant = replace(ant, " XDXiXcXhX ", " mich ");

		for (int x = 0; x < ichVerben.length; x++) {
			ant = replace(ant, " " + ichVerben2[x] + " ", " " + ichVerben3[x]
					+ " ");
		}

		for (int x = 0; x < ichVerben.length; x++) {
			ant = replace(ant, " " + ichVerben[x] + " ", " " + ichVerben2[x]
					+ " ");
		}

		for (int x = 0; x < ichVerben.length; x++) {
			ant = replace(ant, " " + ichVerben3[x] + " ", " " + ichVerben[x]
					+ " ");
		}

		ant = ant.trim();

		String[] antGrund = { "? Willst du das so?",
				"? Entspricht das deinen Vorstellungen?", "? Langweilig...",
				"? Gut.", "? Naja...", "? Dz Dz ...", "? Mmmmm...",
				"? Schwafel nur weiter. Ich hoere dir mit Sicherheit zu!",
				"? Wirklich? Schrecklich ...", "? Sehen das alle so?",
				"? Koenntest du mir sagen warum?", "? Oh gott!", "? Warum?",
				"? Weshalb?", "? Ist das Normal?",
				"? Bist du sicher dass ich das verstehe?",
				"? ich verstehe nicht ..." };
		ant += antGrund[r.nextInt(antGrund.length)];

		return ant;
	}

	public String replace(String ss, String search, String replace) {
		s = ss;
		while (true) {
			int pos = s.indexOf(search);
			if (pos >= 0) {
				s = s.substring(0, pos) + replace
						+ s.substring(pos + search.length(), s.length());

			} else {
				return s;
			}
		}
	}
}