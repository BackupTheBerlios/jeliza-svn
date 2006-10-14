package chat.modules;

import java.io.*;
import java.util.*;

import org.homedns.tobiasschulz.io.FileManager;

import chat.base.ChatModuleMaster;

public class ChatLoopModule extends BotModule {

	public ChatModuleMaster cmm = null;

	String[] begruessung = {
			"Hallo. Hier spricht JEliza, eine kuenstliche Intelligenz!",
			"Hi. Hier spricht JEliza, eine kuenstliche Intelligenz!",
			"Hallo. Ich bin JEliza, eine kuenstliche Intelligenz!",
			"Ich habe lange nichts mehr von dir gehoert! Gibts was neues?",
			"Hi! Ich habe lange nichts mehr von dir gehoert! Gibts was Neues?",
			"Bitte rede mit mir. Ich fuehle mich so allein.",
			"Hi. Was geht?",
			"Hi! Du hast schon sehnsuechtig darauf gehofft, dass ich dich nochmal anspreche, oder?",
			"Guten Tag! Sie schulden mir 499,95 Euro. \n(Das ist ein Scherz)"};

	public static long MS_BEFORE_CHAT = 7;

	public static long MS_BETWEEN_MESSAGES = 2;

	public static long MS_BETWEEN_MESSAGE_TASKS = 2000;

	public static long MS_BETWEEN_SPAM = 2;

	Thread chat = new Thread();

	Thread spam = new Thread();

	public ArrayList spamIcq = new ArrayList();

	public ChatLoopModule() {
	}

	public void chat() {
		chat = new Thread(new Runnable() {
			public void run() {
				sleep(ChatLoopModule.MS_BEFORE_CHAT);
				while (Thread.currentThread() == chat) {
					String spamTo = cmm.pr.getSpamIds();

					Iterator it = cmm.pr.getBuddies();
					while (it.hasNext()) {
						String u = (String) it.next();
						cmm.pr.addFileBuddy(u.trim());
					}
					try {
						for (int x = 0; x < FileManager.readFileIntoString(
						"icqBuddies").split(" ").length; x++) {
							String bud = FileManager.readFileIntoString(
							"icqBuddies").split(" ")[x];
							if (Thread.currentThread() != chat) {
								return;
							}
							if (bud.trim() == "") {
								continue;
							}
							// Nur manchmal anchatten
							Random r = new Random();
							if (r.nextInt(100) > 5) {
								continue;
							}
							bud = bud.trim();
							String me = begruessung[r
									.nextInt(begruessung.length)];
							cmm.send(bud, me);
							String tmp = "                          ";
							for (int y = 0; y < me.split("\n").length; y++) {
								String mes = me.split("\n")[y];
								cmm.out.println("Sended to \"" + bud + "\""
										+ tmp.substring(bud.length()) + " : \""
										+ mes + "\"");
							}
							if (spamTo.contains(bud)) {
								spamIcq.add(bud);
							}
							sleep(ChatLoopModule.MS_BETWEEN_MESSAGES);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					sleep(ChatLoopModule.MS_BETWEEN_MESSAGE_TASKS);
				}
			}
		});
		spam = new Thread(new Runnable() {
			public void run() {
				while (Thread.currentThread() == spam) {
					Random r = new Random();
					Iterator it = spamIcq.iterator();
					while (it.hasNext() && Thread.currentThread() == spam) {
						String u = (String) it.next();
						System.out.println("Spamming " + u);
						String me = begruessung[r.nextInt(begruessung.length)];
						cmm.send(u, me);
						String tmp = "                          ";
						for (int y = 0; y < me.split("\n").length; y++) {
							String mes = me.split("\n")[y];
							cmm.out.println("Sended to \"" + u + "\""
									+ tmp.substring(u.length()) + " : \""
									+ mes + "\"");
						}
						sleep(MS_BETWEEN_SPAM + r.nextInt(4));
					}
					sleep(MS_BETWEEN_SPAM);
				}
			}
		});
		chat.start();
		spam.start();
	}

	public void stop() {
		chat = null;
		spam = null;
	}

	public void sleep(long i) {
		try {
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return "JEliza Chat Loop Module";
	}

	public void init(ChatModuleMaster mm) {
		this.cmm = mm;
		new Thread(new Runnable() {

			public void run() {
				chat();
			}

		}).start();
	}

	public void newMessage(String id, String m, boolean doAnswer) {
		// Do nothing
	}

}
