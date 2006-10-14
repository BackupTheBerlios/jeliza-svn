package chat.base;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import javax.swing.tree.*;

public class JElizaIRC {

	chat.irc.base.Chatter chIrc = null;

	JTextArea log = new JTextArea();

	JDialog d = null;

	String ircUser = "JEliza";

	String ircServer = "irc.freenode.net";

	String ircPw = "";

	String ircChannel = "";

	public JFrame f;

	JTree buddies = null;

	JPanel sidebar;

	DefaultTreeModel buddyModel = null;

	DefaultMutableTreeNode root = null;

	ArrayList buddyModelAL = new ArrayList();

	OutputStream logFile = null;

	public JPanel panel = new JPanel(new BorderLayout());

	public JTextField channel = null;

	JButton joinChannel = new JButton("In Channel");
	
	JButton leaveChannel = new JButton("In Channel");
	
	JPanel logInOut = null;
	
	public JElizaIRC() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			System.out.println("Sie verwenden ein komisches Java ...");
			e.printStackTrace();
			System.exit(2);
		} catch (InstantiationException e) {
			System.out.println("Kann UIManager nicht instanziieren ...");
			e.printStackTrace();
			System.err.println("Fahre trotz Fehler fort.");
		} catch (IllegalAccessException e) {
			System.out.println("Sie verwenden ein komisches Java ...");
			e.printStackTrace();
			System.exit(2);
		} catch (UnsupportedLookAndFeelException e) {
			System.out
					.println("Was, bitte, verwenden sie fuer ein Betriebsystem?");
			e.printStackTrace();
			System.exit(2);
		}

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

		root = new DefaultMutableTreeNode("IRC Nicknames in Channel");
		buddyModel = new DefaultTreeModel(root);
		buddies = new JTree(buddyModel);

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
				for (int y = 0; y < p.length ; y++) {
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
				// ircChannel = channelIrc.getText();
				d.setVisible(false);
				loginIrc(userIrc.getText().trim(), pass, ircChannel, serverIrc.getText().trim());

				chIrc.whenNewMessageComes = new Runnable() {

					public void run() {
						// updateBuddyListIcq();
						genSidebar();
					}

				};

				// updateBuddyListIcq();
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
		chIrc = new chat.irc.base.Chatter(u, pw, chan, serv, new OutputStream() {

			public void write(int b) throws IOException {
				log.append(Character.toString((char) b));
				logFile.write(b);
			}

		});
		genSidebar();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		genSidebar();
	}

	public void logoutIrc() {
		chIrc.stop();
		buddies.removeAll();
		chIrc = null;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		genSidebar();
		panel.setVisible(false);
		panel.setVisible(true);
	}

	public static void main(String[] args) {
		new JElizaIRC();
	}

	void genSidebar() {
		JPanel control = new JPanel(new BorderLayout());

		sidebar = new JPanel(new GridLayout(2, 2, 10, 10));
		channel = new JTextField("");
		joinChannel = new JButton("Channel betreten");
		leaveChannel = new JButton("Channel verlassen");
		if (chIrc != null) {
			joinChannel.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					chIrc.joinChannel(channel.getText().trim());
				}

			});
			leaveChannel.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					chIrc.leaveChannel(channel.getText().trim());
				}

			});
		} 
		sidebar.add(channel);
		sidebar.add(joinChannel);
		sidebar.add(new JLabel(""));
		sidebar.add(leaveChannel);

		control.add("North", sidebar);
		control.add("Center", new JScrollPane(buddies));
		panel.removeAll();
		panel.add("East", control);
		panel.add("North", logInOut);
		panel.add("Center", new JScrollPane(log));
		panel.repaint();
	}

	public synchronized void updateBuddyListIcq() {
		String[] bu = chIrc.pr.getFileBuddies();
		for (int y = 0; y < buddyModelAL.size(); y++) {
			DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) buddyModelAL.get(y);
			buddyModel.removeNodeFromParent(dmtn);
		}
		for (int z = 0; z < bu.length ; z++) {
			String b = bu[z];
			DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(b);
			buddyModel.insertNodeInto(dmtn, root, buddyModel
					.getChildCount(root));
			buddyModelAL.add(dmtn);
		}

		f.repaint();
	}

}
