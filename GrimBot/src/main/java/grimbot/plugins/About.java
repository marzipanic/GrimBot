package grimbot.plugins;

import grimbot.Plugin;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class About extends Plugin{

	public About() {
		super("^about($|\\s+|\\s.+)?");
	}

	@Override
	public String getPrimaryAlias() {
		return "about";
	}

	@Override
	public String[] getOtherAliases() {
		return new String[] {"info"};
	}

	@Override
	public String getUsage() {
		return "Provides bot info.";
	}

	@Override
	public String getDescription() {
		return "Provides bot info.";
	}

	@Override
	public String[] getExamples() {
		return null;
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		// TODO Auto-generated method stub
		
	}

}