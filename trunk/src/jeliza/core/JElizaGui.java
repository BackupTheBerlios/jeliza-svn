package jeliza.core;

import swingwt.awt.*;
import swingwt.awt.event.*;

import java.io.*;
import java.util.*;

import swingwtx.custom.JTaskTrayItem;
import swingwtx.swing.*;
import swingwtx.swing.border.Border;

import jeliza.chat.base.JElizaICQ;
import jeliza.chat.base.JElizaIRC;
import jeliza.io.FileOp;
import jeliza.jwwf.JElizaWissenWortschatzFormat;
import jeliza.util.*;
import jeliza.markov.HiddenMarkovModel;
import jeliza.markovwrapper.*;

import org.homedns.tobiasschulz.io.*;

/**
 * Das Java-Servlet JEliza, ein Programm, welches die Menschliche Sprache
 * versteht (verstehen sollte)
 * 
 * @author Tobias Schulz
 * @version 0.3
 */
public class JElizaGui implements ActionListener {

	String absoluteUrl = "";

	JEliza jel = null;

	String neuWissen = "";

	boolean isQuesAnt = false;

	PrintWriter out;

	JTextField userText;

	JTextArea jelizaText;

	String gespraech = "";

	JFrame fr;

	JPanel sidebar = null;

	JPanel oberSidebar = null;

	public Dialog dia;

	public Dialog dia2;

	public Dialog dia3;

	public Dialog dia4;

	JTabbedPane tabbedPane = null;

	JPanel chatAlone = null;

	JElizaICQ chatIcq = null;

	JElizaIRC chatIrc = null;

	public JMenu profs = null;

	JTaskTrayItem tray = null;

	boolean onlybg = false;

	static final Border empty10Border = BorderFactory.createEmptyBorder(10, 10,
			10, 10);

	static final Border etchedBorder = BorderFactory.createEtchedBorder();

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

		if (System.getProperty("jeliza.name") == null) {
			System.setProperty("jeliza.name", "JEliza");
		}

		JElizaMarkov.chProfile("default");
		System.setProperty("jeliza.user.name", "Benutzer");

		jel = new JEliza();

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

		fr = new JFrame(System.getProperty("jeliza.name", "JEliza")
				+ " version " + version);
		fr.setLayout(new BorderLayout(5, 5));
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		chatIcq = new JElizaICQ(fr);
		chatIrc = new JElizaIRC();
		tabbedPane = new JTabbedPane();
		chatAlone = new JPanel(new BorderLayout(5, 5));
		sidebar = new JPanel(new GridLayout(15, 1, 5, 5));
		oberSidebar = new JPanel(new BorderLayout(5, 5));

		tex.setText("Generiere grafische Oberflaeche ...");

		jelizaText = new JTextArea("");
		jelizaText.setEditable(false);
		jelizaText.setText("");
		jelizaText.setBackground(Color.white);
		jelizaText.setForeground(Color.darkGray);
		chatAlone.add(new JScrollPane(jelizaText), "Center");

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

		chatAlone.add(bottomPanel, "South");

		fr.addWindowListener(new WindowAdapter() {

			public void windowClosed(WindowEvent e) {
				if (!onlybg) {
					System.exit(0);
				}
			}

			public void windowClosing(WindowEvent e) {
				if (!onlybg) {
					System.exit(0);
				}
			}

		});

		tabbedPane.add(System.getProperty("jeliza.name", "JEliza"), chatAlone);
		tabbedPane.add("ICQ Login", chatIcq.panel);
		tabbedPane.add("IRC Login", chatIrc.panel);

		fr.add("Center", tabbedPane);

		profs = new JMenu("Profil");
		initProfileChooser();

		fr.setIconImage(Toolkit.getDefaultToolkit().getImage("jeliza.gif"));

		mkSidebar("");

		tray = new JTaskTrayItem(new ImageIcon("jeliza.gif"), "JEliza",
				genJPopupMenu());
		tray.setVisible(true);

		fr.setLocation(100, 100);
		fr.pack();
		show();
		fr.setSize(800, 500);
		userText.requestFocus();

