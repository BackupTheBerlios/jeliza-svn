import javax.swing.JFrame;

import chat.base.JElizaICQ;
import chat.base.JElizaIRC;

public class JElizaConsole {

	JElizaICQ chatIcq = null;

	JElizaIRC chatIrc = null;

	public JElizaConsole(String[] args) {
		if (args[0].equalsIgnoreCase("-icq")) {
			initIcq(args[1]);
		}
		if (args[0].equalsIgnoreCase("-irc")) {
			initIrc(args[1]);
		}
		if (args[0].equalsIgnoreCase("-icqirc")) {
			initIcq(args[1]);
			initIrc(args[2]);
		}
	}

	private void initIrc(String str) {

	}

	private void initIcq(String str) {
		chatIcq = new JElizaICQ(new JFrame());
		System.out.println("ICQ id=" + str.substring(0, str.indexOf(";"))
				+ ", pw=" + str.substring(str.indexOf(";") + 1, str.length()));
		chatIcq.loginIcq(str.substring(0, str.indexOf(";")), str.substring(str
				.indexOf(";") + 1, str.length()));
	}

	public static void main(String[] args) {
		JElizaConsole jc = new JElizaConsole(args);
	}

}
