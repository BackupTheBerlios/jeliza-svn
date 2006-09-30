package interpreter.base;

import java.io.*;

import interpreter.io.*;

public class Interpreter {

	IteratorReader it = null;

	InputStream i = null;

	public Interpreter() {

	}

	public void useInterpreter(String f) throws FileNotFoundException {
		useInterpreter(new FileInputStream(f));
	}

	public void useInterpreter(InputStream in) {
		i = in;
		it = new IteratorReader(in);
	}

	public AnswerPack get(String sub, String verb, String obj, String fragwrt) {
		useInterpreter(i);

		if (sub == null) {
			sub = "";
		}

		if (verb == null) {
			verb = "";
		}

		if (obj == null) {
			obj = "";
		}

		if (fragwrt == null) {
			fragwrt = "";
		}

		sub = sub.toLowerCase();
		verb = verb.toLowerCase();
		obj = obj.toLowerCase();
		fragwrt = fragwrt.toLowerCase();

		boolean isInRegel = false;

		AnswerPack ap = new AnswerPack();

		while (it.hasNext()) {
			String str = it.next();
			
			if (!str.startsWith(" ")) {
				if (isInRegel) {
					return ap;
					//isInRegel = false;
				}
				if (str.startsWith("if (")) {
					String startsWith = "if ( sub == \"" + sub
							+ "\" && verb == \"" + verb + "\" && obj == \""
							+ obj + "\" && qwrd == \"" + fragwrt
							+ "\" ) ; then";
					if (str.contains(startsWith) || str.startsWith(startsWith)
							|| str.hashCode() == startsWith.hashCode()) {
						isInRegel = true;
					}
/*					System.out.println("O: '" + str + "'");
					System.out.println("T: '" + startsWith + "'");
					System.out.println(str.hashCode());
					System.out.println(startsWith.hashCode());*/
				}
			} else if (isInRegel) {
				if (str.startsWith(" sub = ")) {
					ap.sub = str.substring(7);
				}
				if (str.startsWith(" verb = ")) {
					ap.verb = str.substring(8);
				}
				if (str.startsWith(" obj = ")) {
					ap.obj = str.substring(7);
				}
				if (str.startsWith(" qwrd = ")) {
					ap.qwrd = str.substring(8);
				}
				if (str.startsWith(" othr = ")) {
					ap.othr = str.substring(8);
				}
			}
		}

		return ap;
	}

}
