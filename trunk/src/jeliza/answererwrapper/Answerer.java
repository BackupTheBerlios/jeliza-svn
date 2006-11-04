package jeliza.answererwrapper;

import java.io.*;
import java.text.*;
import java.util.*;
import jeliza.util.Util;

/**
 * Hilfsklasse des Java-Servlets JEliza, die mit Hilfe von 3 "Regeln" das
 * Erstellen einer Antwort ermoeglicht
 * 
 * @author Tobias Schulz
 * @version 0.3
 */

public class Answerer {

	public String naechsteFra = "0";

	public String subjekt = "";

	public String verb = "";

	public String objekt = "";

	public String fragewort = "";

	String antExit = "Auf Wiedersehen.";

	String[] antJa = { "Ach so.", "Gut.", "Warscheinlich hast du Recht!",
			"Wie du meinst!" };

	String[] antNein = { "Sicher?", "Wirklich nicht?", "Meinst du?", "Warum?" };

	String[] vorstellen = { "ich heisse", "mein name ist", "man nennt mich",
			"ich nenne mich" };

	String[] vorstellenAnt = { "Das ist mein Traumname!",
			"Kein schlechter Name.", "So moechte ich auch heissen!",
			"Solche Namen mag ich.", "Heisst du wirklich so?" };

	public static String[] pronomen = { "ich", "du", "er", "sie", "es",
			"meiner", "deiner", "seiner", "ihrer", "seiner", "mir", "dir",
			"ihm", "ihr", "ihm", "mich", "dich", "ihn", "sie", "es", "wir",
			"ihr", "sie", "unser", "euer", "ihrer", "uns", "euch", "ihnen",
			"uns", "euch", "sie", "mein", "meine", "meiner", "meines",
			"meinem", "meinen", "dein", "deine", "deiner", "deines", "deinem",
			"deinen", "sein", "seine", "seiner", "seines", "seinem", "seinen",
			"ihr", "ihre", "ihrer", "ihres", "ihrem", "ihren", "unser",
			"unsere", "unseres", "unserem", "unserer", "unseren", "euer",
			"eures", "eueres", "eurer", "euerer", "eure", "euere", "euren",
			"eueren", "der", "die", "das", "die", "dessen", "deren", "dessen",
			"deren", "derer", "dem", "der", "dem", "denen", "den", "die",
			"das", "die", "dieser", "diese", "dieses", "diese", "dieses",
			"dieser", "dieses", "dieser", "diesem", "dieser", "diesem",
			"diesen", "diesen", "diese", "dieses", "diese", "jemand",
			"niemand", "jemandes", "niemandes", "jemandem", "niemandem",
			"jemanden", "niemanden" };

	Random r = new java.util.Random();

	/**
	 * Konstruktor
	 * 
	 * @param tmp
	 *            Die Absolute URL zum JEliza-Stammordner
	 */
	public Answerer() {
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
	public String getSimple(String fraParameter, String antParameter) {
		String ant = antParameter;
		String fra = fraParameter;

		Date dt = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss !");

		if (fra.indexOf("tschue") > -1)
			return antExit;
		if (fra.indexOf("Tschoe") > -1)
			return antExit;
		if (fra.indexOf("auf wiedersehen") > -1)
			return antExit;
		if (fra.indexOf("bis bald") > -1)
			return antExit;
		if (fra.indexOf("bis nachher") > -1)
			return antExit;
		if (fra.indexOf("auf bald") > -1)
			return antExit;
		if (fra.indexOf("danke") > -1)
			return "Fuer was bedankst du dich?";
		if (fra.indexOf("bitte") > -1)
			return "Warum sagst du bitte? Ich bin eine Maschine.";
		if (fra.toLowerCase().indexOf("wieviel uhr") > -1)
			return "Es ist genau (!) " + df2.format(dt);
		if (fra.toLowerCase().indexOf("wie viel uhr") > -1)
			return "Es ist genau (!) " + df2.format(dt);
		if (fra.toLowerCase().indexOf("der wievielte") > -1)
			return "Wir haben den " + df.format(dt) + "!";
		if (fra.toLowerCase().indexOf("der wie vielte") > -1)
			return "Wir haben den " + df.format(dt) + "!";
		if (fra.toLowerCase().indexOf("den wie vielt") > -1)
			return "Wir haben den " + df.format(dt) + "!";
		if (fra.toLowerCase().indexOf("den wievielte") > -1)
			return "Wir haben den " + df.format(dt) + "!";
		if (fra.toLowerCase().indexOf("ich heisse") > -1) {
			System.setProperty("jeliza.user.name", fra.substring(10).trim());
		}
		if (fra.toLowerCase().indexOf("mein name ist") > -1) {
			System.setProperty("jeliza.user.name", fra.substring(13).trim());
		}
		if (fra.toLowerCase().indexOf("man nennt mich") > -1) {
			System.setProperty("jeliza.user.name", fra.substring(14).trim());
		}

		if (fra.startsWith("ja") && fra.length() < 4)
			return antJa[r.nextInt(antJa.length)];

		if (fra.startsWith("nein") && fra.length() < 6)
			return antNein[r.nextInt(antNein.length)];

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
	public String getQuestionWord(String fraParameter, String antParameter) {
		String ant = antParameter;
		String fra = fraParameter;

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
				if (tok.contains("vielleicht"))
					continue;
				if (tok.contains("wenigstens"))
					continue;
				if (tok.contains("ueberhaupt"))
					continue;
				if (tok.contains("denn"))
					continue;
				if (tok.contains("also"))
					continue;
				obj += tok + " ";
			}
			obj = obj.trim();
			obj = obj.toLowerCase();
			String dasIst = "";
			dasIst = getAntWWW(obj);
			if (dasIst != null) {
				return dasIst;
			}
		}

		return ant;
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
			return "Das weiss ich nicht!";
		}

