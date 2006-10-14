package module.megajeliza;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.homedns.tobiasschulz.io.FileManager;

public class Saver {

	public void save() {
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy_");
		SimpleDateFormat df2 = new SimpleDateFormat("HH-mm-ss");
		Date d = new Date();

		new File(System.getProperty("user.home") + "/.jeliza/saved/brain/"
				+ UltraJEliza.getCurrentProfile()).mkdirs();
		try {
			FileManager.copyFile("megajeliza/" + UltraJEliza.getCurrentProfile()
					+ "/jeliza.sat.brn", System.getProperty("user.home")
					+ "/.jeliza/saved/brain/" + UltraJEliza.getCurrentProfile()
					+ "/megajeliza.sat_" + df.format(d) + df2.format(d) + ".brn");
			FileManager.copyFile("megajeliza/" + UltraJEliza.getCurrentProfile()
					+ "/jeliza.fra.brn", System.getProperty("user.home")
					+ "/.jeliza/saved/brain/" + UltraJEliza.getCurrentProfile()
					+ "/megajeliza.fra_" + df.format(d) + df2.format(d) + ".brn");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
