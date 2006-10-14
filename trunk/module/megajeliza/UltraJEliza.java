package module.megajeliza;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import jeliza.io.FileOp;

import org.homedns.tobiasschulz.apps.jeliza.Util;

/**
 * Creates Babel poetry from user input
 * 
 * @author Will Gorman
 * 
 * @created February 3, 2002
 */
public class UltraJEliza {

	/** used for specifing which brain directory to use */
	protected String directory = "";

	/** remembers previous brain directory */
	protected String last = null;

	UltraModel model = null;

	String[] schWords = { "scheisse", "scheize", "scheise", "mudda", "fuck",
			"fick", "titte", "busen", "brust", "sex", "penis", "pimmel",
			"pimel", "zz", "biz", "how", "you", "that", "what", "talk", "omg",
			"lieb", "english" };

	/**
	 * Constructor for the BabelModule object
	 * 
	 * @param bot
	 */
	public UltraJEliza() {
		init();
	}

	public void init() {
		directory = "./megajeliza/"
				+ System.getProperty("jeliza.profile", "default");
		model = new UltraModel(this);
		model.load_personality(directory);
		
	}

	/**
	 * Describes the usage of the module
	 * 
	 * @return the usage of the module
	 */
	public String help() {
		StringBuffer sb = new StringBuffer();
		sb.append("<B>MEGAHAL</B> commands:\n");
		sb
				.append("<b>megahaladmin save</b> (saves the megahal brain *ADMIN ONLY*)\n");
		sb
				.append("<b>megahaladmin think <i>N</i></b>  (sets the think time to <N> seconds *ADMIN ONLY*)\n");
		return sb.toString();
	}

	/**
	 * 
	 * 
	 * @param buddy
	 * @param query
	 */
	public synchronized String ask(String fra, String ant) {
		init();
		System.out.println("ask");

		fra = fra.replace("ä", "ae");
		fra = fra.replace("ü", "ue");
		fra = fra.replace("ö", "oe");
		fra = fra.replace("ß", "ss");
		fra = fra.replace("&", " und ");
		fra = fra.replace("@", " at ");
		fra = fra.replace(" = ", " ist gleich ");
		fra = fra.replace("  ", " ");
		fra = Util.toASCII(fra);
		fra = fra.trim();

		try {
			String query = fra;
			if (fra.toLowerCase().equals("exit")) {
				System.exit(0);
			} else {
				System.out.println(fra);
				String words = ichToDu(query);
				String output = "";
				boolean isSchWords = false;
				for (int x = 0; x < schWords.length; x++) {
					String w = schWords[x];
					if (query.contains(w)) {
						isSchWords = true;
						break;
					}
				}
				synchronized (model) {
					System.out.println("ask ok");
					output = model.generate_reply(words);
					System.out.println(output);
					if (fra.length() < 80 && fra.length() > 8
							&& !fra.contains(",") && !isSchWords) {
						model.learn(words);
					}
				}

				return output;
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			return ant;
		}
		return "";
	}

	public void debug(String arg0) {

	}

	public boolean progress(String arg0, int arg1, int arg2) {
		return true;
	}

	public boolean warn(String arg0, String arg1) {
		return true;
	}

	public String ichToDu(String ant) {
		ant = " " + ant + " ";
		ant = ant.replace(" Du ", " XDXuX ");
		ant = ant.replace(" Dein", " XDXeXiXnX");
		ant = ant.replace(" Dir ", " XDXiXrX ");
		ant = ant.replace(" Dich ", " XDXiXcXhX ");

		ant = ant.replace(" du ", " XDXuX ");
		ant = ant.replace(" dein", " XDXeXiXnX");
		ant = ant.replace(" dir ", " XDXiXrX ");
		ant = ant.replace(" dich ", " XDXiXcXhX ");

		ant = ant.replace(" ich ", " Du ");
		ant = ant.replace(" mein", " Dein");
		ant = ant.replace(" mir ", " Dir ");
		ant = ant.replace(" mich ", " Dich ");

		ant = ant.replace(" Ich ", " du ");
		ant = ant.replace(" Mein", " dein");
		ant = ant.replace(" Mir ", " dir ");
		ant = ant.replace(" Mich ", " dich ");

		ant = ant.replace(" XDXuX ", " ich ");
		ant = ant.replace(" XDXeXiXnX", " mein");
		ant = ant.replace(" XDXiXrX ", " mir ");
		ant = ant.replace(" XDXiXcXhX ", " mich ");

		return ant.trim();
	}

	public static void newProfile(String p) {
		if (new File("megajeliza/" + p).exists()) {
			return;
		}
		try {
			FileOp.copyDir(new File("megajeliza/new/"), new File("megajeliza/"
					+ p));
		} catch (IOException e) {
			e.printStackTrace();
		}

		JOptionPane.showMessageDialog(null, "Profile '" + p + "' added.");
	}

	public static String getProfilePath(String p) {
		return "megajeliza/" + p;
	}

	public static String getCurrentProfile() {
		return System.getProperty("jeliza.profile", "default");
	}

	public static void chProfile(String p) {
		System.setProperty("jeliza.profile", p);
	}
}