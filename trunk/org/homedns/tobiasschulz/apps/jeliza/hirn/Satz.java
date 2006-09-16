package org.homedns.tobiasschulz.apps.jeliza.hirn;

public class Satz {
	
	public String satzHtml = "";
	
	public String satzPlain = "";
	
	public int gefuehl = 2;
	
	public Satz(String pl, String ht, Integer gef) {
		satzPlain = pl;
		satzHtml = ht;
		if (gef != -1)
			gefuehl = gef;
		System.out.println(gef);
	}
	public Satz(String pl, String ht) {
		satzPlain = pl;
		satzHtml = ht;
		gefuehl = 2;
	}
	
}
