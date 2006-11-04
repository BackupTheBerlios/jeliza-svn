package jeliza.jwwf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.*;

import org.homedns.tobiasschulz.io.FileManager;

import jeliza.markovwrapper.*;

public class JElizaWissenWortschatzFormat {
	
	public static boolean save(String profile, String file) {
		System.out.println("Loading personality " + profile + " '"
				+ System.getProperty("jeliza.profile.there.is.a.new.profile")
				+ "'");

		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return false;
		}
		Statement dbStatement =  null;
		try {
			Connection db = DriverManager.getConnection("jdbc:hsqldb:"
					+ JElizaMarkov.getProfilePath(profile) + "/db", "sa", "");
			dbStatement = db.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			dbStatement
					.executeQuery("CREATE TABLE input ( i INT, s VARCHAR(2000))");
		} catch (SQLException e) {
			// e.printStackTrace();
		}

		ResultSet rs = null;
		try {
			rs = dbStatement.executeQuery("SELECT * FROM input");
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
		
		BufferedWriter bw = null;
		try {
			bw = FileManager.openBufferedWriter(file);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		try {
			while (rs.next()) {
				String tmp = rs.getString(2).toUpperCase();
				bw.write(tmp);
				bw.newLine();
			}
			bw.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public static boolean load(String profile, String file) {
		System.out.println("Loading personality " + profile + " '"
				+ System.getProperty("jeliza.profile.there.is.a.new.profile")
				+ "'");

		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return false;
		}
		Statement dbStatement =  null;
		try {
			Connection db = DriverManager.getConnection("jdbc:hsqldb:"
					+ JElizaMarkov.getProfilePath(profile) + "/db", "sa", "");
			dbStatement = db.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			dbStatement
					.executeQuery("CREATE TABLE input ( i INT, s VARCHAR(2000))");
		} catch (SQLException e) {
			// e.printStackTrace();
		}
		
		BufferedReader bw = null;
		try {
			bw = FileManager.openBufferedReader(file);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		try {
			String line = "";
			while ((line = bw.readLine()) != null) {	
				if (line.trim().length() < 1) {
					continue;
				}
				String tmp = JElizaMarkov.format(line.trim());
				dbStatement.executeQuery("INSERT INTO input ( i, s ) VALUES ( 100, '"
						+ tmp.replace("'", "´") + "' )");
				System.out.println("INSERT INTO input ( i, s ) VALUES ( 100, '"
						+ tmp.replace("'", "´") + "' )");
			}
			bw.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
}
