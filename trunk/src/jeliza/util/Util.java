package jeliza.util;

import swingwt.awt.event.ActionListener;

import swingwtx.swing.*;

/**
 * Hilfsklasse des Java-Servlets JEliza, die ein paar Hilfsmethoden bereitstellt
 * 
 * @author Tobias Schulz
 * @version 0.3
 */
public class Util {

	public static String s = "";

	/**
	 * Ersetzt in ss search durch replace.
	 * 
	 * @param ss
	 *            Der String, in dem ersetzt werden soll
	 * @param search
	 *            Der String, der ersetzt werden soll
	 * @param replace
	 *            Der String, durch den search ersetzt werden soll
	 * @return Der String, in dem alles ersetzt ist.
	 */
	public static String replace(String ss, String search, String replace) {
		s = ss;
		while (true) {
			int pos = s.indexOf(search);
			if (pos >= 0) {
				s = s.substring(0, pos) + replace
						+ s.substring(pos + search.length(), s.length());

			} else {
				return s;
			}
		}
	}

	/**
	 * Sorgt dafür, dass nur ASCII-Zeichen enthalten sind
	 */
	public static String toASCII(String tmp) {
		String ret = "";
		for (int x = 0; x < tmp.length(); x++) {
			char c = tmp.charAt(x);
			if (c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e'
					|| c == 'f' || c == 'g' || c == 'h' || c == 'i' || c == 'j'
					|| c == 'k' || c == 'l' || c == 'm' || c == 'n' || c == 'o'
					|| c == 'p' || c == 'q' || c == 'r' || c == 's' || c == 't'
					|| c == 'u' || c == 'v' || c == 'w' || c == 'x' || c == 'y'
					|| c == 'z' || c == ' ' || c == '"' || c == ',' || c == '.'
					|| c == '-' || c == '~' || c == '+' || c == '$' || c == '@'
					|| c == '€' || c == 'ä' || c == 'ü' || c == 'ö' || c == 'ß'
					|| c == '=' || c == '<' || c == '>' || c == '#' || c == '/'
					|| c == '\\' || c == '?' || c == '!' || c == '%'
					|| c == '&' || c == '(' || c == ')' || c == 'A' || c == 'B'
					|| c == 'C' || c == 'D' || c == 'E' || c == 'F' || c == 'G'
					|| c == 'H' || c == 'I' || c == 'J' || c == 'K' || c == 'L'
					|| c == 'M' || c == 'N' || c == 'O' || c == 'P' || c == 'Q'
					|| c == 'R' || c == 'S' || c == 'T' || c == 'U' || c == 'V'
					|| c == 'W' || c == 'X' || c == 'Y' || c == 'Z' || c == '0'
					|| c == '1' || c == '2' || c == '3' || c == '4' || c == '5'
					|| c == '6' || c == '7' || c == '8' || c == '9') {
				ret += c;
			} else if (c == '„' || c == '“') {
				ret += " ";
			} else {
				ret += "?";
			}

		}

		return ret.replace(" ", " ");
	}

	/**
	 * Sorgt dafuer, dass weniger Abkuerungen enthalten sind
	 */
	public static String wegMitAbkuerzungen(String text) {
		text = text.replace("ca.", "ca");
		text = text.replace("etc.", "etc");
		text = text.replace("usw.", "usw");
		text = text.replace("o.a.", "oder auch");
		text = text.replace("u.U.", "unter Umstaenden");
		text = text.replace("z.B.", "zum Beispiel");
		text = text.replace("u.u.", "unter Umstaenden");
		text = text.replace("z.b.", "zum Beispiel");
		text = text.replace("s.g.", "so genannten");
		text = text.replace("s.g", "so genannten");

		return text;
	}

	/**
	 * Creates a JMenuItem Object named "name" with ActionListener "al" and adds
	 * it to "jm"
	 */
	public static void mkJMenuItem(JMenu jm, String name, ActionListener al,
			String com) {
		if (name == "-") {
			jm.addSeparator();
			return;
		}
		JMenuItem jmi = new JMenuItem(name);
		if (al != null) {
			jmi.addActionListener(al);
		}
		jmi.setActionCommand(com);
		jm.add(jmi);
	}
} // class Util
