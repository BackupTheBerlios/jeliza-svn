package org.homedns.tobiasschulz.apps.speech;

import java.io.IOException;

public class Speech {
	
	public static void say(String text) {
		String[] tmp = { "say", text.replace("\n", " .  . ")};
		try {
			Process pr = Runtime.getRuntime().exec(tmp);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static String preprocessor(String text) {
		
		text = text.replace("Hi", "Hai . ");
		
		return text;
	}
	
}
