package org.homedns.tobiasschulz.apps.jeliza.hirn;

public class Satz {
	
	public String satzHtml = "";
	
	public String satzPlain = "";
	
	public String istAntortAuf = "";
	
	public int gefuehl = 2;
	
	public Gehirn hirn;
	
	public Satz(String pl, String ht, Integer gef) {
		satzPlain = pl;
		satzHtml = ht;
		if (gef != -1)
			gefuehl = gef;
	}

	public Satz(String pl, String ht, Integer gef, Gehirn h) {
		satzPlain = pl;
		satzHtml = ht;
		if (gef != -1)
			gefuehl = gef;
		hirn = h;
	}

	public Satz(String pl, String ht, Integer gef, String answerOf) {
		satzPlain = pl;
		satzHtml = ht;
		if (gef != -1)
			gefuehl = gef;
		istAntortAuf = answerOf;
	}

	public Satz(String pl, String ht, Integer gef, Gehirn h, String answerOf) {
		satzPlain = pl;
		satzHtml = ht;
		if (gef != -1)
			gefuehl = gef;
		hirn = h;
		istAntortAuf = answerOf;
	}

	public Satz(String pl, String ht) {
		satzPlain = pl;
		satzHtml = ht;
		gefuehl = 2;
	}
	
}
