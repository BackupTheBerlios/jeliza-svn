package jeliza.markovwrapper;

import org.homedns.tobiasschulz.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

import jeliza.markov.HiddenMarkovModel;

public class MarkovWrapper {

	public JElizaMarkov uj = null;

	String dir = "";

	HiddenMarkovModel gj = null;

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

	public Connection db = null;

	public Statement dbStatement = null;

	public MarkovWrapper(JElizaMarkov u) {
		this.uj = u;
	}

	private void showTable() {
		ResultSet rs = null;

		try {
			rs = this.executeQuery("SELECT * FROM input ");
			// Get meta data:
			ResultSetMetaData rsmd = rs.getMetaData();
			int i, n = rsmd.getColumnCount();
			// Print table content:
			for (i = 0; i < n; i++)
				System.out.print("+---------------");
			System.out.println("+");
			for (i = 1; i <= n; i++)
				// Attention: first column with 1 instead of 0
				System.out
						.print("| " + extendStringTo14(rsmd.getColumnName(i)));
			System.out.println("|");
			for (i = 0; i < n; i++)
				System.out.print("+---------------");
			System.out.println("+");
			while (rs.next()) {
				for (i = 1; i <= n; i++)
					// Attention: first column with 1 instead of 0
					System.out.print("| " + extendStringTo14(rs.getString(i)));
				System.out.println("|");
			}
			for (i = 0; i < n; i++)
				System.out.print("+---------------");
			System.out.println("+");

			rs = this.executeQuery("SELECT * FROM output ");
			// Get meta data:
			rsmd = rs.getMetaData();
			n = rsmd.getColumnCount();
			// Print table content:
			for (i = 0; i < n; i++)
				System.out.print("+---------------");
			System.out.println("+");
			for (i = 1; i <= n; i++)
				// Attention: first column with 1 instead of 0
				System.out
						.print("| " + extendStringTo14(rsmd.getColumnName(i)));
			System.out.println("|");
			for (i = 0; i < n; i++)
				System.out.print("+---------------");
			System.out.println("+");
			while (rs.next()) {
				for (i = 1; i <= n; i++)
					// Attention: first column with 1 instead of 0
					System.out.print("| " + extendStringTo14(rs.getString(i)));
				System.out.println("|");
			}
			for (i = 0; i < n; i++)
				System.out.print("+---------------");
			System.out.println("+");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Extend String to length of 14 characters
	private static final String extendStringTo14(String s) {
		if (null == s)
			s = "";
		final String sFillStrWithWantLen = "              ";
		final int iWantLen = sFillStrWithWantLen.length();
		final int iActLen = s.length();
		if (iActLen < iWantLen)
			return (s + sFillStrWithWantLen).substring(0, iWantLen);
		if (iActLen > 2 * iWantLen)
			return s.substring(0, 2 * iWantLen);
		return s;
	}

	public ResultSet executeQuery(String str) {
		System.out.println("Executing query : " + str);
		try {
			return this.dbStatement.executeQuery(str);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void load_personality(String dir) {
		this.dir = dir;
		System.out.println("Loading personality " + this.dir + " '"
				+ System.getProperty("jeliza.profile.there.is.a.new.profile")
				+ "'");

		System.setProperty("jeliza.profile.there.is.a.new.profile", "no");

		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			db = DriverManager.getConnection("jdbc:hsqldb:"
					+ JElizaMarkov.getProfilePath(JElizaMarkov
							.getCurrentProfile()) + "/db", "sa", "");
			dbStatement = db.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			this.dbStatement
					.executeQuery("CREATE TABLE sentences ( i INT, s VARCHAR(2000))");
		} catch (SQLException e) {
			// e.printStackTrace();
		}
		try {
			this.dbStatement
					.executeQuery("CREATE TABLE questions ( i INT, s VARCHAR(2000))");
		} catch (SQLException e) {
			// e.printStackTrace();
		}
		try {
			this.dbStatement
					.executeQuery("CREATE TABLE input ( i INT, s VARCHAR(2000))");
		} catch (SQLException e) {
			// e.printStackTrace();
		}
		try {
			this.dbStatement
					.executeQuery("CREATE TABLE output ( i INT, s VARCHAR(2000))");
		} catch (SQLException e) {
			// e.printStackTrace();
		}

		gj = new HiddenMarkovModel();

		ResultSet rs = this.executeQuery("SELECT * FROM input");

		try {
			while (rs.next()) {
				String tmp = rs.getString(2).replace("?", "").replace("!", "")
						.replace(".", "");
				System.out.println("Adding " + tmp);
				gj.add(tmp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		showTable();
	}

	public void save_model(String dir) {

	}

	public void learn(String words) {
		if (!words.contains("?")) {
			this.executeQuery("INSERT INTO input ( i, s ) VALUES ( 100, '"
					+ words + "' )");
			gj.add(words);
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

		double bestKeyCount = 0;

		double bestIndex = 0;

		Random r = new Random();

		ArrayList ants = new ArrayList();

		for (int x = 0; x < ws.length; x++) {
			String out = gj.getSentence(ws[x]);
			System.out.println(ws[x] + " -> " + out);
			ants.add(out);
		}

		for (int x = 0; x < ants.size(); x++) {
			String tmp = " "
					+ ((String) ants.get(x)).replace("?", "").replace("!", "")
							.replace(".", "") + " ";
			if (tmp.trim().length() < 2) {
				continue;
			}
			int w = tmp.trim().split(" ").length;
			int b = tmp.trim().length();
			int userLikeCount = getUserLikeCount((String) ants.get(x));
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

			double index = 0;

			if (b < 1 || w < 1 || goodKeys < 1 || bestKeyCount < 1
					|| userLikeCount < 1) {
				index = 0;
			} else {
				index = (100) * (goodKeys / bestKeyCount)
						* (userLikeCount / 100);
			}

			if (index > bestIndex) {
				bestIndex = index;
			}

			al.add(Double.toString(index) + "#" + tmp.trim());
			System.out.println(Double.toString(index) + "#" + tmp.trim());
		}

		Object[] ants2 = al.toArray();

		String bestNum = Double.toString(bestIndex);

		ants = new ArrayList();

		System.out.println("generate_reply ok3");
		for (int x = 0; x < ants2.length; x++) {
			if (((String) ants2[x]).startsWith(bestNum + "#")) {
				ants.add(((String) ants2[x]).replace(bestNum + "#", "").trim());
				System.out.println(((String) ants2[x]).trim());
			}
		}

		String ant = (String) ants.get(r.nextInt(ants.size()));

		System.setProperty("last.user.satz", words);
		System.setProperty("last.jeliza.satz", ant);

		this.executeQuery("INSERT INTO output ( i, s ) VALUES ( 100, '" + ant
				+ "' )");

		return ant;
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

	public void setUserLikeCount(String words) {
		String lastJEliza = System.getProperty("last.jeliza.satz");

		boolean minusULC = false;

		for (int x = 0; x < JElizaMarkov.schWords.length; x++) {
			if (words.toLowerCase().contains(
					JElizaMarkov.schWords[x].toLowerCase())) {
				minusULC = true;
				break;
			}
		}

		if (minusULC) {
			ResultSet rs = this.executeQuery("SELECT * FROM output WHERE s = '"
					+ lastJEliza + "'");
			try {
				rs.next();
				int i = rs.getInt(1);

				this.executeQuery("UPDATE output SET i = " + (i - 1)
						+ " WHERE s = '" + lastJEliza + "'");

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	public int getUserLikeCount(String last) {
		String lastJEliza = last;

		try {
			ResultSet rs = this.executeQuery("SELECT * FROM output WHERE s = '"
					+ lastJEliza + "'");
			rs.next();
			rs.getString(1);
		} catch (SQLException e) {
			this.executeQuery("INSERT INTO output ( i, s ) VALUES ( 100, '"
					+ last + "' )");
		}

		try {
			ResultSet rs = this.executeQuery("SELECT * FROM output WHERE s = '"
					+ lastJEliza + "'");
			rs.next();
			int i = rs.getInt(1);
			return i;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 100;
	}

}
