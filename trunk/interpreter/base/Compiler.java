package interpreter.base;

import java.io.*;

import org.homedns.tobiasschulz.io.*;

import org.homedns.tobiasschulz.util.satzparser.*;

public class Compiler {

	public void compile(final VerbDataBase vdb) {
		new Thread(new Runnable() {
			public void run() {
				PrintWriter pw;

				// Aussagesatz
				try {
					pw = new PrintWriter(
							new FileOutputStream("simple-sent.jel"));
					File f = new File("wortschatz/simple-sent");
					for (File sub : f.listFiles()) {
						if (sub.getName().contains(".svn")) {
							continue;
						}
						for (File verb : sub.listFiles()) {
							if (verb.getName().contains(".svn")) {
								continue;
							}
							for (File obj : verb.listFiles()) {
								if (obj.getName().contains(".svn")) {
									continue;
								}
								System.out.println(obj);
								pw.println("if ( sub == \"" + sub.getName()
										+ "\" && verb == \"" + verb.getName()
										+ "\" && obj == \"" + obj.getName()
										+ "\" && qwrd == \"\" ) ; then");
								pw.println(" othr = true");
								pw.println("end");
							}
						}
					}
					pw.flush();
					pw.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				// Einfache Frage
				try {
					pw = new PrintWriter(
							new FileOutputStream("simple-ques.jel"));
					File f = new File("wortschatz/simple-ques");
					for (File sub : f.listFiles()) {
						if (sub.getName().contains(".svn")) {
							continue;
						}
						for (File verb : sub.listFiles()) {
							if (verb.getName().contains(".svn")) {
								continue;
							}
							for (File obj : verb.listFiles()) {
								if (obj.getName().contains(".svn")) {
									continue;
								}
								pw.println("if ( sub == \"" + sub.getName()
										+ "\" && verb == \"" + verb.getName()
										+ "\" && obj == \"" + obj.getName()
										+ "\" && qwrd == \"\" ) ; then");
								try {
									pw.println(" othr = "
											+ FileManager.readFileIntoString(
													obj + "").trim());
								} catch (IOException e) {
									e.printStackTrace();
								}
								pw.println("end");
							}
						}
					}
					pw.flush();
					pw.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				// Erweiterte Frage
				SatzParseManager spm;
				try {
					pw = new PrintWriter(new FileOutputStream("ext-ques.jel"));
					File f = new File("wortschatz/ext-ques");
					for (File qwrd : f.listFiles()) {
						if (qwrd.getName().contains(".svn")) {
							continue;
						}
						for (File sub : qwrd.listFiles()) {
							if (sub.getName().contains(".svn")) {
								continue;
							}
							for (File verb : sub.listFiles()) {
								if (verb.getName().contains(".svn")) {
									continue;
								}
								for (File obj : verb.listFiles()) {
									if (obj.getName().contains(".svn")) {
										continue;
									}
									
									pw.println("if ( sub == \"" + sub.getName()
											+ "\" && verb == \""
											+ verb.getName()
											+ "\" && obj == \"" + obj.getName().replace("null", "")
											+ "\" && qwrd == \""
											+ qwrd.getName() + "\" ) ; then");
									try {
										String s = FileManager
												.readFileIntoString(
														obj.toString()).trim();
										spm = SatzParseManager.parse(s, vdb);
										if (spm.strSubjekt != null
												|| spm.strSubjekt != "") {
											pw.println(" sub = "
													+ spm.strSubjekt);
										}
										if (spm.strVerb != null
												|| spm.strVerb != "") {
											pw
													.println(" verb = "
															+ spm.strVerb);
										}
										if (spm.strObjekt != null
												|| spm.strObjekt != "") {
											pw.println(" obj = "
													+ spm.strObjekt);
										}
										if (spm.strFrageWort != null
												|| spm.strFrageWort != "") {
											pw.println(" qwrd = "
													+ spm.strFrageWort);
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
									pw.println("end");
								}
							}
						}
					}
					pw.flush();
					pw.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

}
