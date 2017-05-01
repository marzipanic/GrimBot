package grimbot.plugins;

import grimbot.Plugin;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ChatAutolog extends Plugin{

	public ChatAutolog() {
		super("^autolog($|\\s+|\\s.+)?");
	}

	@Override
	public String getPrimaryAlias() {
		return "autolog";
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
		return "Enables or disables automatic chat logging. If `channel name` parameter is given (including hashtag), will target that channel; otherwise, defaults to the channel where command is issued. #hours parameter is optional and specifies (as a number) over how many hours a log file should be created; if not used, then defaults to logging output to a file once every 24 hours (just before midnight).";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"autolog on", "autolog on #general 3", "autolog on #general 48", "autolog off", "autolog off #general"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"on|off", "channel name", "#hours"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		// TODO Auto-generated method stub
		
	}

}
