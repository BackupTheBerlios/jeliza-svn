package org.homedns.tobiasschulz.util.satzparser;

import java.io.IOException;

public class GernerateVerbDatabase {

	public static void main(String[] args) {
		VerbDataBase vdb = null;
		System.out.println("---- Generating Verb Database ----");
		try {
			vdb = new VerbDataBase(args[0]);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("---- Writing it into verbs.txt ----");
		vdb.writeIntoFile("verbs.txt");
	}

}
