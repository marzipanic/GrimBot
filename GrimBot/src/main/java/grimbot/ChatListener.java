package grimbot;

import java.util.ArrayList;
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
	private ArrayList<PluginSummary> plugins;
	private String helpMenu = "";
	
	public ChatListener() {
		initializeHelp();
	}
	
	public void onMessageReceived(final MessageReceivedEvent event) {
		
		// Handle chat logging?
		
		// Handle potential commands
		User author = event.getMessage().getAuthor();
		if ((event.isFromType(ChannelType.TEXT) || event.isFromType(ChannelType.PRIVATE)) && !author.isBot()) {
			String msg = event.getMessage().getContent();
			// ADD COMMAND LOGGING
			if (msg.startsWith(Bot.prefix)) {
				msg = msg.substring(Bot.prefix.length());
				
				// Handle help commands
				if(help.matcher(msg).matches()) {
					help(author, msg, event);
				}
				
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
	
	private void handleCommand(Plugin plugin, String msg, MessageReceivedEvent event){
		// TO DO!!! Add functionality to check permissions for this command on the given channel, and through the given author
		plugin.handleMessage(msg, event);
	}
	
	private void help(User author, String msg, MessageReceivedEvent event){
		if (!event.isFromType(ChannelType.PRIVATE)) {
			event.getChannel().sendMessage(author.getAsMention() 
					+ " | I sent you a direct message.").queue();
		}
		
		// Build the message content
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(java.awt.Color.BLACK);
        if (msg.split(" ").length == 1) eb.setDescription(helpMenu);
        else eb.setDescription(getPluginHelp(msg.split(" ")[1], eb, event));
		eb.setFooter("Bot built by Marzipanic#4639", event.getJDA().getUserById("140901708493619200").getEffectiveAvatarUrl());
		
		// Send direct message
		MessageEmbed e = eb.build();
        Message m = new MessageBuilder().setEmbed(e).build();
        author.getPrivateChannel().sendMessage(m).queue();
	}
	
	private String getPluginHelp(String cmd, EmbedBuilder eb, MessageReceivedEvent event) {
		for(PluginSummary p: plugins) {
			if ((p.regex).matcher(cmd).matches()) {
				return p.pluginText;
			}
		}
		return "`"+cmd+"` has no help text or is not a valid command.";
	}
	
	private class PluginSummary {
		private Pattern regex;
		private String menuText = "";
		private String pluginText = "";
		
		private PluginSummary(Plugin p) {
			regex = p.pattern;
			buildMenuText(p);
			buildPluginText(p);
		}

		// !help response
		private void buildMenuText(Plugin p) {
			if (p.getPrimaryAlias() != null) {
				menuText +="\n`" + Bot.prefix + p.getPrimaryAlias();
			}
			/*if (p.getParameters() != null) {
				String[] parameters = p.getParameters();
				for (int i=0; i<parameters.length; i++) menuText += " <" + parameters[i] + ">";
			}*/
			if (p.getUsage() != null) {
				menuText +="` - "+ p.getUsage();
			}
		}
		
		// !help <pluginname> response
		private void buildPluginText(Plugin p) {
			String name = p.getPrimaryAlias();
			String[] aliases = p.getOtherAliases();
			String[] params = p.getParameters();
			String desc = p.getDescription();
			String[] ex = p.getExamples();
			
			if (name != null) {
				pluginText +="\n**Command:** `" + Bot.prefix + name;
			}
			if (params != null) {
				for (int i=0; i<params.length; i++) pluginText += " <" + params[i] + ">";
			}
			pluginText += "`\n**Aliases:**";
			if (aliases != null) {
				for (int i=0; i<aliases.length; i++) pluginText += " `" + Bot.prefix + aliases[i] + "`";
			}
			if (desc != null) {
				pluginText +="\n**Description:** " + desc;
			}
			if (ex != null) {
				pluginText += "\n**Examples:** ";
				for (int i=0; i<ex.length; i++) pluginText += "\n`" + Bot.prefix + ex[i] + "`";
			}
			
		};
	}
	
	private void initializeHelp() {
		plugins = new ArrayList<PluginSummary>();
		helpMenu = "__**Available Commands**__"
				+ "\n`" + Bot.prefix + "help <command name>` Displays command help.";
		
		for(Plugin p: Bot.plugins) {
			PluginSummary ps = new PluginSummary(p);
			plugins.add(ps);
			helpMenu += ps.menuText;
			//System.out.println(ps.menuText); // Print list of commands
		}
	}
}
