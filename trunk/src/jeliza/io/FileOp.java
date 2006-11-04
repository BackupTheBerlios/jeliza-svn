package jeliza.io;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import org.homedns.tobiasschulz.io.*;

/**
 * @author Tobias Schulz
 */
public class FileOp {

	static String dummy = "          ";

	public static void copyFile(File f1, File f2) throws IOException {
		if (f1.isFile()) {
			FileManager.copyFile(f1.toString(), f2.toString());
		}
		if (f1.isDirectory()) {
			copyDir(f1, f2);
		}
		System.gc();
	}

	private static void searchDirCopy(File source, File target)
			throws IOException {
		String[] arr = source.list();
		if (arr == null) {
			arr = new String[0];
		}
		for (int x = 0; x < arr.length; x++) {
			File temp = new File(arr[x]);
			File s = new File(source + "/" + temp);
			File t = new File(target + "/" + temp);
			t.getParentFile().mkdirs();
			if (s.isFile()) {
				System.out.println(s + " -> " + t);
				t.createNewFile();
				copyFile(s, t);
			}
			if (s.isDirectory()) {
				System.out.println(s + " -> " + t);
				t.mkdirs();
				searchDirCopy(s, t);
			}
		}
	}

	public static void copyDir(File f1, File f2) throws IOException {
		searchDirCopy(f1, f2);
		System.gc();
	}

	public static void delFile(File f1) throws IOException {
		if (f1.isFile()) {
			cleanFile(f1);
			System.gc();
			f1.delete();
		}
		if (f1.isDirectory()) {
			delDir(f1);
		}
		System.gc();
	}

	private static void searchDirDel(File source) throws IOException {
		String[] arr = source.list();
		if (arr == null) {
			arr = new String[0];
		}
		for (int x = 0; x < arr.length; x++) {
			File temp = new File(arr[x]);
			File s = new File(source + "/" + temp);
			if (s.isDirectory()) {
				System.out.println(s + " -> /dev/null");
				searchDirDel(s);
			}
		}
		for (int x = 0; x < arr.length; x++) {
			File temp = new File(arr[x]);
			File s = new File(source + "/" + temp);
			if (s.isFile()) {
				System.out.println(s + " -> /dev/null");
				delFile(s);
			}
		}
		source.delete();
	}

	public static void delDir(File f1) throws IOException {
		searchDirDel(f1);
		System.gc();
	}

	public static void createFile(File f1) throws IOException {
		cleanFile(f1);
	}

	public static void cleanFile(File f1) throws IOException {
		FileManager.writeStringIntoFile("", f1.toString());
	}

	public static void moveFile(File f1, File f2) throws IOException {
		if (f1.isFile()) {
			try {
				copyFile(f1, f2);
			} catch (IOException e) {
				System.gc();
				try {
					delFile(f2);
				} catch (IOException e1) {
					throw new IOException("Cannot move " + f1.toString()
							+ " to " + f2.toString());
				}
			}
			System.gc();
			delFile(f1);
		}
		if (f1.isDirectory()) {
			moveDir(f1, f2);
		}
		System.gc();
	}

	private static void searchDirMove(File source, File target)
			throws IOException {
		String[] arr = source.list();
		if (arr == null) {
			arr = new String[0];
		}
		for (int x = 0; x < arr.length; x++) {
			File temp = new File(arr[x]);
			File s = new File(source + "/" + temp);
			File t = new File(target + "/" + temp);
			t.getParentFile().mkdirs();
			if (s.isFile()) {
				System.out.println(s + " -> " + t);
				t.createNewFile();
				moveFile(s, t);
			}
			if (s.isDirectory()) {
				System.out.println(s + " -> " + t);
				t.mkdirs();
				searchDirMove(s, t);
			}
		}
		source.delete();
	}

	public static void moveDir(File f1, File f2) throws IOException {
		searchDirMove(f1, f2);
		System.gc();
	}

	public static Iterator listDirIterator(File f1) throws IOException {
		ArrayList al = new ArrayList();
		String[] arr = listDir(f1);
		al.add("..");
		for (int x = 0; x < arr.length; x++) {
			al.add(arr[x]);
		}
		Iterator it = al.iterator();
		return it;
	}

	public static Object[] listDirSize(File f1) throws IOException {
		ArrayList al = new ArrayList();
		String[] arr = listDir(f1);
		for (int x = 0; x < arr.length; x++) {
			double len = new File(f1 + "/" + arr[x].trim()).length();
			String[] xb = { " B", " KiB", " MiB", " GiB", " TiB" };
			int count = 0;
			do {
				double rest = (len % 1024) / 1024;
				len = len * 10 / 1024;
				len = Math.round(len);
				len = len / 10;
				len = len + rest;
				count++;
			} while (len > 1024);
			int strLen = Double.toString(len).substring(0,
					Double.toString(len).indexOf(".") + 2).length()
					+ xb[count].length();
			al.add("<html><body><font color='red'>".concat(
					dummy.substring(strLen).replace(" ", "&nbsp;")).concat(
					Double.toString(len).substring(0,
							Double.toString(len).indexOf(".") + 2)).concat(
					"</font>").concat(xb[count]).concat("</body></html>"));
		}
		Object[] it = al.toArray();
		return it;
	}

	public static String[] listDir(File f1) throws IOException {
		String[] arr2;
		String[] arr = null;
		try {
			arr2 = f1.list();
			arr = new String[arr2.length + 1];

			arr[0] = "  ..  ";
			for (int x = 0; x < arr2.length; x++) {
				arr[x + 1] = "  ".concat(arr2[x]).concat("  ");
			}
		} catch (NullPointerException e) {
			throw new IOException();
		}

		return arr;
	}

	public static ImageIcon[] listDirSimpleTypes(File f1) throws IOException {
		String[] arr2 = listDir(f1);
		ImageIcon[] arr = new ImageIcon[arr2.length];

		for (int x = 0; x < arr2.length; x++) {

			File f = null;
			try {
				f = new File(f1.toString().concat("/").concat(arr2[x].trim()))
						.getCanonicalFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (f.isFile()) {
				arr[x] = new ImageIcon("./filemanager/ico/file.gif");
				continue;
			} else if (f.isDirectory()) {
				arr[x] = new ImageIcon("./filemanager/ico/dir.gif");
				continue;
			} else {
				arr[x] = new ImageIcon("./filemanager/ico/ques.gif");
				continue;
			}
		}

		return arr;
	}

	public static String[][] listDirRowData(File f1) {
		String[] arr2 = f1.list();
		String[][] arr3 = new String[10][1000];
		int arr3length = 1;
		for (int x = 0; x < arr2.length; x++) {
			arr3[0][x] = arr2[x];
			arr3length++;
		}
		arr2 = null;
		System.gc();
		String[][] arr = new String[1][arr3length];
		for (int x = 0; x < arr3[0].length; x++) {
			if (arr3[0][x] == null) {
				break;
			}
			arr[0][x] = arr3[0][x];
		}
		arr3 = null;
		System.gc();
		return arr;
	}

	public static File[] listRoots() {
		return File.listRoots();
	}

	public static Iterator listRootsIterator() {
		ArrayList al = new ArrayList();
		File[] arr = listRoots();
		for (int x = 0; x < arr.length; x++) {
			al.add(arr[x].toString());
		}
		Iterator it = al.iterator();
		return it;
	}
}
