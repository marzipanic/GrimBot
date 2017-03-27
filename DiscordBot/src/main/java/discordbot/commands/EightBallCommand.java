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


public class EightBallCommand extends ListenerAdapter {
	
    HashMap<Integer, String> map;
    List<Integer> keys;
    
    HashMap<Integer, String> mapDefault;
    List<Integer> keysDefault;
    
    public EightBallCommand () {
    	map = BotUtil.getBotFileAsMap("files/8ball.txt");
        keys = new ArrayList<Integer>(map.keySet());
        //System.out.println(map.toString());
        mapDefault = BotUtil.getBotFileAsMap("files/8balldefault.txt");
        keysDefault = new ArrayList<Integer>(mapDefault.keySet());
    }
	
	public void onMessageReceived(MessageReceivedEvent event) {
		User author = event.getAuthor();
	    if((event.isFromType(ChannelType.TEXT) || event.isFromType(ChannelType.PRIVATE)) && !author.isBot()) {
	    	String msg = event.getMessage().getContent();
	        if (msg.startsWith("!ask") || msg.startsWith("!8ball")) {
	        	String post = "";
	        	String[] cmd = msg.split(" ");
                if (cmd.length == 1) post = getDefaultAnswer();
                else if (invalidQuestion(cmd[1])) post = "Who, what, where, when, why, how. So many questions...so few answers! My divining powers have limits.";
                //else if (cmd[1].trim() == "count") post = "You doubt me? I know " + map.size() + " foreseeable answers.";
                //else if (BotUtil.isInteger(cmd[1])) post = getAnswer(Integer.parseInt(cmd[1]));
                else post = getRandomAnswer();
                event.getChannel().sendMessage(post).queue();
	        }
	    }
	}
	
	public boolean invalidQuestion(String sentence) {
		String test = sentence.toLowerCase();
		if (test.indexOf("who") == 0) return true;
		if (test.indexOf("what") == 0) return true;
		if (test.indexOf("where") == 0) return true;
		if (test.indexOf("when") == 0) return true;
		if (test.indexOf("why") == 0) return true;
		if (test.indexOf("how") == 0) return true;
		return false;
	}
	
	public String getDefaultAnswer() {
		Random rand = new Random();
        Integer i = rand.nextInt(keysDefault.size());
        if (mapDefault.get(i) != null) return String.format(mapDefault.get(keysDefault.get(i)));
        // if (map.get(i) != null) return String.format("Answer # %d: %s", keys.get(i), map.get(keys.get(i)));
        else return getDefaultAnswer();
	}
	
	public String getRandomAnswer() {
		Random rand = new Random();
        Integer i = rand.nextInt(keys.size());
        if (map.get(i) != null) return String.format(map.get(keys.get(i)));
        // if (map.get(i) != null) return String.format("Answer # %d: %s", keys.get(i), map.get(keys.get(i)));
        else return getRandomAnswer();
	}
	
	public String getAnswer(Integer i) {
		if (map.get(i) != null) return String.format("Answer # %d: %s", i, map.get(i));
		else return String.format("What, you think I can manipulate the universe for you, on command? *Kids these days.* [There is no answer with that number.]");
	}
}
