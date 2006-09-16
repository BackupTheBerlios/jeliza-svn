package org.homedns.tobiasschulz.apps.jelizateacher;

import java.awt.*;
import javax.swing.*;

/**
 * Mit JElizaTeacher kann JEliza schnell viel beigebracht werden.
 * 
 * @author Tobias Schulz
 * @version 0.2
 */
public class JElizaTeacher extends JFrame {

	private static final long serialVersionUID = 1L;

	private TeacherData td = new TeacherData();
	
	/**
	 * Der Konstruktor
	 * 
	 */
	public JElizaTeacher() {
		setLayout(new BorderLayout(10, 10));
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 4, 10, 10));
		Box panelBottom = new Box(BoxLayout.X_AXIS);
		panel.setLayout(new GridLayout(1, 4, 10, 10));
		
		Box pAlias = new Box(BoxLayout.Y_AXIS);
		// pAlias.
		JTextField tAliasWort = new JTextField("");
		JTextField tAliasBedeutet = new JTextField("");
		JButton bAliasSpeichern = new JButton("Alias speichern");
		pAlias.add(new JLabel("Aliase"));
		pAlias.add(tAliasWort);
		pAlias.add(tAliasBedeutet);
		pAlias.add(bAliasSpeichern);
		panelBottom.add(new JScrollPane(new JTextArea(td.getRemoteFileData("gehirn/alias.ghn"))));
		panel.add(pAlias);
		
		add(panel, "North");
		add(panelBottom, "Center");
		
		setSize(850, 500);
		setVisible(true);
	}
	
	/**
	 * Das Hauptprogramm.
	 * 
	 * @param args
	 *            Die Komandozeilenparameter
	 */
	public static void main(String[] args) {
		new JElizaTeacher();
	}

}
