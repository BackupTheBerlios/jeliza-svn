package chat.modules;

import chat.base.ChatModuleMaster;

public class BotModule {
	
	public ChatModuleMaster cmm = null;
	
	public String getName() {
		return "Unbekanntes Modul";
	}
	
	public void newMessage(String id, String m, boolean privat) {
		
	}
	
	public void init(ChatModuleMaster mm) {
		cmm = mm;
	}
	
	public void stop() {
		
	}
	
}
