import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

import org.homedns.tobiasschulz.apps.jeliza.*;

/**
 * Das Java-Servlet JEliza, ein Programm, welches die Menschliche Sprache
 * versteht (verstehen sollte) Diese Version ist eine Standalone-version (TomCat
 * wird nicht benötigt), daher der Name JElizaHome.
 * 
 * @author Tobias Schulz
 * @version 0.3
 */
public class JElizaGui implements ActionListener {

	String absoluteUrl = "";

	String oldFra = "";

	String oldAnt = "";

	String oldObj = "";

	boolean isQuesAnt = false;

	Regeln re;

	PrintWriter out;

	Gehirn hirn = new Gehirn(absoluteUrl);

	String outBuf = "";

	String outAll = "";

	FragenAntworter fragenAntworter = new FragenAntworter();

	JTextField userText;

	JEditorPane jelizaText;

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
			System.out.println("Kann UIManager nicht instanzieren ...");
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

		JFrame fr = new JFrame();
		fr.setLayout(new BorderLayout(5, 5));
		fr.setBackground(Color.darkGray);
		fr.setForeground(Color.white);

		jelizaText = new JEditorPane("text/html", "");
		jelizaText.setEditable(false);
		jelizaText.setText("<html><body>"
				+ "Hallo!, <br>\n<br>\n"
				+ "Ich heisse JEliza und bin ein Computerprogramm. <br>\n"
				+ "Ich bin Anfang Juni 2006 \"geboren\" worden (zu dieser Zeit<br>\n"
				+ "gab es mich zu ersten mal zum Download).<br>\n" + "<br>\n"
				+ "Ich wuerde mich gerne mit dir etwas unterhalten!<br>\n"
				+ "Bitte benutze leichte Woerter und erwarte nicht<br>\n"
				+ "zu viel von mir.<br>\n" + "<br>\n"
				+ "Ach ja! Bitte Antworte IMMER in ganzen Saetzen.<br>\n"
				+ "<br>\n" + "Wie ist dein Name?"
				+ "</body></html>");
		jelizaText.setBackground(Color.darkGray);
		jelizaText.setForeground(Color.white);
		fr.add(new JScrollPane(jelizaText), "Center");

		JPanel userPanel = new JPanel(new BorderLayout(10, 10));

		userText = new JTextField();
		userText.setText("");
		userText.addActionListener(this);
		userPanel.add(userText, "Center");

		JButton senden = new JButton("Fragen");
		senden.addActionListener(this);
		userPanel.add(senden, "East");

		fr.add(userPanel, "South");

		re = new Regeln(absoluteUrl);

		fr.setSize(500, 400);
		fr.setVisible(true);

		userText.requestFocus();
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

	/**
	 * Methode zum Beantworten der Frage des Users.
	 * 
	 * @see doGet
	 * @param fra
	 *            Die Frage
	 * @return Die Antwort
	 */
	public String processQuestion(String fra) {
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
			oldFra = fra;
			oldAnt = ant;

			return ant;
		}
		return null;
	}

	public void actionPerformed(ActionEvent e) {
		String fra = userText.getText();
		String ant = fragenAntworter.processQuestion(fra, re, hirn);
		ant = ant.replace("\n", "<br>\n");
		jelizaText.setText("<html><body>" + ant + "</body></html>");
		userText.setText("");
		userText.requestFocus();
	}

} // class JEliza
