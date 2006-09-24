import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;

import javax.swing.*;
import org.homedns.tobiasschulz.io.*;
import org.homedns.tobiasschulz.util.satzparser.*;
import org.homedns.tobiasschulz.apps.speech.*;
import org.homedns.tobiasschulz.apps.jeliza.Util;
import org.homedns.tobiasschulz.apps.jeliza.hirn.*;

/**
 * Das Java-Servlet JEliza, ein Programm, welches die Menschliche Sprache
 * versteht (verstehen sollte) 
 * 
 * @author Tobias Schulz
 * @version 0.3
 */
public class JElizaGui implements ActionListener {

	String absoluteUrl = "";

	String oldFra = "";

	String oldAnt = "";

	String oldObj = "";

	Gehirn hirn = Gehirn.newGehirn(absoluteUrl);

	String neuWissen = "";

	boolean isQuesAnt = false;

	PrintWriter out;

	String outBuf = "";

	String outAll = "";

	JTextField userText;

	JEditorPane jelizaText;

	String gespraech = "";

	JFrame fr;

	JPanel sidebar = new JPanel(new GridLayout(15, 1, 5, 5));

	JPanel oberSidebar = new JPanel(new BorderLayout(5, 5));

	public Dialog dia;

	public Dialog dia2;

	public Dialog dia3;

	public Dialog dia4;

	/**
	 * Der Standard-Konstruktor
	 */
	public JElizaGui() {
		Frame win = new Frame("Bitte warten ...");
		TextArea tex = new TextArea("Lade Daten ...");
		win.add(tex);
		win.add("South", new Label("Copyright 2006 by Tobias Schulz"));
		win.setSize(300, 200);
		win.setLocation(300, 250);
		win.setVisible(true);

		sleep(500);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			System.out.println("Sie verwenden ein komisches Java ...");
			e.printStackTrace();
			System.exit(2);
		} catch (InstantiationException e) {
			System.out.println("Kann UIManager nicht instanziieren ...");
			e.printStackTrace();
			System.err.println("Fahre trotz Fehler fort.");
		} catch (IllegalAccessException e) {
			System.out.println("Sie verwenden ein komisches Java ...");
			e.printStackTrace();
			System.exit(2);
		} catch (UnsupportedLookAndFeelException e) {
			System.out
					.println("Was, bitte, verwenden sie fuer ein Betriebsystem?");
			e.printStackTrace();
			System.exit(2);
		}
		
		tex.setText("Generiere Wortschatz ...");

		String version;
		try {
			version = FileManager.readFileIntoString("version.ver");
		} catch (HeadlessException e) {
			version = "unknown";
		} catch (IOException e) {
			version = "unknown";
		}

		fr = new JFrame("JEliza version " + version);
		fr.setLayout(new BorderLayout(5, 5));
		fr.setBackground(Color.white);
		fr.setForeground(Color.darkGray);
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		prepareShowWords();
		
		sleep(1500);
		
		tex.setText("Generiere grafische Oberflaeche ...");

		jelizaText = new JEditorPane("text/html", "");
		jelizaText.setEditable(false);
		jelizaText.setText("<html><body>" + "Hallo!\n<br>" + "\n<br>"
				+ "Bitte Antworte IMMER in ganzen Saetzen.<br>\n" + "<br>\n"
				+ "Wie ist dein Name?" + "</body></html>");
		jelizaText.setBackground(Color.white);
		jelizaText.setForeground(Color.darkGray);
		fr.add(new JScrollPane(jelizaText), "Center");

		JPanel userPanel = new JPanel(new BorderLayout(10, 10));
		JPanel bottomPanel = new JPanel(new BorderLayout(0, 0));

		userText = new JTextField();
		userText.setText("");
		userText.addActionListener(this);
		userText.setActionCommand("fra");
		userPanel.add(userText, "Center");

		JButton senden = new JButton("Fragen");
		senden.addActionListener(this);
		senden.setActionCommand("fra");
		userPanel.add(senden, "East");

		bottomPanel.add(userPanel, "North");
		// bottomPanel.add(new JLabel(
		// " "), "South");

		fr.add(bottomPanel, "South");

		generateSidebar("");
		oberSidebar.add(sidebar, "North");
		fr.add(oberSidebar, "East");

		sleep(1000);

		fr.addWindowListener(new WindowAdapter() {

			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});

		show();
		userText.requestFocus();
		
