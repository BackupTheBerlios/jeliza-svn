package org.homedns.tobiasschulz.apps.jeliza.hirn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.homedns.tobiasschulz.apps.jeliza.Util;
import org.homedns.tobiasschulz.util.satzparser.VerbDataBase;

/**
 * Hilfsklasse des Java-Servlets JEliza, die einen Wrapper für die Klassen
 * Regeln und Gehirn darstellen
 * 
 * @author Tobias Schulz
 * @version 0.3
 */
public class AnswerWrapper {

	public Answerer re;

	public String[] satzTeile = new String[4];

	/**
	 * Methode zum Beantworten der Frage / Des Satzes des Users.
	 * 
	 * @param fra
	 *            Die Frage
	 * @return Die Antwort
	 */
	public Satz processQuestion(Satz fr, Answerer reParam, Gehirn hirn) {
		String fra = fr.satzPlain;
		re = reParam;
		boolean fertig = false;
		String ant = "Uff! Da bin ich überfragt!\nKontaktieren sie doch bitte meinen Programmierer "
				+ "darüber,\n"
				+ "indem sie eine Mail an tobischulz@arcor.de mit dieser Frage schicken!";

		VerbDataBase vdb = null;
		System.out.println("---- Generating Verb Database ----");
		try {
			vdb = new VerbDataBase();
			vdb.loadFromFile("verbs.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		fra = Util.replace(fra, "ä", "ae");
		fra = Util.replace(fra, "ö", "oe");
		fra = Util.replace(fra, "ü", "ue");
		fra = Util.replace(fra, "ß", "ss");

		fra = Util.wegMitAbkuerzungen(fra);

		System.out.println(hirn.erSieEsGedaechtnis[0]
				+ hirn.erSieEsGedaechtnis[1] + hirn.erSieEsGedaechtnis[2]);
		String[] fs = fra.split(" ");
		String q = "";
		for (String st : fs) {
			if (st.toLowerCase().hashCode() == "er".hashCode()) {
				q += ((Genus.getGenus(hirn.erSieEsGedaechtnis[0]) != Genus.UNKNOWN) ? Genus
						.getDerDieDas(Genus
								.getGenus(hirn.erSieEsGedaechtnis[0]))
						: "")
						+ " " + hirn.erSieEsGedaechtnis[0];
			} else if (st.toLowerCase().hashCode() == "sie".hashCode()) {
				q += ((Genus.getGenus(hirn.erSieEsGedaechtnis[1]) != Genus.UNKNOWN) ? Genus
						.getDerDieDas(Genus
								.getGenus(hirn.erSieEsGedaechtnis[1]))
						: "")
						+ " " + hirn.erSieEsGedaechtnis[1];
			} else if (st.toLowerCase().hashCode() == "es".hashCode()) {
				q += ((Genus.getGenus(hirn.erSieEsGedaechtnis[2]) != Genus.UNKNOWN) ? Genus
						.getDerDieDas(Genus
								.getGenus(hirn.erSieEsGedaechtnis[2]))
						: "")
						+ " " + hirn.erSieEsGedaechtnis[2];
			} else {
				q += st;
			}
			q += " ";
		}
		fra = q.trim();

		String ofra = fra.trim();

		fra = Util.replace(fra, "?", "");
		fra = Util.replace(fra, ".", "");
		fra = Util.replace(fra, "!", "");

		fra = fra.trim();

		String s = "";

		s = re.getSimple(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = re.parseSatz(ofra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = re.getInformation(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = re.getQuestionWord(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = hirn.getAntPublicGehirn(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = hirn.getAntBaseGehirn(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = re.getFra2Ant(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		satzTeile[0] = re.subjekt;

		fertig = true;
		if (fertig) {

			ant = Util.replace(ant, "  ", " ");

			String antPlain = ant;

			int i = 0;
			while ((i = antPlain.indexOf('<')) > -1) {
				System.out.println(antPlain);
				antPlain = antPlain.substring(0, i)
						+ antPlain.substring(antPlain.indexOf('>', i));
			}
			antPlain = antPlain.replace("<", "");
			antPlain = antPlain.replace(">", "          ");
			System.out.println(antPlain);

			String[] woerter = fra.split(" ");
			int feel = -1;
			for (String tmp : woerter) {
				System.out.println(tmp + hirn.gefuehlHeute.wortToFeeling);
				if (hirn.gefuehlHeute.wortToFeeling.containsKey(tmp)) {
					System.out.println(tmp
							+ hirn.gefuehlHeute.wortToFeeling.get(tmp));
					feel = hirn.gefuehlHeute.wortToFeeling.get(tmp);
				}
			}
			for (String tmp : hirn.gefuehlHeute.wortToFeeling.keySet()) {
				if (fra.toLowerCase().contains(tmp.toLowerCase())) {
					feel = hirn.gefuehlHeute.wortToFeeling.get(tmp);
				}
			}

			woerter = fra.split(" ");
			String vorher = "";
			String warschon = "";
			for (String st : woerter) {
				if ((st.charAt(0) + "").toLowerCase() == (st.charAt(0) + "")
						|| (st.toLowerCase() == "der"
								|| st.toLowerCase() == "die" || st
								.toLowerCase() == "das")) {
					vorher = s;
					continue;
				}
				String[] tmp = { st, vorher + " " + st };
				for (String str : tmp) {
					str = str.trim();
					String strLow = str.toLowerCase();
					if (vdb.isVerb(str)) {
						continue;
					}
					if (Genus.getGenus(strLow) != Genus.UNKNOWN
							&& !warschon.toLowerCase().contains(
									Genus.getErSieEs(Genus.getGenus(strLow))
											.toLowerCase())) {
						if (Genus.getErSieEs(Genus.getGenus(strLow))
								.toLowerCase().hashCode() == "er".hashCode()) {
							hirn.erSieEsGedaechtnis[0] = str;
							warschon += Genus
									.getErSieEs(Genus.getGenus(strLow))
									.toLowerCase();
							System.out.println(Genus.getErSieEs(Genus
									.getGenus(str))
									+ ": " + str);
						}
						if (Genus.getErSieEs(Genus.getGenus(strLow))
								.toLowerCase().hashCode() == "sie".hashCode()) {
							hirn.erSieEsGedaechtnis[1] = str;
							warschon += Genus
									.getErSieEs(Genus.getGenus(strLow))
									.toLowerCase();
							System.out.println(Genus.getErSieEs(Genus
									.getGenus(str))
									+ ": " + str);
						}
						if (Genus.getErSieEs(Genus.getGenus(strLow))
								.toLowerCase().hashCode() == "es".hashCode()) {
							hirn.erSieEsGedaechtnis[2] = str;
							warschon += Genus
									.getErSieEs(Genus.getGenus(strLow))
									.toLowerCase();
							System.out.println(Genus.getErSieEs(Genus
									.getGenus(str))
									+ ": " + str);
						}
					}
				}
				vorher = s;
			}

			return new Satz(antPlain, ant, feel, hirn, ofra);
		}
		return null;
	}
}
