package jeliza.markovwrapper;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import jeliza.io.FileOp;

import jeliza.util.Util;

/**
 * Creates Babel poetry from user input
 * 
 * @author Will Gorman
 * 
 * @created February 3, 2002
 */
public class JElizaMarkov {

	/** used for specifing which brain directory to use */
	protected String directory = "";

	/** remembers previous brain directory */
	protected String last = null;

	public MarkovWrapper model = null;

	String curProf = "";

	public static String[] schWords = { "scheisse", "scheize", "scheise",
			"mudda", "fuck", "fick", "titte", "busen", "brust", "sex", "penis",
			"pimmel", "pimel", "zz", "biz", "how", "you", "that", "what",
			"talk", "omg", "lieb", "english", "missgeburt" };

	/**
	 * Constructor for the BabelModule object
	 * 
	 * @param bot
	 */
	public JElizaMarkov() {
		model = new MarkovWrapper(this);
		init();
	}

	public void init() {
		directory = "./profiles/"
				+ System.getProperty("jeliza.profile", "default");
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
				output = model.generate_reply(words);
				System.out.println(output);

				learn(query);

				if (output != null) {
					return format(output);
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			return ant;
		}
		return ant;
	}

	/**
	 * 
	 * 
	 * @param buddy
	 * @param query
	 */
	public synchronized void learn(String query) {
		init();
		String words = ichToDu(query);
		boolean isSchWords = false;
		for (int x = 0; x < schWords.length; x++) {
			String w = schWords[x];
			if (query.contains(w)) {
				isSchWords = true;
				break;
			}
		}

		model.setUserLikeCount(words);
		System.out.println("Try Learning " + words);
		System.out.println("asking ok");
		if (!isSchWords) {
			System.out.println("Learning " + words);
			model.learn(words);
		}

		return;
	}

	public static String format(String output) {
		output = output.replace("!", ".");
		output = output.replace(";", "");
		output = output.replace(")", "");
		output = output.replace("(", "");
		output = output.replace("[", "");
		output = output.replace("]", "");
		output = output.replace("{", "");
		output = output.replace("}", "");
		output = output.replace("  ", " ");
		output = output.toLowerCase();
		output = output.substring(0, 1).toUpperCase() + output.substring(1);
		output = output + ".";

		return output;
	}

	/**
	 * 
	 * 
	 * @param buddy
	 * @param query
	 */
	public synchronized String askOld(String fra, String ant) {
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

				System.out.println("Try Learning " + words);
				if (fra.length() < 80 /* && fra.length() > 8 */
						&& !fra.contains(",") && !isSchWords) {
					System.out.println("Learning " + words);
					model.learn(words);
				}
				System.out.println("asking ok");
				output = model.generate_reply(words);
				System.out.println(output);

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
		if (new File("profiles/" + p).exists()) {
			return;
		}
		try {
			FileOp
					.copyDir(new File("profiles/new/"), new File("profiles/"
							+ p));
		} catch (IOException e) {
			e.printStackTrace();
		}

		JOptionPane.showMessageDialog(null, "Profile '" + p + "' added.");
	}

	public static String getProfilePath(String p) {
		return "profiles/" + p;
	}

	public static String getCurrentProfile() {
		return System.getProperty("jeliza.profile", "default");
	}

	public static void chProfile(String p) {
		System.setProperty("jeliza.profile", p);
		System.setProperty("jeliza.profile.there.is.a.new.profile", "yes");
	}
}