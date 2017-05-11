package grimbot.plugins;

import grimbot.Plugin;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ChatLog extends Plugin{

	public ChatLog() {
		super("^log($|\\s+|\\s.+)?");
	}

	@Override
	public String getPrimaryAlias() {
		return "log";
	}

	@Override
	public String[] getOtherAliases() {
		return null;
	}

	@Override
	public String getUsage() {
		return "NOT CURRENTLY CODED.";
	}

	@Override
	public String getDescription() {
		return "Logs chat channel immediately to a file. If channel name and time interval are left blank, will default to channel that the command was given in and a period of 24 hours.";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"log", "log #general 0M0D2h0m", "log #general all"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"channel name", "#M#D#h#m|all"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		
	}

}