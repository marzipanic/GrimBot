package grimbot.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import grimbot.Util;
import grimbot.Plugin;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Joke extends Plugin {
	
	private HashMap<Integer, String> map;
    private List<Integer> keys;

	public Joke() {
		super("^(joke|silly)($|\\s+|\\s.+)?");
		map = Util.getBotFileAsMap("jokes.txt");
        keys = new ArrayList<Integer>(map.keySet());
	}

	@Override
	public String getPrimaryAlias() {
		return "joke";
	}

	@Override
	public String[] getOtherAliases() {
		return new String[] {"silly"};
	}

	@Override
	public String getUsage() {
		return "Tells a dad joke.";
	}

	@Override
	public String getDescription() {
		return "Bot responds with a random joke. If provided a `<joke#>`, will tell the corresponding joke. If `<count>` is requested, will report number of jokes known.";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"joke", "joke 42"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"joke#"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
    	String post = "No joke!";
    	String[] cmd = msg.split(" ", 2);
        if (cmd.length == 1) post = getRandomJoke();
        else if (Util.isInteger(cmd[1])) post = getJoke(Integer.parseInt(cmd[1]));
        else if (cmd[1].trim() == "count") {
        	
        	post = "You doubt me? I know " + map.size() + " clever jokes.";
        }
        else post = "...You speak gibberish. [Bot command was malformed. Type `!help joke` for more info.]";
        event.getChannel().sendMessage(post).queue();
	}
	
	private String getRandomJoke() {
		Random rand = new Random();
        Integer i = rand.nextInt(keys.size());
        return String.format("Joke # %d: %s", keys.get(i), map.get(keys.get(i)));
	}
	
	private String getJoke(Integer i) {
		if (map.get(i) != null) return String.format("Joke # %d: %s", i, map.get(i));
		else return String.format("There is no joke with that number. ...*No joke!*");
	}

}
