package grimbot.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import grimbot.Util;
import grimbot.Plugin;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MagicBall extends Plugin{
	
	HashMap<Integer, String> map;
    List<Integer> keys;
    
    HashMap<Integer, String> mapDefault;
    List<Integer> keysDefault;

	public MagicBall() {
		super("^(8ball|ask|crystalball|magicball)($|\\s+|\\s.+)?");
		map = Util.getBotFileAsMap("8ball.txt");
        keys = new ArrayList<Integer>(map.keySet());
        //System.out.println(map.toString());
        mapDefault = Util.getBotFileAsMap("8balldefault.txt");
        keysDefault = new ArrayList<Integer>(mapDefault.keySet());
	}

	@Override
	public String getPrimaryAlias() {
		return "ask";
	}

	@Override
	public String[] getOtherAliases() {
		return new String[] {"8ball", "crystalball", "magicball"};
	}

	@Override
	public String getUsage() {
		return "Consults a crystal ball";
	}

	@Override
	public String getDescription() {
		return "Bot responds to a yes or no question. Questions beginning with `who`, `what`, `when`, `where`, `why`, or `how` will not be answered. Failure to ask any question at all may result in an unusual response.";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"ask Will it rain in Numbani tomorrow?",
				"ask Does Mageroyal taste pleasant?",
				"ask Is my starship going to survive its next encounter?"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"question"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		String post = "";
    	String[] cmd = msg.split(" ",2);
        if (cmd.length == 1) post = getDefaultAnswer();
        else if (invalidQuestion(cmd[1])) post = "Who, what, where, when, why, how. Try asking me a yes or no question instead.";
        //else if (cmd[1].trim() == "count") post = "You doubt me? I know " + map.size() + " foreseeable answers.";
        //else if (BotUtil.isInteger(cmd[1])) post = getAnswer(Integer.parseInt(cmd[1]));
        else post = getRandomAnswer();
        event.getChannel().sendMessage(post).queue();
	}
	
	private boolean invalidQuestion(String sentence) {
		String test = sentence.toLowerCase();
		if (test.indexOf("who") == 0) return true;
		if (test.indexOf("what") == 0) return true;
		if (test.indexOf("where") == 0) return true;
		if (test.indexOf("when") == 0) return true;
		if (test.indexOf("why") == 0) return true;
		if (test.indexOf("how") == 0) return true;
		return false;
	}
	
	private String getDefaultAnswer() {
		Random rand = new Random();
        Integer i = rand.nextInt(keysDefault.size());
        return String.format(mapDefault.get(keysDefault.get(i)));
        // return String.format("Answer # %d: %s", keys.get(i), map.get(keys.get(i)));
	}
	
	private String getRandomAnswer() {
		Random rand = new Random();
        Integer i = rand.nextInt(keys.size());
        return String.format(map.get(keys.get(i)));
        // return String.format("Answer # %d: %s", keys.get(i), map.get(keys.get(i)));
	}
	
	private String getAnswer(Integer i) {
		if (map.get(i) != null) return String.format("Answer # %d: %s", i, map.get(i));
		else return String.format("I can't alter the universe for you. [There is no answer with that number.]");
	}

}
