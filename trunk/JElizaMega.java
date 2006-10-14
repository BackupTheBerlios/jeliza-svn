import java.io.*;

import module.megajeliza.UltraJEliza;


public class JElizaMega {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		UltraJEliza mj = new UltraJEliza();
		
		String fra = "";
		try {
			while((fra = br.readLine()) != null) {
				System.out.println(mj.ask(fra, "ERROR"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
