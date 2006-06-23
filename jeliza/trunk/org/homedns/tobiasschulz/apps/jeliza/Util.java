package org.homedns.tobiasschulz.apps.jeliza;

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
} // class Util
