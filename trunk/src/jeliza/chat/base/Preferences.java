package jeliza.chat.base;

import java.util.Iterator;

public class Preferences {

	public void addFileBuddy(String id) {

	}

	public String[] getFileBuddies() {
		return new String[] {};
	}

	public Iterator getBuddies() {
		return new Iterator() {

			public boolean hasNext() {
				return false;
			}

			public Object next() {
				return null;
			}

			public void remove() {
			}

		};
	}

	public void addBuddy(String bud) {

	}

	public String getSpamIds() {
		return "";
	}

	public boolean isSpamBuddy(String id) {
		return false;
	}

	public String getIdOf(String id) {
		return id;
	}

}
