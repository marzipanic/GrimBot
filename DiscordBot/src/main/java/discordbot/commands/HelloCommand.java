package discordbot.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import discordbot.BotUtil;


public class HelloCommand extends ListenerAdapter {
	
    HashMap<Integer, String> map;
    List<Integer> keys;
    
    public HelloCommand () {
    	map = BotUtil.getBotFileAsMap("files/hellos.txt");
        keys = new ArrayList<Integer>(map.keySet());
        System.out.println(map.toString());
    }
	
	public void onMessageReceived(MessageReceivedEvent event) {
		User author = event.getAuthor();
	    if((event.isFromType(ChannelType.TEXT) || event.isFromType(ChannelType.PRIVATE)) && !author.isBot()) {
	    	String msg = event.getMessage().getContent();
	        if (msg.startsWith("!hello") || msg.startsWith("!hi")) {
	        	String post = getRandomHello();
                event.getChannel().sendMessage(post).queue();
	        }
	    }
	}
	
	public String getRandomHello() {
		Random rand = new Random();
        Integer i = rand.nextInt(keys.size());
        if (map.get(i) != null) return String.format(map.get(keys.get(i)));
        else return getRandomHello();
	}
}
