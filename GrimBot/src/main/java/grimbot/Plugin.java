package grimbot;

import java.util.regex.Pattern;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Plugin {
	
	public Pattern pattern;
	
	public Plugin (String p) {
		pattern = Pattern.compile(p);
	}

	// Config File (if Needed)
	//public abstract JSONObject getConfig();
	
	// Return the plugin name
	public abstract String getPrimaryAlias();
	
	// Return command aliases
	public abstract String[] getOtherAliases();
	
	// Return short description for the help menu
	public abstract String getUsage();
	
	// Return longer description
	public abstract String getDescription();
	
	// Return examples
	public abstract String[] getExamples();
	
	// Return command parameters
	public abstract String[] getParameters();
	
	// Channel restrictions
	//public abstract String[] getChannelRestrictions();
	
	// Tag restrictions
	//public abstract String[] getChannelRestrictions();
	
	// Primary 
	public abstract void handleMessage(String msg, MessageReceivedEvent event);

}
