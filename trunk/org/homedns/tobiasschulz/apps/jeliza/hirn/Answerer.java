package org.homedns.tobiasschulz.apps.jeliza.hirn;

import java.io.*;
import java.text.*;
import java.util.*;
import org.homedns.tobiasschulz.util.satzparser.*;
import org.homedns.tobiasschulz.apps.jeliza.Util;
import org.homedns.tobiasschulz.io.*;

/**
 * Hilfsklasse des Java-Servlets JEliza, die mit Hilfe von 3 "Regeln" das
 * Erstellen einer Antwort ermoeglicht
 * 
 * @author Tobias Schulz
 * @version 0.3
 */

public class Answerer {

	String absoluteUrl = "";

	public Gehirn hirn;

	public String naechsteFra = "0";

	public String subjekt = "";

	public String verb = "";

	public String objekt = "";

	public String fragewort = "";

	public SatzParseManager spm = SatzParseManager.createNew();

	/**
	 * Konstruktor
	 * 
	 * @param tmp
	 *            Die Absolute URL zum JEliza-Stammordner
	 */
	public Answerer(String tmp) {
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
	public String getSimple(String fraParameter, String antParameter) {
		String ant = antParameter;
		String fra = fraParameter;

		String antExit = "Auf Wiedersehen.";
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
			addPerson(fra.substring(10).trim(), "heisse", "ich");
		}

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
	public String getQuestionWord(String fraParameter, String antParameter) {
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
			dasIst = hirn.getAntWWW(obj);
			if (dasIst != null) {
				return dasIst;
			}
		}

		if (fra.startsWith("was")) {
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
				dasIst = ant;
			}

			return dasIst;
		}

		if (fra.startsWith("wer")) {
			StringTokenizer stoken = new StringTokenizer(fra, " ");
			stoken.nextToken();
			String obj = "";
			while (stoken.hasMoreTokens()) {
				obj += stoken.nextToken() + " ";
			}
			obj = obj.trim();
			obj = obj.toLowerCase();
			String dasIst = "";
			dasIst = hirn.getAnt("wer " + obj);
			if (dasIst == null) {
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
				dasIst = ant;
			}

			return dasIst;
		}

