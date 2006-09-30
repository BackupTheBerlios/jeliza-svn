import java.awt.*;
import java.io.*;

import javax.swing.*;

import org.homedns.tobiasschulz.apps.jeliza.hirn.*;
import org.homedns.tobiasschulz.io.FileManager;
import org.homedns.tobiasschulz.util.satzparser.VerbDataBase;

/**
 * Das Java-Servlet JEliza, ein Programm, welches die Menschliche Sprache
 * versteht (verstehen sollte) Diese Version ist eine Standalone-version (TomCat
 * wird nicht benötigt), daher der Name JElizaHome.
 * 
 * @author Tobias Schulz
 * @version 0.3
 */
public class JElizaSelfTalkGui {

	String absoluteUrl = "";

	PrintWriter out;

	Gehirn hirn1 = Gehirn.newGehirn(absoluteUrl);

	Gehirn hirn2 = Gehirn.newGehirn(absoluteUrl);

	JFrame fr;

	String gespraech = "";
	
	JEditorPane jep;

	/**
	 * Der Standard-Konstruktor
	 */
	public JElizaSelfTalkGui() {
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

		fr = new JFrame("Gespräch: JEliza 1 <-> JEliza 2");
		fr.setLayout(new BorderLayout(5, 5));
		fr.setBackground(Color.white);
		fr.setForeground(Color.darkGray);
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jep = new JEditorPane();
		jep.setContentType("text/html");
		jep.setEditable(false);
		fr.add("Center", new JScrollPane(jep));

		fr.setSize(400, 400);
		fr.setVisible(true);

		PrintStream out = System.out;

		System.setOut(new PrintStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				// Verwerfen
			}

		}));

		String anf = null;
		anf = JOptionPane.showInputDialog("Bitte Anfangssatz eingeben:");
		if (anf == null) {
			return;
		}
		if (anf.trim().hashCode() == "".hashCode() || anf.length() < 2) {
			anf = "Wie heisst du?";
		} else {
			anf = anf.trim();
		}

		VerbDataBase vdb = null;
		try {
			vdb = new VerbDataBase();
			vdb.loadFromFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		int n = 2;
		String satzVon1 = anf;
		String satzVon2 = "";
		out.println("JEliza 1: " + satzVon1);
		gespraech += "1::".concat(satzVon1).concat("\n");
		genText();
		while (true) {
			if (n == 1) {
				String ant = hirn1.fragenAntworter.processQuestion(new Satz(
						satzVon2, satzVon2), hirn1.re, hirn1, vdb).satzPlain;
				out.println("JEliza 1: " + ant);
				satzVon1 = ant;
				n = 2;
				gespraech += "1::".concat(ant).concat("\n");
			} else if (n == 2) {
				String ant = hirn2.fragenAntworter.processQuestion(new Satz(
						satzVon1, satzVon1), hirn2.re, hirn2, vdb).satzPlain;
				out.println("JEliza 2: " + ant);
				satzVon2 = ant;
				n = 1;
				gespraech += "2::".concat(ant).concat("\n");
			}
			genText();
			jep.repaint();
			sleep();
		}

	}
	
	private void genText() {
		jep.setText("<html><body>"
				+ gespraech.replace("1::", "JEliza 1: <font color='red'>")
						.replace("2::", "JEliza 2: <font color='green'>")
						.replace("\n", "</font><br>\n") + "</body></html>");
	}

	private void sleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Das Hauptprogramm
	 * 
	 * @param args
	 *            Die Kommandozeilenargumente
	 */
	public static void main(String[] args) {
		new JElizaSelfTalkGui();
	}

} // class JEliza