		win.setVisible(false);
		tex = null;
		win = null;
	}

	private void initProfileChooser() {
		ActionListener al = new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				new Thread(new Runnable() {

					public void run() {
						String pro = ((String) ((JMenuItem) e.getSource())
								.getActionCommand()).replace("standard",
								"default");
						if (pro.trim().toLowerCase().equals("new")) {
							String tmp = JOptionPane.showInputDialog(fr, "Wie soll das Profil heissen?");
							
							// Werte aus : Name des Profils
							String name = tmp;
							System.out.println("A" + name);
							if (name == null || name.trim().length() < 1) {
								return;
							}
							System.out.println("B" + name);
							name = name.trim().toLowerCase().replace(" ", "_");
							System.out.println("C" + name);
							pro = name;
							JElizaMarkov.newProfile(pro);
							JElizaMarkov.chProfile(pro);

							tmp = JOptionPane.showInputDialog(fr, 
									"Geben sie nun JEliza Informationen ueber sich selbst.\n" +
									"(Bsp.: 'Du heisst xxx')", 
							"Du heisst JEliza.\nDu bist ein Programm.\n");

							// Werte aus : Anfangswissen
							String text = tmp;
							
							StringBuffer buffer = new StringBuffer();
							int ch = 0;
							for (int y = 0; y < text.length(); y++) {
								ch = text.charAt(y);
								buffer.append((char) ch);
								if (HiddenMarkovModel.END_CHARS.indexOf((char) ch) >= 0) {
									String sentence = buffer.toString();
									sentence = sentence.replace('\r', ' ');
									sentence = sentence.replace('\n', ' ');
									scan(sentence);
									buffer = new StringBuffer();
								}
							}
							scan(buffer.toString());
						}
						JElizaMarkov.newProfile(pro);
						JElizaMarkov.chProfile(pro);

						JOptionPane.showMessageDialog(null, "Profil '" + pro
								+ "' wurde ausgewaehlt.");

						initProfileChooser();

						gespraech = "";
						jelizaText.setText("");
					}

				}).start();
			}

		};
		profs.removeAll();

		JMenuItem newpr = new JMenuItem("Neues Profil");
		newpr.setActionCommand("new");
		newpr.addActionListener(al);
		profs.add(newpr);
		profs.addSeparator();

		try {
			Iterator it = FileOp.listDirIterator(new File("profiles"));
			while (it.hasNext()) {
				String tmp = ((String) it.next()).trim();
				if (tmp.equalsIgnoreCase(".") || tmp.equalsIgnoreCase("..")
						|| tmp.equalsIgnoreCase("new")) {
					continue;
				}
				if (tmp.trim().length() < 2) {
					continue;
				}
				String temp = tmp.replace("default", "standard");
				JMenuItem item = new JMenuItem(temp);
				item.setActionCommand(temp);
				item.addActionListener(al);
				profs.add(item);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Generiert die Sidebar, die Rechts im Fenster angezeigt wird und Sachen
	 * wie Gefuehle etc. anzeigt.
	 */
	private void mkSidebar(String fra) {
		StringBuffer jeliza = new StringBuffer();

		sidebar = new JPanel(new BorderLayout());
		sidebar.setPreferredSize(new Dimension(150, 300));

		jeliza.append("");
		jeliza
				.append(System.getProperty("jeliza.name")
						+ " ist ein Multiplattform-\nComputerprogramm, "
						+ "welches auf \ns.g. Hidden Markov Modellen \nbasiert und ein Gespaech \nsimulieren kann. ");
		if (System.getProperty("jeliza.name") == "JEliza") {
			jeliza
					.append("\n\n\nMehr Informationen zu JEliza \nfinden sie auf: \n"
							+ "http://jeliza.berlios.de");
		}
		jeliza.append("");
		JTextArea l = new JTextArea(jeliza.toString());
		l.setPreferredSize(new Dimension(250, 350));
		sidebar.add("Center", l);

		fr.setJMenuBar(genJMenuBar());

		oberSidebar.add(sidebar, "North");
		chatAlone.add(oberSidebar, "East");
		chatAlone.repaint();
		fr.repaint();
		oberSidebar.repaint();
		sidebar.repaint();
	}

	/**
	 * Macht das Fenster sichtbar.
	 */
	public void show() {
		// fr.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
			final String antPlain = jel.answerwrapper.processQuestion(fra,
					jel.answerer, jel.mj);
			gespraech += "1::".concat(fra).concat("\n").concat("2::").concat(
					antPlain).concat("\n");
			// String tmp = " ";
			updateJElizaText();
			userText.setText("");
			new Thread(new Runnable() {
				public void run() {
					userText.requestFocus();
				}
			}).start();
		}
		if (e.getActionCommand() == "save") {
			saveTalking();
		}
		if (e.getActionCommand() == "open") {
			openTalking();
		}
		if (e.getActionCommand() == "savewissen") {
			saveWissen();
		}
		if (e.getActionCommand() == "openwissen") {
			openWissen();
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
		if (e.getActionCommand() == "bg") {
			onlybg = true;
			fr.setVisible(false);
		}
		if (e.getActionCommand() == "fg") {
			fr.setVisible(true);
			onlybg = false;
		}
		if (e.getActionCommand() == "help") {
			displayHelp();
		}

	}

	private void updateJElizaText() {
		jelizaText.setText(""
				+ gespraech.replace("1::",
						System.getProperty("jeliza.user.name") + ": ").replace(
						"2::", "JEliza: ").replace("\n", "\n") + "");
	}

	/**
	 * Fordert einen Text und ruft scan() auf.
	 */
	private void scanText() {
		final Dialog dia = new Dialog(fr, "Text eingeben");
		dia.setLayout(new BorderLayout(10, 10));
		final JTextArea ar = new JTextArea("");
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

	/**
	 * Scannt einen Text nach Informationen und Speichert sie ab.
	 */
	private void scan(String text) {
		jel.mj.learn(text);
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
	 * Generates a JMenuBar
	 */
	private JMenuBar genJMenuBar() {
		JMenuBar jmb = new JMenuBar();

		JMenu file = new JMenu("Gespräch");
		Util.mkJMenuItem(file, "Speichern ...", this, "save");
		Util.mkJMenuItem(file, "Öffnen ...", this, "open");
		jmb.add(file);

		jmb.add(profs);

		JMenu wissen = new JMenu("Wissen");
		Util.mkJMenuItem(wissen, "Speichern ...", this, "savewissen");
		Util.mkJMenuItem(wissen, "Öffnen ...", this, "openwissen");
		jmb.add(wissen);

		JMenu help = new JMenu("?");
		Util.mkJMenuItem(help, "Über", this, "help");
		jmb.add(help);

		return jmb;
	}

	/**
	 * Generates a JMenuBar
	 */
	private JPopupMenu genJPopupMenu() {
		JPopupMenu jmb = new JPopupMenu();

		JMenu bgfg = new JMenu("Status");
		Util.mkJMenuItem(bgfg, "Im Hintergrund", this, "bg");
		Util.mkJMenuItem(bgfg, "Im Vordergrund", this, "fg");
		jmb.add(bgfg);

		jmb.add(profs);

		JMenu file = new JMenu("Gespräch");
		Util.mkJMenuItem(file, "Speichern ...", this, "save");
		Util.mkJMenuItem(file, "Öffnen ...", this, "open");
		jmb.add(file);

		JMenu wissen = new JMenu("Wissen");
		Util.mkJMenuItem(wissen, "Speichern ...", this, "savewissen");
		Util.mkJMenuItem(wissen, "Öffnen ...", this, "openwissen");
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
		JOptionPane.showMessageDialog(fr, System.getProperty("jeliza.name",
				"JEliza")
				+ "\n"
				+ "Version "
				+ version
				+ "\n"
				+ "\n"
				+ "Copyright 2006 by Tobias Schulz\n"
				+ "License: "
				+ "GNU Lesser General Public License (LGPL)", System
				.getProperty("jeliza.name", "JEliza")
				+ " - Help!", JOptionPane.PLAIN_MESSAGE);
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
			gespraech = "Konnte Gespraech nicht laden.";
			JOptionPane.showMessageDialog(fr, "Konnte Gespraech nicht von "
					+ fc.getSelectedFile() + " laden.");
			return;
		}
		JOptionPane.showMessageDialog(fr, "Gespraech von "
				+ fc.getSelectedFile() + " geladen.");
		updateJElizaText();
		fr.repaint();
	}

	/**
	 * Speichert das Wissen.
	 */
	private synchronized void saveWissen() {
		JFileChooser fc = new JFileChooser();
		fc.showSaveDialog(fr);
		if (fc.getSelectedFile() == null
				|| fc.getSelectedFile().toString() == "") {
			return;
		}
		String file = fc.getSelectedFile() + "";
		if (!file.endsWith(".jwwf")) {
			file += ".jwwf";
		}
		if (!JElizaWissenWortschatzFormat.save(
				JElizaMarkov.getCurrentProfile(), file)) {
			JOptionPane.showMessageDialog(fr, "Konnte Wissen nicht nach "
					+ file + " speichern.");
			return;
		}
		JOptionPane.showMessageDialog(fr, "Wissen nach " + file
				+ " gespeichert.");
		fr.repaint();
	}

	/**
	 * Öffnet ein Wissen.
	 */
	private synchronized void openWissen() {
		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(fr);
		if (fc.getSelectedFile() == null
				|| fc.getSelectedFile().toString() == "") {
			return;
		}
		if (!JElizaWissenWortschatzFormat.load(
				JElizaMarkov.getCurrentProfile(), fc.getSelectedFile() + "")) {
			JOptionPane.showMessageDialog(fr, "Konnte Wissen nicht von "
					+ fc.getSelectedFile() + " laden.");
			return;
		}
		JOptionPane.showMessageDialog(fr, "Wissen von " + fc.getSelectedFile()
				+ " geladen.");
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

		String[] sc = verbs.replace("\n", "").split(" ");

		for (int x = 0; x < sc.length; x++) {
			String tmp = sc[x];
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

		String[] sc = adj.replace("\n", "").split(" ");

		for (int x = 0; x < sc.length; x++) {
			String tmp = sc[x];
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
		JOptionPane.showMessageDialog(fr, "Habe Datenbank neu generiert.");
		fr.repaint();
	}

	public void sleep(long x) {
		try {
			Thread.sleep(x);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

} // class JEliza
