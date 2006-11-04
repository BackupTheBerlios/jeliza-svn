package jeliza.poem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import jeliza.markovwrapper.JElizaMarkov;

public class Gedichte {

	public Connection db = null;

	public Statement dbStatement = null;

	ArrayList sentences = new ArrayList();

	void init(String pro) {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			db = DriverManager.getConnection("jdbc:hsqldb:"
					+ JElizaMarkov.getProfilePath(pro) + "/db", "sa", "");
			dbStatement = db.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

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

	public static void main(String[] args) {
		Gedichte g = new Gedichte();
		ArrayList poem = g.mkPoem(args[0], "poem");
		Iterator po = poem.iterator();
		while (po.hasNext()) {
			System.out.println(po.next());
		}
	}

	public String format(String output) {
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

	public ArrayList mkPoem(String thema, String p) {
		init(p);
		sentences = new ArrayList();
		thema = thema.toLowerCase().replace("ei", "ai").replace("eu", "oi");
		Random r = new Random();
		int len = 2 * (r.nextInt(2) + 1);
		ResultSet rs = this.executeQuery("SELECT * FROM input ");
		String WORD_CHARS = "abcdefghijklmnopqrstuvwxyz"
				+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + " " + "	";
		try {
			while (rs.next()) {
				String txt = rs.getString(2);

				char[] chars = txt.toCharArray();
				int i = 0;
				boolean punctuation = false;
				StringBuffer buffer = new StringBuffer();
				while (i < chars.length) {
					char ch = chars[i];
					if ((WORD_CHARS.indexOf(ch) >= 0) == punctuation) {
						punctuation = !punctuation;
						String token = buffer.toString();
						if (token.length() > 0) {
							sentences.add(token.toLowerCase().replace("ei",
									"ai").replace("eu", "oi"));
						}
						buffer = new StringBuffer();
						// i++;
						continue;
					}
					buffer.append(ch);
					i++;
				}
				String lastToken = buffer.toString();
				if (lastToken.length() > 0) {
					sentences.add(lastToken.toLowerCase().replace("ei", "ai")
							.replace("eu", "oi"));
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList poem = new ArrayList();
		int curr = 0;
		String end = "";

		Iterator it = sentences.iterator();
		while (it.hasNext()) {
			String s = (String) it.next();
			if (s.contains(thema)) {
				if (curr % 2 == 0) {
					end = s.substring(s.length() - 3);
					poem.add(format(s.replace("ai", "ei").replace("oi", "eu")));
					curr++;
					break;
				}
			}
		}
		
		if (poem.size() < 1) {
			return null;
		}
		
		it = sentences.iterator();
		while (it.hasNext()) {
			String s = (String) it.next();
			if (curr % 2 == 0 && s.endsWith(end)) {
				end = s.substring(s.length() - 3);
				poem.add(format(s.replace("ai", "ei").replace("oi", "eu")));
				curr++;
				continue;
			}
			if (curr % 2 == 1 && s.endsWith(end)) {
				poem.add(format(s.replace("ai", "ei").replace("oi", "eu")));
				curr++;
			}
		}
		
		if (poem.size() < 2) {
			return null;
		}
		
		return poem;

	}

}
