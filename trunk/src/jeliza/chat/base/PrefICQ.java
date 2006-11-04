package jeliza.chat.base;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.homedns.tobiasschulz.io.FileManager;

import com.levelonelabs.aim.AIMBuddy;

public class PrefICQ extends Preferences {

	JElizaICQ ch = null;

	public PrefICQ(JElizaICQ j) {
		ch = j;
	}

	public void addFileBuddy(String id) {
		try {
			if (!FileManager.readFileIntoString("icqBuddies").contains(id)) {
				FileManager.writeStringIntoFile(FileManager
						.readFileIntoString("icqBuddies")
						+ " " + id, "icqBuddies");
			}
		} catch (IOException e1) {
			try {
				new File("icqBuddies").createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String[] getFileBuddies() {
		try {
			return FileManager.readFileIntoString("icqBuddies").replace("\n",
					"").replace("  ", "").split(" ");
		} catch (IOException e1) {
			try {
				new File("icqBuddies").createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String[] s = {};
		return s;
	}

	public void addBuddy(String bud) {
		AIMBuddy bu = new AIMBuddy(bud);
		ch.aim.provider.addBuddy(bu);
		addFileBuddy(bud);
		ch.out.println("Added Buddy " + bud);
	}

	public String getSpamIds() {
		String spamTo = "";
		try {
			spamTo = FileManager.readFileIntoString("icqToSpam.txt");
		} catch (IOException e1) {
			try {
				new File("icqToSpam.txt").createNewFile();
				spamTo = "";
			} catch (IOException e) {
				spamTo = "";
			}
		}
		spamTo = spamTo.replace("\n", " ");
		spamTo = spamTo.replace("\r", " ");
		spamTo = spamTo.replace("  ", " ");
		spamTo = spamTo.replace("  ", " ");
		spamTo = spamTo.replace("  ", " ");
		return spamTo;
	}

	public boolean isSpamBuddy(String id) {
		id = getIdOf(id);
		String spam = getSpamIds();
		if (spam.contains(id)) {
			return true;
		}

		return false;
	}

	public String getIdOf(String id) {
		if (id.contains(":")) {
			id = id.substring(0, id.indexOf(":")).replace(":", "");
		}
		return id;
	}

	public Iterator getBuddies() {
		return ch.aim.provider.getBuddyNames();
	}

}
