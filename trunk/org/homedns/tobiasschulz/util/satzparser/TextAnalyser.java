package org.homedns.tobiasschulz.util.satzparser;

import java.io.IOException;
import java.util.*;

import org.homedns.tobiasschulz.apps.jeliza.Util;
import org.homedns.tobiasschulz.io.FileManager;

public class TextAnalyser {

	public ArrayList<String[]> analyse(String text) {
		ArrayList<String[]> saetze = new ArrayList<String[]>();
		VerbDataBase vdb = new VerbDataBase();
		try {
			vdb.loadFromFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		text = Util.toASCII(text);

		text = Util.wegMitAbkuerzungen(text);

		Scanner sc = new Scanner(text);
		sc.useDelimiter("[.!\\?]");

		SatzParseManager spm;

		while (sc.hasNext()) {
			String tm = sc.next().trim();
			if (tm == "" || tm.contains(":")) {
				continue;
			}
			String[] k = { tm };
			for (String tmp : k) {
				if (tmp.contains(",")) {
					tmp = tmp.substring(0, tmp.indexOf(",") + 1).replace(",",
							"");
				}
				while (tmp.contains("(")) {
					int i = tmp.indexOf("(");
					String t = tmp;
					try {
						t = t.substring(0, i)
								+ t.substring(t.indexOf(")"), t.length());
					} catch (StringIndexOutOfBoundsException e) {
						break;
					}
					tmp = t.replace("(", "");
					tmp = t.replace(")", "");
				}
				tmp = tmp.replace("\"", "");
				tmp = tmp.replace("'", "");
				spm = SatzParseManager.parse(tmp, vdb);
				try {
					spm.getSubject();
					spm.getPraedikat();

					// if (spm.satzType == SatzParseManager.EINFACHE_FRAGE
					// || spm.satzType == SatzParseManager.ERWEITERTE_FRAGE) {
					// }
				} catch (SubjektNotFoundException e) {
					continue;
				} catch (VerbNotFoundException e) {
					continue;
				}
				boolean noObj = false;
				try {
					System.err.println("S: " + spm.getSubject());
					System.err.println("P: " + spm.getPraedikat());
					System.err.println("O: " + spm.getObjekt());
				} catch (SubjektNotFoundException e) {
					// wurde oben schon geprueft
				} catch (VerbNotFoundException e) {
					// wurde oben schon geprueft
				} catch (ObjektNotFoundException e) {
					// Man braucht kein Objekt
					noObj = true;
				}
				try {
					String s = "";
					if (spm.satzType == SatzParseManager.EINFACHE_FRAGE) {
						s = "simple-ques";
					}
					if (spm.satzType == SatzParseManager.ERWEITERTE_FRAGE) {
						s = "ext-ques";
					}
					if (spm.satzType == SatzParseManager.AUSSAGESATZ) {
						s = "simple-sent";
					}
					if (noObj) {
						String[] t = { s, spm.getSubject(), spm.getPraedikat(),
								"" };
						saetze.add(t);
					} else {
						String[] t = { s, spm.getSubject(), spm.getPraedikat(),
								spm.getObjekt() };
						saetze.add(t);
					}
				} catch (SubjektNotFoundException e) {
					// wurde oben schon geprueft
				} catch (VerbNotFoundException e) {
					// wurde oben schon geprueft
				} catch (ObjektNotFoundException e) {
					// Man braucht kein Objekt
				}
			}
		}

		return saetze;
	}

	public static void main(String[] args) {
		try {
			ArrayList<String[]> al = new TextAnalyser().analyse(FileManager
					.readFileIntoString("text.vdb"));
			for (String[] t : al) {
				System.out.println(t[0] + " - " + t[1] + " " + t[2] + " "
						+ t[3]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
