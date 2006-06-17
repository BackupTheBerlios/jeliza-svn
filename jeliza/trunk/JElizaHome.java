import java.io.*;
import org.homedns.tobiasschulz.apps.jeliza.*;

/**
 * Das Java-Servlet JEliza, ein Programm, welches die Menschliche Sprache
 * versteht (verstehen sollte) Diese Version ist eine Standalone-version (TomCat
 * wird nicht benötigt), daher der Name JElizaHome.
 * 
 * @author Tobias Schulz
 * @version 0.2
 */
public class JElizaHome {

	String oldFra = "";

	String oldAnt = "";

	String oldObj = "";

	boolean isQuesAnt = false;

	Regeln re;

	PrintWriter out;

	Gehirn hirn = new Gehirn();

	String outBuf = "";

	String outAll = "";

	FragenAntworter fragenAntworter = new FragenAntworter();
	
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
	public JElizaHome() {
		re = new Regeln();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("JEliza: Hallo, wie ist dein Name? Antworte bitte in ganzen Sätzen!");
		System.out.print("Mensch: ");

		String line = "";
		try {
			while((line = br.readLine()) != null) {
				String fra = line;
				String ant = fragenAntworter.processQuestion(fra, re, hirn);
				System.out.println("JEliza: " + ant);
				System.out.print("Mensch: ");
			}
		} catch (IOException e) {
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
		new JElizaHome();
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

} // class JEliza
