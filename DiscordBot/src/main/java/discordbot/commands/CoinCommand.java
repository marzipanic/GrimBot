package discordbot.commands;

import java.util.Random;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CoinCommand extends ListenerAdapter {
	
	public void onMessageReceived(MessageReceivedEvent event) {
		User author = event.getAuthor();
	    if((event.isFromType(ChannelType.TEXT) || event.isFromType(ChannelType.PRIVATE)) && !author.isBot()) {
	    	String msg = event.getMessage().getContent();
	        if (msg.startsWith("!coin")) {
	        	String result;
	        	Random rand = new Random();
	            Integer i = rand.nextInt(2);
	            result = i.equals(0) ? "heads" : "tails";
	            String post = author.getName() + " flips a coin and gets `" + result + "`.";
	            event.getChannel().sendMessage(post).queue();
	        }
	    }
	}
}
