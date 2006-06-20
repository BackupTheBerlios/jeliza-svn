import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.homedns.tobiasschulz.apps.jeliza.*;
import org.homedns.tobiasschulz.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Das Java-Servlet JEliza, ein Programm, welches die Menschliche Sprache
 * versteht (verstehen sollte).
 * 
 * @author Tobias Schulz
 * @version 0.2
 */
public class JEliza extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	String absoluteUrl = "/var/kunden/webs/jeliza/intelligenz/jeliza/";

	String oldFra = "";

	String oldAnt = "";

	String oldObj = "";

	boolean isQuesAnt = false;

	Regeln re;

	PrintWriter out;

	HttpSession session;

	Gehirn hirn = null;

	String outBuf = "";

	String outAll = "";

	FragenAntworter fragenAntworter = new FragenAntworter();
	
	/**
	 * Mit der Methode println wird ein String, den JEliza sagt, in den
	 * Ausgabepuffer geschrieben. Die Methode printIt senden dann den
	 * Ausgabepuffer!
	 * 
	 * @see printIt
	 * @see userSayln
	 * @param str
	 *            Der String, den JEliza sagt
	 */
	void println(String str) {
		outBuf += "JEliza: <font color=\"red\">" + str + "</font><br>" + "\n";
	}

	/**
	 * Mit der Methode println wird ein String, den der Benutzer, der mit JEliza
	 * spricht, sagt, in den Ausgabepuffer geschrieben. Die Methode printIt
	 * senden dann den Ausgabepuffer!
	 * 
	 * @see printIt
	 * @see println
	 * @param str
	 *            Der String, den der User sagt
	 */
	void userSayln(String str) {
		outBuf += "Mensch: <font color=\"green\">" + str + "</font><br>" + "\n";
	}

	/**
	 * Methode zum beantworten der HTTP-GET-Anfrage. JEliza bantwortet die Frage
	 * und sendet ein HTML-Dokument mit der Antwort.
	 * 
	 * @see printIt
	 * @param request
	 *            Der HTTP-Request
	 * @param response
	 *            Die HTTP-Antwort
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html");
		out = response.getWriter();
		session = request.getSession(true);
		hirn = new Gehirn(absoluteUrl);

		if (session.getAttribute("regeln") == null) {
			session.setAttribute("regeln", new Regeln(absoluteUrl));
		}
		re = (Regeln) session.getAttribute("regeln");

		String ofra = "";
		String fra = "";
		String ant = "";
		@SuppressWarnings("unused")
		String oldfra = "";
		@SuppressWarnings("unused")
		String oldant = "";

		oldfra = (String) session.getAttribute("oldfra");
		oldant = (String) session.getAttribute("oldant");

		ofra = request.getParameter("fra");
		fra = request.getParameter("fra");
		if (re.naechsteFra != "0") {
			fra = re.naechsteFra;
			re.naechsteFra = "0";
		}
		outBuf = (session.getAttribute("outBuf") != null) ? (String) session
				.getAttribute("outBuf") : "";
		if (fra == null || fra.length() < 2) {
			fra = "";
		}
		fra = fra.trim();

		if (fra == "") {
			printIt(request, response);
			return;
		}

		re.naechsteFra = "0";

		ant = fragenAntworter.processQuestion(fra, re, hirn);
		userSayln(ofra);
		println(ant);
		session.setAttribute("ant", ant);
		session.setAttribute("oldant", ant);
		session.setAttribute("oldfra", fra);
		session.setAttribute("outBuf", outBuf);

		Date dt = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd.MM.yyyy");
		if (!new File("/var/www/intelligenz/jeliza/log/log_" + df2.format(dt)
				+ ".txt").exists()) {
			new File("/var/www/intelligenz/jeliza/log/log_" + df2.format(dt)
					+ ".txt").createNewFile();
		}
		String log = FileManager
				.readFileIntoString("/var/www/intelligenz/jeliza/log/log_"
						+ df2.format(dt) + ".txt");
		log += df.format(dt);
		log += " - " + request.getRemoteAddr() + " : ";
		log += "Asking question '" + fra + "' ...";
		log += "\n";
		log += "                   ";
		log += "Answer is: '" + ant + "' ...";
		log += "\n";
		FileManager.writeStringIntoFile(log,
				"/var/www/intelligenz/jeliza/log/log_" + df2.format(dt)
						+ ".txt");

		printIt(request, response);
	}

	/**
	 * Methode zum beantworten der HTTP-POST-Anfrage. Bei POST-Anfragen wird das
	 * gesamte bisherige Gespraech geloescht.
	 * 
	 * @param request
	 *            Der HTTP-Request
	 * @param response
	 *            Die HTTP-Antwort
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html");
		out = response.getWriter();

		session = request.getSession(true);

		session.removeAttribute("oldfra");
		session.removeAttribute("oldant");
		session.removeAttribute("ant");
		session.removeAttribute("outBuf");

		outAll = "<script>self.location.href="
				+ "'http://tobiasschulz.homedns.org/intelligenz-jeliza.jsp';</script>";

		printIt(request, response);
	}

	/**
	 * Methode zum Schreiben des Ausgabepuffers und den HTTP-Ausgabe-Strom.
	 * 
	 * @see doGet
	 * @see doPost
	 * @param request
	 *            Der HTTP-Request
	 * @param response
	 *            Die HTTP-Antwort
	 */
	public void printIt(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		outAll = re.outAll;
		if (outAll.length() < 5) {
			out.println(HTMLOutputData.outHead);
			out.println(outBuf);
			out.println(HTMLOutputData.outForm);
			out.println(HTMLOutputData.outFoot);
		} else {
			out.println(outAll);
			outAll = "";
		}
	}


} // class JEliza
