package grimbot.plugins;

import java.util.Random;

import grimbot.Plugin;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Coin extends Plugin{

	public Coin(String p) {
		super("^coin($|\\s+|\\s.+)?");
	}

	@Override
	public String getPrimaryAlias() {
		return "coin";
	}

	@Override
	public String[] getOtherAliases() {
		return null;
	}

	@Override
	public String getUsage() {
		return "Flips a coin.";
	}

	@Override
	public String getDescription() {
		return "Flips a coin and returns `heads` or `tails`.";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"coin"};
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		String result;
    	Random rand = new Random();
        Integer i = rand.nextInt(2);
        result = i.equals(0) ? "heads" : "tails";
        String post = event.getAuthor().getName() + " flips a coin and gets `" + result + "`.";
        event.getChannel().sendMessage(post).queue();
	}

}
