package jeliza.chat.base;

import java.util.HashMap;
import java.util.Map;

import com.levelonelabs.aim.*;

public class AIMListener {

	/** The id of the bot for which this listener works. */
	protected String botID;

	/** The parameters that can be set for this listener. */
	protected Map parameters;

	/** The userid. */
	private String userid;

	/** The password. */
	private String password;

	/** The AIM connection provider. */
	public AIMSender provider;

	public AIMAdapter adapter = null;

	/**
	 * Constructs a new <code>ICQandAIMListener</code> listener and sets up
	 * parameters.
	 * 
	 * @param coreToUse
	 *            the Core object in use
	 * @param botToListenFor
	 *            the bot for whom to listen
	 * @param parametersToUse
	 *            the parameters for the listener and their default values
	 */
	public AIMListener(HashMap parametersToUse) {
		this.botID = "JEliza";
		this.parameters = parametersToUse;

		// Set parameters.
		this.userid = (String) this.parameters.get("userid");
		this.password = (String) this.parameters.get("password");
	}

	public void checkParameters() {
		if (this.userid == null) {
			System.out.println("Must specify a userid!");
		}
		if (this.userid.length() == 0) {
			System.out.println("Userid cannot be zero-length!");
		}
		if (this.password == null) {
			System.out.println("Must specify a password!");
		}
		if (this.password.length() == 0) {
			System.out.println("Password cannot be zero-length!");
		}
	}

	/**
	 * @see org.aitools.programd.util.ManagedProcess#shutdown()
	 */
	public void shutdown() {
		// Log out.
		this.provider.signOff();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// Try to log in.
		this.provider = new AIMClient(this.userid, this.password, "JEliza",
				"Hallo, Fremder", true);
		this.provider.signOn();
	}
}
