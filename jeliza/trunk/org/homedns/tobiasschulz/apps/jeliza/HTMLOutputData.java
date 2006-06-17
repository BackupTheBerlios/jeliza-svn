package org.homedns.tobiasschulz.apps.jeliza;

/**
 * Hilfsklasse des Java-Servlets JEliza, die nur ein paar Varaiblen enthält, die die grafische Oberfläche von JEliza beschreiben
 * 
 * @author Tobias Schulz
 * @version 0.2
 */
public class HTMLOutputData {
	public static String outHead = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
			+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">"
			+ "<head>"
			+ "                <style type=\"text/css\">"
			+ "                    * html .noIe { display: none; }"
			+ "                </style>"
			+ ""
			+ "<title>JEliza</title>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"/intelligenz/jeliza/styles.css\" />"
			+ "<meta name=\"keywords\" content=\"\" />"
			+ "<meta name=\"description\" content=\"\" />"
			+ "</head>"
			+ "<body style=\"overflow-x: hidden !important; overflow-y: auto;\" "
			+ "onload=\"document.ffff.fra.focus();\">"
			+ "    <div><a name=\"top\"></a></div>"
			+ "    <div id=\"website\">"
			+ "        <div id=\"bars\">"
			+ "            <div id=\"content\">"
			+ "<div style=\"position: absolute; top: 10px; left: 10px; width: 580px; "
			+ "z-index: 20; height: 325px; overflow:scroll; border: 1px solid gray;"
			+ "padding: 5px;\">"
			+ ""
			+ "JEliza: <font color=\"red\">Hallo, wie ist dein Name? <b><big>Antworte bitte in ganzen S&auml;tzen!</big></b></font><br>\n";

	public static String outFoot = "                      <br />" + "                </div>"
			+ "            </div>" + "            <div id= \"footer\">"
			+ "                <div id=\"drop\">&#160;</div>"
			+ "            </div>" + "        </div>" + "    </div>"
			+ "</body></html>" + "";

	public static String outForm = "</div>"
			+ "<div style=\"position: absolute; top: 355px; left: 10px; width: 580px; "
			+ "z-index: 20; height: 40px;\">"
			+ "<form action=\"/intelligenz-jeliza.jsp\" method=\"get\" name=\"ffff\" style=\"display: inline;\">"
			+ "<input type=text size=46 name=fra bgcolor='mintcream' style=\"display: inline;\">"
			+ "<input type=\"hidden\" name=\"new\" value=\"nonew\">"
			+ "&nbsp;"
			+ "<input type=submit value=\"Antworten!\" bgcolor='mintcream' "
			+ "style=\"display: inline; width: 100px;\">"
			+ "</form>"
			+ "<form action=\"/intelligenz-jeliza.jsp\" method=\"post\" style=\"display: inline;\">"
			+ "<input type=\"hidden\" name=\"new\" value=\"true\">"
			+ "&nbsp;"
			+ "<input type=\"submit\" value=\"Neues Gespräch!\" bgcolor='mintcream' "
			+ "style=\"display: inline; width: 120px;\">" + "</form>";

} // class HTMLOutputData
