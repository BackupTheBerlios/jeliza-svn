package org.homedns.tobiasschulz.apps.jeliza.hirn;

import java.util.Iterator;

import module.megajeliza.Saver;

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

	Saver sa = new Saver();

	/**
	 * Methode zum Beantworten der Frage / Des Satzes des Users.
	 * 
	 * @param fra
	 *            Die Frage
	 * @return Die Antwort
	 */
	public Satz processQuestion(Satz fr, Answerer reParam, Gehirn hirn,
			final VerbDataBase vdb) {
		String fra = fr.satzPlain;
		re = reParam;
		re.vdb = vdb;
		hirn.re = re;
		if (System.getProperty("jeliza.name") != null) {
			fra = fra.replace(System.getProperty("jeliza.name"), "JEliza");
			fra = fra.replace(System.getProperty("jeliza.name").toLowerCase(), "JEliza");
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

		if (hirn.erSieEsGedaechtnis[0].trim().toLowerCase().hashCode() != "er"
				.hashCode()) {
			System.out.println("  Folgende Woerter werden ersetzt:");
			System.out.println("  Er  -> " + hirn.erSieEsGedaechtnis[0]);
		}
		if (hirn.erSieEsGedaechtnis[1].trim().toLowerCase().hashCode() != "sie"
				.hashCode()) {
			System.out.println("  Folgende Woerter werden ersetzt:");
			System.out.println("  Sie -> " + hirn.erSieEsGedaechtnis[1]);
		}
		if (hirn.erSieEsGedaechtnis[2].trim().toLowerCase().hashCode() != "es"
				.hashCode()) {
			System.out.println("  Folgende Woerter werden ersetzt:");
			System.out.println("  Es  -> " + hirn.erSieEsGedaechtnis[2]);
		}

		String[] fs = fra.split(" ");
		String q = "";
		for (int x = 0; x < fs.length; x++) {
			String st = fs[x];
			if (st.toLowerCase().hashCode() == "er".hashCode()
					|| st.toLowerCase().hashCode() == "sie".hashCode()
					|| st.toLowerCase().hashCode() == "es".hashCode()) {
				q += st;
			} else if (st.toLowerCase().hashCode() == "er".hashCode()) {
				q += ((Genus.getGenus(hirn.erSieEsGedaechtnis[0], vdb) != Genus.UNKNOWN) ? Genus
						.getDerDieDas(Genus.getGenus(
								hirn.erSieEsGedaechtnis[0], vdb))
						: "")
						+ " " + hirn.erSieEsGedaechtnis[0];
			} else if (st.toLowerCase().hashCode() == "sie".hashCode()) {
				q += ((Genus.getGenus(hirn.erSieEsGedaechtnis[1], vdb) != Genus.UNKNOWN) ? Genus
						.getDerDieDas(Genus.getGenus(
								hirn.erSieEsGedaechtnis[1], vdb))
						: "")
						+ " " + hirn.erSieEsGedaechtnis[1];
			} else if (st.toLowerCase().hashCode() == "es".hashCode()) {
				q += ((Genus.getGenus(hirn.erSieEsGedaechtnis[2], vdb) != Genus.UNKNOWN) ? Genus
						.getDerDieDas(Genus.getGenus(
								hirn.erSieEsGedaechtnis[2], vdb))
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

/*		if (!fertig) {
			s = re.parseSatz(ofra, ant);
			if (!fertig && s != ant) {
				ant = s;
				fertig = true;
			}
		}*/

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
			s = hirn.mj.ask(ofra, ant);
			if (!fertig && s != ant && s.trim().hashCode() != "".hashCode()) {
				ant = s;
				fertig = true;
			}
		}

		if (!fertig) {
			s = hirn.getAntPublicGehirn(fra, ant);
			if (!fertig && s != ant && s.trim().hashCode() != "".hashCode()) {
				ant = s;
				fertig = true;
			}
		}

		if (!fertig) {
			s = re.getFraOfThema(fra, ant);
			if (!fertig && s != ant) {
				ant = s;
				fertig = true;
			}
		}

		if (!fertig) {
			s = hirn.getAntBaseGehirn(fra, ant);
			if (!fertig && s != ant) {
				ant = s;
				fertig = true;
			}
		}

		if (!fertig) {
			s = re.getFra2Ant(fra, ant);
			if (!fertig && s != ant) {
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

			String[] woerter = fra.split(" ");
			int feel = -1;
			for (int x = 0; x < woerter.length; x++) {
				String tmp = woerter[x];
				if (hirn.gefuehlHeute.wortToFeeling.containsKey(tmp)) {
					feel = Integer.parseInt((String) hirn.gefuehlHeute.wortToFeeling.get(tmp));
				}
			}
			Iterator it = hirn.gefuehlHeute.wortToFeeling.keySet().iterator();
			while (it.hasNext()) {
				String tmp = (String) it.next();
				if (fra.toLowerCase().contains(tmp.toLowerCase())) {
					feel = Integer.parseInt((String) hirn.gefuehlHeute.wortToFeeling.get(tmp));
				}
			}
			
			woerter = fra.split(" ");
			for (int x = 0; x < woerter.length; x++) {
				String st = woerter[x];
				if (vdb.isNomen(st)) {
					Genus.getGenus(st, vdb);
				}
			}
			String vorher = "";
			String warschon = "";
			for (int x = 0; x < woerter.length; x++) {
				String st = woerter[x];
				if (st.length() < 2) {
					continue;
				}
				if (Character.toLowerCase(st.charAt(0)) == st.charAt(0)
						|| (st.toLowerCase().hashCode() == "der".hashCode()
								|| vdb.isAdj(st.toLowerCase())
								|| st.toLowerCase().hashCode() == "die"
										.hashCode() || st.toLowerCase()
								.hashCode() == "das".hashCode())) {
					vorher = s;
					continue;
				}
				String[] tmp = { st, vorher + " " + st };
				for (int y = 0; y < tmp.length; y++) {
					String str = tmp[y];
					str = str.trim();
					String strLow = str.toLowerCase();
					if (vdb.isVerb(str)) {
						continue;
					}
					short genus = Genus.getGenus(strLow, vdb);
					String erSieEs = Genus.getErSieEs(genus);
					if (genus != Genus.UNKNOWN
							&& !warschon.toLowerCase().contains(
									erSieEs.toLowerCase())) {
						String[] w = strLow.split(" ");
						if (w.length > 2) {
							continue;
						}
						if (erSieEs.toLowerCase().hashCode() == "er".hashCode()) {
							hirn.erSieEsGedaechtnis[0] = str;
							warschon += erSieEs.toLowerCase();
						}
						if (erSieEs.toLowerCase().hashCode() == "sie"
								.hashCode()) {
							hirn.erSieEsGedaechtnis[1] = str;
							warschon += erSieEs.toLowerCase();
						}
						if (erSieEs.toLowerCase().hashCode() == "es".hashCode()) {
							hirn.erSieEsGedaechtnis[2] = str;
							warschon += erSieEs.toLowerCase();
						}
					}
				}
				vorher = s;
			}

			new Thread(new Runnable() {

				public void run() {
//					interpreter.base.Compiler c = new interpreter.base.Compiler();
//					c.compile(vdb);
				}

			}).start();

			System.out.println("  Antwort: \"" + antPlain + "\"");

			if (System.getProperty("jeliza.name") != null) {
				ant = ant.replace("JEliza", System.getProperty("jeliza.name"));
				ant = ant.replace("jeliza", System.getProperty("jeliza.name").toLowerCase());
				antPlain = antPlain.replace("JEliza", System.getProperty("jeliza.name"));
				antPlain = antPlain.replace("jeliza", System.getProperty("jeliza.name").toLowerCase());
			}
			
			sa.save();

			return new Satz(antPlain, ant, new Integer(feel), hirn, ofra);
		}
		return null;
	}
}
