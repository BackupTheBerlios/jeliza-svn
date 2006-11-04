/* 
 Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

 This file is part of JMegaHal.

 This software is dual-licensed, allowing you to choose between the GNU
 General Public License (GPL) and the www.jibble.org Commercial License.
 Since the GPL may be too restrictive for use in a proprietary application,
 a commercial license is also provided. Full license information can be
 found at http://www.jibble.org/licenses/

 $Author: pjm2 $
 $Id: JMegaHal.java,v 1.4 2004/02/01 13:24:06 pjm2 Exp $

 */

package jeliza.markov;

import java.util.*;
import java.net.*;
import java.io.*;

public class HiddenMarkovModel {

	// These are valid chars for words. Anything else is treated as punctuation.
	public static final String WORD_CHARS = "abcdefghijklmnopqrstuvwxyz"
			+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

	public static final String END_CHARS = ".!?";

	// This maps a single word to a HashSet of all the Quads it is in.
	private Hashtable littleSents = new Hashtable();


	// This maps a single word to a HashSet of all the Quads it is in.
	private Hashtable words2 = new Hashtable();

	// A self-referential Hashtable of Quads.
	private Hashtable quads2 = new Hashtable();

	// This maps a Quad onto a Set of Strings that may come next.
	private Hashtable next2 = new Hashtable();

	// This maps a Quad onto a Set of Strings that may come before it.
	private Hashtable previous2 = new Hashtable();

	// This maps a single word to a HashSet of all the Quads it is in.
	private Hashtable words4 = new Hashtable();

	// A self-referential Hashtable of Quads.
	private Hashtable quads4 = new Hashtable();

	// This maps a Quad onto a Set of Strings that may come next.
	private Hashtable next4 = new Hashtable();

	// This maps a Quad onto a Set of Strings that may come before it.
	private Hashtable previous4 = new Hashtable();

	// This maps a single word to a HashSet of all the Quads it is in.
	private Hashtable words6 = new Hashtable();

	// A self-referential Hashtable of Quads.
	private Hashtable quads6 = new Hashtable();

	// This maps a Quad onto a Set of Strings that may come next.
	private Hashtable next6 = new Hashtable();

	// This maps a Quad onto a Set of Strings that may come before it.
	private Hashtable previous6 = new Hashtable();

	private Random rand = new Random();

	/**
	 * Construct an instance of JMegaHal with an empty brain.
	 */
	public HiddenMarkovModel() {
		add("Guten Tag");
	}

