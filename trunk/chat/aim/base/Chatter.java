package chat.aim.base;

import java.io.*;
import java.util.*;

import org.homedns.tobiasschulz.io.*;

import chat.base.*;
import chat.modules.*;

import com.levelonelabs.aim.*;

public class Chatter extends ChatModuleMaster {

	public AIMListener aim = null;

	String userid = "";

	public BotModule jelizaModule = new JElizaModule();

	public BotModule chatLoopModule = new ChatLoopModule();

	public Runnable whenNewMessageComes = new Runnable() {

		public void run() {

		}

	};

	public Chatter(String user, String pw, OutputStream log) {
		out = new PrintStream(log);

		HashMap hm = new HashMap();
		hm.put("userid", user);
		userid = user;
		hm.put("password", pw);

		aim = new AIMListener(hm);

		aim.run();
		aim.adapter = new Adapter();
		System.out.println("Logged in");
		aim.provider.setPermitMode(AIMSender.PERMIT_ALL);
		aim.provider.addAIMListener(aim.adapter);

		pr = new Pref(this);

		initModules();
	}

	private void initModules() {
		jelizaModule.init(this);
		out.println("Modul " + jelizaModule.getName() + " initialisiert. CMM="
				+ jelizaModule.cmm);
		chatLoopModule.init(this);
		out.println("Modul " + chatLoopModule.getName()
				+ " initialisiert. CMM=" + chatLoopModule.cmm);
	}
	
	public void stopModules() {
		jelizaModule.stop();
		chatLoopModule.stop();
	}

	public void send(String user, String m) {
		AIMBuddy b = aim.provider.getBuddy(user);
		if (b == null) {
			aim.provider.addBuddy(new AIMBuddy(user));
			b = aim.provider.getBuddy(user);
		}
		if (user.trim() == userid.trim() || b.getName().trim() == userid.trim()) {
			out.println("Sending error: BuddyId == JElizaUserId");
			return;
		}
		for (int x = 0; x < m.split("\n").length; x++) {
			String me = m.split("\n")[x];
			aim.provider.sendMessage(b, me);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void getAnswer(String fra, AIMBuddy sender) {
		String id = pr.getIdOf(sender.getName());
		jelizaModule.newMessage(id, fra, true);
	}

	class Adapter extends AIMAdapter {

		public Adapter() {
		}

		public void handleMessage(AIMBuddy sender, String request) {
			request = request.trim();
			System.out.println(sender.getName() + " : " + request);

			String id = sender.getName();
			id = pr.getIdOf(id);
			pr.addFileBuddy(id);

			if (request.startsWith("/buddy ")) {
				AIMBuddy bu = new AIMBuddy(request.substring(7).replace("-", ""));
				aim.provider.addBuddy(bu);
				aim.provider.permitBuddy(bu);
				pr.addFileBuddy(pr.getIdOf(bu.getName()));
				System.out.println("Added Buddy " + request.substring(7).replace("-", ""));
				send(sender.getName(), "Added no-spam buddy "
						+ request.substring(7));
				return;
			}

			if (request.startsWith("/spam ")) {
				AIMBuddy bu = new AIMBuddy(request.substring(6).replace("-", ""));
				aim.provider.addBuddy(bu);
				aim.provider.permitBuddy(bu);
				pr.addFileBuddy(pr.getIdOf(bu.getName()));
				((ChatLoopModule) chatLoopModule).spamIcq.add(request
						.substring(6).replace("-", ""));
				try {
					FileManager.writeStringIntoFile(FileManager
							.readFileIntoString("icqToSpam.txt")
							+ " " + request.substring(6), "icqToSpam.txt");
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Added spam Buddy " + request.substring(6));
				send(sender.getName(), "Added spam buddy "
						+ request.substring(6));
				return;
			}

			getAnswer(request, sender);

			new Thread(whenNewMessageComes).start();
		}

	}

	public void stop() {
		stopModules();
		aim.shutdown();
	}

}
