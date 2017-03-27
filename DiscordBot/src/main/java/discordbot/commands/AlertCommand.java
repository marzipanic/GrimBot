package discordbot.commands;

import discordbot.BotUtil;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AlertCommand extends ListenerAdapter{
	public void onMessageReceived(MessageReceivedEvent event) {
		User author = event.getAuthor();
	    if((event.isFromType(ChannelType.TEXT) || event.isFromType(ChannelType.PRIVATE)) && !author.isBot()) {
	    	String msg = event.getMessage().getContent();
	        if (msg.startsWith("!alert")) {
	        	String post = "There are no server alerts at this time.";
	        	String[] cmd = msg.split(" ");
                event.getChannel().sendMessage(post).queue();
	        }
	    }
	}
}