		return ant;
	}

	/**
	 * "Regel" 3, eine Methode zum Beantworten der Frage des Users. Liefert die
	 * bisherige Antwort zurueck, wenn keine Eigene erstellbar. Antwortet auf
	 * alle Fragen ; Stellt die Frage zu einer Antwort um.
	 * 
	 * @param fraParameter
	 *            Die Frage
	 * @param antParameter
	 *            Die bisherige Antwort
	 * @return Die Antwort
	 */
	public String getFra2Ant(String fraParameter, String antParameter) {
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

		ant = Util.replace(ant, " wir ", " XiXhXrX ");
		ant = Util.replace(ant, " Wir ", " XIXhXrX ");
		ant = Util.replace(ant, " unser", " XuXnXsXeXrX");
		ant = Util.replace(ant, "Unser", "XUXnXsXeXrX");
		ant = Util.replace(ant, " unsere", " XeXuXrXeX ");
		ant = Util.replace(ant, " uns", " XeXuXcXhX ");

		ant = Util.replace(ant, " ihr ", " wir ");
		ant = Util.replace(ant, " Ihr ", " Wir ");
		ant = Util.replace(ant, " euer", " unser");
		ant = Util.replace(ant, " eure ", " unsere");
		ant = Util.replace(ant, " euch ", " uns");

		ant = Util.replace(ant, " XiXhXrX ", " ihr ");
		ant = Util.replace(ant, " XIXhXrX ", " Ihr ");
		ant = Util.replace(ant, " XuXnXsXeXrX", " euer");
		ant = Util.replace(ant, "XUXnXsXeXrX", "Euer");
		ant = Util.replace(ant, " XeXuXrXeX ", " eure");
		ant = Util.replace(ant, " XeXuXcXhX ", " Euch");

		ant = Util.replace(ant, " XDXuX ", " ich ");
		ant = Util.replace(ant, " XDXeXiXnX", " mein");
		ant = Util.replace(ant, " XDXiXrX ", " mir ");
		ant = Util.replace(ant, " XDXiXcXhX ", " mich ");

		if (ant.toLowerCase().contains("du")) {
			ant = Util.replace(ant, " heisse ", " heisst ");
		}
		if (ant.toLowerCase().contains("ihr")) {
			ant = Util.replace(ant, " heissen ", " heisst ");
		}

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
				"? Wirklich? Schrecklich ...", "? Sehen das alle so?",
				"? Oh gott!", "? Warum?",
				"? Weshalb?", "? Ist das Normal?", };
		String[] einzelAntwort = { "Das finde ich auch !", "Der Meinung bin ich auch.",
				"Das ist wohl so.", "Das sagt mein Programmierer auch.", 
				"Stimmt das wirklich?", "Wie kann das passieren.", "Schade.", "Traurig.",
				"Glaubst du, das weiss ich nicht?"};

		if (r.nextInt(10) < 7) {
			ant += antGrund[r.nextInt(antGrund.length)];
		} else  {
			ant = einzelAntwort[r.nextInt(einzelAntwort.length)];
		}

		return ant;
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

			// Vector<String> nomen = new Vector<String>();

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
					sum *= nums[x];
				}
			}

			if (rechenArt.contains("/")) {
				sum = nums[0];
				for (int x = 1; x < nums.length; x++) {
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

	/**
	 * Eine Weitere Methode, die den Satz in Subjekt, Praedikat und Objekt
	 * aufteilt und ihn so besser "versteht".
	 * 
	 * @param fraParameter
	 *            Die Frage
	 * @param antParameter
	 *            Die bisherige Antwort
	 * @return Die Antwort
	 */
	public String[] analyseSatz(String fraParameter, String antParameter) {
		@SuppressWarnings("unused")
		String ant = antParameter;
		String fra = fraParameter;

		if (fra.trim().length() == 0) {
			return null;
		}

		VerbDataBase vdb = null;
		System.out.println("---- Generating Verb Database ----");
		try {
			vdb = new VerbDataBase();
			vdb.loadFromFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("---- Parsing Sentence ----");

		spm = SatzParseManager.parse(fra, vdb);

		try {
			subjekt = spm.getSubject();
			if (subjekt == fra) {
				return null;
			}
		} catch (SubjektNotFoundException e) {
			return null;
		}

		try {
			verb = spm.getPraedikat();
			if (verb == fra) {
				return null;
			}
		} catch (VerbNotFoundException e) {
			return null;
		}

		try {
			objekt = spm.getObjekt();
			if (objekt == fra) {
				return null;
			}
		} catch (ObjektNotFoundException e) {
			return null;
		}

		fragewort = spm.getFrageWort();
		if (objekt == fra) {
			return null;
		}

		String[] tmp = { subjekt, verb, objekt, fragewort };

		return tmp;
	}

	/**
	 * Eine Weitere Methode, die den Satz in Subjekt, Praedikat und Objekt
	 * aufteilt und ihn so besser "versteht".
	 * 
	 * @param fraParameter
	 *            Die Frage
	 * @param antParameter
	 *            Die bisherige Antwort
	 * @return Die Antwort
	 */
	public String parseSatz(String fraParameter, String antParameter) {
		String ant = antParameter;
		String fra = fraParameter;

		if (fra.trim().length() == 0) {
			return "Du hast nichts eingegeben...";
		}

		VerbDataBase vdb = null;
		System.out.println("---- Generating Verb Database ----");
		try {
			vdb = new VerbDataBase();
			vdb.loadFromFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("---- Parsing Sentence ----");

		spm = SatzParseManager.parse(fra, vdb);

		try {
			subjekt = spm.getSubject();
			if (subjekt == fra) {
				return ant;
			}
		} catch (SubjektNotFoundException e) {
			return ant;
		}

		try {
			verb = spm.getPraedikat();
			if (verb == fra) {
				return ant;
			}
		} catch (VerbNotFoundException e) {
			return ant;
		}

		try {
			objekt = spm.getObjekt();
			if (objekt == fra) {
				return ant;
			}
		} catch (ObjektNotFoundException e) {
			objekt = "";
		}

		fragewort = spm.getFrageWort();
		if (objekt == fra) {
			return ant;
		}

		if (objekt == "") {
			objekt = "null";
		}

		String[] ja = { "Das finde ich auch !", "Der Meinung bin ich auch.",
				"Das ist wohl so.", "Warscheinlich.",
				"Das war ja auch schon immer so", "Klar",
				"Das sagt mein Programmierer auch.", "Ich weiss.",
				"Stimmt das wirklich?", "Wie kann das passieren.", "Schade.","Traurig.",
				"Glaubst du, das weiss ich nicht?", "Natuerlich." };

		String[] einzelAntwort = { "Das finde ich auch !", "Der Meinung bin ich auch.",
				"Das ist wohl so.", "Das sagt mein Programmierer auch.", "Ich weiss.",
				"Stimmt das wirklich?", "Wie kann das passieren.", "Schade.","Traurig.",
				"Super",
				"Glaubst du, das weiss ich nicht?"};

		if (spm.satzType == SatzParseManager.EINFACHE_FRAGE) {
			addPerson(subjekt, verb, objekt);
			String yesno = "";
			try {
				yesno = FileManager.readFileIntoString(
						absoluteUrl + "wortschatz/simple-ques/" + subjekt + "/"
								+ verb + "/" + objekt).trim();
			} catch (IOException e) {
			}

			if (yesno.hashCode() == "true".hashCode()) {
				return "Ja";
			}
			if (yesno.hashCode() == "false".hashCode()) {
				return "Nein!";
			}
			try {
				yesno = FileManager.readFileIntoString(
						absoluteUrl + "wortschatz/simple-ques/" + subjekt.toLowerCase() + "/"
								+ verb.toLowerCase() + "/" + objekt.toLowerCase()).trim();
			} catch (IOException e) {
			}

			if (yesno.hashCode() == "true".hashCode()) {
				return "Ja";
			}
			if (yesno.hashCode() == "false".hashCode()) {
				return "Nein!";
			}
			return ant;
		} else {
			System.out.println("Satzart: " + spm.satzType);
		}

		if (spm.satzType == SatzParseManager.ERWEITERTE_FRAGE) {
			addPerson(subjekt, verb, objekt);
			String yesno;
			try {
				yesno = FileManager.readFileIntoString(
						absoluteUrl + "wortschatz/ext-ques/" + fragewort + "/"
								+ subjekt + "/" + verb + "/" + objekt).trim();
			} catch (IOException e) {
				return ant;
			}

			if (yesno.hashCode() == "true".hashCode()) {
				return "Ja";
			}
			if (yesno.hashCode() == "false".hashCode()) {
				return "Nein!";
			}
		} else {
			System.out.println("Satzart: " + spm.satzType);
		}

		if (spm.satzType == SatzParseManager.AUSSAGESATZ) {
			addPerson(subjekt, verb, objekt);

			String subj = subjekt.replace("nicht", "").replace("  ", "");
			String obj = objekt.replace("nicht", "").replace("  ", "");

			File f = new File("wortschatz/simple-sent/" + subjekt.toLowerCase() + "/" + verb.toLowerCase()
					+ "/" + objekt.toLowerCase());
			f.getParentFile().mkdirs();
			try {
				FileManager.writeStringIntoFile("true", f.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}

			f = new File("wortschatz/simple-sent/" + subj.toLowerCase() + "/" + verb.toLowerCase() + "/"
					+ obj.toLowerCase());
			f.getParentFile().mkdirs();
			try {
				FileManager.writeStringIntoFile("true", f.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}

			f = new File("wortschatz/simple-ques/" + subjekt.toLowerCase() + "/" + verb.toLowerCase() + "/"
					+ objekt.toLowerCase());
			f.getParentFile().mkdirs();
			try {
				FileManager.writeStringIntoFile("true", f.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (subjekt.contains("nicht") || subjekt.contains("nicht")) {
				f = new File("wortschatz/simple-ques/" + subj.toLowerCase() + "/" + verb.toLowerCase()
						+ "/" + obj.toLowerCase());
				f.getParentFile().mkdirs();
				try {
					FileManager.writeStringIntoFile("true", f.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				BufferedReader br = FileManager.openBufferedReader(absoluteUrl
						+ "wortschatz/simple-sent/" + subjekt + "/" + verb
						+ "/" + objekt);

				String line = "";
				boolean isYes = false;
				String[] answers = new String[100];
				int y = 0;
				while ((line = br.readLine()) != null) {
					answers[y] = line.trim();
					if (line.trim().hashCode() == "true".hashCode()) {
						isYes = true;
					}
					y++;
				}

				Random r = new Random();

				if (isYes) {
					if (r.nextInt(10) < 7) {
						ant = ja[r.nextInt(ja.length - 1)];
					} else {
						ant = einzelAntwort[r.nextInt(einzelAntwort.length - 1)];
					}
					return ant;
				} else {
					ant = answers[r.nextInt(y)];
					if (ant == null) {
						ant = "Äh";
					}
				}
				return ant;

			} catch (IOException e) {
				return ant;

			}
		} else {
			System.out.println("Satzart: " + spm.satzType);
		}

		return ant;
	}

	public static void addPerson(String str, String verb, String obj) {
		File f = new File("personen/" + str + "/" + verb + "/" + obj);
		f.getParentFile().mkdirs();
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

} // class Regeln
