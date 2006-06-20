package org.homedns.tobiasschulz.apps.jeliza;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Hilfsklasse des Java-Servlets JEliza, die mit Hilfe von 3 "Regeln" das
 * Erstellen einer Antwort ermoeglicht
 * 
 * @author Tobias Schulz
 * @version 0.2
 */
public class Regeln {

	String absoluteUrl = "";

	Gehirn hirn = new Gehirn(absoluteUrl);

	public String outAll = "";

	public String naechsteFra = "0";

	/**
	 * Konstruktor
	 * 
	 * @param tmp
	 *            Die Absolute URL zum JEliza-Stammordner
	 */
	public Regeln(String tmp) {
		absoluteUrl = tmp;
	}

	/**
	 * "Regel" 1, eine Methode zum Beantworten der Frage des Users. Liefert die
	 * Bisherige Antwort zurueck, wenn keine Eigene erstellbar. Antwortet auf
	 * einige einfache Fragen
	 * 
	 * @param fraParameter
	 *            Die Frage
	 * @param antParameter
	 *            Die bisherige Antwort
	 * @return Die Antwort
	 */
	public String regel1(String fraParameter, String antParameter) {
		String ant = antParameter;
		String fra = fraParameter;

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

		if (fra.indexOf("eliza") > -1)
			return "Ja ich bin JEliza. Oder was hast du gefragt?";

		return ant;

	}

	/**
	 * "Regel" 2, eine Methode zum Beantworten der Frage des Users. Liefert die
	 * bisherige Antwort zurueck, wenn keine Eigene erstellbar. Antwortet auf
	 * Fragen mit "Wer", "Was", "Wie", "Warum", "Wieso", "Weshalb". Benutzt bei
	 * "Was Ist" die Wikipedia
	 * 
	 * @param fraParameter
	 *            Die Frage
	 * @param antParameter
	 *            Die bisherige Antwort
	 * @return Die Antwort
	 */
	public String regel2(String fraParameter, String antParameter) {
		String ant = antParameter;
		String fra = fraParameter;

		@SuppressWarnings("unused")
		Random r = new java.util.Random();

		fra = Util.replace(fra, "ä", "ae");
		fra = Util.replace(fra, "ö", "oe");
		fra = Util.replace(fra, "ü", "ue");
		fra = Util.replace(fra, "ß", "ss");
		fra = Util.replace(fra, "#", "");
		fra = Util.replace(fra, "?", "");
		fra = Util.replace(fra, "!", "");
		fra = Util.replace(fra, "\n", "");
		fra = Util.replace(fra, "\r", "");

		String ofra = fra;
		fra = fra.toLowerCase();
		{
			StringTokenizer stoken = new StringTokenizer(fra, " ");
			@SuppressWarnings("unused")
			String obj = "";
			while (stoken.hasMoreTokens()) {
				String tok = stoken.nextToken();
				String alias = hirn.getAlias(tok.toLowerCase());
				if (tok.toLowerCase() != alias.toLowerCase()
						&& !fra.toLowerCase().contains(alias.toLowerCase())) {
					ant = "Meintest du vielleicht " + alias
							+ ", du hast nämlich " + tok + " eingegeben?";
					// session.setAttribute("extra", fra.toLowerCase().replace(
					// tok.toLowerCase(), alias.toLowerCase()));
					naechsteFra = fra.toLowerCase().replace(tok.toLowerCase(),
							alias.toLowerCase());
					return ant;
				}
			}
		}

		fra = Util.replace(fra, "\"", "");
		fra = Util.replace(fra, "\"", "");
		fra = Util.replace(fra, ".", "");
		fra = Util.replace(fra, ",", "");
		fra = Util.replace(fra, ";", "");
		fra = Util.replace(fra, "$", "");
		fra = Util.replace(fra, "§", "");
		fra = Util.replace(fra, "(", " ");
		fra = Util.replace(fra, ")", " ");
		fra = Util.replace(fra, "=", " ");
		fra = Util.replace(fra, "  ", "");
		fra = Util.replace(fra, "  ", "");

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
					continue;
				if (tok.contains("ein"))
					continue;
				if (tok.contains("die"))
					continue;
				if (tok.contains("den"))
					continue;
				if (tok.contains("der"))
					continue;
				if (tok.contains("des"))
					continue;
				if (tok.contains("das"))
					continue;
				obj += tok + " ";
			}
			obj = obj.trim();
			obj = obj.toLowerCase();
			String dasIst = "";
			dasIst = hirn.getAntKategorie(obj);
			if (dasIst == null) {
				dasIst = ant;
			}