		win.setVisible(false);
		tex = null;
		win = null;
	}

	/**
	 * Generiert die Sidebar, die Rechts im Fenster angezeigt wird und Sachen
	 * wie Gefuehle etc. anzeigt.
	 */
	private void generateSidebar(String fra) {
		fr.remove(oberSidebar);
		sidebar.removeAll();
		oberSidebar.removeAll();

		sidebar.add(new JLabel("Gefuehl heute:"));
		sidebar.add(new JTextField(hirn.gefuehlHeute.getFeeling()));

		JButton save = new JButton("Gespraech Speichern");
		save.addActionListener(this);
		save.setActionCommand("save");
		sidebar.add(save);

		JButton open = new JButton("Gespraech Laden");
		open.addActionListener(this);
		open.setActionCommand("open");
		sidebar.add(open);

		JButton genWissenDatenbank = new JButton("Datenbank generieren");
		genWissenDatenbank.addActionListener(this);
		genWissenDatenbank.setActionCommand("genWissenDatenbank");
		sidebar.add(genWissenDatenbank);

		String[] st = hirn.re.analyseSatz(fra, "");
		if (st == null) {
			st = new String[4];
		}
		sidebar.add(new JLabel("Subjekt im Satz:"));
		sidebar.add(new JTextField(st[0]));
		sidebar.add(new JLabel("Verb im Satz:"));
		sidebar.add(new JTextField(st[1]));
		sidebar.add(new JLabel("Objekt im Satz:"));
		sidebar.add(new JTextField(st[2]));
		sidebar.add(new JLabel("Fragewort im Satz:"));
		sidebar.add(new JTextField(st[3]));

		fr.setJMenuBar(genJMenuBar());

		oberSidebar.add(sidebar, "North");
		fr.add(oberSidebar, "East");
		show();
		fr.repaint();
		oberSidebar.repaint();
		sidebar.repaint();

		System.out.println("Generated Sidebar");
	}

	/**
	 * Macht das Fenster sichtbar.
	 */
	public void show() {
		fr.setExtendedState(JFrame.MAXIMIZED_BOTH);
		fr.setVisible(true);
	}

	/**
	 * Das Hauptprogramm
	 * 
	 * @param args
	 *            Die Kommandozeilenargumente
	 */
	public static void main(String[] args) {
		new JElizaGui();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "fra") {
			String fra = userText.getText();
			Satz antwort = hirn.fragenAntworter.processQuestion(new Satz(fra,
					fra), hirn.re, hirn);
			hirn = antwort.hirn;
			fra = antwort.istAntortAuf;
			String ant = antwort.satzHtml;
			String antPlain = antwort.satzPlain;
			ant = ant.replace("\n", "<br>\n");
			gespraech += "1::".concat(fra).concat("\n").concat("2::").concat(
					antPlain).concat("\n");
			jelizaText.setText("<html><body>"
					+ gespraech.replace("1::", "<font color='red'>").replace(
							"2::", "<font color='green'>").replace("\n",
							"</font><br>\n") + "</body></html>");
			userText.setText("");
			hirn.gefuehlHeute.setFeeling(antwort.gefuehl);
			generateSidebar(fra);
			userText.requestFocus();
			Speech.say(Speech.preprocessor(antPlain
					.replace("          ", " . ")));
		}
		if (e.getActionCommand() == "save") {
			saveTalking();
		}
		if (e.getActionCommand() == "open") {
			openTalking();
		}
		if (e.getActionCommand() == "newtext") {
			scanText();
		}
		if (e.getActionCommand() == "genWissenDatenbank") {
			genWissenDatenbank();
		}
		if (e.getActionCommand() == "addVerb") {
			addVerb();
		}
		if (e.getActionCommand() == "addAdj") {
			addAdj();
		}
		if (e.getActionCommand() == "showPersons") {
			showWords();
		}
		if (e.getActionCommand() == "help") {
			displayHelp();
		}
	}

	/**
	 * Fordert einen Text und ruft scan() auf.
	 */
	private void scanText() {
		new Thread(new Runnable() {
			public void run() {
				final Dialog dia = new Dialog(fr, "Bekannte Personen / Dinge");
				dia.setLayout(new BorderLayout(10, 10));
				final JTextArea ar = new JTextArea(
						"Bitte Text Eingeben.\nVerwende bitte kurze und "
								+ "eindeutige Saetze.\n"
								+ "Vermeide Nebensaetze.\n"
								+ "\n"
								+ "Achtung !!!!!! :\n"
								+ "Beachten sie jeden Satz als vollstaendig unabhängig zu anderen.\n"
								+ "\n"
								+ "Also Nicht (!):\n"
								+ "\"Yasmin flog los. Sie stuerzte nach 2 Minuten ab.\"\n"
								+ "Sondern:\n"
								+ "\"Yasmin flog los. Yasmin stuerzte 2 Minuten nach ihrem Start ab.\"\n");
				dia.add(new JScrollPane(ar));
				JButton cl = new JButton("Scannen");
				cl.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String text = ar.getText();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						ar.setEditable(false);
						ar.setText("Bitte warten ...");
						scan(text);
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						dia.setVisible(false);
					}
				});
				dia.add("South", cl);
				dia.setSize(600, 400);
				dia.setVisible(true);
			}
		}).start();
	}

	/**
	 * Scannt einen Text nach Informationen und Speichert sie ab.
	 */
	private void scan(String text) {
		ArrayList<String[]> al = new TextAnalyser().analyse(text);
		for (String[] t : al) {
			System.out.println(t[0] + " - " + t[1] + " " + t[2] + " " + t[3]);
			String txt = "";
			txt += "Ist folgende Information richtig?\n\n";
			if (!t[0].contains("ques")) {
				txt += "Subjekt : " + t[1] + "\n";
				txt += "Verb(en) : " + t[2] + "\n";
				txt += "Objekt/Rest : " + t[3] + "\n";
				txt += "\n";
				txt += "Also:\n";
				txt += t[1] + " " + t[2] + " " + t[3] + " .";
				txt += "\n\nKlick auf Ja, falls du diese Information zu meiner Datenbank hinzufuegen willst.";
				if (JOptionPane.showConfirmDialog(fr, txt, "Ist das richtig?",
						JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
					if (t[3] == "") {
						t[3] = "null";
					}
					File f = new File("wortschatz/" + t[0] + "/" + t[1] + "/"
							+ t[2] + "/" + t[3]);
					f.getParentFile().mkdirs();
					try {
						FileManager.writeStringIntoFile("true", f.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				txt += "Subjekt : " + t[1] + "\n";
				txt += "Verb(en) : " + t[2] + "\n";
				txt += "Objekt/Rest : " + t[3] + "\n";
				txt += "\n";
				txt += "Also:\n";
				txt += t[2] + " " + t[1] + " " + t[3] + " ?";
				txt += "\n\nFalls du diese Information zu meiner Datenbank hinzufuegen willst, klicke"
						+ "auf Ja oder Nein, je nach dem, ob sie stimmt.\n"
						+ "Falls du diese Information verwerfen willst, klicke auf Abbrechen.";
				int i = JOptionPane.showConfirmDialog(fr, txt,
						"Ist das richtig?", JOptionPane.YES_NO_CANCEL_OPTION);
				if (i == JOptionPane.YES_OPTION || i == JOptionPane.NO_OPTION) {
					if (t[3] == "") {
						t[3] = "null";
					}
					File f = new File("wortschatz/" + t[0] + "/" + t[1] + "/"
							+ t[2] + "/" + t[3]);
					f.getParentFile().mkdirs();
					try {
						if (i == JOptionPane.YES_OPTION) {
							FileManager.writeStringIntoFile("true", f
									.toString());
						}
						if (i == JOptionPane.NO_OPTION) {
							FileManager.writeStringIntoFile("false", f
									.toString());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Zeigt einen Dialog mit einer Liste von bekannten Personen/Dingen an.
	 */
	private void showWords() {
		new Thread(new Runnable() {
			public void run() {
				dia.setSize(300, 300);
				dia.setLocation(0, 0);
				dia.setVisible(true);

				dia2.setSize(300, 300);
				dia2.setLocation(0, 340);
				dia2.setVisible(true);

				dia3.setSize(300, 300);
				dia3.setLocation(310, 0);
				dia3.setVisible(true);

				dia4.setSize(300, 300);
				dia4.setLocation(310, 340);
				dia4.setVisible(true);
			}
		}).start();
	}

	/**
	 * Zeigt einen Dialog mit einer Liste von bekannten Personen/Dingen an.
	 */
	private void prepareShowWords() {
		dia = new Dialog(fr, "Bekannte Personen");
		dia.setLayout(new BorderLayout(10, 10));
		Box b = new Box(BoxLayout.Y_AXIS);
		File f = new File("personen" + File.separator);
		String[] ps = f.list();
		for (String p : ps) {
			b.add(new JLabel(p));
		}
		dia.add(new JScrollPane(b));
		JButton cl = new JButton("OK");
		cl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dia.setVisible(false);
			}
		});
		dia.add("South", cl);
		dia.setSize(400, 400);
		dia.setLocation(0, 0);
		System.gc();

		dia2 = new Dialog(fr, "Bekannte Nomen");
		dia2.setLayout(new BorderLayout(10, 10));
		Box b2 = new Box(BoxLayout.Y_AXIS);
		VerbDataBase vdb = null;
		System.out.println("---- Generating Verb Database ----");
		try {
			vdb = new VerbDataBase();
			vdb.loadFromFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Object[] arr = vdb.nomen.keySet().toArray();
		Arrays.sort(arr);
		System.gc();
		for (Object p2 : arr) {
			b2.add(new JLabel((String) p2));
		}
		dia2.add(new JScrollPane(b2));
		JButton cl2 = new JButton("OK");
		cl2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dia2.setVisible(false);
			}
		});
		dia2.add("South", cl2);
		dia2.setSize(400, 400);
		dia2.setLocation(0, 410);
		System.gc();

		dia3 = new Dialog(fr, "Bekannte Verben");
		dia3.setLayout(new BorderLayout(10, 10));
		Box b3 = new Box(BoxLayout.Y_AXIS);
		Object[] arr3 = vdb.verbs.keySet().toArray();
		Arrays.sort(arr3);
		System.gc();
		for (Object p3 : arr3) {
			b3.add(new JLabel((String) p3));
		}
		dia3.add(new JScrollPane(b3));
		JButton cl3 = new JButton("OK");
		cl3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dia3.setVisible(false);
			}
		});
		dia3.add("South", cl3);
		dia3.setSize(400, 400);
		dia3.setLocation(410, 0);
		System.gc();

		dia4 = new Dialog(fr, "Bekannte Adjektive");
		dia4.setLayout(new BorderLayout(10, 10));
		Box b4 = new Box(BoxLayout.Y_AXIS);
		Object[] arr4 = vdb.adj.keySet().toArray();
		Arrays.sort(arr4);
		System.gc();
		for (Object p4 : arr4) {
			b4.add(new JLabel((String) p4));
		}
		dia4.add(new JScrollPane(b4));
		JButton cl4 = new JButton("OK");
		cl4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dia4.setVisible(false);
			}
		});
		dia4.add("South", cl4);
		dia4.setSize(400, 400);
		dia4.setLocation(410, 410);
		System.gc();
	}

	/**
	 * Generates a JMenuBar
	 */
	private JMenuBar genJMenuBar() {
		JMenuBar jmb = new JMenuBar();

		JMenu file = new JMenu("Gespräch");
		Util.mkJMenuItem(file, "Speichern ...", this, "save");
		Util.mkJMenuItem(file, "Öffnen ...", this, "open");
		jmb.add(file);

		JMenu wissen = new JMenu("Wissens-Datenbank");
		Util.mkJMenuItem(wissen, "Bekannte Wörter anzeigen", this,
				"showPersons");
		Util.mkJMenuItem(wissen, "-", this, "open");
		Util.mkJMenuItem(wissen, "Informationen aus Text beibringen", this,
				"newtext");
		Util.mkJMenuItem(wissen, "Adjektiv beibringen", this, "addAdj");
		Util.mkJMenuItem(wissen, "Verb beibringen", this, "addVerb");
		Util.mkJMenuItem(wissen, "-", this, "open");
		Util.mkJMenuItem(wissen, "Datenbank generieren", this,
				"genWissenDatenbank");
		jmb.add(wissen);

		JMenu help = new JMenu("?");
		Util.mkJMenuItem(help, "Über", this, "help");
		jmb.add(help);

		return jmb;
	}

	/**
	 * Displays a Help dialog
	 */
	private void displayHelp() {
		String version;
		try {
			version = FileManager.readFileIntoString("version.ver");
		} catch (HeadlessException e) {
			version = "unknown";
		} catch (IOException e) {
			version = "unknown";
		}
		JOptionPane.showMessageDialog(fr, "JEliza\n" + "Version " + version
				+ "\n" + "\n" + "Copyright 2006 by Tobias Schulz\n"
				+ "License: " + "GNU Lesser General Public License (LGPL)",
				"JEliza - Help!", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Speichert das Gespraech.
	 */
	private synchronized void saveTalking() {
		JFileChooser fc = new JFileChooser();
		fc.showSaveDialog(fr);
		if (fc.getSelectedFile() == null
				|| fc.getSelectedFile().toString() == "") {
			return;
		}
		String file = fc.getSelectedFile() + "";
		if (!file.endsWith(".gsp")) {
			file += ".gsp";
		}
		try {
			FileManager.writeStringIntoFile(gespraech, file);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(fr, "Konnte Gespraech nicht nach "
					+ file + " speichern.");
			return;
		}
		JOptionPane.showMessageDialog(fr, "Gespraech nach " + file
				+ " gespeichert.");
		fr.repaint();
	}

	/**
	 * Öffnet ein Gespraech.
	 */
	private synchronized void openTalking() {
		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(fr);
		if (fc.getSelectedFile() == null
				|| fc.getSelectedFile().toString() == "") {
			return;
		}
		try {
			gespraech = FileManager.readFileIntoString(fc.getSelectedFile()
					+ "");
		} catch (IOException e) {
			gespraech = "Konnte Gespraech laden.";
			JOptionPane.showMessageDialog(fr, "Konnte Gespraech nicht von "
					+ fc.getSelectedFile() + " laden.");
			return;
		}
		JOptionPane.showMessageDialog(fr, "Gespraech von "
				+ fc.getSelectedFile() + " geladen.");
		jelizaText.setText("<html><body>"
				+ gespraech.replace("1::", "<font color='red'>").replace("2::",
						"<font color='green'>").replace("\n", "</font><br>\n")
				+ "</body></html>");
		fr.repaint();
	}

	/**
	 * Fügt Verben hinzu.
	 */
	private synchronized void addVerb() {
		String verbs = JOptionPane
				.showInputDialog(
						fr,
						"Verben ?\nbitte in allen Formen angeben, dh. z.b."
								+ "nicht nur 'sein', sondern auch 'bin', 'bist', 'ist', 'sind', 'seid', 'war'");
		if (verbs == null || verbs == "") {
			return;
		}
		verbs = Util.replace(verbs, "ä", "ae");
		verbs = Util.replace(verbs, "ö", "oe");
		verbs = Util.replace(verbs, "ü", "ue");
		verbs = Util.replace(verbs, "ß", "ss");
		verbs = Util.toASCII(verbs);

		Scanner sc = new Scanner(verbs);
		while (sc.hasNext()) {
			String tmp = sc.next();
			if (tmp.endsWith("en")) {
				verbs += " " + tmp.substring(0, tmp.length() - 2) + "e";
				verbs += " " + tmp.substring(0, tmp.length() - 2) + "st";
			}
		}
		try {
			FileManager.writeStringIntoFile(FileManager
					.readFileIntoString("knownVerbs.txt")
					+ " " + verbs, "knownVerbs.txt");
		} catch (IOException e) {
			neuWissen = "";
			JOptionPane.showMessageDialog(fr, "IO-Error: knownVerbs.txt");
			return;
		}

		JOptionPane.showMessageDialog(fr, "Verben hinzugefuegt.");
		fr.repaint();
	}

	/**
	 * Fügt Verben hinzu.
	 */
	private synchronized void addAdj() {
		String adj = JOptionPane
				.showInputDialog(
						fr,
						"Adjektive ?\nbitte in allen Formen angeben, dh. z.b."
								+ "nicht nur 'schön', sondern 'schön', 'schöner' und 'schönsten'");
		if (adj == null || adj == "") {
			return;
		}
		adj = Util.replace(adj, "ä", "ae");
		adj = Util.replace(adj, "ö", "oe");
		adj = Util.replace(adj, "ü", "ue");
		adj = Util.replace(adj, "ß", "ss");
		adj = Util.replace(adj, "am", " ");
		adj = Util.replace(adj, "  ", " ");
		adj = Util.toASCII(adj);

		Scanner sc = new Scanner(adj);
		while (sc.hasNext()) {
			String tmp = sc.next();
			adj += " " + tmp + "er";
			adj += " " + tmp + "sten";
		}
		try {
			FileManager.writeStringIntoFile(FileManager
					.readFileIntoString("knownAdj.txt")
					+ " " + adj, "knownAdj.txt");
		} catch (IOException e) {
			neuWissen = "";
			JOptionPane.showMessageDialog(fr, "IO-Error: knownAdj.txt");
			return;
		}

		JOptionPane.showMessageDialog(fr, "Adjektive hinzugefuegt.");
		fr.repaint();
	}

	/**
	 * Fügt Texte dem Wissen hinzu.
	 */
	private synchronized void genWissenDatenbank() {
		prepareShowWords();
		VerbDataBase vdb = null;
		System.out.println("---- Generating Verb Database ----");
		try {
			vdb = new VerbDataBase("text.vdb");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("---- Writing it into verbs.txt ----");
		vdb.writeIntoFile();
		JOptionPane.showMessageDialog(fr, "Habe Datenbank neu generiert.");
		fr.repaint();
	}
	
	private void sleep(long x) {
		try {
			Thread.sleep(x);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

} // class JEliza