		if (definition.toLowerCase().contains("artikel verschwunden")) {
			return "Das weiss ich nicht!";
		}

		return definition;
	}

	/**
	 * "Regel" 4, eine Methode zum Beantworten von Rechnungen (Gleichungen) und
	 * Wetter-Fragen etc.
	 * 
	 * @param fraParameter
	 *            Die Frage
	 * @param antParameter
	 *            Die bisherige Antwort
	 * @return Die Antwort
	 */
	public String getInformation(String fraParameter, String antParameter) {
		String ant = antParameter;
		String fra = fraParameter;

		boolean isMath = false;
		for (int x = 0; x < 9; x++) {
			if (fra.contains("" + x)) {
				isMath = true;
			}
		}

		if (isMath) {

			fra = fra.replace("?", "");
			fra = fra.replace("!", "");
			fra = fra.replace(",", ".");
			fra = fra.replace(" mal ", "*");
			fra = fra.replace(" und ", "+");
			fra = fra.replace(" plus ", "+");
			fra = fra.replace(" geteilt ", "/");
			fra = fra.replace(" durch ", "");
			fra = fra.replace(" minus ", "-");
			fra = fra.replace("+", " + ");
			fra = fra.replace("-", " - ");
			fra = fra.replace("*", " * ");
			fra = fra.replace("/", " / ");
			fra = fra.replace("  ", " ");
			fra = fra.replace("  ", " ");

			String rechenArt = "";

			double[] nums = new double[10];
			int z = 0;
			StringTokenizer stoken = new StringTokenizer(fra, " ");
			while (stoken.hasMoreElements()) {
				String token = stoken.nextToken();
				if (token == "" || token == " " || token == null)
					continue;
				token = token.trim();
				if (token == "?" || token == "!" || token == ".")
					continue;
				if (token.contains("+") || token.contains("-")
						|| token.contains("*") || token.contains("/")) {
					rechenArt = token;
					continue;
				}
				try {
					int y = -1;
					for (char x = 'a'; x < 'z'; x++) {
						if (token.indexOf(x) > -1) {
							y = 2;
							continue;
						}
					}
					if (y > -1) {
						continue;
					}

					nums[z] = Double.parseDouble(token);
					z++;
					continue;
				} catch (NumberFormatException nfe) {
					// Nur Zahlen sollen num1 hinzugefuegt werden
					continue;
				}
			}

			if (rechenArt.trim() == "") {
				return ant;
			}

			double sum = 0;

			if (rechenArt.contains("+")) {
				sum = nums[0];
				for (int x = 1; x < nums.length; x++) {
					sum += nums[x];
				}
			}

			if (rechenArt.contains("-")) {
				sum = nums[0];
				for (int x = 1; x < nums.length; x++) {
					sum -= nums[x];
				}
			}

			if (rechenArt.contains("*")) {
				sum = nums[0];
				for (int x = 1; x < nums.length; x++) {
					System.out.println(nums[x - 1]);
					if (Double.toString(nums[x]) == null
							|| Double.toString(nums[x]) == Double.toString(0d)) {
						continue;
					}
					sum *= nums[x];
				}
			}

			if (rechenArt.contains("/")) {
				sum = nums[0];
				for (int x = 1; x < nums.length; x++) {
					if (Double.toString(nums[x]) == null || nums[x] == 0d) {
						continue;
					}
					sum /= nums[x];
				}
			}

			ant = "Das Ergebnis ist " + sum + "!";
			ant = ant.replace(".", ",");
			return ant;
		}

		ant = ant.trim();

		return ant;

	}

} // class Regeln
