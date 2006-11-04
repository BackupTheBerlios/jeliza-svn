package jeliza.chat.modules;

import java.io.IOException;
import java.util.Hashtable;

import jeliza.chat.base.ChatModuleMaster;
import jeliza.core.JEliza;

public class JElizaModule extends BotModule {

	ChatModuleMaster cmm = null;

	Hashtable hirn = new Hashtable();

	JEliza jel = null;

	public void init(ChatModuleMaster mm) {
		cmm = mm;
		jel = new JEliza();
	}

	public void newMessage(final String id2, String me, boolean doAnswer) {
		final String id = id2.replace("PR ", "").trim();

		for (int y = 0; y < me.split("\n").length; y++) {
			String fra = me.split("\n")[y];
			fra = fra.trim();
			String ant = jel.answerwrapper.processQuestion(fra, jel.answerer,
					jel.mj);
			String tmp = "                                     ";
			if (doAnswer) {
				final String ant2 = ant;
				new Thread(new Runnable() {
					public void run() {
						cmm.send(id.replace("PR", "").trim(), ant2);
						;
					}
				}).start();
				cmm.out.println(id + tmp.substring(id.length()) + " : " + fra);
				cmm.out.println("JEliza to " + id
						+ tmp.substring(("JEliza to " + id).length()) + " : "
						+ ant);
			} else {
				cmm.out.println(id + tmp.substring(id.length()) + " : " + fra
						+ " (learnd only)");
			}

		}
	}

	public String getName() {
		return "JEliza Chat Module";
	}

}
