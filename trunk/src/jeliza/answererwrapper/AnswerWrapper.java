package jeliza.answererwrapper;

import ext.speech.core.Speech;
import jeliza.markovwrapper.JElizaMarkov;
import jeliza.util.Util;

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
	public String processQuestion(String fr, Answerer reParam, JElizaMarkov mj) {
		String fra = fr;
		re = reParam;
		if (System.getProperty("jeliza.name") != null) {
			fra = fra.replace(System.getProperty("jeliza.name"), "JEliza");
			fra = fra.replace(System.getProperty("jeliza.name").toLowerCase(),
					"JEliza");
		}
		boolean fertig = false;
		String ant = "Uff! Da bin ich überfragt!\nKontaktieren sie doch bitte meinen Programmierer "
				+ "darüber,\n"
				+ "indem sie eine Mail an tobischulz@arcor.de mit dieser Frage schicken!";

		fra = Util.replace(fra, "ä", "ae");
		fra = Util.replace(fra, "ö", "oe");
		fra = Util.replace(fra, "ü", "ue");
		fra = Util.replace(fra, "ß", "ss");

		fra = Util.wegMitAbkuerzungen(fra);

		System.out.println("* Generiere Antwort auf Frage");
		System.out.println("  Frage:   \"" + fra + "\"");

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

		if (!fertig) {
			s = re.getInformation(fra, ant);
			if (!fertig && s != ant) {
				ant = s;
				fertig = true;
			}
		}

		if (!fertig) {
			s = re.getQuestionWord(fra, ant);
			if (!fertig && s != ant) {
				ant = s;
				fertig = true;
			}
		}

		if (!fertig) {
			s = mj.ask(ofra, ant);
			if (!fertig && s != ant && s.trim().hashCode() != "".hashCode()) {
				ant = s;
				fertig = true;
			}
		}

		satzTeile[0] = re.subjekt;

		fertig = true;
		if (fertig) {

			ant = Util.replace(ant, "  ", " ");

			String antPlain = ant;

			int i = 0;
			while ((i = antPlain.indexOf('<')) > -1) {
				antPlain = antPlain.substring(0, i)
						+ antPlain.substring(antPlain.indexOf('>', i));
			}
			antPlain = antPlain.replace("<", "");
			antPlain = antPlain.replace(">", "          ");

			System.out.println("  Antwort: \"" + antPlain + "\"");

			if (System.getProperty("jeliza.name") != null) {
				ant = ant.replace("JEliza", System.getProperty("jeliza.name"));
				ant = ant.replace("jeliza", System.getProperty("jeliza.name")
						.toLowerCase());
				antPlain = antPlain.replace("JEliza", System
						.getProperty("jeliza.name"));
				antPlain = antPlain.replace("jeliza", System.getProperty(
						"jeliza.name").toLowerCase());
			}

			{
				final String str = antPlain;
				new Thread(new Runnable() {
					public void run() {
						Speech sp = new Speech();
						sp.speak(str);
					}
				}).start();
			}

			return antPlain;
		}
		return null;
	}
}
