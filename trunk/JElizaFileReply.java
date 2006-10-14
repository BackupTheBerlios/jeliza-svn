import java.io.*;

import org.homedns.tobiasschulz.apps.jeliza.hirn.Gehirn;
import org.homedns.tobiasschulz.apps.jeliza.hirn.Answerer;
import org.homedns.tobiasschulz.apps.jeliza.hirn.Satz;
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
public class JElizaFileReply {

	String absoluteUrl = "";

	PrintWriter out;

	Gehirn hirn = Gehirn.newGehirn(absoluteUrl);

	/**
	 * Der Standard-Konstruktor
	 */
	public JElizaFileReply() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		VerbDataBase vdb = null;
		try {
			vdb = new VerbDataBase();
			vdb.loadFromFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		File f = new File("reply.fra");
		while (true) {
			if (!f.exists()) {
				sleep();
				continue;
			}
			String fra;
			try {
				fra = FileManager.readFileIntoString("reply.fra").trim();
				f.delete();
				if (fra.length() < 2) {
					continue;
				}

				String ant = hirn.fragenAntworter.processQuestion(new Satz(fra,
						fra), hirn.re, hirn, vdb).satzPlain;
				FileManager.writeStringIntoFile(ant, "reply.ant");
				new File("canGetAnswer.reply").createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
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
		new JElizaFileReply();
	}

} // class JEliza
