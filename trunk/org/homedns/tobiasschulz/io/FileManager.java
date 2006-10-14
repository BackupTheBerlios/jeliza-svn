package org.homedns.tobiasschulz.io;

import java.io.*;

public class FileManager {

	protected static final int BLOCKSIZE = 256;

	public static void copyFile(String inName, String outName)
			throws FileNotFoundException, IOException {
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(
				inName));
		BufferedOutputStream os = new BufferedOutputStream(
				new FileOutputStream(outName));
		copyFile(is, os, true);
	}

	public static void copyFile(InputStream is, OutputStream os, boolean close)
			throws IOException {
		byte[] b = new byte[BLOCKSIZE];
		int i;
		while ((i = is.read(b)) != -1) {
			os.write(b, 0, i);
		}
		is.close();
		if (close)
			os.close();
	}

	public static void copyFile(Reader is, Writer os, boolean close)
			throws IOException {
		int b;
		BufferedReader bis = new BufferedReader(is);
		while ((b = is.read()) != -1) {
			os.write(b);
		}
		is.close();
		if (close)
			os.close();
	}

	public static void copyFile(String inName, PrintWriter pw, boolean close)
			throws FileNotFoundException, IOException {
		BufferedReader ir = new BufferedReader(new FileReader(inName));
		copyFile(ir, pw, close);
	}

	public static String readLine(String inName) throws FileNotFoundException,
			IOException {
		BufferedReader is = new BufferedReader(new FileReader(inName));
		String line = null;
		line = is.readLine();
		is.close();
		return line;
	}

	public static void copyFileBuffered(String inName, String outName)
			throws FileNotFoundException, IOException {
		InputStream is = new FileInputStream(inName);
		OutputStream os = new FileOutputStream(outName);
		int count = 0;
		byte[] b = new byte[BLOCKSIZE];
		while ((count = is.read(b)) != -1) {
			os.write(b, 0, count);
		}
		is.close();
		os.close();
	}

	public static String readFileIntoString(Reader is) throws IOException {
		StringBuffer sb = new StringBuffer();
		char[] b = new char[BLOCKSIZE];
		int n;
		while ((n = is.read(b)) > 0) {
			sb.append(b, 0, n);
		}
		return sb.toString();
	}

	public static String readFileIntoString(InputStream is) throws IOException {
		return readFileIntoString(new InputStreamReader(is));
	}

	public static String readFileIntoString(String s) throws IOException {
		return readFileIntoString(new FileReader(s));
	}

	public static void writeStringIntoFile(String text, String fileName)
			throws IOException {
		BufferedWriter os = new BufferedWriter(new FileWriter(fileName));
		os.write(text);
		os.flush();
		os.close();
	}

	public static BufferedReader openBufferedReader(String fileName)
			throws IOException {
		return new BufferedReader(new FileReader(fileName));
	}

	public static FileReader openFileReader(String fileName) throws IOException {
		return new FileReader(fileName);
	}

	public static FileWriter openFileWriter(String fileName) throws IOException {
		return new FileWriter(fileName);
	}

	public static BufferedWriter openBufferedWriter(String fileName)
			throws IOException {
		return new BufferedWriter(new FileWriter(fileName));
	}
}
