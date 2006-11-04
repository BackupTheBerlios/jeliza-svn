/*------------------------------------------------------------------------------
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is levelonelabs.com code.
 * The Initial Developer of the Original Code is Level One Labs. Portions
 * created by the Initial Developer are Copyright (C) 2001 the Initial
 * Developer. All Rights Reserved.
 *
 *         Contributor(s):
 *             Scott Oster      (ostersc@alum.rpi.edu)
 *             Steve Zingelwicz (sez@po.cwru.edu)
 *             William Gorman   (willgorman@hotmail.com)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable
 * instead of those above. If you wish to allow use of your version of this
 * file only under the terms of either the GPL or the LGPL, and not to allow
 * others to use your version of this file under the terms of the NPL, indicate
 * your decision by deleting the provisions above and replace them with the
 * notice and other provisions required by the GPL or the LGPL. If you do not
 * delete the provisions above, a recipient may use your version of this file
 * under the terms of any one of the NPL, the GPL or the LGPL.
 *----------------------------------------------------------------------------*/

package com.levelonelabs.aimbot.modules;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.levelonelabs.aim.AIMBuddy;
import com.levelonelabs.aimbot.AIMBot;
import com.levelonelabs.aimbot.BotModule;

/**
 * Default module to give non AI responses to non-commands
 * 
 * @author (matty501 AT yahoo.com)
 */
public class DefaultModule extends BotModule {
	private static ArrayList services;

	/**
	 * Initialize the service commands.
	 */
	static {
		// init the services
		services = new ArrayList();
		services.add("addTerm");
	}

	/**
	 * Constructor for the Default module
	 * 
	 * @param bot
	 */
	public DefaultModule(AIMBot bot) {
		super(bot);
	}

	/**
	 * Gets the services
	 * 
	 * @return The services
	 */
	public ArrayList getServices() {
		return services;
	}

	/**
	 * Gets the name of the module
	 * 
	 * @return The name value
	 */
	public String getName() {
		return "Default Module";
	}

	/**
	 * Describes the usage of the module
	 * 
	 * @return the usage of the module
	 */
	public String help() {
		StringBuffer sb = new StringBuffer();
		sb
				.append("Default Module\nThis will output default text if the input \n");
		sb.append("is not recognized\n");
		sb.append("<B>addTerm <i>TEXT</i></B> "
				+ "(add possible lines for the module to output) *Admin only*");
		return sb.toString();
	}

	/**
	 * Responds to an unknown command, or adds a term to respond with
	 * 
	 * @param buddy
	 *            requesting buddy
	 * @param query
	 *            the message
	 */
	public void performService(AIMBuddy buddy, String query) {
		StringTokenizer st = new StringTokenizer(query);
		String output = "";

		// potential future function to add terms for the bot to reply with at
		// random
		if (query.toLowerCase().startsWith("addTerm")) {
			if (!buddy.hasRole(AIMBot.ROLE_ADMINISTRATOR)) {
				super.sendMessage(buddy, "Sorry, only admins can add terms");
				return;
			}
			if (st.countTokens() < 2) {
				super.sendMessage(buddy, "ERROR:\n" + help());
				return;
			}

			String imcommand = st.nextToken();
			String term = st.nextToken();
			while (st.hasMoreTokens()) {
				term = term + " " + st.nextToken();
			}
			super.sendMessage(buddy,
					"Sorry, this feature hasn't been implemented yet");
			// addTerm(term);
		} else {
			output = "Sorry, I don't understand that\n type \"help\" to see available commands";
			super.sendMessage(buddy, output);
		}
	}
}
