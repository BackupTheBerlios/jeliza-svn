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
 * versteht (verstehen sollte) Diese Version ist eine Standalone-version (TomCat
 * wird nicht benötigt), daher der Name JElizaGui.
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

	/**
	 * Der Standard-Konstruktor
	 */
	public JElizaGui() {
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

		show();

		userText.requestFocus();

		fr.addWindowListener(new WindowAdapter() {

			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});
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

		/*
		 * JButton newtext = new JButton("Texte hinzufuegen");
		 * newtext.addActionListener(this); newtext.setActionCommand("newtext");
		 * sidebar.add(newtext);
		 */

		JButton newverb = new JButton("Verben hinzufuegen");
		newverb.addActionListener(this);
		newverb.setActionCommand("addVerb");
		sidebar.add(newverb);

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
		if (e.getActionCommand() == "showPersons") {
			showPersons();
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
					File f = new File("wortschatz/" + t[0] + "/" + t[1] + "/" + t[2] + "/" + t[3]);
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
				txt += "\n\nFalls du diese Information zu meiner Datenbank hinzufuegen willst, klicke" +
						"auf Ja oder Nein, je nach dem, ob sie stimmt.\n" +
						"Falls du diese Information verwerfen willst, klicke auf Abbrechen.";
				int i = JOptionPane.showConfirmDialog(fr, txt, "Ist das richtig?",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (i == JOptionPane.YES_OPTION || i == JOptionPane.NO_OPTION) {
					if (t[3] == "") {
						t[3] = "null";
					}
					File f = new File("wortschatz/" + t[0] + "/" + t[1] + "/" + t[2] + "/" + t[3]);
					f.getParentFile().mkdirs();
					try {
						if (i == JOptionPane.YES_OPTION) {
							FileManager.writeStringIntoFile("true", f.toString());
						}
						if (i == JOptionPane.NO_OPTION) {
							FileManager.writeStringIntoFile("false", f.toString());
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
	private void showPersons() {
		new Thread(new Runnable() {
			public void run() {
				final Dialog dia = new Dialog(fr, "Bekannte Personen / Dinge");
				dia.setLayout(new BorderLayout(10, 10));
				Box b = new Box(BoxLayout.Y_AXIS);
				File f = new File("personen/");
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
				dia.setVisible(true);
			}
		}).start();
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
		Util.mkJMenuItem(wissen, "Bekannte Personen / Dinge anzeigen", this,
				"showPersons");
		Util.mkJMenuItem(wissen, "-", this, "open");
		Util.mkJMenuItem(wissen, "Informationen aus Text beibringen", this,
				"newtext");
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
	 * Fügt Texte dem Wissen hinzu.
	 */
	private synchronized void addTextWissen() {
		long millis = Calendar.getInstance().getTimeInMillis();
		try {
			FileManager.copyFileBuffered(absoluteUrl + "text.vdb", absoluteUrl
					+ "backup/text.vdb-ms-" + millis);
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(fr, "Konnte bisheriges Wissen "
					+ absoluteUrl + "text.vdb"
					+ " nicht laden. (File not found)");
			return;
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(fr, "Konnte bisheriges Wissen "
					+ absoluteUrl + "text.vdb" + " nicht laden. (IO-Error)");
			return;
		}
		JOptionPane.showMessageDialog(fr, "ACHTUNG: \n " + "\n"
				+ "Es duerfen nur reine Text-Dateien (plain text), "
				+ "die normalerweise mit '.txt' enden, hinzugefuegt werden.\n"
				+ "\n" + "Ansonsten kann das Programm kaputt gehen.");
		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(fr);
		if (fc.getSelectedFile() == null
				|| fc.getSelectedFile().toString() == "") {
			return;
		}
		try {
			neuWissen = FileManager.readFileIntoString(fc.getSelectedFile()
					+ "");
		} catch (IOException e) {
			neuWissen = "";
			JOptionPane.showMessageDialog(fr, "Konnte Text "
					+ fc.getSelectedFile() + " nicht laden.");
			return;
		}

		InputStream is;
		try {
			is = new FileInputStream(absoluteUrl + "backup/text.vdb-ms-"
					+ millis);
			BufferedOutputStream os = new BufferedOutputStream(
					new FileOutputStream(absoluteUrl + "text.vdb"));
			os.write(neuWissen.getBytes());
			int count = 0;
			byte[] b = new byte[256];
			while ((count = is.read(b)) != -1) {
				os.write(b, 0, count);
			}
			is.close();
			os.close();
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(fr, "Konnte Wissen " + absoluteUrl
					+ "text.vdb" + " nicht speichern.");
			return;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(fr, "Konnte Wissen " + absoluteUrl
					+ "text.vdb" + " nicht speichern.");
			return;
		}
		JOptionPane.showMessageDialog(fr, "Wissen " + fc.getSelectedFile()
				+ " hinzugefuegt.");
		fr.repaint();
	}

	/**
	 * Fügt Texte dem Wissen hinzu.
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
	 * Fügt Texte dem Wissen hinzu.
	 */
	private synchronized void genWissenDatenbank() {
		long millis = Calendar.getInstance().getTimeInMillis();
		try {
			FileManager.copyFileBuffered(absoluteUrl + "text.vdb", absoluteUrl
					+ "backup/text.vdb-ms-" + millis);
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(fr, "Konnte bisheriges Wissen "
					+ absoluteUrl + "text.vdb"
					+ " nicht laden. (File not found)");
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(fr, "Konnte bisheriges Wissen "
					+ absoluteUrl + "text.vdb" + " nicht laden. (IO-Error)");
			return;
		}
		VerbDataBase vdb = null;
		System.out.println("---- Generating Verb Database ----");
		try {
			vdb = new VerbDataBase("text.vdb");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("---- Writing it into verbs.txt ----");
		vdb.writeIntoFile("verbs.txt");
		JOptionPane.showMessageDialog(fr, "Habe Datenbank neu generiert.");
		fr.repaint();
	}

} // class JEliza
