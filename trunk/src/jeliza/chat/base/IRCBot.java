package jeliza.chat.base;

import org.jibble.pircbot.*;

public class IRCBot extends PircBot {

	public Runnable whenMessComes = null;

	public Runnable whenPrivateMessComes = null;

	public String message = "";

	public String sender = "";

	public String channel = "";

	public IRCBot() {
		this.setName("JEliza");
	}

	public void setLoginName(String n) {
		this._name = n;
		this._login = n;
		this._nick = n;
		setName(n);
	}

	public void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		this.message = message;
		this.sender = sender;
		this.channel = channel;
		new Thread(whenMessComes).start();
	}

	public void onPrivateMessage(String sender, String login, String hostname,
			String message) {
		this.message = message;
		this.sender = sender;
		new Thread(whenPrivateMessComes).start();
	}
}
