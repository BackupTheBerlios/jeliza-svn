package chat.base;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import javax.swing.tree.*;

import com.levelonelabs.aim.AIMBuddy;

public class JElizaICQ {

	chat.aim.base.Chatter chIcq = null;

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

	public JElizaICQ(JFrame f) {
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
			logFile = new FileOutputStream("chatlog.icq.log", true);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		this.f = f;

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

		/*
		 * f.add(panel);
		 * 
		 * f.setVisible(true); f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		 */
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
			

				chIcq.whenNewMessageComes = new Runnable() {

					public void run() {
						updateBuddyListIcq();
					}

				};

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
		chIcq = new chat.aim.base.Chatter(u, pw, new OutputStream() {

			public void write(int b) throws IOException {
				log.append(Character.toString((char) b));
				logFile.write(b);
			}

		});
	}

	public void logoutIcq() {
		chIcq.stop();
		chIcq.aim.adapter = null;
		chIcq.aim.provider = null;
		chIcq = null;
		buddies.removeAll();
	}

	public static void main(String[] args) {
		new JElizaICQ(new JFrame());
	}

	void genSidebar() {
		JPanel control = new JPanel(new BorderLayout());

		sidebar = new JPanel(new GridLayout(1, 2, 10, 10));
		final JTextField buddy = new JTextField("");
		sidebar.add(buddy);
		JButton addBuddy = new JButton("Neuer Buddy");
		addBuddy.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				chIcq.pr.addBuddy(buddy.getText().trim());
			}

		});
		sidebar.add(addBuddy);

		control.add("North", sidebar);
		control.add("Center", new JScrollPane(buddies));
		panel.add("East", control);
	}

	public synchronized void updateBuddyListIcq() {
		String[] bu = chIcq.pr.getFileBuddies();
		for (int y = 0; y < buddyModelAL.size(); y++) {
			DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) buddyModelAL
					.get(y);
			buddyModel.removeNodeFromParent(dmtn);
		}
		for (int z = 0; z < bu.length; z++) {
			String b = bu[z];
			if (chIcq.aim.provider.getBuddy(b) == null) {
				chIcq.aim.provider.addBuddy(new AIMBuddy(b));
			}
			boolean online = chIcq.aim.provider.getBuddy(b).isOnline();
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

}