			return dasIst;
		}

		if (fra.startsWith("was ist") || fra.startsWith("wer ist")) {
			StringTokenizer stoken = new StringTokenizer(ofra, " ");
			stoken.nextToken();
			stoken.nextToken();
			String obj = "";
			while (stoken.hasMoreTokens()) {
				String tok = stoken.nextToken();
				if (tok.startsWith("ein"))
					continue;
				if (tok.startsWith("die"))
					continue;
				if (tok.startsWith("den"))
					continue;
				if (tok.startsWith("der"))
					continue;
				if (tok.startsWith("des"))
					continue;
				if (tok.startsWith("das"))
					continue;
				obj += tok + " ";
			}
			obj = obj.trim();
			obj = obj.toLowerCase();
			String dasIst = "";
			dasIst = hirn.getAntWWW(obj);
			if (dasIst != null) {
				return dasIst;
			}
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
			dasIst = hirn.getAnt("was " + obj);
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
			dasIst = hirn.getAnt("wieso " + obj);
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
			dasIst = hirn.getAnt("wie " + obj);
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

	/**
	 * "Regel" 3, eine Methode zum Beantworten der Frage des Users. Liefert die
	 * bisherige Antwort zurueck, wenn keine Eigene erstellbar. Antwortet auf
	 * alle Fragen, auf die mit Regel 1 und 2 keine Antworten gefunden wurden.
	 * 
	 * @param fraParameter
	 *            Die Frage
	 * @param antParameter
	 *            Die bisherige Antwort
	 * @return Die Antwort
	 */
	public String regel3(String fraParameter, String antParameter) {
		String ant = antParameter;
		String fra = fraParameter;

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

		fra = Util.replace(fra, "-", "");
		fra = Util.replace(fra, "$", "");
		fra = Util.replace(fra, "§", "");
		fra = Util.replace(fra, "/", " ");
		fra = Util.replace(fra, "(", " ");
		fra = Util.replace(fra, ")", " ");
		fra = Util.replace(fra, "=", " ");
		fra = Util.replace(fra, "  ", "");
		fra = Util.replace(fra, "  ", "");
		fra = Util.replace(fra, "\n", "");
		fra = Util.replace(fra, "\r", "");

		fra = Util.replace(fra, "?", " ? ");
		fra = Util.replace(fra, "!", " ! ");
		fra = Util.replace(fra, ".", " . ");

		int y = 0;
		Vector<String> nomen = new Vector<String>();
		StringTokenizer stoken = new StringTokenizer(fra, " ");
		while (stoken.hasMoreElements()) {
			String token = stoken.nextToken();
			if (token == "" || token == " " || token == null)
				continue;
			if (token == "?" || token == "!" || token == ".")
				break;
			nomen.add(token);
			y++;
		}

		fra = Util.replace(fra, "?", "");
		fra = Util.replace(fra, "!", "");
		fra = Util.replace(fra, ".", "");
		fra = Util.replace(fra, "  ", "");
		fra = Util.replace(fra, "  ", "");

		ant = " ";
		for (int x = 0; x < nomen.size(); x++) {
			ant += nomen.get(x) + " ";
		}
		ant = Util.replace(ant, " Du ", " XDXuX ");
		ant = Util.replace(ant, " Dein", " XDXeXiXnX");
		ant = Util.replace(ant, " Dir ", " XDXiXrX ");
		ant = Util.replace(ant, " Dich ", " XDXiXcXhX ");

		ant = Util.replace(ant, " du ", " XDXuX ");
		ant = Util.replace(ant, " dein", " XDXeXiXnX");
		ant = Util.replace(ant, " dir ", " XDXiXrX ");
		ant = Util.replace(ant, " dich ", " XDXiXcXhX ");

		ant = Util.replace(ant, " ich ", " Du ");
		ant = Util.replace(ant, " mein", " Dein");
		ant = Util.replace(ant, " mir ", " Dir ");
		ant = Util.replace(ant, " mich ", " Dich ");

		ant = Util.replace(ant, " Ich ", " du ");
		ant = Util.replace(ant, " Mein", " dein");
		ant = Util.replace(ant, " Mir ", " dir ");
		ant = Util.replace(ant, " Mich ", " dich ");

		ant = Util.replace(ant, " XDXuX ", " ich ");
		ant = Util.replace(ant, " XDXeXiXnX", " mein");
		ant = Util.replace(ant, " XDXiXrX ", " mir ");
		ant = Util.replace(ant, " XDXiXcXhX ", " mich ");

		for (int x = 0; x < ichVerben.length; x++) {
			ant = Util.replace(ant, " " + ichVerben2[x] + " ", " "
					+ ichVerben3[x] + " ");
		}

		for (int x = 0; x < ichVerben.length; x++) {
			ant = Util.replace(ant, " " + ichVerben[x] + " ", " "
					+ ichVerben2[x] + " ");
		}

		for (int x = 0; x < ichVerben.length; x++) {
			ant = Util.replace(ant, " " + ichVerben3[x] + " ", " "
					+ ichVerben[x] + " ");
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
} // class Regeln
