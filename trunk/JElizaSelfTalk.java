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
public class JElizaSelfTalk {

	String absoluteUrl = "";

	PrintWriter out;

	Gehirn hirn1 = Gehirn.newGehirn(absoluteUrl);

	Gehirn hirn2 = Gehirn.newGehirn(absoluteUrl);

	/**
	 * Der Standard-Konstruktor
	 */
	public JElizaSelfTalk() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		PrintStream out = System.out;
		
		System.setOut(new PrintStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				// Verwerfen
			}
			
		}));

		out.print("Bitte Anfangssatz eingeben: ");
		String anf = null;
		try {
			anf = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (anf == null) {
			return;
		}
		if (anf.trim() == "") {
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
		while (true) {
			if (n == 1) {
				String ant = hirn1.fragenAntworter.processQuestion(new Satz(
						satzVon2, satzVon2), hirn1.re, hirn1, vdb).satzPlain;
				out.println("JEliza 1: " + ant);
				satzVon1 = ant;
				n = 2;
				sleep();
			}
			if (n == 2) {
				String ant = hirn2.fragenAntworter.processQuestion(new Satz(
						satzVon1, satzVon1), hirn2.re, hirn2, vdb).satzPlain;
				out.println("JEliza 2: " + ant);
				satzVon2 = ant;
				n = 1;
				sleep();
			}
		}

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
		new JElizaSelfTalk();
	}

} // class JEliza
