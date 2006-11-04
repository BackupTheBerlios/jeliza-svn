/* 
 Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

 This file is part of JMegaHal.

 This software is dual-licensed, allowing you to choose between the GNU
 General Public License (GPL) and the www.jibble.org Commercial License.
 Since the GPL may be too restrictive for use in a proprietary application,
 a commercial license is also provided. Full license information can be
 found at http://www.jibble.org/licenses/

 $Author: pjm2 $
 $Id: Quad.java,v 1.3 2004/02/01 13:24:06 pjm2 Exp $

 */

package jeliza.markov;

public class Two implements java.io.Serializable {

	public Two(String s1, String s2) {
		tokens = new String[] { s1, s2 };
	}

	public String getToken(int index) {
		return tokens[index];
	}

	public void setCanStart(boolean flag) {
		canStart = flag;
	}

	public void setCanEnd(boolean flag) {
		canEnd = flag;
	}

	public boolean canStart() {
		return canStart;
	}

	public boolean canEnd() {
		return canEnd;
	}

	public int hashCode() {
		return tokens[0].hashCode() + tokens[1].hashCode();
	}

	public boolean equals(Object o) {
		Two other = (Two) o;
		return other.tokens[0].equals(tokens[0])
				&& other.tokens[1].equals(tokens[1]);
	}

	private String[] tokens;

	private boolean canStart = false;

	private boolean canEnd = false;

}