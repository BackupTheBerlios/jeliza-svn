package jeliza.chat.base;

import swingwt.awt.*;
import swingwt.awt.event.*;
import java.io.*;
import java.util.*;

import swingwtx.swing.*;

import swingwtx.swing.tree.*;

import jeliza.chat.modules.BotModule;
import jeliza.chat.modules.ChatLoopModule;
import jeliza.chat.modules.JElizaModule;

import org.homedns.tobiasschulz.io.FileManager;

import com.levelonelabs.aim.*;

public class JElizaICQ extends ChatModuleMaster {

	JTextArea log = new JTextArea();

	JDialog d = null;

	String icqUser = "384851102";

	String icqPw = "";

	public JFrame f;

	JTree buddies = null;

	JPanel sidebar;

	DefaultTreeModel buddyModel = null;

	DefaultMutableTreeNode root = null;

	ArrayList buddyModelAL = new ArrayList();

	OutputStream logFile = null;

	public JPanel panel = new JPanel(new BorderLayout());

	public AIMListener aim = null;

	String userid = "";

	public BotModule jelizaModule = new JElizaModule();

	public BotModule chatLoopModule = new ChatLoopModule();

	PrefICQ pr = null;

	public JElizaICQ(JFrame f) {
		try {
			logFile = new FileOutputStream("chatlog.icq.log", true);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		this.f = f;

		out = new PrintStream(new OutputStream() {

			public void write(int b) throws IOException {
				log.append(Character.toString((char) b));
				logFile.write(b);
			}

		});

		
		JPanel logInOut = new JPanel();
		JButton login = new JButton("Login");
		login.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				newLoginDialog();
			}

		});
		logInOut.add(login);
		JButton logout = new JButton("Logout");
		logout.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				logoutIcq();
				log.append("Logged out\n");
			}

		});
		logInOut.add(logout);
		panel.add("North", logInOut);

		root = new DefaultMutableTreeNode("ICQ Buddies");
		buddyModel = new DefaultTreeModel(root);
		buddies = new JTree(buddyModel);

		genSidebar();

		log.setFont(new Font("Monospaced", Font.PLAIN, 10));
		panel.add("Center", new JScrollPane(log));

	}

	public void newLoginDialog() {
		d = new JDialog(f, true);
		d.setTitle("JEliza ICQ Client - Login");
		d.setLayout(new BorderLayout());
		final JPanel dia = new JPanel(new GridLayout(4, 2));
		final JTextField userIcq = new JTextField(icqUser);
		final JPasswordField pwIcq = new JPasswordField(icqPw);
		JButton loginIcq = new JButton("ICQ Login");
		loginIcq.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				char[] p = pwIcq.getPassword();
				String pass = "";
				for (int y = 0; y < p.length; y++) {
					char x = p[y];
					pass = pass.concat(Character.toString(x));
				}
				if (userIcq.getText().trim().length() < 2 || pass.length() < 2) {
					return;
				}
				log.append("ICQ: Logging in with ID " + userIcq.getText()
						+ "\n");
				icqUser = userIcq.getText().trim();
				icqPw = pass;
				d.setVisible(false);
				loginIcq(userIcq.getText().trim(), pass);

				updateBuddyListIcq();
			}

		});
		JButton cancelIcq = new JButton("Abbrechen");
		cancelIcq.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				d.setVisible(false);
			}

		});
		dia.add(new JLabel("JEliza"));
		dia.add(new JLabel("ICQ Login"));
		dia.add(new JLabel("ICQ-Nummer: "));
		dia.add(userIcq);
		dia.add(new JLabel("ICQ Passwort: "));
		dia.add(pwIcq);
		dia.add(loginIcq);
		dia.add(cancelIcq);
		d.add(dia);
		d.pack();
		d.setVisible(true);
	}

	public void loginIcq(String u, String pw) {
		HashMap hm = new HashMap();
		hm.put("userid", u);
		hm.put("password", pw);

		aim = new AIMListener(hm);

		aim.run();
		aim.adapter = new Adapter();
		System.out.println("Logged in");
		aim.provider.setPermitMode(AIMSender.PERMIT_ALL);
		aim.provider.addAIMListener(aim.adapter);

		pr = new PrefICQ(this);

		initModules();
	}

	public void logoutIcq() {
		stop();
		aim.adapter = null;
		aim.provider = null;
		buddies.removeAll();
	}

	void genSidebar() {
		JPanel control = new JPanel(new BorderLayout());

		sidebar = new JPanel(new GridLayout(1, 2, 10, 10));
		final JTextField buddy = new JTextField("");
		sidebar.add(buddy);
		JButton addBuddy = new JButton("Neuer Buddy");
		addBuddy.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				pr.addBuddy(buddy.getText().trim());
			}

		});
		sidebar.add(addBuddy);

		control.add("North", sidebar);
		control.add("Center", new JScrollPane(buddies));
		panel.add("East", control);
	}

	public synchronized void updateBuddyListIcq() {
		String[] bu = pr.getFileBuddies();
		for (int y = 0; y < buddyModelAL.size(); y++) {
			DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) buddyModelAL
					.get(y);
			buddyModel.removeNodeFromParent(dmtn);
		}
		for (int z = 0; z < bu.length; z++) {
			String b = bu[z];
			if (aim.provider.getBuddy(b) == null) {
				aim.provider.addBuddy(new AIMBuddy(b));
			}
			boolean online = aim.provider.getBuddy(b).isOnline();
			DefaultMutableTreeNode dmtn = null;
			if (online) {
				dmtn = new DefaultMutableTreeNode(
						"<html><body><font color='green'>" + b
								+ "</font></body></html>");
			} else {
				dmtn = new DefaultMutableTreeNode(
						"<html><body><font color='red'>" + b
								+ "</font></body></html>");
			}
			buddyModel.insertNodeInto(dmtn, root, buddyModel
					.getChildCount(root));
			buddyModelAL.add(dmtn);
		}

		f.repaint();
		f.setSize(f.getSize());
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
				AIMBuddy bu = new AIMBuddy(request.substring(7)
						.replace("-", ""));
				aim.provider.addBuddy(bu);
				aim.provider.permitBuddy(bu);
				pr.addFileBuddy(pr.getIdOf(bu.getName()));
				System.out.println("Added Buddy "
						+ request.substring(7).replace("-", ""));
				send(sender.getName(), "Added no-spam buddy "
						+ request.substring(7));
				return;
			}

			if (request.startsWith("/spam ")) {
				AIMBuddy bu = new AIMBuddy(request.substring(6)
						.replace("-", ""));
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

			updateBuddyListIcq();
		}

	}

	public void stop() {
		stopModules();
		aim.shutdown();
	}

}
