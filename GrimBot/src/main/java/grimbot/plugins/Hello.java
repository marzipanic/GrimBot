package grimbot.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import grimbot.Util;
import grimbot.Plugin;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Hello extends Plugin{
	
	HashMap<Integer, String> map;
    List<Integer> keys;

	public Hello() {
		super("^(hello|hi|hola)($|\\s+|\\s.+)?");
		map = Util.getBotFileAsMap("hellos.txt");
        keys = new ArrayList<Integer>(map.keySet());
	}

	@Override
	public String getPrimaryAlias() {
		return "hi";
	}

	@Override
	public String[] getOtherAliases() {
		return new String[] {"hello"};
	}

	@Override
	public String getUsage() {
		return "Says hello.";
	}

	@Override
	public String getDescription() {
		return "The bot will say hello.";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"hello"};
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
        if (msg.startsWith("!hello") || msg.startsWith("!hi")) {
        	String post = getRandomHello();
            event.getChannel().sendMessage(post).queue();
        }
	}
	
	private String getRandomHello() {
		Random rand = new Random();
        Integer i = rand.nextInt(keys.size());
        return String.format(map.get(keys.get(i)));
	}
}
