package chat.modules;

import java.io.IOException;
import java.util.Hashtable;

import org.homedns.tobiasschulz.apps.jeliza.hirn.Gehirn;
import org.homedns.tobiasschulz.apps.jeliza.hirn.Satz;
import org.homedns.tobiasschulz.util.satzparser.VerbDataBase;

import chat.base.ChatModuleMaster;

public class JElizaModule extends BotModule {

	ChatModuleMaster cmm = null;

	Hashtable hirn = new Hashtable();

	VerbDataBase vdb = null;

	public void init(ChatModuleMaster mm) {
		cmm = mm;
		try {
			vdb = new VerbDataBase();
			vdb.loadFromFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public void newMessage(final String id2, String me, boolean doAnswer) {
		final String id = id2.replace("PR ", "").trim();
		if (hirn.get(id) == null) {
			hirn.put(id, Gehirn.newGehirn("./"));
		}
		if (hirn.get(id) == null) {
			cmm.out.println("Hirn error : " + id + " - " + hirn.get(id));
		}

		// String fra = m;

		for (int y = 0; y < me.split("\n").length; y++) {
			String fra = me.split("\n")[y];
			fra = fra.trim();
			String ant = ((Gehirn) hirn.get(id)).fragenAntworter
					.processQuestion(new Satz(fra, fra),
							((Gehirn) hirn.get(id)).re,
							((Gehirn) hirn.get(id)), vdb).satzPlain;
			String tmp = "                                     ";
			if (doAnswer) {
				final String ant2 = ant;
				new Thread(new Runnable() {
					public void run() {
						cmm.send(id, ant2);
						;
					}
				}).start();
				cmm.out.println(id + tmp.substring(id.length()) + " : " + fra);
				cmm.out.println("JEliza to " + id + tmp.substring(("JEliza to " + id).length())
						+ " : " + ant);
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
