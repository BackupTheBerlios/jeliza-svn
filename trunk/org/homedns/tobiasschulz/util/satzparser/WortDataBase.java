package org.homedns.tobiasschulz.util.satzparser;

import java.io.*;
import java.net.*;
import java.util.*;

import org.homedns.tobiasschulz.apps.jeliza.hirn.Genus;
import org.homedns.tobiasschulz.io.FileManager;

/**
 * Klasse zum bereitstellen von Verben in angegebenen Dateien.
 * 
 * @author Tobias Schulz
 * @version 0.1
 */
public class WortDataBase {

	public Hashtable<String, String> verbs = new Hashtable<String, String>();

	public Hashtable<String, String> nomen = new Hashtable<String, String>();

	public Hashtable<String, String> adj = new Hashtable<String, String>();

	public Hashtable[] genus = new Hashtable[3];

	public static short MAENNLICH = 1;

	public static short WEIBLICH = 2;

	public static short SAECHLICH = 3;

	public static short UNKNOWN = 0;

	/**
	 * Standard-Konstruktor, generiert Datenbank.
	 * 
	 * @param str
	 *            Die Text-Datei mit den Saetzen
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public WortDataBase(String str) throws IOException {

		verbs.put("bin", "bin");
		verbs.put("bist", "bist");
		verbs.put("ist", "ist");
		verbs.put("sind", "sind");
		verbs.put("seid", "seid");
		verbs.put("war", "war");
		verbs.put("warst", "warst");
		verbs.put("waren", "waren");
		verbs.put("wart", "wart");

		// Verben
		Scanner sc = new Scanner(new FileInputStream("knownVerbs.txt"));

		while (sc.hasNext()) {
			String t = sc.next();
			verbs.put(t, t);
		}

		// System.out.println("--- " + (verbs.size()) + " Verbs found.");

		// Adjektive
		sc = new Scanner(FileManager.readFileIntoString("knownAdj.txt"));

		while (sc.hasNext()) {
			String pat = sc.next();
			pat = pat.trim();

			adj.put(pat, pat);
		}

		genus[0] = new Hashtable<String, String>();
		genus[1] = new Hashtable<String, String>();
		genus[2] = new Hashtable<String, String>();

		// Genus
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("genus.txt")));
		String tmp = "";
		final Stack<String> s = new Stack<String>();
		while ((tmp = br.readLine()) != null) {
			s.push(tmp.trim());
		}
		new Thread(new Runnable() {
			public void run() {
				String tmpTh = "";
				while (!s.isEmpty()) {
					tmpTh = s.pop();
					String wert = tmpTh.substring(2);
					if (tmpTh.startsWith("1-")) {
						genus[0].put(wert, wert);
					}
					if (tmpTh.startsWith("2-")) {
						genus[1].put(wert, wert);
					}
					if (tmpTh.startsWith("3-")) {
						genus[2].put(wert, wert);
					}
				}
			}
		}).start();
	}

	/**
	 * Standard-Konstruktor. Generiert nix.
	 */
	public WortDataBase() {
	}

