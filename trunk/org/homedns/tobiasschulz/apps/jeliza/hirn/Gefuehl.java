package org.homedns.tobiasschulz.apps.jeliza.hirn;

import java.util.*;

/**
 * Beschreibt die Gefuele von JEliza.
 * 
 * @author Tobias Schulz
 * @version 0.3
 */
public class Gefuehl {
	
	public int feeling = 2;
	
	public HashMap<String, Integer> wortToFeeling = new HashMap<String, Integer>();
	
	public String getFeeling() {
		switch(feeling) {
		case 1:
			return "Sehr Gluecklich";
		case 2:
			return "Gluecklich";
		case 3:
			return "Traurig";
		case 4:
			return "Entsetzt";
		case 5:
			return "Zornig";
		case 6:
			return "Wuetend";
		}
		return "Unentschlossen";
	}	
	
	public void setFeeling(int f) {
		feeling = f;
	}
	
	public Gefuehl() {
		wortToFeeling.put("scheisse", 4);
		wortToFeeling.put("bescheuert", 3);
		wortToFeeling.put("beleidig", 5);
		wortToFeeling.put("wahnsinnig", 4);
		wortToFeeling.put("schrecklich", 3);
		wortToFeeling.put("super", 1);
		wortToFeeling.put("fuck", 6);
		wortToFeeling.put("fick dich", 6);
		wortToFeeling.put("hass", 4);
		wortToFeeling.put("lieb", 1);
		wortToFeeling.put("gern", 1);
		wortToFeeling.put("umpf", 4);
		wortToFeeling.put("RARR", 5);
		wortToFeeling.put("du bist scheisse", 6);
		wortToFeeling.put("du bist dumm", 6);
		wortToFeeling.put("du bist bloed", 5);
		wortToFeeling.put("du bist kacke", 6);
	}
}
