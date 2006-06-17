package org.homedns.tobiasschulz.apps.jeliza;

public class FragenAntworter {

	/**
	 * Methode zum Beantworten der Frage / Des Satzes des Users.
	 * 
	 * @see doGet
	 * @param fra
	 *            Die Frage
	 * @return Die Antwort
	 */
	public String processQuestion(String fra, Regeln re, Gehirn hirn) {
		boolean fertig = false;
		String ant = "Uff! Da bin ich überfragt!\nKontaktieren sie doch bitte meinen Programmierer "
				+ "darüber,\n"
				+ "indem sie im Gästebuch dieser Seite einen Eintrag mit dieser Frage hinterlassen!";

		fra = Util.replace(fra, "?", "");
		fra = Util.replace(fra, ".", "");
		fra = Util.replace(fra, "!", "");

		fra = fra.trim();

		String s = "";

		s = re.regel1(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = re.regel2(fra, ant);
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

		s = re.regel3(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		fertig = true;
		if (fertig) {

			ant = Util.replace(ant, "  ", " ");

			return ant;
		}
		return null;
	}

}
