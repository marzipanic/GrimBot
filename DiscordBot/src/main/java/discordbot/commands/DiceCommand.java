package discordbot.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Random;

import discordbot.BotUtil;


public class DiceCommand extends ListenerAdapter {
    
    public DiceCommand () {
    	// Nothing yet.
    }
	
	public void onMessageReceived(MessageReceivedEvent event) {
		User author = event.getAuthor();
	    if((event.isFromType(ChannelType.TEXT) || event.isFromType(ChannelType.PRIVATE)) && !author.isBot()) {
	    	String msg = event.getMessage().getContent();
	        if (msg.startsWith("!roll") || msg.startsWith("!dice")) {
	        	String post = author.getName();
	        	String[] cmd = msg.split(" ", 2);
                if (cmd.length == 1) post += getRandom100Roll();
                else post += getRoll(cmd[1]);
                event.getChannel().sendMessage(post).queue();
	        }
	    }
	}
	
	public String getRandom100Roll() {
		Random rand = new Random();
		int i = rand.nextInt(100) + 1;
        return " rolls a lucky :game_die: and gets "+i+ " out of 100.";
	}
	
	public int getDie(String[] roll) {
		int die;
		if (roll.length == 1 || roll[0].isEmpty()) die = 1;
		else {
			if (roll[0] != "" && BotUtil.isInteger(roll[0])) die = Integer.parseInt(roll[0]);
			else die = 0;
		}
		if (die < 1 || die > 20) die = 0;
		return die;
	}
	
	public int getFaces(String faces) {
		int result = 0;
		if (BotUtil.isInteger(faces)) result = Integer.parseInt(faces);
		if (result < 1 || result > 1000000) result = 0;
		return result;
	}
	
	public String getRollResult(int die, int faces) {
		String post = " rolls "+die+" :game_die: "+faces+" and gets:";
		if (die > 0 && faces > 0) {
			Random rand = new Random();
			int sum = 0;
			for (int i = 1; i <= die; i++) {
				int r = rand.nextInt(faces)+1;
				post += " "+r;
				sum += r;
				if (i < die) post += " +";
			}
			if (die != 1) post +=" = "+sum+"";
		}
		else post = " needs a new set of dice. [Roll was malformed or out of range. Type `!help roll` for more info.]";
		return post;
	}
	
	public String getRoll(String roll) {
		String[] temp = roll.split("d");
		int die = getDie(temp);
		int faces = getFaces(temp[temp.length-1]);
		return getRollResult(die, faces);
	}
}