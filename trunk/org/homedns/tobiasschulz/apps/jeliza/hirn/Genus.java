package org.homedns.tobiasschulz.apps.jeliza.hirn;

import java.io.*;

import org.homedns.tobiasschulz.apps.jeliza.Util;
import org.homedns.tobiasschulz.io.FileManager;
import org.homedns.tobiasschulz.util.satzparser.*;

public class Genus {

	public static short MAENNLICH = 1;

	public static short WEIBLICH = 2;

	public static short SAECHLICH = 3;

	public static short UNKNOWN = 0;

	public static String[] m = { "Fruehling", "Sommer", "Herbst", "Winter",
			"Januar", "Februar", "Maerz", "April", "Mai", "Juni", "Juli",
			"August", "September", "Oktober", "November", "Dezember", "Montag",
			"Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag",
			"Sonntag", "Osten", "Sueden", "Regen", "Schnee", "Hagel", "Nebel",
			"Foehn", "Passat", "Mistral", "Granit", "Basalt", "Schiefer",
			"Quarz", "Gneis", "Mercedes", "BMW", "Renault", "Saab", "Toyota",
			"Lada" };

	public static String[] w = { "Ulme", "Eiche", "Esche", "Tanne", "Palme",
			"Rose", "Tulpe", "Orchidee", "Geranie", "Nelke", "Lilie" };

	public static String[] s = { "Gold", "Kupfer", "Aluminium", "Eisen",
			"Neon", "Chlor" };

	public static String[] derdiedas = { "der", "er", "die", "das", "dieser",
			"dessen", "sie", "es", "das", "die", "herr", "frau", "ich" };

	public static String getDerDieDas(short s) {
		if (s == Genus.MAENNLICH) {
			return "der";
		}
		if (s == Genus.WEIBLICH) {
			return "die";
		}
		if (s == Genus.SAECHLICH) {
			return "das";
		}
		return "";
	}

	public static String getErSieEs(short s) {
		if (s == Genus.MAENNLICH) {
			return "er";
		}
		if (s == Genus.WEIBLICH) {
			return "Sie";
		}
		if (s == Genus.SAECHLICH) {
			return "Es";
		}
		return "";
	}

	public static short getGenus(String str, WortDataBase vdb) {
		return Genus.getGenus(str, true, true, vdb);
	}

	public static short getGenus(String str, boolean lookInWeb,
			boolean writeInFile, WortDataBase vdb) {
		str = Util.replace(str, "ä", "ae");
		str = Util.replace(str, "ö", "oe");
		str = Util.replace(str, "ü", "ue");
		str = Util.replace(str, "ß", "ss");

		str = Util.wegMitAbkuerzungen(str);
		str = str.trim().toLowerCase();

		short genus = UNKNOWN;
		boolean readedFromFile = true;

		try {
			genus = vdb.getGenusFromDatabase(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		for (int x = 0; x < derdiedas.length; x++) {
			String tmp = derdiedas[x];
			if (tmp.toLowerCase().hashCode() == str.hashCode()) {
				genus = Genus.UNKNOWN;
			}
		}

		for (int x = 0; x < m.length; x++) {
			String tmp = m[x];
			if (tmp.toLowerCase().hashCode() == str.hashCode()) {
				genus = Genus.MAENNLICH;
			}
		}

		for (int x = 0; x < w.length; x++) {
			String tmp = w[x];
			if (tmp.toLowerCase().hashCode() == str.hashCode()) {
				genus = Genus.WEIBLICH;
			}
		}

		for (int x = 0; x < s.length; x++) {
			String tmp = s[x];
			if (tmp.toLowerCase().hashCode() == str.hashCode()) {
				genus = Genus.SAECHLICH;
			}
		}

		if (genus == UNKNOWN) {
			readedFromFile = false;
			if (lookInWeb) {
				genus = WortDataBase.getGenusFromWeb(str);
			}
		}

		if (genus == UNKNOWN) {

			if ((str.endsWith("ling") || str.endsWith("ich")
					|| str.endsWith("ig") || str.endsWith("er")
					|| str.endsWith("ent") || str.endsWith("iker")
					|| str.endsWith("ismus") || str.endsWith("or") || str
					.endsWith("ist"))
					&& (str != "er" && str != "der" && str != "dieser" && str != "den")) {
				genus = Genus.MAENNLICH;
			}

			if ((str.endsWith("ung") || str.endsWith("schaft")
					|| str.endsWith("ion") || str.endsWith("heit")
					|| str.endsWith("keit") || str.endsWith("taet")
					|| str.endsWith("ik") || str.endsWith("rin")
					|| str.endsWith("tin") || str.endsWith("age")
					|| str.endsWith("anz") || str.endsWith("ette")
					|| str.endsWith("euse") || str.endsWith("ie")
					|| str.endsWith("enz") || str.endsWith("itis")
					|| str.endsWith("ose") || str.endsWith("uer"))
					&& (str != "die" && str != "sie" && str != "wie")) {
				genus = Genus.WEIBLICH;
			}

			if ((str.endsWith("lein") || str.endsWith("chen")
					|| str.endsWith("um") || str.endsWith("ment")
					|| str.endsWith("ma") || str.endsWith("ett")
					|| str.endsWith("ing") || str.startsWith("ge"))
					&& (str != "das")) {
				genus = Genus.SAECHLICH;
			}
		}

		if (genus != UNKNOWN && !readedFromFile) {
			try {
				if (writeInFile) {
					FileManager.writeStringIntoFile(FileManager
							.readFileIntoString("genus.txt") +
							Short.toString(genus)
									+ "-"
									+ str
									+ System.getProperty("line.separator"),
							"genus.txt");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return genus;
	}

}
