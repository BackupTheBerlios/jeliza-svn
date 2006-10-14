package chat.irc.base;

import java.io.*;
import java.util.*;

import org.homedns.tobiasschulz.io.*;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import chat.base.*;
import chat.modules.*;

import com.levelonelabs.aim.*;

public class Chatter extends ChatModuleMaster {

	public IRCBot irc = null;

	String userid = "";

	public BotModule jelizaModule = new JElizaModule();

	public BotModule chatLoopModule = new ChatLoopModule();

	public Runnable whenNewMessageComes = new Runnable() {

		public void run() {

		}

	};

	public Chatter(String user, String pw, String channel, String server, OutputStream log) {
		out = new PrintStream(log);

	       // Now start our bot up.
        irc = new IRCBot();
        
        irc.setLoginName(user);
        
        // Enable debugging output.
        // irc.setVerbose(true);
        
        // Connect to the IRC server.
        try {
			irc.connect(server);
		} catch (NickAlreadyInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}

		System.out.println("name: " + user);
		System.out.println("pw: " + pw);

		irc.whenMessComes = new WhenMessComes();
		irc.whenPrivateMessComes = new WhenPrivateMessComes();
		
		initModules();
	}
	
	public void joinChannel(String channel) {
		if (!channel.startsWith("#")) {
			channel = "#" + channel;
		}
		System.out.println("joining channel " + channel);
		irc.channel = channel;
        irc.joinChannel(channel);
        String tmp = "                          ";
        out.print("Joining channel: " + tmp.substring("Joining channel: ".length()) + channel + "\n");
	}

	public void leaveChannel(String channel) {
		if (!channel.startsWith("#")) {
			channel = "#" + channel;
		}
		System.out.println("leaving channel " + channel);
		irc.channel = channel;
        irc.joinChannel(channel);
        String tmp = "                          ";
        out.print("Leaving channel: " + tmp.substring("Leaving channel: ".length()) + channel + "\n");
	}

	private void initModules() {
		jelizaModule.init(this);
		out.println("Modul " + jelizaModule.getName() + " initialisiert. CMM="
				+ jelizaModule.cmm);
	}
	
	public void stopModules() {
		jelizaModule.stop();
		chatLoopModule.stop();
	}

	public void send(String user, String m) {
		if (user.contains("->")) {
			user = user.substring(0, user.indexOf("->"));
		}
		irc.sendMessage(user, m);
		System.out.println("irc.sendMessage(\"" + user + "\", \"" + m + "\")");
	}

	public void getAnswer(String fra, String id, boolean doAnswer) {
		jelizaModule.newMessage(id, fra, doAnswer);
	}

	class WhenMessComes implements Runnable {

		public void run() {
			String request = irc.message;
			
			request = request.trim();
			System.out.println(irc.sender + " : " + request);

	        if (request.equalsIgnoreCase("time")) {
	            String time = new java.util.Date().toString();
	            irc.sendMessage(irc.channel, irc.sender + ": The time is now " + time);
	        } else {
	        	if (request.contains(irc._name)) {
	        		getAnswer(request, irc.channel + "->" + irc.sender, true);
	        	} else {
	        		getAnswer(request, irc.channel + "->" + irc.sender, false);
	        	}
	        }

			new Thread(whenNewMessageComes).start();
		}

	}

	class WhenPrivateMessComes implements Runnable {

		public void run() {
			String request = irc.message;
			
			request = request.trim();
			System.out.println(irc.sender + " : " + request);

	        if (request.equalsIgnoreCase("time")) {
	            String time = new java.util.Date().toString();
	            irc.sendMessage(irc.channel, irc.sender + ": The time is now " + time);
	        } else {
        		getAnswer(request, "PR " + irc.sender, true);
	        }

			new Thread(whenNewMessageComes).start();
		}

	}

	public void stop() {
		stopModules();
		irc.disconnect();
	}

}
