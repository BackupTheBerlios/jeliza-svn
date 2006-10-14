import java.io.*;

import org.homedns.tobiasschulz.apps.jeliza.hirn.Gehirn;
import org.homedns.tobiasschulz.apps.jeliza.hirn.Answerer;
import org.homedns.tobiasschulz.apps.jeliza.hirn.Satz;
import org.homedns.tobiasschulz.util.satzparser.VerbDataBase;

/**
 * Das Java-Servlet JEliza, ein Programm, welches die Menschliche Sprache
 * versteht (verstehen sollte) Diese Version ist eine Standalone-version (TomCat
 * wird nicht benötigt), daher der Name JElizaHome.
 * 
 * @author Tobias Schulz
 * @version 0.3
 */
public class JElizaCgi {

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
	public JElizaCgi(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		PrintStream out = System.out;
		System.setOut(new PrintStream(new OutputStream() {

			public void write(int b) throws IOException {
				
			}
			
		}));
		out.println("Content-type: text/html\n\n");
		hirn = Gehirn.newGehirn(absoluteUrl);
		VerbDataBase vdb = null;
		try {
			vdb = new VerbDataBase();
			vdb.loadFromFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		String fra = "";
		String query = "";
		try {
			query = br.readLine();
			
			String[] ups = query.split("&");
			for (int x = 0; x < ups.length; x++) {
				if (ups[x].startsWith("fra=")) {
					fra = ups[x].substring(4);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		String ant = 
			hirn.fragenAntworter.processQuestion(new Satz(fra, fra), hirn.re, hirn, vdb).satzPlain;
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
		new JElizaCgi(args);
	}


} // class JEliza
