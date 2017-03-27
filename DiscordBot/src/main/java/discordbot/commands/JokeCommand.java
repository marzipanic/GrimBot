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


public class JokeCommand extends ListenerAdapter {
	
    HashMap<Integer, String> map;
    List<Integer> keys;
    
    public JokeCommand () {
    	map = BotUtil.getBotFileAsMap("files/jokes.txt");
        keys = new ArrayList<Integer>(map.keySet());
        System.out.println(map.toString());
    }
	
	public void onMessageReceived(MessageReceivedEvent event) {
		User author = event.getAuthor();
	    if((event.isFromType(ChannelType.TEXT) || event.isFromType(ChannelType.PRIVATE)) && !author.isBot()) {
	    	String msg = event.getMessage().getContent();
	        if (msg.startsWith("!joke")) {
	        	String post = "No joke!";
	        	String[] cmd = msg.split(" ");
                if (cmd.length == 1) post = getRandomJoke();
                else if (cmd[1].trim() == "count") post = "You doubt me? I know " + map.size() + " clever jokes.";
                else if (BotUtil.isInteger(cmd[1])) post = getJoke(Integer.parseInt(cmd[1]));
                else post = "...You speak gibberish. [Bot command was malformed. Type `!help joke` for more info.]";
                event.getChannel().sendMessage(post).queue();
	        }
	    }
	}
	
	public String getRandomJoke() {
		Random rand = new Random();
        Integer i = rand.nextInt(keys.size());
        if (map.get(i) != null) return String.format("Joke # %d: %s", keys.get(i), map.get(keys.get(i)));
        else return getRandomJoke();
	}
	
	public String getJoke(Integer i) {
		if (map.get(i) != null) return String.format("Joke # %d: %s", i, map.get(i));
		else return String.format("There is no joke with that number. ...*No joke!*");
	}
}
