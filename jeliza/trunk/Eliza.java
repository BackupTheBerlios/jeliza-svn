import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Eliza extends Applet implements ActionListener {

	public String name = "PC";

	public String nameCmd = "PC# ";

	public String user = "Du";

	public String userCmd = "Du# ";

	public String oldFra = "";

	public String oldAnt = "";

	public String oldObj = "";

	public String s = "";

	public boolean isQuesAnt = false;

	TextArea t;

	Panel p;

	Button b;

	TextField tf;

	public static void main(String[] args) {
		new Eliza();
	}

	void println(String str) {
		t.setText(str + "\n");
	}

	void println2(String str) {
		t.setText(t.getText() + str + "\n");
	}

	public void start() {
		tf.requestFocus();
	}

	public void stop() {
		tf.requestFocus();
	}

	public void destroy() {
	}

	public void init() {

		Eliza f = this;

		f.setSize(700, 500);

		f.setLayout(new BorderLayout());
		t = new TextArea();
		p = new Panel(new BorderLayout());
		b = new Button("Fragen");
		b.setFont(new Font("Verdana", Font.PLAIN, 10));
		b.addActionListener(this);
		tf = new TextField();

		f.add(t, "Center");
		f.add(p, "South");
		p.add(tf, "Center");
		p.add(b, "East");

		tf.setText("");
		t.setText("");

		t.setBackground(Color.white);
		tf.setBackground(Color.white);
		t.setFont(new Font("Verdana", Font.PLAIN, 10));
		tf.setFont(new Font("Verdana", Font.PLAIN, 10));

		println2(nameCmd + "Guten Tag");
		println2("");
		println2(nameCmd + "Rede mich bitte mit \"Du\" an.");
		println2(nameCmd + "Formuliere deine Saetzte in hochdeutsch.");
		println2(nameCmd + "Verwende statt ä ae etc.");
		println2(nameCmd + "Schreibe statt \"usw.\" \"etc.\"");
		println2(nameCmd + "Achte auf die Gross- und Kleinschreibung");
		println2(nameCmd + "Schreibe kurze und sachliche Saetzte!");
		println2("");
		println2(nameCmd + "Sag mit nun dein Problem.");

		println2("");

		String fra = "";

		// f.show();

		tf.requestFocus();
	}

	public String processQues(String fra) {
		boolean fertig = false;
		String ant = "Uff! Da bin ich überfragt!\nKontaktieren sie doch bitte meinen Programmierer "
				+ "darüber,\n"
				+ "indem sie im Gästebuch dieser Seite einen Eintrag mit dieser Frage hinterlassen!";

		Random r = new java.util.Random();

		fra = replace(fra, "?", "");

		
		fra = fra.trim();
		// fra = fra.toLowerCase();

		// if(regel1()
		// String a ;

		String s = "";

		s = regel1(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = regel2(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = regel2_5(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		/*
		 * if (r.nextInt(100) < 30) {
		 * 
		 * s = regel3(fra, ant); if (!fertig && s != ant) { ant = s; fertig =
		 * true; } } else {
		 */

		/*
		 * s = regel4(fra, ant); if (!fertig && s != ant) { ant = s; fertig =
		 * true; }
		 * 
		 * s = regel3(fra, ant); if (!fertig && s != ant) { ant = s; fertig =
		 * true; } /* }
		 */

		s = getAntFromGehirn(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = regel4(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		s = regel3(fra, ant);
		if (!fertig && s != ant) {
			ant = s;
			fertig = true;
		}

		fertig = true;
		if (fertig) {

			ant = replace(ant, "  ", " ");
			oldFra = fra;
			oldAnt = ant;

			return ant;
		}
		return null;
	}

	private String regel1(String _fra, String _ant) {
		String ant = _ant;
		String fra = _fra;

		String antExit = "Auf Wiedersehen. Du schuldest mit 500 Euro fuer die Beratung.";
		String[] antJa = { "Ach so.", "Gut.", "Warscheinlich hast du Recht!",
				"Wie du meinst!" };
		String[] antNein = { "Sicher?", "Wirklich nicht?", "Meinst du?",
				"Warum?" };
		Random r = new java.util.Random();

		if (fra.indexOf("tschue") > -1)
			return antExit;
		if (fra.indexOf("auf wiedersehen") > -1)
			return antExit;
		if (fra.indexOf("bis bald") > -1)
			return antExit;
		if (fra.indexOf("bis nachher") > -1)
			return antExit;
		if (fra.indexOf("danke") > -1)
			return "Fuer was bekankst du dich?";
		if (fra.indexOf("bitte") > -1)
			return "Warum sagst du bitte? Ich bin eine Maschine.";

		if (fra.startsWith("ja") && fra.length() < 4)
			return antJa[r.nextInt(antJa.length)];

		if (fra.startsWith("nein") && fra.length() < 6)
			return antNein[r.nextInt(antJa.length)];

		return ant;
	}

	private String regel2(String _fra, String _ant) {
		String ant = _ant;
		String fra = _fra;

		fra = fra.toLowerCase();

		Random r = new java.util.Random();

		if (fra.indexOf("eliza") > -1)
			return "Ja ich bin JEliza. Oder was hast du gefragt?";

		return ant;

	}

	private String regel2_5(String _fra, String _ant) {
		String ant = _ant;
		String fra = _fra;

		fra = fra.toLowerCase();

		String[] antWas = {
				"Keine Ahnung. Ich bin eine Software, die es erst sehr \n "
						+ "kurz gibt. So ausgereift bin ich nicht!",
				"Mit sowas kenne ich mich nicht aus.",
				"Spreche mal mit einem Freund darueber",
				"Spreche mal mit einer Freundin darueber",
				"Hä?",
				"Was du meinst ist sicherlich interssanter",
				"Was meinst du?",
				"Uff. Ich muss erstmal in der Wikipedia nachschlagen. \n"
						+ "Frag mich was anderes oder komm spaeter wieder.",
				"Frag besser einen Experten. Nicht dass ich dir noch falsches sage.",
				"Hä? Sag doch bitte meinem Programmierer (tobidonald@arcor.de) deine Frage.",
				"Da bin ich ueberfordert!" };
		String[] antWieso = {
				"Keine Ahnung. Ich bin eine Software, die es erst sehr \n "
						+ "kurz gibt. So ausgereift bin ich nicht!",
				"Mit sowas kenne ich mich nicht aus.", "Wieso? Uff." };
		String[] antTod = { "Wer ist gestorben?", "Ist jemand gestorben?" };
		String[] antGeburt = { "Wer ist geboren?",
				"Sprachst du von einer Geburt?" };

		Random r = new java.util.Random();

		fra = replace(fra, "ä", "ae");
		fra = replace(fra, "ö", "oe");
		fra = replace(fra, "ü", "ue");
		fra = replace(fra, "ß", "ss");
		fra = replace(fra, "#", "");
		fra = replace(fra, "?", "");
		fra = replace(fra, "!", "");
		fra = replace(fra, ".", "");
		fra = replace(fra, ",", "");
		fra = replace(fra, ";", "");
		fra = replace(fra, "-", "");
		fra = replace(fra, "$", "");
		fra = replace(fra, "§", "");
		fra = replace(fra, "/", " ");
		fra = replace(fra, "(", " ");
		fra = replace(fra, ")", " ");
		fra = replace(fra, "=", " ");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "\n", "");
		fra = replace(fra, "\r", "");
		String ofra = fra;

		if (fra.startsWith("was ist") || fra.startsWith("wer ist")) {
			StringTokenizer stoken = new StringTokenizer(fra, " ");
			stoken.nextToken();
			stoken.nextToken();
			String obj = "";
			while (stoken.hasMoreTokens()) {
				obj += stoken.nextToken() + " ";
			}
			obj = obj.trim();
			obj = obj.toLowerCase();
			String dasIst = "";
			dasIst = wasIst("was ist " + obj);
			System.out.println(obj);
			System.out.println(dasIst);
			if (dasIst == null) {
				try {
					getAppletContext().showDocument(
							new URL(
									"http://tobiasschulz.homedns.org/jeliza/addWord.php?was=was ist "
											+ obj));
				} catch (IOException e) {
					println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n" + e.getMessage());
				}
			}

			return dasIst;
		}

		if (fra.startsWith("wieso") || fra.startsWith("warum")
				|| fra.startsWith("weshalb")) {
			StringTokenizer stoken = new StringTokenizer(fra, " ");
			stoken.nextToken();
			String obj = "";
			while (stoken.hasMoreTokens()) {
				obj += stoken.nextToken() + " ";
			}
			obj = obj.trim();
			obj = obj.toLowerCase();
			String dasIst = "";
			dasIst = wasIst("wieso " + obj);
			System.out.println(obj);
			System.out.println(dasIst);
			if (dasIst == null) {
				try {
					getAppletContext().showDocument(
							new URL(
									"http://tobiasschulz.homedns.org/jeliza/addWord.php?was=wieso "
											+ obj));
				} catch (IOException e) {
					println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n" + e.getMessage());
				}
			}

			return dasIst;
		}

		if (fra.startsWith("wie")) {
			StringTokenizer stoken = new StringTokenizer(fra, " ");
			stoken.nextToken();
			String obj = "";
			while (stoken.hasMoreTokens()) {
				obj += stoken.nextToken() + " ";
			}
			obj = obj.trim();
			obj = obj.toLowerCase();
			String dasIst = "";
			dasIst = wasIst("wie " + obj);
			System.out.println(obj);
			System.out.println(dasIst);
			if (dasIst == null) {
				try {
					getAppletContext().showDocument(
							new URL(
									"http://tobiasschulz.homedns.org/jeliza/addWord.php?was=wie "
											+ obj));
				} catch (IOException e) {
					println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n" + e.getMessage());
				}
			}

			return dasIst;
		}

		return ant;
	}

	private String regel3(String _fra, String _ant) {
		String ant = _ant;
		String fra = _fra;

		Random r = new java.util.Random();
		fra = replace(fra, "?", "");
		fra = replace(fra, "!", "");
		fra = replace(fra, ".", "");
		fra = replace(fra, ",", "");
		fra = replace(fra, ";", "");
		fra = replace(fra, "-", "");
		fra = replace(fra, "$", "");
		fra = replace(fra, "§", "");
		fra = replace(fra, "/", " ");
		fra = replace(fra, "(", " ");
		fra = replace(fra, ")", " ");
		fra = replace(fra, "=", " ");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "\n", "");
		fra = replace(fra, "\r", "");

		// String[] words = fra.split(" ");
		Vector nomen = new Vector();
		String tmpAdj = "";
		int tmpAdjNum = 0;
		int num = 0;
		StringTokenizer stoken = new StringTokenizer(fra, " ");
		while (stoken.hasMoreElements()) {
			String token = stoken.nextToken().trim();
			// for (int x = 0; x < words.length; x++) {
			System.out.println(token);
			if (token == "" || token == " " || token == null)
				continue;
			if (token.charAt(0) == (token.charAt(0) + "").toUpperCase().charAt(
					0)) {
				nomen.add(((tmpAdjNum == num - 1) ? tmpAdj : "") + " " + token);
			} else if (token != "und" || token != "oder" || token != "aber") {
				token = ((tmpAdjNum == num - 1) ? tmpAdj : "") + " " + token;
				tmpAdj = token;
				tmpAdjNum = num;
			}
			num++;
		}

		/*
		 * System.out.println(words[x]); if (words[x] == "" || words[x] == " " ||
		 * words[x] == null) continue; if (words[x].charAt(0) ==
		 * (words[x].charAt(0) + "").toUpperCase() .charAt(0)) {
		 * nomen.add(words[x]); }
		 */
		// }
		if (nomen.size() == 0) {
			ant = ant;
		} else if (nomen.size() < 2) {
			ant = nomen.get(0) + "? Erzaehle mir, was du darueber weisst !";
		} else if (nomen.size() < 3) {
			ant = nomen.get(0) + " und " + nomen.get(1) + "? Erzaehle mir, "
					+ "was du darueber weisst !";
		} else {
			ant = nomen.get(0) + ", " + nomen.get(1) + " und " + nomen.get(2)
					+ "? Was hat es damit auf sich ?";
		}

		return ant;
	}

	private String regel4(String _fra, String _ant) {
		String ant = _ant;
		String fra = _fra;

		String[] ichVerben = { "habe", "bin", "fuehle", "laufe", "renne",
				"gehe", "kann", "sehe", "hatte", "war", "fuehlte", "lief",
				"rannte", "ging", "konnte", "gebe", "gab", "nehme", "nahm",
				"spiele", "spielte", "arbeite", "arbeitete" };
		String[] ichVerben2 = { "hast", "bist", "fuehst", "laeufst", "rennst",
				"gehst", "kannst", "siehst", "hattest", "warst", "fuehltest",
				"liefst", "rannte", "gegangen bist", "konntest", "gibst",
				"gabst", "nimmst", "nahmst", "spielst", "spieltest",
				"arbeitest", "hattest gearbeitet" };
		String[] ichVerben3 = { "XhaXstX", "XbiXstX", "XfuXehXstX",
				"XlaXeuXfsXt", "XreXnnXstX", "gXehXstX", "XkaXnnXstX",
				"sXiehXstX", "hXatXteXstX", "wXarXstX", "fXueXhlXteXstX",
				"XliXefXstX", "XraXnnXteX", "XgeXgaXngXenXYbXisXt",
				"XkoXnnXteXstX", "XgiXbsXt", "gXabXstX", "XniXmmXstX",
				"XnaXhmXstX", "sXpiXelXstX", "sXpiXelXteXstX",
				"XarXbeXitXesXt", "XhaXttXesXtYXgeXarXbeXitXetX" };
		Random r = new java.util.Random();

		fra = replace(fra, "-", "");
		fra = replace(fra, "$", "");
		fra = replace(fra, "§", "");
		fra = replace(fra, "/", " ");
		fra = replace(fra, "(", " ");
		fra = replace(fra, ")", " ");
		fra = replace(fra, "=", " ");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "\n", "");
		fra = replace(fra, "\r", "");

		fra = replace(fra, "?", " ? ");
		fra = replace(fra, "!", " ! ");
		fra = replace(fra, ".", " . ");

		// String[] words = fra.split(" ");
		int y = 0;
		Vector nomen = new Vector();
		StringTokenizer stoken = new StringTokenizer(fra, " ");
		while (stoken.hasMoreElements()) {
			String token = stoken.nextToken();
			// for (int x = 0; x < words.length; x++) {
			System.out.println(token);
			if (token == "" || token == " " || token == null)
				continue;
			if (token == "?" || token == "!" || token == ".")
				break;
			nomen.add(token);
			y++;
		}

		fra = replace(fra, "?", "");
		fra = replace(fra, "!", "");
		fra = replace(fra, ".", "");
		fra = replace(fra, "  ", "");
		fra = replace(fra, "  ", "");

		// if (y < 12) {
		ant = " ";
		for (int x = 0; x < nomen.size(); x++) {
			ant += nomen.get(x) + " ";
		}
		ant = replace(ant, " Du ", " XDXuX ");
		ant = replace(ant, " Dein", " XDXeXiXnX");
		ant = replace(ant, " Dir ", " XDXiXrX ");
		ant = replace(ant, " Dich ", " XDXiXcXhX ");

		ant = replace(ant, " du ", " XDXuX ");
		ant = replace(ant, " dein", " XDXeXiXnX");
		ant = replace(ant, " dir ", " XDXiXrX ");
		ant = replace(ant, " dich ", " XDXiXcXhX ");

		ant = replace(ant, " ich ", " Du ");
		ant = replace(ant, " mein", " Dein");
		ant = replace(ant, " mir ", " Dir ");
		ant = replace(ant, " mich ", " Dich ");

		ant = replace(ant, " Ich ", " du ");
		ant = replace(ant, " Mein", " dein");
		ant = replace(ant, " Mir ", " dir ");
		ant = replace(ant, " Mich ", " dich ");

		ant = replace(ant, " XDXuX ", " ich ");
		ant = replace(ant, " XDXeXiXnX", " mein");
		ant = replace(ant, " XDXiXrX ", " mir ");
		ant = replace(ant, " XDXiXcXhX ", " mich ");

		for (int x = 0; x < ichVerben.length; x++) {
			ant = replace(ant, " " + ichVerben2[x] + " ", " " + ichVerben3[x]
					+ " ");
		}

		for (int x = 0; x < ichVerben.length; x++) {
			ant = replace(ant, " " + ichVerben[x] + " ", " " + ichVerben2[x]
					+ " ");
		}

		for (int x = 0; x < ichVerben.length; x++) {
			ant = replace(ant, " " + ichVerben3[x] + " ", " " + ichVerben[x]
					+ " ");
		}

		ant = ant.trim();

		String[] antGrund = { "? Willst du das so?",
				"? Entspricht das deinen Vorstellungen?",
				"? Wirklich? Schrecklich ...", "? Sehen das alle so?",
				"? Koenntest du mir sagen warum?", "? Oh gott!", "? Warum?",
				"? Weshalb?", "? Ist das Normal?",
				"? ich verstehe nicht warum?!?" };
		ant += antGrund[r.nextInt(antGrund.length)];

		System.out.println("##################");
		// }

		return ant;
	}

	public void actionPerformed(ActionEvent e) {
		if (!isQuesAnt) {
			println(processQues(tf.getText()));
			tf.setText("");
			tf.requestFocus();
		} else {
			isQuesAnt = false;
			try {
				getAppletContext().showDocument(
						new URL(
								"http://tobiasschulz.homedns.org/jeliza/addWord.php?was="
										+ oldObj + "&dasist=" + oldFra));
			} catch (IOException ee) {
				println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n" + ee.getMessage()); try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public String replace(String ss, String search, String replace) {
		s = ss;
		while (true) {
			int pos = s.indexOf(search);
			if (pos >= 0) {
				s = s.substring(0, pos) + replace
						+ s.substring(pos + search.length(), s.length());

				t.setText(s);
			} else {
				return s;
			}
		}
	}

	// private String wasIst(String obj) {
	// return new WasIst().get(obj);
	// }

	public String wasIst(String obj) {
		java.lang.String strDatei = "http://tobiasschulz.homedns.org/WasIst.txt";
		java.lang.String strLs = System.getProperty("line.separator");
		java.io.InputStream file = null;
		java.net.URL url = null;
		try {
			url = new java.net.URL(this.getDocumentBase(), strDatei); //$NON-NLS-1$
			file = url.openStream();
		} catch (java.net.MalformedURLException mue) {
			System.out.println("Fehlerhafte URL-Adresse / Datei");
			return null;
		} catch (java.io.IOException e) {
			System.out
					.println("IOException beim Initialisieren der Verbindung zum Server");
			return null;
		}
		java.io.BufferedReader br = new java.io.BufferedReader(
				new java.io.InputStreamReader(file));
		String dasIst = null;
		int xy = 0;
		while (true) {
			try {
				java.lang.String line = br.readLine();
				println(xy + "");
				if (null == line)
					break;

				if (line.startsWith(obj + "=====================")) {
					StringTokenizer stoken = new StringTokenizer(line,
							"=====================");
					String token = stoken.nextToken();
					token = stoken.nextToken();
					dasIst = token;
					return token;
				}
			} catch (IOException e) {
				println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n" + e.getMessage());
			}
			xy++;
		}

		return dasIst;
	}

	public String getAntFromGehirn(String obj, String ant) {
		java.lang.String strDatei = "http://tobiasschulz.homedns.org/JavaGehirn.ghn";
		java.lang.String strLs = System.getProperty("line.separator");
		java.io.InputStream file = null;
		java.net.URL url = null;
		try {
			url = new java.net.URL(this.getDocumentBase(), strDatei); //$NON-NLS-1$
			file = url.openStream();
		} catch (java.net.MalformedURLException mue) {
			System.out.println("Fehlerhafte URL-Adresse / Datei");
		} catch (java.io.IOException e) {
			System.out
					.println("IOException beim Initialisieren der Verbindung zum Server");
		}
		java.io.BufferedReader br = new java.io.BufferedReader(
				new java.io.InputStreamReader(file));
		String dasIst = ant;
		while (true) {
			try {
				java.lang.String line = br.readLine();
				if (null == line)
					break;
				line = line.trim().toLowerCase();
				if ("-" == line)
					break;

				if (line.startsWith("#") && obj.indexOf(line.substring(1)) > -1) {
					Object[] anws;
					Vector anws2 = new Vector();
					int z = 0;
					java.lang.String line2 = br.readLine();
					while (!line2.trim().startsWith("-")) {
						if (null == line2)
							break;
						line2 = line2.trim();
						System.out.println(line2);
						// if ("-" == line2.trim()) {
						// z = 1000;
						// break;
						// }
						anws2.add(line2);

						z++;
						line2 = br.readLine().trim();
					}
					if (z == 1000) {
						break;
					}

					Random r = new java.util.Random();

					anws = anws2.toArray();
					System.out.println(anws.length);
					System.out.println(anws[r.nextInt(anws.length)]);
					return (String) anws[r.nextInt(anws.length)];
				}
			} catch (IOException e) {
				println("Achtung!\nHirnfehler!\n\nIch habe einen Fehler!\n\nBitte kontaktieren sie meinen \nProgrammierer, indem sie in \ndas Gästebuch schreiben oder eine\nMail an tobischulz@arcor.de\n schreiben!\n\nFehler:\n" + e.getMessage());
			}
		}

		return dasIst;
	}

}
