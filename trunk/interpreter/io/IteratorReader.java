package interpreter.io;

import java.io.*;
import java.util.*;

public class IteratorReader implements Iterable, Iterator {

	BufferedReader br = null;
	
	public IteratorReader(String str) throws FileNotFoundException {
		makeNew(new FileInputStream(str));
	}

	public IteratorReader(InputStream in) {
		makeNew(in);
	}

	void makeNew(InputStream in) {
		br = new BufferedReader(new InputStreamReader(in));
	}

	public Iterator iterator() {
		return this;
	}

	public boolean hasNext() {
		try {
			return br.ready();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String next() {
		try {
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void remove() {
//		s.pop();
	}

}
