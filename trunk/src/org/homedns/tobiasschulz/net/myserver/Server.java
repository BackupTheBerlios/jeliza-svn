package org.homedns.tobiasschulz.net.myserver;

/**
 * Ein kleiner, portabler (wegen Java), schneller, ressourcenschonender, vollständiger HTTP-Server mit webbasiertem Admin-Bereich.
 *
 * @author  Tobias Schlz
 * @version 1.0, 11.04.06
 */

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.io.*;

public class Server {
	public static int HTTP = 80;

	public static int STAT_HTTP = 8888;

	ServerSocket s;

	static Properties prop;

	private static Properties mimeTypes = new Properties();

	Executor tp = Executors.newFixedThreadPool(80);

	public static Executor gc = Executors.newFixedThreadPool(10);

	public static Executor posttp = Executors.newFixedThreadPool(10);

	public static long connections = 0;
	
	public Server(int port, String docdir) {

	}

	public Server() {
		createServer(0, null, null);
	}

	public Server(String serverName) {
		createServer(0, null, serverName);
	}

	public void setPort(int port) {
		if(port != 0) prop.setProperty("host.port", port + "");
	}

	public void setDocRoot(String documentRoot) {
		if(documentRoot != null) prop.setProperty("host.dir", documentRoot);
	}

