package jeliza.chat.base;

import swingwt.awt.*;
import swingwt.awt.event.*;
import java.io.*;
import java.util.*;

import swingwtx.swing.*;

import jeliza.chat.modules.BotModule;
import jeliza.chat.modules.ChatLoopModule;
import jeliza.chat.modules.JElizaModule;

import org.jibble.pircbot.*;

public class JElizaIRC extends ChatModuleMaster {

	JTextArea log = new JTextArea();

	JDialog d = null;

	String ircUser = "JEliza";

	String ircServer = "irc.freenode.net";

	String ircPw = "";

	String ircChannel = "";

	public JFrame f;

	JPanel sidebar;

	ArrayList buddyModelAL = new ArrayList();

	OutputStream logFile = null;

	public JPanel panel = new JPanel(new BorderLayout());

	public JTextField channel = null;

	JButton joinChannel = new JButton("In Channel");

	JButton leaveChannel = new JButton("In Channel");

	JPanel logInOut = null;

	public IRCBot irc = null;

	String userid = "";

	public BotModule jelizaModule = new JElizaModule();

	public BotModule chatLoopModule = new ChatLoopModule();

	public JElizaIRC() {
		try {
			logFile = new FileOutputStream("chatlog.irc.log", true);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		f = new JFrame("JEliza - mit IRC verbinden");
		f.setSize(450, 450);
		f.setLayout(new BorderLayout(10, 10));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		logInOut = new JPanel();
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
				logoutIrc();
				log.append("Logged out\n");
			}

		});
		logInOut.add(logout);

		log.setFont(new Font("Monospaced", Font.PLAIN, 10));

		genSidebar();
	}

	public void newLoginDialog() {
		d = new JDialog(f, true);
		d.setTitle("JEliza IRC Client - Login");
		d.setLayout(new BorderLayout());
		final JPanel dia = new JPanel(new GridLayout(5, 2));
		final JTextField userIrc = new JTextField(ircUser);
		final JTextField serverIrc = new JTextField(ircServer);
		final JPasswordField pwIrc = new JPasswordField(ircPw);
		JButton loginIrc = new JButton("IRC Login");
		loginIrc.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				char[] p = pwIrc.getPassword();
				String pass = "";
				for (int y = 0; y < p.length; y++) {
					char x = p[y];
					pass = pass.concat(Character.toString(x));
				}
				if (userIrc.getText().trim().length() < 2) {
					return;
				}
				log.append("IRC: Logging in with ID " + userIrc.getText()
						+ "\n");
				ircUser = userIrc.getText().trim();
				ircPw = pass;
				d.setVisible(false);
				loginIrc(userIrc.getText().trim(), pass, ircChannel, serverIrc
						.getText().trim());

				genSidebar();
			}

		});
		JButton cancelIrc = new JButton("Abbrechen");
		cancelIrc.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				d.setVisible(false);
			}

		});
		dia.add(new JLabel("JEliza"));
		dia.add(new JLabel("IRC Login"));
		dia.add(new JLabel("IRC-Name: "));
		dia.add(userIrc);
		dia.add(new JLabel("IRC-Passwort: "));
		dia.add(pwIrc);
		dia.add(new JLabel("IRC-Server: "));
		dia.add(serverIrc);
		dia.add(loginIrc);
		dia.add(cancelIrc);
		d.add(dia);
		d.pack();
		d.setVisible(true);
	}

	public void loginIrc(String u, String pw, String chan, String serv) {
		out = new PrintStream(new OutputStream() {

			public void write(int b) throws IOException {
				log.append(Character.toString((char) b));
				logFile.write(b);
			}

		});

		irc = new IRCBot();

		irc.setLoginName(u);
		try {
			irc.connect(serv);
		} catch (NickAlreadyInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}

		System.out.println("name: " + u);
		System.out.println("pw: " + pw);

		irc.whenMessComes = new WhenMessComes();
		irc.whenPrivateMessComes = new WhenPrivateMessComes();

		initModules();
		genSidebar();
	}

	public void logoutIrc() {
		stop();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		genSidebar();
		panel.setVisible(false);
		panel.setVisible(true);
	}

	void genSidebar() {
		JPanel control = new JPanel(new BorderLayout());

		sidebar = new JPanel(new GridLayout(2, 2, 10, 10));
		channel = new JTextField("");
		joinChannel = new JButton("Channel betreten");
		leaveChannel = new JButton("Channel verlassen");
		joinChannel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				joinChannel(channel.getText().trim());
			}

		});
		leaveChannel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				leaveChannel(channel.getText().trim());
			}

		});
		sidebar.add(channel);
		sidebar.add(joinChannel);
		sidebar.add(new JLabel(""));
		sidebar.add(leaveChannel);

		control.add("North", sidebar);
//		control.add("Center", new JScrollPane(buddies));
		panel.removeAll();
		panel.add("East", control);
		panel.add("North", logInOut);
		panel.add("Center", new JScrollPane(log));
		panel.repaint();
	}

	public void joinChannel(String channel) {
		if (!irc.isConnected()) {
			return;
		}
		if (!channel.startsWith("#")) {
			channel = "#" + channel;
		}
		System.out.println("joining channel " + channel);
		irc.channel = channel;
		irc.joinChannel(channel);
		String tmp = "                          ";
		out.print("Joining channel: "
				+ tmp.substring("Joining channel: ".length()) + channel + "\n");
	}

	public void leaveChannel(String channel) {
		if (!irc.isConnected()) {
			return;
		}
		if (!channel.startsWith("#")) {
			channel = "#" + channel;
		}
		System.out.println("leaving channel " + channel);
		irc.channel = channel;
		irc.joinChannel(channel);
		String tmp = "                          ";
		out.print("Leaving channel: "
				+ tmp.substring("Leaving channel: ".length()) + channel + "\n");
	}

	private void initModules() {
		jelizaModule.init(this);
		jelizaModule.cmm = this;
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
				irc.sendMessage(irc.channel, irc.sender + ": The time is now "
						+ time);
			} else {
				if (request.toLowerCase().contains(irc._name.toLowerCase())) {
					getAnswer(request, irc.channel + "->" + irc.sender, true);
				} else {
					getAnswer(request, irc.channel + "->" + irc.sender, false);
				}
			}

		}

	}

	class WhenPrivateMessComes implements Runnable {

		public void run() {
			String request = irc.message;

			request = request.trim();
			System.out.println(irc.sender + " : " + request);

			if (request.equalsIgnoreCase("time")) {
				String time = new java.util.Date().toString();
				irc.sendMessage(irc.channel, irc.sender + ": The time is now "
						+ time);
			} else {
				getAnswer(request, irc.sender, true);
			}

		}

	}

	public void stop() {
		stopModules();
		irc.disconnect();
	}

}
