package module.megajeliza;

import org.homedns.tobiasschulz.io.*;
import java.io.*;
import java.util.*;

public class UltraModel {

	public UltraJEliza uj = null;

	String dir = "";

	public static String[] noKeys = ("ja nein aber und oder"
			+ " doch der die das ich du er sie es"
			+ " meiner deiner seiner ihrer seiner mir dir"
			+ " ihm ihr ihm mich dich ihn sie es wir"
			+ " ihr sie unser euer ihrer uns euch ihnen"
			+ " uns euch sie mein meine meiner meines"
			+ " meinem meinen dein deine deiner deines deinem"
			+ " deinen sein seine seiner seines seinem seinen"
			+ " ihr ihre ihrer ihres ihrem ihren unser"
			+ " unsere unseres unserem unserer unseren euer"
			+ " eures eueres eurer euerer eure euere euren"
			+ " eueren der die das die dessen deren dessen"
			+ " deren derer dem der dem denen den die"
			+ " das die dieser diese dieses diese dieses"
			+ " dieser dieses dieser diesem dieser diesem"
			+ " diesen diesen diese dieses diese jemand"
			+ " niemand jemandes niemandes jemandem niemandem"
			+ " jemanden niemanden "
			+ " bin bist ist sind seid hab habe hast hat haben "
			+ " was wer wie wo wann warum wieso weshalb"
			+ " ein eine einer eins eines ").split(" ");

	public UltraModel(UltraJEliza u) {
		this.uj = u;
	}

	public void load_personality(String dir) {
		this.dir = dir;
		System.out.println("Loading personality " + this.dir);
		try {
			if (!new File(dir + File.separator + "jeliza.fra.brn").exists()) {
				FileManager.copyFile("megajeliza/new/jeliza.fra.brn", dir
						+ File.separator + "jeliza.fra.brn");
			}
			if (!new File(dir + File.separator + "jeliza.sat.brn").exists()) {
				FileManager.copyFile("megajeliza/new/jeliza.sat.brn", dir
						+ File.separator + "jeliza.sat.brn");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save_model(String dir) {

	}

	public Iterator getSaetze(String type) {
		ArrayList al = new ArrayList();

		if (type.equalsIgnoreCase("?")) {
			try {
				String[] lines = FileManager.readFileIntoString(
						dir + File.separator + "jeliza.fra.brn").replace("\n",
						"").replace("\r", "").trim().split("||");
				String line = "";

				for (int x = 0; x < lines.length; x++) {
					line = lines[x].trim();
					if (line.length() < 2) {
						continue;
					}
					al.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return new ArrayList().iterator();
			}
		} else if (type.equalsIgnoreCase("!")) {
			try {
				String[] lines = FileManager.readFileIntoString(
						dir + File.separator + "jeliza.sat.brn").trim().split(
						"\\|\\|");
				String line = "";

				for (int x = 0; x < lines.length; x++) {
					line = lines[x].trim();
					System.out.println(line);
					if (line.length() < 2) {
						continue;
					}
					al.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return new ArrayList().iterator();
			}
		}

		return al.iterator();
	}

	public void learn(String words) {
		try {
			String file = "";
			if (isQues(words)) {
				file = dir + File.separator + "jeliza.fra.brn";
			} else {
				file = dir + File.separator + "jeliza.sat.brn";
			}
			FileManager.writeStringIntoFile(FileManager
					.readFileIntoString(file)
					+ "||" + words.trim(), file);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public String generate_reply(String words) {
		System.out.println("generate_reply");
		ArrayList al = new ArrayList();

		String[] ws = words.replace("?", "").replace("!", "").replace(".", "")
				.split(" ");
		ArrayList keys = new ArrayList();
		for (int x = 0; x < ws.length; x++) {
			if (isKeyWord(ws[x])) {
				keys.add(ws[x]);
			}
		}
		System.out.println("generate_reply ok1");

		int bestKeyCount = 0;
		Iterator it = null;
		it = getSaetze("!");
		while (it.hasNext()) {
			String tmp = " " + (String) it.next() + " ";
			int goodKeys = 0;
			Iterator i = keys.iterator();
			while (i.hasNext()) {
				String k = (String) i.next();
				if (tmp.toLowerCase().contains(" " + k.toLowerCase() + " ")) {
					goodKeys++;
					if (goodKeys > bestKeyCount) {
						bestKeyCount = goodKeys;
					}
				}
			}

			al.add(Integer.toString(goodKeys) + "#" + tmp.trim());
			System.out.println(Integer.toString(goodKeys) + "#" + tmp.trim());
		}

		System.out.println("generate_reply ok2");
		Random r = new Random();

		if (al.size() == 0) {
			return "";
		}

		Object[] ants2 = al.toArray();

		String bestNum = Integer.toString(bestKeyCount);

		ArrayList ants = new ArrayList();

		System.out.println("generate_reply ok3");
		for (int x = 0; x < ants2.length; x++) {
			if (((String) ants2[x]).startsWith(bestNum + "#")) {
				ants.add(((String) ants2[x]).replace(bestNum + "#", "").trim());
				System.out.println(((String) ants2[x]).trim());
			}
		}

		return (String) ants.get(r.nextInt(ants.size()));
	}

	public boolean isQues(String line) {
		if (line.trim().endsWith("?") || line.contains("wie ")
				|| line.contains("was ") || line.contains("wer ")
				|| line.contains("wo ") || line.contains("wann")
				|| line.contains("warum") || line.contains("wieso")
				|| line.contains("weshalb") || line.contains("welch")) {
			return true;
		}
		return false;
	}

	public boolean isKeyWord(String key) {
		for (int x = 0; x < noKeys.length; x++) {
			if (noKeys[x].length() == 0) {
				continue;
			}
			if (key.toLowerCase().trim().equalsIgnoreCase(
					noKeys[x].toLowerCase().trim())) {
				return false;
			}
		}

		return true;
	}

}