	private void createServer(int port, String documentRoot, String serverName) {
		FileInputStream propInFile;
		FileInputStream mimePropFile;
		try {
			mimePropFile = new FileInputStream(
					"org/homedns/tobiasschulz/net/myserver/mime.conf");
			propInFile = new FileInputStream(
					"org/homedns/tobiasschulz/net/myserver/server.conf");
			prop = new Properties();
			prop.load(propInFile);
			mimeTypes.load(mimePropFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(port != 0) prop.setProperty("host.port", port + "");
		if(documentRoot != null) prop.setProperty("host.dir", documentRoot);
		if(serverName != null) prop.setProperty("host.name", serverName);
	}

	public static void main(String argv[]) {
		final Server hs = new Server();
		hs.runTheServer();
	}

	public void runTheServer() {
		HTTP = Integer.parseInt(prop.getProperty("host.port"));

		STAT_HTTP = Integer.parseInt(prop.getProperty("host.admin.port"));

		try {
			new Thread(new Runnable() {
				public void run() {
					try {
						HTTP = Integer.parseInt(prop.getProperty("host.port"));
						STAT_HTTP = Integer.parseInt(prop
								.getProperty("host.admin.port"));
						final Server hs = new Server();
						hs.runServer(HTTP, false);
					} catch (Exception e) {
					}
				}
			}).start();
			new Thread(new Runnable() {
				public void run() {
					try {
						HTTP = Integer.parseInt(prop.getProperty("host.port"));
						STAT_HTTP = Integer.parseInt(prop
								.getProperty("host.admin.port"));
						final Server hs = new Server();
						hs.runServer(STAT_HTTP, true);
					} catch (Exception e) {
					}
				}
			}).start();
			new Thread(new Runnable() {
				public void run() {
					Date date = new Date();
					while (true) {
						date = new Date();
						System.out
								.println(date.getHours()
										+ " Uhr "
										+ date.getMinutes()
										+ ":"
										+ date.getSeconds()
										+ " - Schalte GC ein ++++++++++++++++++++++++++++++++++++ ");
						Runtime.getRuntime().gc();
						try {
							Thread.currentThread().sleep(30000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected ServerSocket getServerSocket(int port) throws Exception {
		return new ServerSocket(port);
	}

	public long actCon = 0;

	private void runServer(int port, final boolean isStat) throws Exception {
		s = getServerSocket(port);
		while (true) {
			try {
				actCon++;
				final Socket us = s.accept();
				tp.execute(new Runnable() {
					public void run() {
						System.out.println("Connections: " + connections);
						WWWHandler wwwHand;
						try {
							wwwHand = new WWWHandler(us, isStat, actCon);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						wwwHand = null;
						Runtime.getRuntime().gc();
					}
				});
			} catch (IOException e) {
				System.err.println(e);
				System.exit(1);
				return;
			}
		}
	}

	class WWWHandler {

		public void STATHandler(Socket s) {
			BufferedReader is;
			PrintStream os;
			String request;
			String dat = "";
			String all = "";
			try {
				String from = s.getInetAddress().toString();
				is = new BufferedReader(new InputStreamReader(s
						.getInputStream()));
				os = new PrintStream(s.getOutputStream());

				request = is.readLine();
				if (request == null) {
					return;
				}
				Pattern p = Pattern.compile(" ");
				String[] date1 = p.split(request);
				String file = date1[1];
				println();
				println();
				println("### Zeitpunkt: " + getTime() + " ### " + "Computer "
						+ from + " will eine interne Serverseite sehen. ###\n"
						+ from + " sagt: " + request);
				// println();
				String ss = "";
				String hdrLine;
				while ((hdrLine = is.readLine()) != null
						&& hdrLine.length() != 0) {
					int ix;
					if ((ix = hdrLine.indexOf(':')) != -1) {
						println("Header von " + from + ": "
								+ hdrLine.replace(":", " ="));
					}
				}

				os.println("HTTP/1.0 200 OK");
				os.println("Content-type: text/html; charset=iso-8859-1");
				os.println("Server-name: Tobias webserver");

				os.println("");

				dat = prop.getProperty("server.page.start");
				dat += "<link rel='stylesheet' type='text/css' href='http://"
						+ prop.getProperty("host.name") + ":"
						+ prop.getProperty("host.port")
						+ "/xxxxserverxxxxicoxxxx/css.css' />";
				dat += "<style type='text/css'>"
						+ "body { padding: 0 !important; margin: 0 !important; }"
						+ ".data { position: absolute; left: 22%; top: 100px; width: 78%;  }"
						+ ".navi { z-index: 9; position: absolute; left:  0; top: 98px; width: 20%; background-color: #aaddff; "
						+ "/xxxxserverxxxxicoxxxx/dago.gif); height: 400px; padding: 0px; "
						+ "border-right: 1px solid #006699; border-bottom: 1px solid #006699; }"
						+ ".top { z-index: 10; position: absolute; right: 0px; top: 0px; width: 100%; height: 97px; background-color: #99ccff; "
						+ "background-image: url(http://"
						+ prop.getProperty("host.name")
						+ ":"
						+ prop.getProperty("host.port")
						+ "/xxxxserverxxxxicoxxxx/dago.gif);"
						+ "padding: 0px; border-bottom: 3px solid #006699; /*border-bottom: 1px solid maroon;*/ color: #007777; }"
						+ "a { color: #082984; text-decoration: none; margin-bottom: 10px; } "
						+ "a:hover { color: #218429; text-decoration: underline; } "
						+ "</style>"
						+ "<div class='top'>"
						+ "<h1> &nbsp; </h1> "
						+ "</div>"
						+ "<div class='navi'><div style=\"margin: 10px;\" id='menu'>"
						+ "<h4>Status</h4><ul class='liste'>"
						+ "<li><a href='/'><span class='dot'>&#8226;&nbsp;</span>&Uuml;bersicht</a></li>"
						+ "<li><a href='/stat.sgd'><span class='dot'>&#8226;&nbsp;</span>Zugriffe</a></li>"
						+ "<li><a href='/network/info.sgd'><span class='dot'>&#8226;&nbsp;</span>IP-Info-Tool</a></li>"
						+ "<li><a href='/restart.sgd'><span class='dot'>&#8226;&nbsp;</span>Server neu starten</a></li>"
						+ "</ul>" + "<!--</ul>--></div></div>" + ""
						+ "<div class='data'>";
				dat += "<h2>Interne Server Seite";

				println("SGD: 'file'");
				if (file.equals("/")) { // *.sgd : ServerGenerierteDatei

					dat += ": &Uuml;bersicht</h2><p>  </p>";
					dat += "<script type='text/javascript'>function MyFunc() {";
					dat += "location.href=self.location.href+'';}window.setTimeout('MyFunc()',60000);</script>";
					dat += "<div style='float:right;width:30%;'>Die Werte unter 'Server' und 'Externe Programme' k&ouml;nnen in der Datei ";
					dat += "'server.conf' im Programmverzeichnis ge&auml;ndert werden!</div>";
					dat += "<h3>Server</h3>";
					dat += "<ul>";
					dat += "<li>Hostname: " + prop.getProperty("host.name")
							+ "</li>";
					dat += "<li>Port: " + prop.getProperty("host.port")
							+ "</li>";
					dat += "<li>Port f&uuml;r <i>diese</i> Seiten: "
							+ prop.getProperty("host.admin.port") + "</li>";
					dat += "</ul>";
					dat += "<h3>Externe Programme</h3>";
					dat += "<ul>";
					dat += "<li>PHP-Interpreter: "
							+ prop.getProperty("ext.php.binary") + "</li>";
					dat += "</ul>";
					dat += "<h3>Java</h3>";
					dat += "<ul>";
					dat += "<li>RAM : "
							+ (Runtime.getRuntime().totalMemory() / 1024)
							+ " MB</li>";
					long ramfrei = ((Runtime.getRuntime().freeMemory() / 1024) * 100 / (Runtime
							.getRuntime().totalMemory() / 1024));
					dat += "<li>RAM Frei: "
							+ (ramfrei < 30 ? "<font color='red'>"
									: "<font color='greep'>");

					dat += (ramfrei) + " % "
							+ (ramfrei < 30 ? " (BAD)</font>" : " (OK)</font>")
							+ "</li>";
					// dat += "<li>RAM Belegt: " + ( 100 - ( (
					// Runtime.getRuntime().freeMemory() / 1024 ) * 100 / (
					// Runtime.getRuntime()
					// .totalMemory() / 1024 ) ) ) + " %</li>";
					dat += "</ul>";
					dat += "";
					dat += "";

					os.println(dat);

				}

				if (file.equals("/stat.sgd")) { // *.sgd : ServerGenerierteDatei

					dat += ": Statistiken</h2><p> Diese Datei wird vom Server automatisch generiert.</p><pre>";

					os.println(dat);

					String pr[] = { "cat", "log/access_log.txt" };
					Process p1 = Runtime.getRuntime().exec(pr);
					try {
						InputStreamReader isr = new InputStreamReader(p1
								.getInputStream());
						BufferedReader br = new BufferedReader(isr);
						String line = "";
						while ((line = br.readLine()) != null) {
							// all=all.concat(line.concat("\r\n"));
							os.println(line);
						}
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					dat += all;

					// dat += "</pre>" + prop.getProperty("server.page.end");
					os.println("</pre>");

				}

				if (file.equals("/network/info.sgd")) { // *.sgd :
					// ServerGenerierteDatei

					dat += ": Netzwerk-Info-Tool</h2><p>Die ist ein Tool, das m&ouml;glichst viele Informationen &uuml;ber eine IP ";
					dat += "herausfinden kann!</p><p><form target='inlineframe' action='http://"
							+ prop.getProperty("host.name")
							+ ":"
							+ prop.getProperty("host.port")
							+ "/xxxxserverxxxxicoxxxx/network.info.do.php' method='get'>"
							+ "IP: <input type='text' name='ip'> <input type='submit' value='Nach Infos suchen!'> </form></p>"
							+ "<iframe width='90%' height='300' href='' name='inlineframe' id='inlineframe'>";

					os.println(dat);

				}

				if (file.equals("/mime.sgd")) { // *.sgd : ServerGenerierteDatei

					dat += ": Registrierte Dateitypen</h2><p> Diese Datei wird vom Server automatisch generiert.</p><pre>";

					os.println(dat);

					String pr[] = { "cat",
							"org/homedns/tobiasschulz/net/myserver/mime.conf" };
					Process p1 = Runtime.getRuntime().exec(pr);
					try {
						InputStreamReader isr = new InputStreamReader(p1
								.getInputStream());
						BufferedReader br = new BufferedReader(isr);
						String line = "";
						while ((line = br.readLine()) != null) {
							// all=all.concat(line.concat("\r\n"));
							os.println(line);
						}
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					dat += all;

					// dat += "</pre>" + prop.getProperty("server.page.end");
					os.println("</pre>");

				}

				if (file.equals("/restart.sgd")) { // *.sgd :
					// ServerGenerierteDatei
					dat += ": Server restart</h2><p><a href=\"/\">Zur&uuml;ck</a></p><pre>";
					os.println(dat);
					os.println(prop.getProperty("server.page.end") + "</div>");
					Runtime.getRuntime().exit(5);
					System.out.println(output);
					os.flush();
					s.close();
				}

				os.println(prop.getProperty("server.page.end") + "</div>");

				System.out.println(output);

				os.flush();
				s.close();
				// os.println("Content-length: " + dat.length());
				// os.println("");
				// os.println(dat);

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			Runtime.getRuntime().gc();

		}

		long actCon = 1;

		public WWWHandler(Socket s, boolean isStat, long actCon) throws InterruptedException {
			if (isStat) {
				STATHandler(s);
				return;
			}

			this.actCon = actCon;

			final BufferedReader is;
			PrintStream os;
			String request = "";
			postData = "";
			String[] date1;
			boolean sendZurueck = false;
			try {
				String from = s.getInetAddress().toString();
				InputStreamReader is2 = new InputStreamReader(s
						.getInputStream());
				is = new BufferedReader(is2);
				os = new PrintStream(s.getOutputStream());

				Date date = new Date();
				// long millis = date.getTime() + 20000;

				int wieOftInSchleife = 0;

				while (!s.isClosed()) {
					// do {

					wieOftInSchleife++;
					if ((wieOftInSchleife % 5) == 0) {
						Runtime.getRuntime().gc();
					}

					connections++;

					/*
					 * if(millis < date.getTime()) { println(getTime() + " - " +
					 * millis + " - Schalte GC ein
					 * ++++++++++++++++++++++++++++++++++++ "); millis =
					 * date.getTime() + 20000; Runtime.getRuntime().gc(); }
					 */

					try {
						request = is.readLine();
					} catch (SocketTimeoutException ee) {
						s.close();
						connections--;
						return;
					}
					if (request == null) {
						s.close();
						break;
					}

					s.setSoTimeout(0);

					println();
					println();
					println("### Zeitpunkt: " + getTime() + " ### "
							+ "Computer " + from
							+ " will eine Verbindung aufbauen. ###");
					println(from + " sagt: " + request);
					// println();
					String szHeaders = "";
					String ss = "";
					String hdrLine;
					String subDom = prop.getProperty("host.name");
					int conlen2 = 0;
					boolean conWillClose = false;
					boolean scheinSubDomZuSein = false;
					while ((hdrLine = is.readLine()) != null
							&& hdrLine.length() != 0) {
						int ix;
						if (hdrLine.length() > 300) {
							sendZurueck = true;
						}
						if (hdrLine.startsWith("Content-Length")) {
							// System.out.println(hdrLine);
							Pattern p2 = Pattern.compile(" ");
							String[] tmp3 = p2.split(hdrLine);
							conlen2 = Integer.parseInt(tmp3[1]);
						}
						if (hdrLine.startsWith("Connection: close")) {
							conWillClose = true;
						}
						if (hdrLine.startsWith("Host:")) {
							Pattern p3 = Pattern.compile(" ");
							String[] tmp3 = p3.split(hdrLine);
							if (subDom != tmp3[1])
								scheinSubDomZuSein = true;
							subDom = tmp3[1];
						}

						if ((ix = hdrLine.indexOf(':')) != -1) {
							println("Header von " + from + ": "
									+ hdrLine.replaceFirst(":", " ="));
							if (sendZurueck)
								szHeaders += hdrLine;
						}
					}

					if (sendZurueck)
						os.println("HTTP/1.0 200 Was soll das");
					if (sendZurueck)
						os.println(szHeaders);
					if (sendZurueck)
						os.println("Server-name:  jedenfalls nicht IIS");
					if (sendZurueck)
						os.println("Content-length: 10000000");
					if (sendZurueck)
						os.println("Content-type: application/muell");
					if (sendZurueck)
						os.println("");
					if (sendZurueck)
						os.println(szHeaders);
					// if(sendZurueck) os.flush();
					// if(sendZurueck) s.close();
					if (sendZurueck)
						connections--;
					if (sendZurueck) {
						while (true) {
							Thread.sleep(1000);
							os.println("Das ist Muell, oder ??");
						}
					}
					if (sendZurueck)
						return;

					final int conlen = conlen2;

					String dat = new String();
					Pattern p = Pattern.compile(" ");
					// System.out.println(request);
					date1 = p.split(request);
					String method = date1[0];
					boolean isGet = false;
					boolean isPost = false;
					boolean isHead = false;
					if (method.equalsIgnoreCase("get"))
						isGet = true;
					if (method.equalsIgnoreCase("post"))
						isPost = true;
					if (method.equalsIgnoreCase("head"))
						isHead = true;
					println("HTTP-Methode: " + method);
					if (isPost) {
						posttp.execute(new Runnable() {
							public void run() {
								int pst;
								try {
									int tmp;
									int wieoft = 0;
									for (; wieoft < conlen; wieoft++) {
										tmp = is.read();
										postData += (char) tmp;
									}
								} catch (IOException ee) {
								}
							}
						});
						String tmp2 = "";
						// for(int xx = 0; xx <= 999; xx++) tmp2 += (char)xx;
						/*
						 * while(true){ if(postData != "") break; }
						 */
					}
					String file = "";
					String xx = "";
					int a = 0;
					for (int i = 0; i < date1.length; i++) {
						// System.out.println(date1[i]);
						a++;
					}
					if (a < 3) {
						s.close();
						connections--;
						return;
					}

					try {
						file = date1[1];
					} catch (Exception ee) {
						System.out.println(date1[0] + "|" + date1[1] + "|"
								+ date1[2]);
						ee.printStackTrace();
					}
					String args = "";
					println(file);
					if (date1[1].contains("?")) {
						Pattern p2 = Pattern.compile("\\?");
						String[] date2 = p2.split(date1[1]);
						file = date2[0];
						args = date2[1];
					}
					println(file);
					file = file.replaceAll("%20", " ");
					String[] li;

					String docroot = "";
					docroot = prop.getProperty("host.dir");

					boolean subDomIsUsed = false;

					if (scheinSubDomZuSein) {
						if (!subDom.equals(prop.getProperty("host.name"))) {
							// System.out.println("#" + subDom + "#" +
							// prop.getProperty("host.name") + "#");
							Pattern p4 = Pattern.compile("\\.");
							String[] tmp5 = p4.split(subDom);
							subDomIsUsed = true;
							docroot += "." + tmp5[0];
						}
					}

					docroot += "/";

					println(docroot);

					if (!file.startsWith("/xxxxserverxxxxicoxxxx"))
						file = docroot + file.substring(1);
					else
						file = file.replaceFirst("/xxxxserverxxxxicoxxxx",
								"ico");

					if (new File(file).isDirectory() && !file.endsWith("/"))
						file += "/";
					byte c_content[] = null;

					os.println(date1[2].concat(" 200 OK"));

					if (file.endsWith(".html") || file.endsWith(".htm")
							|| new File(file).isDirectory()) {
						os
								.println("Content-type: text/html; charset=iso-8859-1");
					} else if (file.endsWith(".jpg") || file.endsWith(".jpeg")) {
						os
								.println("Content-type: image/jpeg; charset=iso-8859-1");
					} else if (file.endsWith(".cgi")) {
						// kein Content-type - muss script machen
					} else if (file.endsWith(".gif")) {
						os
								.println("Content-type: image/gif; charset=iso-8859-1");
					} else {
						os.println("Content-type: " + guessMime(file)
								+ "; charset=iso-8859-1");
					}

					os.println("Server-name: Tobias webserver");

					if (file.endsWith("/")
							&& new File(file + "index.html").isFile())
						file += "index.html";

					if ((new File(file).isFile() && file.endsWith("php"))) {
						String hdrs = "";
						try {
							String all = "";
							String pr[] = { prop.getProperty("ext.php.binary"),
									"org/homedns/tobiasschulz/net/myserver/php.php", file.trim(), method, args };
							Process p1 = Runtime.getRuntime().exec(pr);
							if (isPost) {
								try {
									OutputStreamWriter isr = new OutputStreamWriter(
											p1.getOutputStream());
									BufferedWriter bw = new BufferedWriter(isr);
									String line = "";
									bw.write(postData);
									bw.flush();
									bw.close();
								} catch (IOException ioe) {
									ioe.printStackTrace();
								}
							}

							InputStreamReader isr = new InputStreamReader(p1
									.getInputStream());
							BufferedReader br = new BufferedReader(isr);
							String line = "";
							while ((line = br.readLine()) != null
									&& line.length() != 0) {
								hdrs = hdrs.concat(line).concat("\n");
							}
							while ((line = br.readLine()) != null) {
								all = all.concat(line).concat("\n");
								// if (isPost)
								// System.out.println(line.concat("\n"));
							}
							p1.waitFor();

							os.println("Content-length: " + all.length());
							if (!isHead)
								os.print(hdrs);
							if (!isHead)
								os.println();
							if (!isHead)
								os.print(all);

							/*
							 * 
							 * StreamGobbler errorGobbler = new StreamGobbler(p1
							 * .getInputStream(), "ERROR", all);
							 * errorGobbler.run(); all =
							 * errorGobbler.getString(); StreamGobbler
							 * errorGobbler2 = new StreamGobbler(p1
							 * .getErrorStream(), "ERROR", all);
							 * errorGobbler2.run(); all +=
							 * errorGobbler2.getString();
							 * 
							 * p1.waitFor(); // os.println("Content-length: " +
							 * all.length()); // os.println(""); if(!isHead)
							 * os.println(all);
							 */
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if ((new File(file).isFile() && file.endsWith("cgi"))) {
						String hdrs = "";
						String all = "";
						try {
							String pr[] = { file.trim(), args };
							Process p1 = Runtime.getRuntime().exec(pr);
							InputStreamReader isr = new InputStreamReader(p1
									.getInputStream());
							BufferedReader br = new BufferedReader(isr);
							String line = "";
							while ((line = br.readLine()) != null
									&& line.length() != 0) {
								hdrs = hdrs.concat(line).concat("\n");
							}
							while ((line = br.readLine()) != null) {
								all = all.concat(line).concat("\n");
							}
							p1.waitFor();
						} catch (Exception e) {
							all = prop.getProperty("server.page.start")
									+ "<h2>Error 500 Internel Server Error</h2> Ein interner Server Fehler ist aufgetreten: \n"
									+ e + prop.getProperty("server.page.end");
						}
						os.println("Content-length: " + all.length());
						if (!isHead)
							os.print(hdrs);
						if (!isHead)
							os.println();
						if (!isHead)
							os.print(all);
					} else if ((new File(file).isFile() && file.endsWith("sh"))) {
						String hdrs = "";
						String all = "";
						try {
							String pr[] = { "sh", file.trim(), args };
							Process p1 = Runtime.getRuntime().exec(pr);
							InputStreamReader isr = new InputStreamReader(p1
									.getInputStream());
							BufferedReader br = new BufferedReader(isr);
							String line = "";
							while ((line = br.readLine()) != null
									&& line.length() != 0) {
								hdrs = hdrs.concat(line).concat("\n");
							}
							while ((line = br.readLine()) != null) {
								all = all.concat(line).concat("\n");
							}
							p1.waitFor();
						} catch (Exception e) {
							all = prop.getProperty("server.page.start")
									+ "<h2>Error 500 Internel Server Error</h2> Ein interner Server Fehler ist aufgetreten: \n"
									+ e + prop.getProperty("server.page.end");
						}
						os.println("Content-length: " + all.length());
						if (!isHead)
							os.print(hdrs);
						if (!isHead)
							os.println();
						if (!isHead)
							os.print(all);
					} else if (new File(file).isFile()) {
						byte[] buffer;
						int line = 0;
						try {
							InputStream in = new FileInputStream(file);
							c_content = new byte[(int) new File(file).length()];
							os.println("Content-length: "
									+ new File(file).length());
							os.println("");
							// os.write(c_content);
							int b;
							for (; (b = in.read()) != -1;) {
								if (!isHead)
									os.write((byte) b);
							}
							runGc();
							/*
							 * InputStream in = new FileInputStream(file);
							 * c_content = new byte[(int) new
							 * File(file).length()]; // Single large read,
							 * should be fast. int n = in.read(c_content); //
							 * println("Habe die Datei, die " + from + " will,
							 * also " + file + " geladen. Ich werde sie jetzt
							 * //"+ // "senden!!"); // h.put(rqName, c_content);
							 * dat = ""; os.println("Content-length: " + new
							 * File(file).length()); os.println("");
							 * os.write(c_content);
							 */

						} catch (Exception e) {
						}
					} else if (new File(file).isDirectory()) {
						li = new File(file).list();
						java.util.Arrays
								.sort(li, String.CASE_INSENSITIVE_ORDER);
						dat = prop.getProperty("server.page.start");
						dat += "<div style='float:right;'><img src='"
								+ "/xxxxserverxxxxicoxxxx/null' border=0></div><!--<div style='width: "
								+ "90%;background-color:white;'>-->";
						dat += "<h2>Alle Dateien von " + file + "</h2>";
						for (int x = 0; x < li.length; x++) {
							if (new File(file + li[x]).isDirectory())
								dat += "<img src=\"/xxxxserverxxxxicoxxxx/dir.jpg\" border=\"0\">"
										+ " &nbsp; <a href=\"/"
										+ file.replaceFirst(docroot, "")
										+ li[x] + "\">" + li[x] + "</a><br>\n";
						}
						for (int x = 0; x < li.length; x++) {
							if (!new File(file + li[x]).isDirectory())
								dat += "<img src=\"/xxxxserverxxxxicoxxxx/file.gif\" border=\"0\">"
										+ " &nbsp; <a href=\"/"
										+ file.replaceFirst(docroot, "")
										+ li[x] + "\">" + li[x] + "</a><br>\n";
						}
						dat += "<!--</div>-->";
						dat += prop.getProperty("server.page.end");
						os.println("Content-length: " + dat.length());
						os.println("");
						os.println(dat);

					} else {
						dat = prop.getProperty("server.page.start")
								+ "<h2>Error 404 Not Found</h2> Die Datei wurde auf dem Server nicht gefunden!"
								+ prop.getProperty("server.page.end");

						os.println("Content-length: " + dat.length());
						os.println("");
						os.println(dat);
					}

					System.out.println(output);

					os.flush();

					connections--;

					if (conWillClose || date1[2] == "HTTP/1.0") {
						s.close();
						System.out
								.println("HTTP 1.0 is used. Closing Socket...");

						return;
					}

					System.out.println(is.ready());

					// } while(is2.ready());

					if (date1[2] == "HTTP/1.0") {
						s.close();
						System.out
								.println("HTTP 1.0 is used. Closing Socket...");
						return;
					}

					Runtime.getRuntime().gc();

				}

				// s.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("OI-Error");
			}

			Runtime.getRuntime().gc();

			return;
		}

		public String getTime() {
			try {
				String pr = "date";
				Process p1 = Runtime.getRuntime().exec(pr);
				InputStreamReader isr = new InputStreamReader(p1
						.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				String line = "";
				while ((line = br.readLine()) != null) {
					return "\"" + line + "\"";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		protected String guessMime(String fn) {
			String lcname = fn.toLowerCase();
			int extenStartsAt = lcname.lastIndexOf('.');
			if (extenStartsAt < 0) {
				if (fn.equalsIgnoreCase("makefile"))
					return "text/plain";
				return "unknown/unknown";
			}
			String exten = lcname.substring(extenStartsAt);
			String guess = getMimeType(exten, "unknown/unknown");

			runGc();

			return guess;
		}

		public String getMimeType(String type, String dflt) {
			return mimeTypes.getProperty(type, dflt);
		}

		public String output = "";

		public void println(String s) {
			output = output.concat("\r\n").concat(
					(s != "") ? "".concat("Socket " + this.actCon).concat(": ")
							: "").concat(s);
		}

		public void println() {
			println("");
		}

		public void print(String s) {
			output = output.concat(
					(s != "") ? "".concat("Socket " + this.actCon).concat(": ")
							: "").concat(s);
		}

		public String postData = "";

		public void runGc() {
			gc.execute(new Runnable() {
				public void run() {
					Runtime.getRuntime().gc();
				}
			});
		}

	}

}
