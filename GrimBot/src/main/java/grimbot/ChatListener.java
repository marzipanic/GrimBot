package grimbot;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChatListener extends ListenerAdapter {
	
	private final Pattern help = Pattern.compile("^((H|h)(E|e)(L|l)(P|p)|(C|c)(O|o)(M|m)(M|m)(A|a)(N|n)(D|d)(S|s)?)($|\\s+|\\s.+)?");
	
	public void onMessageReceived(final MessageReceivedEvent event) {
		
		// Handle chat logging?
		
		// Handle potential commands
		User author = event.getMessage().getAuthor();
		if ((event.isFromType(ChannelType.TEXT) || event.isFromType(ChannelType.PRIVATE)) && !author.isBot()) {
			String msg = event.getMessage().getContent();
			System.out.println("Bot Prefix: "+Bot.prefix);
			if (msg.startsWith(Bot.prefix)) {
				msg = msg.substring(Bot.prefix.length());
				
				// Handle help commands
				if(help.matcher(msg).matches()) help(author, event);
				
				// Handle all other commands
				else {
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
	
	private void help(User author, MessageReceivedEvent event){
		if (!event.isFromType(ChannelType.PRIVATE)) event.getChannel().sendMessage(author.getAsMention() 
				+ " | I sent you a direct message.").queue();
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(java.awt.Color.BLACK);
		
		// Build the message content
		String text = "__**Available Commands**__"
				+ "\n`" + Bot.prefix + "help <command name>` Displays command help.";
		for(Plugin p: Bot.plugins) {
			text +="\n`" + Bot.prefix + p.getPrimaryAlias();
			String[] parameters = p.getParameters();
			for (int i=0; i<parameters.length; i++) text += " <" + parameters[i] + ">";
			text +="` " + p.getUsage();
		}
		eb.setDescription(text);
		eb.setFooter("Bot built by Marzipanic#4639", event.getJDA().getUserById("140901708493619200").getEffectiveAvatarUrl());
		
		// Send message in a direct message to the original message author
		MessageEmbed e = eb.build();
        MessageBuilder mb = new MessageBuilder();
        mb.setEmbed(e);
        Message m = mb.build();
        author.getPrivateChannel().sendMessage(m).queue();
		
		/*"**Command:** `-----`"
		+ "\n**Aliases:** `-----`"
		+ "\n**Description:** -----"
		+ "\n**Examples:** "
		+ "\n`-----`"*/
	}
	
	private void handleCommand(Plugin plugin, String msg, MessageReceivedEvent event){
		// TO DO!!! Add functionality to check permissions for this command on the given channel, and through the given author
		plugin.handleMessage(msg, event);
	}
}
