package org.homedns.tobiasschulz.util.satzparser;

import java.util.Scanner;

/**
 * Klasse zum Auftrennen eines Satzes in Subjekt, Praedikat und Objekt.
 * 
 * @author Tobias Schulz
 * @version 0.1
 */
public class SatzParseManager {

	public String strSubjekt = null;

	public String strVerb = null;

	public String strObjekt = null;

	public String strFrageWort = null;

	public short satzType = 0;

	public static short AUSSAGESATZ = 0;

	public static short EINFACHE_FRAGE = 1;

	public static short ERWEITERTE_FRAGE = 2;

	String[] frageWoerter = { "Was", "Wer", "Wie", "Wo", "Wann", "Wieso",
			"Weshalb", "Warum" };

	/**
	 * Konstruktor ist private, Klasse kann nur mit Methode parse() erzeugt
	 * werden.
	 * 
	 * @see parse
	 * @param str
	 *            Der Satz
	 */
	private SatzParseManager() {
	}

	/**
	 * Konstruktor ist private, Klasse kann nur mit Methode parse() erzeugt
	 * werden.
	 * 
	 * @see parse
	 * @param str
	 *            Der Satz
	 */
	private SatzParseManager(String str, VerbDataBase vdb) {
		str = str.trim();
		if (str.endsWith("?")) {
			boolean isSimple = true;
			for (int x = 0; x < frageWoerter.length; x++) {
				if (str.toLowerCase().startsWith(frageWoerter[x].toLowerCase())) {
					isSimple = false;
				}
			}
			if (isSimple) {
				parseSimpleFrage(str.substring(0, str.length() - 1), vdb);
			} else {
				parseExtendedFrage(str.substring(0, str.length() - 1), vdb);
			}
		} else {
			parseHauptsatz(str, vdb);
		}
	}

	/**
	 * Statische Methode zum Erzeugen dieser Klasse.
	 * 
	 * @param str
	 *            Der Satz
	 * @return Die Instanz
	 */
	public static SatzParseManager parse(String str, VerbDataBase vdb) {
		return new SatzParseManager(str, vdb);
	}

	/**
	 * Statische Methode Nr. 2 zum Erzeugen dieser Klasse.
	 * 
	 * @param str
	 *            Der Satz
	 * @return Die Instanz
	 */
	public static SatzParseManager createNew() {
		return new SatzParseManager();
	}

	/**
	 * Parst normale Sätze wie "ich bin doof".
	 * 
	 * @param str
	 *            Der Satz
	 * @return Die Instanz
	 */
	private void parseHauptsatz(String str, VerbDataBase vdb) {
		Scanner scanner = new Scanner(str);

		@SuppressWarnings("unused")
		short mode = 0;

		strSubjekt = "";
		strObjekt = "";
		strVerb = "";

		while (scanner.hasNext()) {
			String pat = scanner.next();
			pat = pat.toLowerCase();

			if (vdb.isVerb(pat)) {
				strVerb += pat + " ";

				mode = 1;

				continue;
			}
			if (mode == 0) {
				strSubjekt += pat + " ";
				continue;
			}

			if (mode == 1) {
				strObjekt += pat + " ";
				continue;
			}

		}

		strSubjekt = strSubjekt.trim();
		strObjekt = strObjekt.trim();
		strVerb = strVerb.trim();

		satzType = AUSSAGESATZ;
	}

	/**
	 * Parst einfache Fragen wie "Bin ich doof?".
	 * 
	 * @param str
	 *            Der Satz
	 * @return Die Instanz
	 */
	private void parseSimpleFrage(String str, VerbDataBase vdb) {
		Scanner scanner = new Scanner(str);

		@SuppressWarnings("unused")
		short mode = 1;

		strSubjekt = "";
		strObjekt = "";
		strVerb = "";

		String pat2 = scanner.next();
		pat2 = pat2.toLowerCase();

		strVerb = pat2 + " ";

		while (scanner.hasNext()) {
			mode++;

			String pat = scanner.next();
			String origPat = pat;
			pat = pat.toLowerCase();

			if (vdb.isVerb(pat)) {
				strVerb += pat + " ";
				mode = 1;
				continue;
			}

			if (((short) (mode) == (short) (2) || (origPat.hashCode() != pat
					.hashCode()))
					&& mode < 4) {
				// System.out.println(origPat + origPat.hashCode());
				// System.out.println(pat + pat.hashCode());

				strSubjekt += pat + " ";
				continue;
			}

			strObjekt += pat + " ";
		}

		strSubjekt = strSubjekt.trim();
		strObjekt = strObjekt.trim();
		strVerb = strVerb.trim();

		satzType = EINFACHE_FRAGE;
	}

	/**
	 * Parst Fragen wie "Warum bin ich?".
	 * 
	 * @param str
	 *            Der Satz
	 * @return Die Instanz
	 */
	private void parseExtendedFrage(String str, VerbDataBase vdb) {
		satzType = ERWEITERTE_FRAGE;

		Scanner scanner = new Scanner(str);

		@SuppressWarnings("unused")
		short mode = 1;

		strSubjekt = "";
		strObjekt = "";
		strVerb = "";

		String pat2 = scanner.next();

		strFrageWort = pat2.trim().toLowerCase();
		if (strFrageWort == "warum" || strFrageWort == "weshalb") {
			strFrageWort = "wieso";
		}
		if (!scanner.hasNext()) {
			return;
		}
		String pat3 = scanner.next();
		pat3 = pat3.toLowerCase();

		strVerb = pat3 + " ";

		while (scanner.hasNext()) {
			mode++;

			String pat = scanner.next();
			String origPat = pat;
			pat = pat.toLowerCase();

			if (vdb.isVerb(pat)) {
				strVerb += pat + " ";
				mode = 1;
				continue;
			}

			if (((short) (mode) == (short) (2) || (origPat.hashCode() != pat
					.hashCode()))
					&& mode < 4) {
				strSubjekt += pat + " ";
				continue;
			}

			strObjekt += pat + " ";
		}

		strSubjekt = strSubjekt.trim();
		strObjekt = strObjekt.trim();
		strVerb = strVerb.trim();
	}

	/**
	 * Liefert das Subjekt zurück.
	 * 
	 * @return Das Subjekt
	 * @throws SubjektNotFoundException
	 */
	public String getSubject() throws SubjektNotFoundException {
		String satzTeil = strSubjekt;

		if (satzTeil == null || satzTeil == "") {
			throw new SubjektNotFoundException();
		}

		return satzTeil;
	}

	/**
	 * Liefert das Objekt zurück.
	 * 
	 * @return Das Objekt
	 * @throws SubjektNotFoundException
	 */
	public String getObjekt() throws ObjektNotFoundException {
		String satzTeil = strObjekt;

		if (satzTeil == null || satzTeil == "") {
			throw new ObjektNotFoundException();
		}

		return satzTeil;
	}

	/**
	 * Liefert das Objekt zurück.
	 * 
	 * @return Das Objekt
	 * @throws SubjektNotFoundException
	 */
	public String getPraedikat() throws VerbNotFoundException {
		String satzTeil = strVerb;

		if (satzTeil == null || satzTeil == "") {
			throw new VerbNotFoundException();
		}

		return satzTeil;
	}

	/**
	 * Liefert das FrageWort (wer, was etc.) zurück.
	 * 
	 * @return Das Objekt
	 * @throws SubjektNotFoundException
	 */
	public String getFrageWort() {
		String satzTeil = strFrageWort;

		if (satzTeil == null) {
			satzTeil = "";
		}

		return satzTeil;
	}

}
