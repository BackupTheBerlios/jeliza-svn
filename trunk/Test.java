import java.io.*;

import org.homedns.tobiasschulz.apps.jeliza.hirn.Gehirn;
import org.homedns.tobiasschulz.apps.jeliza.hirn.Answerer;
import org.homedns.tobiasschulz.apps.jeliza.hirn.Satz;
import org.homedns.tobiasschulz.util.satzparser.VerbDataBase;
import org.jibble.jmegahal.JMegaHal;

/**
 * Das Java-Servlet JEliza, ein Programm, welches die Menschliche Sprache
 * versteht (verstehen sollte) Diese Version ist eine Standalone-version (TomCat
 * wird nicht benötigt), daher der Name JElizaHome.
 * 
 * @author Tobias Schulz
 * @version 0.3
 */
public class Test {

	String absoluteUrl = "";

	String oldFra = "";

	String oldAnt = "";

	String oldObj = "";

	boolean isQuesAnt = false;

	PrintWriter out;

	Gehirn hirn = null;

	String outBuf = "";

	String outAll = "";

	/**
	 * Mit der Methode println wird ein String, den JEliza sagt, in den
	 * Ausgabepuffer geschrieben. Die Methode printIt senden dann den
	 * Ausgabepuffer!
	 * 
	 * @see printIt
	 * @param str
	 *            Der String, den JEliza sagt
	 */
	void println(String str) {
		outBuf += "JEliza: <font color=\"red\">" + str + "</font><br>" + "\n";
	}

	/**
	 * Mit der Methode println wird ein String, den der Benutzer, der mit JEliza
	 * spricht, sagt, in den Ausgabepuffer geschrieben. Die Methode printIt
	 * senden dann den Ausgabepuffer!
	 * 
	 * @see printIt
	 * @param str
	 *            Der String, den der User sagt
	 */
	void userSayln(String str) {
		outBuf += "Mensch: <font color=\"green\">" + str + "</font><br>" + "\n";
	}

	/**
	 * Der Standard-Konstruktor
	 */
	public Test(String[] args) {
		PrintStream out = System.out;
		System.setOut(new PrintStream(new OutputStream() {

			public void write(int b) throws IOException {

			}

		}));
		JMegaHal hal = new JMegaHal();

		// We need to teach it a few things first...
		hal.add("Hello, my name is Paul.");
		hal.add("This is a sentence.");
		hal.add("Bin ich schrecklich?");
		hal.add("Linux ist cool");
		hal.add("Du bist du");
		// (The more sentences we add, the more
		// sense we will get out of it!)

		String fra = args[0];
		String ant = hal.getSentence();
		out.println(ant);
		System.exit(0);
	}

	/**
	 * Das Hauptprogramm
	 * 
	 * @param args
	 *            Die Kommandozeilenargumente
	 */
	public static void main(String[] args) {
		new Test(args);
	}

} // class JEliza
