package discordbot.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AboutCommand extends ListenerAdapter {
	 public AboutCommand () {
	    	// Nothing yet.
	    }
		
		public void onMessageReceived(final MessageReceivedEvent event) {
			User author = event.getAuthor();
		    if((event.isFromType(ChannelType.TEXT) || event.isFromType(ChannelType.PRIVATE)) && !author.isBot()) {
		    	String msg = event.getMessage().getContent();
		        if (msg.startsWith("!about") || msg.startsWith("!info")) {
		        	if (!event.isFromType(ChannelType.PRIVATE)) event.getChannel().sendMessage(author.getAsMention() + " | Let's speak in private.").queue();
		            author.openPrivateChannel().queue( success -> {
		                EmbedBuilder eb = new EmbedBuilder();
		                eb.setColor(java.awt.Color.BLACK);
		                eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
		                eb.setFooter("Contact Mordavrin with questions.", event.getJDA().getUserById("140901708493619200").getEffectiveAvatarUrl());
		                eb.setDescription("__**In-Character (IC) Attributes**__ "
		                		+ "\n**Name:** Atticus P. Grimaldi"
		                		+ "\n**Psuedonym:** \"Grim\""
		                		+ "\n**Guild:** Royal Apothecary Society"
		                		+ "\n**Title:** Apothecary Emeritus"
		                		+ "\n**Description:** As a floating, supernatural skull, what Grim lacks in corporeal limbs he makes up for in wit and candor. A large, sanguine-hued garnet sits tightly wedged in his left eye-socket; he claims it gives him divining power. Black runes etch his bones."
		                		+ "\n\n__**Out-of-Character (OOC) Attributes**__"
		                		+ "\n**Faction:** Horde"
		                		+ "\n**Realm:** Wyrmrest Accord"
		                		+ "\n**Region:** US"
		                		+ "\n**Website:** https://www.royalapothecarysociety.net"
		                		+ "\n\nEnter `!help` for a list of commands.");
		                MessageEmbed e = eb.build();
		                MessageBuilder mb = new MessageBuilder();
		                mb.setEmbed(e);
		                Message m = mb.build();
		                author.getPrivateChannel().sendMessage(m).queue();
		            });
		        }
		    }
		}

}
