package org.homedns.tobiasschulz.apps.jeliza.hirn;

import org.homedns.tobiasschulz.apps.jeliza.Util;

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

			return new Satz(antPlain, ant, feel);
		}
		return null;
	}

}
