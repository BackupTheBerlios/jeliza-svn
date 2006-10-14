package org.homedns.tobiasschulz.util.satzparser;

/**
 * Klasse zum Auftrennen eines Satzes in Subjekt, Praedikat und Objekt.
 * 
 * @author Tobias Schulz
 * @version 0.1
 */
public class SatzParseManager {

	public String strSubjekt = "";

	public String strVerb = "";

	public String strObjekt = "";

	public String strFrageWort = "";

	public short satzType = 0;

	public static short AUSSAGESATZ = 0;

	public static short EINFACHE_FRAGE = 1;

	public static short ERWEITERTE_FRAGE = 2;

	String[] frageWoerter = { "Was", "Wer", "Wie", "Wo", "Wann", "Wieso",
			"Weshalb", "Warum" };

	String[] anrede = { "Herr", "Frau", "Fraeulein", "Mr", "Mrs", "Mr.",
			"Mrs.", "Herrn" };

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
		for (int x = 0; x < anrede.length; x++) {
			String tmp = anrede[x];
			str.replace(tmp.toLowerCase() + " ", tmp + " ");
			str.replace(tmp + " ", tmp.toLowerCase() + " ");
		}
		str = str.trim();
		if (vdb.isVerb(str.split(" ")[0]) && !str.endsWith("?")) {
			str += "?";
		}
		boolean isQues = false;
		for (int x = 0; x < frageWoerter.length; x++) {
			if (str.toLowerCase().startsWith(frageWoerter[x].toLowerCase())) {
				isQues = true;
			}
		}
		if (str.endsWith("?") || isQues) {
			boolean isSimple = true;
			for (int x = 0; x < frageWoerter.length; x++) {
				if (str.toLowerCase().startsWith(frageWoerter[x].toLowerCase())) {
					isSimple = false;
				}
			}
			if (isSimple && !isQues) {
				parseSimpleFrage(str.substring(0, str.length() - 1), vdb);
			} else if (!isSimple && !isQues) {
				parseExtendedFrage(str.substring(0, str.length() - 1), vdb);
			}
			if (isSimple && isQues) {
				parseSimpleFrage(str, vdb);
			} else if (!isSimple && isQues) {
				parseExtendedFrage(str, vdb);
			}
		} else {
			parseHauptsatz(str, vdb);
		}
		
		strSubjekt = strSubjekt.replace("?", "").replace("!", "");
		strVerb = strVerb.replace("?", "").replace("!", "");
		strObjekt = strObjekt.replace("?", "").replace("!", "");
		strFrageWort = strFrageWort.replace("?", "").replace("!", "");
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
		if (str.endsWith(".")) {
			str = str.substring(0, str.length() - 1);
		}
		if (str.toLowerCase().startsWith("ja, ")) {
			str = str.substring(4);
		}
		if (str.toLowerCase().startsWith("nein, ")) {
			str = str.substring(6);
		}

		short mode = 0;

		strSubjekt = "";
		strObjekt = "";
		strVerb = "";

		String[] sc = str.replace("\n", "").split(" ");

		for (int x = 0; x < sc.length; x++) {
			String pat = sc[x];

			if (vdb.isVerb(pat.toLowerCase())) {
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
		int mode = 0;
		int maxNomen = 1;
		int maxWoerterForSubj = 5;

		for (int x = 0; x < anrede.length; x++) {
			String tmp = anrede[x];
			if (str.contains(tmp + " ")) {
				maxWoerterForSubj++;
				maxNomen++;
			}
			str.replace(tmp + " ", tmp.toLowerCase() + " ");
		}

		strSubjekt = "";
		strObjekt = "";
		strVerb = "";

		String[] sc = str.replace("\n", "").split(" ");

		String pat2 = sc[0];

		strVerb = pat2 + " ";

		for (int x = 1; x < sc.length; x++) {
			String pat = sc[x];
			mode++;

			String origPat = pat;

			if (vdb.isVerb(pat.toLowerCase())) {
				strVerb += pat + " ";
				mode = 1;
				continue;
			}

			if (mode <= maxWoerterForSubj && maxNomen > 0) {
				if (x != sc.length) {
					if ((origPat.hashCode() != pat.toLowerCase().hashCode())) {
						maxNomen--;
					}
					strSubjekt += pat + " ";
					continue;
				}
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

		short mode = 1;

		strSubjekt = "";
		strObjekt = "";
		strVerb = "";

		String[] sc = str.replace("\n", "").split(" ");

		String pat2 = sc[0];

		strFrageWort = pat2.trim();
		if (strFrageWort == "warum" || strFrageWort == "weshalb") {
			strFrageWort = "wieso";
		}
		if (sc.length == 1) {
			return;
		}
		String pat3 = sc[1];

		strVerb = pat3 + " ";

		for (int x = 2; x < sc.length; x++) {
			String pat = sc[x];
			mode++;

			String origPat = pat;

			if (vdb.isVerb(pat.toLowerCase())) {
				strVerb += pat + " ";
				mode = 1;
				continue;
			}

			if (((short) (mode) == (short) (2) || (origPat.hashCode() != pat
					.toLowerCase().hashCode()))
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
