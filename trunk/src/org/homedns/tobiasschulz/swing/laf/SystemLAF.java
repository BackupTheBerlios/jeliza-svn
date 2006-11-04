package org.homedns.tobiasschulz.swing.laf;

import javax.swing.UIManager;

public class SystemLAF {
	private final String METAL = "javax.swing.plaf.metal.MetalLookAndFeel";

	private final String WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel ";

	private final String GTK = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
	
	private final String MOTIF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

	private String OperatingSystem = "unknown";

	private String LAFtoSet = "";

	public SystemLAF() {
		checkOS();
		setLAF();
	}

	private void checkOS() {
		OperatingSystem = System.getProperty("os.name");
	}

	private void setLAF() {
		LAFtoSet = METAL;

		if (OperatingSystem.startsWith("Windows"))
			LAFtoSet = WINDOWS;

		if (OperatingSystem.startsWith("Linux") )
			LAFtoSet = GTK;

		if (OperatingSystem.startsWith("SunOS"))
			LAFtoSet = MOTIF;

		try {
			UIManager.setLookAndFeel(LAFtoSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}