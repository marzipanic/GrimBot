package grimbot;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChatListener extends ListenerAdapter {
	
	private final Pattern helpRegex = Pattern.compile("^((H|h)(E|e)(L|l)(P|p)|(C|c)(O|o)(M|m)(M|m)(A|a)(N|n)(D|d)(S|s)?)($|\\s+|\\s.+)?");
	private String prefix = "";
	private Help help;
	
	public ChatListener(String p) {
		prefix = p;
		setHelp(new Help(prefix, Bot.plugins));
	}
	
	public void onMessageReceived(final MessageReceivedEvent event) {
		
		// Handle chat logging?
		
		// Handle potential commands
		User author = event.getMessage().getAuthor();
		if ((event.isFromType(ChannelType.TEXT) || event.isFromType(ChannelType.PRIVATE)) && !author.isBot()) {
			String msg = event.getMessage().getContent();
			// ADD COMMAND LOGGING
			if (msg.startsWith(prefix)) {
				msg = msg.substring(prefix.length());
				
				// Route Commands
				if(helpRegex.matcher(msg).matches()) {
					Help.handle(event, msg);
				} else {
					for(Plugin p: Bot.plugins) {
						if ((p.pattern).matcher(msg).matches()) {
							handleCommand(p, msg, event);
						}
					}
					// Add handler for misstyped commands?
				}
			
			}
		}
	}
	
	
	private void handleCommand(Plugin plugin, String msg, MessageReceivedEvent event){
		// TO DO!!! Add functionality to check permissions for this command on the given channel, and through the given author
		plugin.handleMessage(msg, event);
	}

	public Help getHelp() {
		return help;
	}

	public void setHelp(Help help) {
		this.help = help;
	}
}