	/**
	 * Adds an entire documents to the 'brain'. Useful for feeding in stray
	 * theses, but be careful not to put too much in, or you may run out of
	 * memory!
	 */
	public void addDocument(String uri) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new URL(uri).openStream()));
		StringBuffer buffer = new StringBuffer();
		int ch = 0;
		while ((ch = reader.read()) != -1) {
			buffer.append((char) ch);
			if (END_CHARS.indexOf((char) ch) >= 0) {
				String sentence = buffer.toString();
				sentence = sentence.replace('\r', ' ');
				sentence = sentence.replace('\n', ' ');
				add(sentence);
				buffer = new StringBuffer();
			}
		}
		add(buffer.toString());
		reader.close();
	}

	/**
	 * Adds a new sentence to the 'brain'
	 */
	public void add(String sentence) {
		System.out.println("Learning " + sentence);

		sentence = sentence.trim();
		ArrayList parts = new ArrayList();
		char[] chars = sentence.toCharArray();
		int i = 0;
		boolean punctuation = false;
		StringBuffer buffer = new StringBuffer();
		while (i < chars.length) {
			char ch = chars[i];
			if ((WORD_CHARS.indexOf(ch) >= 0) == punctuation) {
				punctuation = !punctuation;
				String token = buffer.toString();
				if (token.length() > 0) {
					parts.add(token);
				}
				buffer = new StringBuffer();
				// i++;
				continue;
			}
			buffer.append(ch);
			i++;
		}
		String lastToken = buffer.toString();
		if (lastToken.length() > 0) {
			parts.add(lastToken);
		}
		
		if (parts.size() == 1) {
			parts.add(".");
		}

		if (parts.size() >= 6) {
			for (i = 0; i < parts.size() - 5; i++) {
				// System.out.println("\"" + parts.get(i) + "\"");
				Six quad = new Six((String) parts.get(i), (String) parts
						.get(i + 1), (String) parts.get(i + 2), (String) parts
						.get(i + 3),  (String) parts
						.get(i + 4),  (String) parts
						.get(i + 5));
				if (quads6.containsKey(quad)) {
					quad = (Six) quads6.get(quad);
				} else {
					quads6.put(quad, quad);
				}

				if (i == 0) {
					quad.setCanStart(true);
				}
				// else if (i == parts.size() - 4) {
				if (i == parts.size() - 6) {
					quad.setCanEnd(true);
				}

				for (int n = 0; n < 6; n++) {
					String token = (String) parts.get(i + n);
					if (!words6.containsKey(token)) {
						words6.put(token, new HashSet(1));
					}
					HashSet set = (HashSet) words6.get(token);
					set.add(quad);
				}

				if (i > 0) {
					String previousToken = (String) parts.get(i - 1);
					if (!previous6.containsKey(quad)) {
						previous6.put(quad, new HashSet(1));
					}
					HashSet set = (HashSet) previous6.get(quad);
					set.add(previousToken);
				}

				if (i < parts.size() - 6) {
					String nextToken = (String) parts.get(i + 6);
					if (!next6.containsKey(quad)) {
						next6.put(quad, new HashSet(1));
					}
					HashSet set = (HashSet) next6.get(quad);
					set.add(nextToken);
				}

			}
		} else if (parts.size() >= 4) {
			for (i = 0; i < parts.size() - 3; i++) {
				// System.out.println("\"" + parts.get(i) + "\"");
				Four quad = new Four((String) parts.get(i), (String) parts
						.get(i + 1), (String) parts.get(i + 2), (String) parts
						.get(i + 3));
				if (quads4.containsKey(quad)) {
					quad = (Four) quads4.get(quad);
				} else {
					quads4.put(quad, quad);
				}

				if (i == 0) {
					quad.setCanStart(true);
				}
				// else if (i == parts.size() - 4) {
				if (i == parts.size() - 4) {
					quad.setCanEnd(true);
				}

				for (int n = 0; n < 4; n++) {
					String token = (String) parts.get(i + n);
					if (!words4.containsKey(token)) {
						words4.put(token, new HashSet(1));
					}
					HashSet set = (HashSet) words4.get(token);
					set.add(quad);
				}

				if (i > 0) {
					String previousToken = (String) parts.get(i - 1);
					if (!previous4.containsKey(quad)) {
						previous4.put(quad, new HashSet(1));
					}
					HashSet set = (HashSet) previous4.get(quad);
					set.add(previousToken);
				}

				if (i < parts.size() - 4) {
					String nextToken = (String) parts.get(i + 4);
					if (!next4.containsKey(quad)) {
						next4.put(quad, new HashSet(1));
					}
					HashSet set = (HashSet) next4.get(quad);
					set.add(nextToken);
				}

			}
		} else if (parts.size() >= 2) {
			for (i = 0; i < parts.size() - 1; i++) {
				// System.out.println("\"" + parts.get(i) + "\"");
				Two quad = new Two((String) parts.get(i), (String) parts
						.get(i + 1));
				if (quads2.containsKey(quad)) {
					quad = (Two) quads2.get(quad);
				} else {
					quads2.put(quad, quad);
				}

				if (i == 0) {
					quad.setCanStart(true);
				}
				// else if (i == parts.size() - 4) {
				if (i == parts.size() - 2) {
					quad.setCanEnd(true);
				}

				for (int n = 0; n < 2; n++) {
					String token = (String) parts.get(i + n);
					if (!words2.containsKey(token)) {
						words2.put(token, new HashSet(1));
					}
					HashSet set = (HashSet) words2.get(token);
					set.add(quad);
				}

				if (i > 0) {
					String previousToken = (String) parts.get(i - 1);
					if (!previous2.containsKey(quad)) {
						previous2.put(quad, new HashSet(1));
					}
					HashSet set = (HashSet) previous2.get(quad);
					set.add(previousToken);
				}

				if (i < parts.size() - 2) {
					String nextToken = (String) parts.get(i + 2);
					if (!next2.containsKey(quad)) {
						next2.put(quad, new HashSet(1));
					}
					HashSet set = (HashSet) next2.get(quad);
					set.add(nextToken);
				}

			}
		}
		littleSents.put(sentence, sentence);

	}

	/**
	 * Generate a random sentence from the brain.
	 */
	public String getSentence() {
		return getSentence(null);
	}

	/**
	 * Generate a sentence that includes (if possible) the specified word.
	 */
	public String getSentence(String word) {
		String ant = "";
		
		Random r = new Random();
		
		ArrayList ants = new ArrayList();
		
		ant = getSentence6(word, false);
		if (ant != null) {
			ants.add(ant);
		}
		ant = getSentence4(word, false);
		if (ant != null) {
			ants.add(ant);
		}
		ant = getSentence2(word, false);
		if (ant != null) {
			ants.add(ant);
		}
		if (ants.size() > 0) {
			return (String) ants.get(r.nextInt(ants.size())); 
		}
		
		
		ant = getSentence6(word, true);
		if (ant != null) {
			ants.add(ant);
		}
		if (ants.size() > 0) {
			return (String) ants.get(r.nextInt(ants.size())); 
		}
		
		ant = getSentence4(word, true);
		if (ant != null) {
			ants.add(ant);
		}
		if (ants.size() > 0) {
			return (String) ants.get(r.nextInt(ants.size())); 
		}
		
		ant = getSentence2(word, true);
		if (ant != null) {
			ants.add(ant);
		}
		if (ants.size() > 0) {
			return (String) ants.get(r.nextInt(ants.size())); 
		}
		
		
		return "...";
	}
	public String getSentence6(String word, boolean randomIfNoFound) {
		LinkedList parts = new LinkedList();

		Six[] quads;
		if (words6.containsKey(word)) {
			quads = (Six[]) ((HashSet) words6.get(word)).toArray(new Six[0]);
		} else if (randomIfNoFound) {
			quads = (Six[]) this.quads6.keySet().toArray(new Six[0]);
		} else {
			return null;
		}

		if (quads.length == 0) {
			return null;
		}

		Six middleQuad = quads[rand.nextInt(quads.length)];
		Six quad = middleQuad;

		for (int i = 0; i < 6; i++) {
			parts.add(quad.getToken(i));
		}

		while (quad.canEnd() == false) {
			String[] nextTokens = (String[]) ((HashSet) next6.get(quad))
					.toArray(new String[0]);
			String nextToken = nextTokens[rand.nextInt(nextTokens.length)];
			quad = (Six) this.quads6.get(new Six(quad.getToken(1), quad
					.getToken(2), quad.getToken(3), quad.getToken(4), quad.getToken(5), nextToken));
			parts.add(nextToken);
		}

		quad = middleQuad;
		while (quad.canStart() == false) {
			String[] previousTokens = (String[]) ((HashSet) previous6.get(quad))
					.toArray(new String[0]);
			String previousToken = previousTokens[rand
					.nextInt(previousTokens.length)];
			quad = (Six) this.quads6.get(new Six(previousToken, quad
					.getToken(0), quad.getToken(1), quad.getToken(2), quad.getToken(3), quad.getToken(4)));
			parts.addFirst(previousToken);
		}

		StringBuffer sentence = new StringBuffer();
		Iterator it = parts.iterator();
		while (it.hasNext()) {
			String token = (String) it.next();
			sentence.append(token);
		}

		return sentence.toString();
	}

	/**
	 * Generate a sentence that includes (if possible) the specified word.
	 */
	public String getSentence4(String word, boolean randomIfNoFound) {
		LinkedList parts = new LinkedList();

		Four[] quads;
		if (words4.containsKey(word)) {
			quads = (Four[]) ((HashSet) words4.get(word)).toArray(new Four[0]);
		} else if (randomIfNoFound) {
			quads = (Four[]) this.quads4.keySet().toArray(new Four[0]);
		} else {
			return null;
		}

		if (quads.length == 0) {
			return null;
		}

		Four middleQuad = quads[rand.nextInt(quads.length)];
		Four quad = middleQuad;

		for (int i = 0; i < 4; i++) {
			parts.add(quad.getToken(i));
		}

		while (quad.canEnd() == false) {
			String[] nextTokens = (String[]) ((HashSet) next4.get(quad))
					.toArray(new String[0]);
			String nextToken = nextTokens[rand.nextInt(nextTokens.length)];
			quad = (Four) this.quads4.get(new Four(quad.getToken(1), quad
					.getToken(2), quad.getToken(3), nextToken));
			parts.add(nextToken);
		}

		quad = middleQuad;
		while (quad.canStart() == false) {
			String[] previousTokens = (String[]) ((HashSet) previous4.get(quad))
					.toArray(new String[0]);
			String previousToken = previousTokens[rand
					.nextInt(previousTokens.length)];
			quad = (Four) this.quads4.get(new Four(previousToken, quad
					.getToken(0), quad.getToken(1), quad.getToken(2)));
			parts.addFirst(previousToken);
		}

		StringBuffer sentence = new StringBuffer();
		Iterator it = parts.iterator();
		while (it.hasNext()) {
			String token = (String) it.next();
			sentence.append(token);
		}

		return sentence.toString();
	}

	/**
	 * Generate a sentence that includes (if possible) the specified word.
	 */
	public String getSentence2(String word, boolean randomIfNoFound) {
		LinkedList parts = new LinkedList();

		Two[] quads;
		if (words2.containsKey(word)) {
			quads = (Two[]) ((HashSet) words2.get(word)).toArray(new Two[0]);
		} else if (randomIfNoFound) {
			quads = (Two[]) this.quads2.keySet().toArray(new Two[0]);
		} else {
			return null;
		}

		if (quads.length == 0) {
			return null;
		}

		Two middleQuad = quads[rand.nextInt(quads.length)];
		Two quad = middleQuad;

		for (int i = 0; i < 2; i++) {
			parts.add(quad.getToken(i));
		}

		while (quad.canEnd() == false) {
			String[] nextTokens = (String[]) ((HashSet) next2.get(quad))
					.toArray(new String[0]);
			String nextToken = nextTokens[rand.nextInt(nextTokens.length)];
			quad = (Two) this.quads2.get(new Two(quad.getToken(1), nextToken));
			parts.add(nextToken);
		}

		quad = middleQuad;
		while (quad.canStart() == false) {
			String[] previousTokens = (String[]) ((HashSet) previous2.get(quad))
					.toArray(new String[0]);
			String previousToken = previousTokens[rand
					.nextInt(previousTokens.length)];
			quad = (Two) this.quads2.get(new Two(previousToken, quad
					.getToken(0)));
			parts.addFirst(previousToken);
		}

		StringBuffer sentence = new StringBuffer();
		Iterator it = parts.iterator();
		while (it.hasNext()) {
			String token = (String) it.next();
			sentence.append(token);
		}

		return sentence.toString();
	}

}