	/**
	 * Läd Datenbank von Datei.
	 * 
	 * @param str
	 *            Die Text-Datei mit den Verben
	 * @throws IOException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void loadFromFile() throws IOException {
		verbs.put("bin", "bin");
		verbs.put("bist", "bist");
		verbs.put("ist", "ist");
		verbs.put("sind", "sind");
		verbs.put("seid", "seid");
		verbs.put("war", "war");
		verbs.put("warst", "warst");
		verbs.put("waren", "waren");
		verbs.put("wart", "wart");

		genus[0] = new Hashtable<String, String>();
		genus[1] = new Hashtable<String, String>();
		genus[2] = new Hashtable<String, String>();

		// Verben
		String dataBase;
		dataBase = FileManager.readFileIntoString("verbs.txt");

		Scanner scanner = new Scanner(dataBase);

		while (scanner.hasNext()) {
			String pat = scanner.next();
			pat = pat.toLowerCase().trim();

			verbs.put(pat, pat);
		}

		// Nomen
		dataBase = FileManager.readFileIntoString("nomen.txt");

		scanner = new Scanner(dataBase);

		while (scanner.hasNext()) {
			String pat = scanner.next();
			pat = pat.trim();

			String[] k = pat.split(" ; ");
			for (String j : k) {
				if (j.charAt(0) != Character.toLowerCase(j.charAt(0))) {
					nomen.put(j, j);
				}
			}
		}

		// Adjektive
		dataBase = FileManager.readFileIntoString("adj.txt");

		scanner = new Scanner(dataBase);

		while (scanner.hasNext()) {
			String pat = scanner.next();
			pat = pat.trim();

			adj.put(pat, pat);
		}

		// Genus
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("genus.txt")));
		String tmp = "";
		while ((tmp = br.readLine()) != null) {
			tmp = tmp.trim();
			String wert = tmp.substring(2);
			if (tmp.startsWith("1-")) {
				genus[0].put(wert, wert);
			}
			if (tmp.startsWith("2-")) {
				genus[1].put(wert, wert);
			}
			if (tmp.startsWith("3-")) {
				genus[2].put(wert, wert);
			}
		}
	}

	public boolean isVerb(String str) {
		return isVerb(str, false);
	}

	public boolean isVerb(String str, boolean alreadyKonjugiert) {
		str = str.trim().toLowerCase();

		if ((verbs.get(str) == null ? "" : verbs.get(str)).trim().toLowerCase()
				.hashCode() == str.hashCode()) {
			return true;
		}

		if (verbs.containsKey(str)) {
			return true;
		}

		if (!alreadyKonjugiert
				&& (str.endsWith("en") || str.endsWith("e")
						|| str.endsWith("st") || str.endsWith("en")
						|| str.endsWith("t") || str.endsWith("te")
						|| str.endsWith("ten") || str.endsWith("test") || str
						.startsWith("ge"))) {
			for (int x = 1; x < 3; x++) {
				if (isVerb(str.substring(0, str.length() - x) + "e", true)) {
					return true;
				}
				if (isVerb(str.substring(0, str.length() - x) + "st", true)) {
					return true;
				}
				if (isVerb(str.substring(0, str.length() - x) + "en", true)) {
					return true;
				}
				if (isVerb(str.substring(0, str.length() - x) + "t", true)) {
					return true;
				}
				if (isVerb(str.substring(0, str.length() - x) + "te", true)) {
					return true;
				}
				if (isVerb(str.substring(0, str.length() - x) + "ten", true)) {
					return true;
				}
				if (isVerb(str.substring(0, str.length() - x) + "test", true)) {
					return true;
				}
				if (isVerb("ge" + str.substring(0, str.length() - 2) + "t",
						true)) {
					return true;
				}
			}
		}
		alreadyKonjugiert = false;

		return false;
	}

	public boolean isNomen(String str) {
		str = str.trim();

		if ((nomen.get(str) == null ? "" : nomen.get(str)).trim().toLowerCase()
				.hashCode() == str.hashCode()) {
			return true;
		}

		if (nomen.containsKey(str)) {
			return true;
		}

		if (nomen.containsKey(str.toLowerCase())) {
			return true;
		}

		return false;
	}

	public boolean isAdj(String str) {
		str = str.trim();

		if ((adj.get(str) == null ? "" : adj.get(str)).trim().toLowerCase()
				.hashCode() == str.hashCode()) {
			return true;
		}

		if (adj.containsKey(str)) {
			return true;
		}

		if (adj.containsKey(str.toLowerCase())) {
			return true;
		}

		if (str.endsWith("bar") || str.endsWith("haft") || str.endsWith("ig")
				|| str.endsWith("isch") || str.endsWith("lich")
				|| str.endsWith("sam")) {
			return true;
		}

		return checkAdjInWeb(str.toLowerCase());
	}

	public short getGenusFromDatabase(String str) {
		str = str.trim();
		short s = Genus.UNKNOWN;
		for (Hashtable<String, String> gen : genus) {
			s++;

			if ((gen.get(str) == null ? "" : gen.get(str)).trim().toLowerCase()
					.hashCode() == str.hashCode()) {
				return s;
			}

			if (gen.containsKey(str)) {
				return s;
			}

			if (gen.containsKey(str.toLowerCase())) {
				return s;
			}
		}

		return Genus.UNKNOWN;
	}

	public void writeIntoFile() {
		// Verben
		{
			StringBuffer tmp = new StringBuffer();

			Enumeration<String> keys = verbs.keys();
			while (keys.hasMoreElements()) {
				tmp.append(keys.nextElement()).append(" ");
			}

			try {
				FileManager.writeStringIntoFile(tmp.toString(), "verbs.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Verben
		{
			StringBuffer tmp = new StringBuffer();

			Enumeration<String> keys = adj.keys();
			while (keys.hasMoreElements()) {
				tmp.append(keys.nextElement()).append(" ");
			}

			try {
				FileManager.writeStringIntoFile(tmp.toString(), "adj.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean checkAdjInWeb(String str) {
		InputStream file = null;
		URL url = null;
		try {
			url = new URL("http://de.wiktionary.org/wiki/" + str);
			file = url.openStream();
		} catch (MalformedURLException mue) {
			System.out.println("Fehlerhafte URL-Adresse / Datei");
			return false;
		} catch (IOException e) {
			System.out
					.println("IOException beim Initialisieren der Verbindung zum Server");
			return false;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(file));
		try {
			int tasksDone = 0;
			while (true) {
				String line = br.readLine();

				if (line == null) {
					break;
				}

				if (line.contains("<!-- start content -->")) {
					tasksDone++;
					break;
				}
			}
			if (tasksDone < 1) {
				return false;
			}
			while (true) {
				String line = br.readLine();

				if (line == null) {
					break;
				}

				if (line.contains("<h2>" + str + " (<a href=\"/wiki/"
						+ "Wiktionary:Deutsch\" title=\"Wiktionary:"
						+ "Deutsch\">Deutsch</a>)</h2>")) {
					tasksDone++;
					break;
				}
			}
			if (tasksDone < 2) {
				return false;
			}
			while (true) {
				String line = br.readLine();

				if (line == null) {
					break;
				}

				if (line
						.contains("<h3><a href=\"/wiki/Hilfe:Wortart\" title=\"Hilfe:Wortart\">Adjektiv</a></h3>")) {
					tasksDone++;
					break;
				}
			}
			if (tasksDone < 3) {
				return false;
			}
			System.out.println("This is an adj.: " + str);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static short getGenusFromWeb(String str) {
		InputStream file = null;
		StringBuffer sb = new StringBuffer(str);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		URL url = null;
		try {
			url = new java.net.URL("http://de.wiktionary.org/wiki/"
					+ sb.toString());
			file = url.openStream();
		} catch (MalformedURLException mue) {
			System.out.println("Fehlerhafte URL-Adresse / Datei");
			return UNKNOWN;
		} catch (IOException e) {
			System.out
					.println("IOException beim Initialisieren der Verbindung zum Server");
			return UNKNOWN;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(file));
		try {
			int tasksDone = 0;
			while (true) {
				String line = br.readLine();

				if (line == null) {
					break;
				}

				if (line.contains("<!-- start content -->")) {
					tasksDone++;
					break;
				}
			}
			if (tasksDone < 1) {
				return UNKNOWN;
			}
			while (true) {
				String line = br.readLine();

				if (line == null) {
					break;
				}

				if (line.contains("/wiki/Wiktionary:Deutsch")) {
					tasksDone++;
					break;
				}
			}
			if (tasksDone < 2) {
				return UNKNOWN;
			}
			while (true) {
				String line = br.readLine();

				if (line == null) {
					break;
				}

				if (line
						.contains("<h3><a href=\"/wiki/Hilfe:Wortart\" title=\"Hilfe:Wortart\">Substantiv</a>")) {
					if (line.trim().contains("n</em></h3>")) {
						return SAECHLICH;
					}
					if (line.trim().contains("m</em></h3>")) {
						return MAENNLICH;
					}
					if (line.trim().contains("f</em></h3>")) {
						return WEIBLICH;
					}
					break;
				}
			}
			if (tasksDone < 3) {
				return UNKNOWN;
			}
			return UNKNOWN;
		} catch (IOException e) {
			return UNKNOWN;
		}
	}

}
