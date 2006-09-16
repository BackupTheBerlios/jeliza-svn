package org.homedns.tobiasschulz.util.satzparser;

import java.io.*;
import java.util.*;

import org.homedns.tobiasschulz.io.FileManager;

/**
 * Klasse zum bereitstellen von Verben in angegebenen Dateien.
 * 
 * @author Tobias Schulz
 * @version 0.1
 */
public class VerbDataBase {

	public Hashtable<String, String> data = new Hashtable<String, String>();

	public Hashtable<String, String> baseVerbs = new Hashtable<String, String>();

	/**
	 * Standard-Konstruktor, generiert Datenbank.
	 * 
	 * @param str
	 *            Die Text-Datei mit den Saetzen
	 * @throws IOException
	 */
	public VerbDataBase(String str) throws IOException {

		baseVerbs.put("bin", "bin");
		baseVerbs.put("bist", "bist");
		baseVerbs.put("ist", "ist");
		baseVerbs.put("sind", "sind");
		baseVerbs.put("seid", "seid");
		baseVerbs.put("war", "war");
		baseVerbs.put("warst", "warst");
		baseVerbs.put("waren", "waren");
		baseVerbs.put("wart", "wart");
		
		Scanner sc = new Scanner(new FileInputStream("knownVerbs.txt"));

		while (sc.hasNext()) {
			String t = sc.next();
			baseVerbs.put(t, t);
		}

		
		

		InputStream in = new FileInputStream(str);
		Scanner scanner = new Scanner(in);

		System.out.println("--- Now scanning all words");

		long words = 0;
		long allWords = 0;
		String pat;

		while (scanner.hasNext()) {
			scanner.next();
			allWords++;
			if ((allWords % 250000) == 0) {
				System.out.println(allWords + " words found in text");
				System.gc();
			}
		}
		System.out.println("--- There are " + allWords + " words.");
		System.out.println("--- Now scanning for verbs in this words");

		in = new FileInputStream(str);
		scanner = new Scanner(in);

		while (scanner.hasNext()) {
			pat = scanner.next().toLowerCase().replace("\"", "").replace(
					"'", "").replace("(", "").replace(")", "")
					.replace(",", "");

			words++;
			if ((words % 250000) == 0) {
				double d = (1000l / allWords * words);
				System.out.println(Math.round(d) + "% done");
				System.out.println("100 / " + allWords + " * " + words + "% done");
				System.gc();
				try {
					Thread.sleep(750);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (pat.contains("[") || pat.contains("]") || pat.length() < 3) {
				continue;
			}

			if (pat.startsWith("ge") && pat.length() > 5) {
				data.put(pat, pat);
				continue;
			}

			if (pat.startsWith("aus") && pat.length() > 3) {
				data.put(pat, pat);
				continue;
			}

			if (pat.startsWith("auf") && pat.length() > 3) {
				data.put(pat, pat);
				continue;
			}

			if (pat.startsWith("ver") && pat.length() > 3) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("den") && pat.length() > 3) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("lle")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("ffe")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("hen")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("ben") && pat.length() > 3) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("sen")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("ll")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("lle")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("ke")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("mme")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("be")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("ste")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("gt")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("gte")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("st")) {
				data.put(pat, pat);
				continue;
			}

			if (pat.endsWith("rd")) {
				data.put(pat, pat);
				continue;
			}
		}

		System.out.println("--- " + (data.size() + baseVerbs.size()) + " Verbs found.");
	}

	/**
	 * Standard-Konstruktor. Generiert nix.
	 */
	public VerbDataBase() {
	}

	/**
	 * Läd Datenbank von Datei.
	 * 
	 * @param str
	 *            Die Text-Datei mit den Verben
	 * @throws IOException
	 * @throws IOException
	 */
	public void loadFromFile(String str) throws IOException {
		baseVerbs.put("bin", "bin");
		baseVerbs.put("bist", "bist");
		baseVerbs.put("ist", "ist");
		baseVerbs.put("sind", "sind");
		baseVerbs.put("seid", "seid");
		baseVerbs.put("war", "war");
		baseVerbs.put("warst", "warst");
		baseVerbs.put("waren", "waren");
		baseVerbs.put("wart", "wart");

		Scanner sc = new Scanner(new FileInputStream("knownVerbs.txt"));

		while (sc.hasNext()) {
			String t = sc.next();
			baseVerbs.put(t, t);
		}

		String dataBase;
		dataBase = FileManager.readFileIntoString(str);

		Scanner scanner = new Scanner(dataBase);

		while (scanner.hasNext()) {
			String pat = scanner.next();
			pat = pat.toLowerCase().trim();

			data.put(pat, pat);
		}

	}

	public boolean isVerb(String str) {
		str = str.trim().toLowerCase();
		

		/*
		 * for(int x = 0; x < data.size(); x++) { }
		 * 
		 * for(int x = 0; x < baseVerbs.size(); x++) {
		 * if(baseVerbs.get(x).trim().toLowerCase().equals(str)) { return true; } }
		 */
		
		System.out.println(str);

		if ((data.get(str) == null ? "" : data.get(str)).trim().toLowerCase().hashCode()
				== str.hashCode()) {
			return true;
		}

		if ((baseVerbs.get(str) == null ? "" : baseVerbs.get(str)).trim().toLowerCase().hashCode()
				== str.hashCode()) {
			return true;
		}

		if (data.containsKey(str)) {
			return true;
		}

		if (baseVerbs.containsKey(str)) {
			return true;
		}

		return false;
	}

	public void writeIntoFile(String file) {
		StringBuffer tmp = new StringBuffer();

		Enumeration keys = data.keys();
		while (keys.hasMoreElements()) {
			tmp.append(keys.nextElement()).append(" ");
		}

		keys = baseVerbs.keys();
		while (keys.hasMoreElements()) {
			tmp.append(keys.nextElement()).append(" ");
		}

		try {
			FileManager.writeStringIntoFile(tmp.toString(), file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
