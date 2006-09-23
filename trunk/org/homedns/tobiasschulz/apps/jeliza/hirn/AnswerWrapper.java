package org.homedns.tobiasschulz.apps.jeliza.hirn;

import java.util.ArrayList;
import java.util.Random;

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

		fra = Util.replace(fra, "ä", "ae");
		fra = Util.replace(fra, "ö", "oe");
		fra = Util.replace(fra, "ü", "ue");
		fra = Util.replace(fra, "ß", "ss");
		
		fra = Util.wegMitAbkuerzungen(fra);

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
			
			Gedanke ged = new Gedanke("", feel);
			ArrayList<Gedanke> geds = new ArrayList<Gedanke>();
			for (Integer in : ged.feelingToGedanke.keySet()) {
				if (Integer.parseInt(in.toString()) == feel) {
					geds.add(new Gedanke(ged.feelingToGedanke.get(in), feel));
				}
			}
			if (geds.size() > 1) {
				Random r = new Random();
				int n = r.nextInt(geds.size() -1);
				ged = geds.get(n);
			} else {
				ged = new Gedanke("", feel);
			}

			return new Satz(antPlain, ant, feel, ged);
		}
		return null;
	}

}
