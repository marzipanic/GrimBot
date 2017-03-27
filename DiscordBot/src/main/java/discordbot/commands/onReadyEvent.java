package discordbot.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class onReadyEvent extends ListenerAdapter{
	
	public void onReady(ReadyEvent event) {
		String out = "\n This bot is running on the following servers: \n";
		
		for (Guild g : event.getJDA().getGuilds()) {
			out += g.getName() + " (" + g.getId() + ") \n";
		}
		
		System.out.println(out);
		
		for (Guild g : event.getJDA().getGuilds()) {
			g.getTextChannels().get(0).sendMessage(
					"Atticus P. Grimaldi, at your service. [Enter `!help` for assistance.]"
					).queue();
		}
	}
}
