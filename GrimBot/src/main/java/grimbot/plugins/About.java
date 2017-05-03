package grimbot.plugins;

import grimbot.Bot;
import grimbot.Plugin;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class About extends Plugin{

	public About() {
		super("^(about|info)$");
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
		return "Provides a information about the bot's persona, creation, and sourcecode.";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"about", "info"};
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		User author = event.getAuthor();
		if (!event.isFromType(ChannelType.PRIVATE)) {
			event.getChannel().sendMessage(author.getAsMention() + " | Let's speak in private.").queue();
		}
		author.openPrivateChannel().queue( success -> {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(java.awt.Color.BLACK);
            eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
            eb.setFooter("Bot built by Marzipanic#4639", event.getJDA().getUserById("140901708493619200").getEffectiveAvatarUrl());
            eb.setDescription("__**Attributes**__ "
            		+ "\n**Name:** Grimelda"
            		+ "\n**Psuedonym:** \"GrimBot\""
            		+ "\n**Description:** What she lacks in prehensile digits and limbs, Grimelda more than "
            		+ "makes up for in wit and candor. Despite her rigorous programming, she claims she has"
            		+ "harnessed the secrets of divination and the supernatural."
            		+ "\n**Purpose:** To broker information from various services to Discord, offer improved"
            		+ " chat maintenance features, and provide other fun tools for gamers."
            		+ "\n**Website:** https://github.com/marzipanic/GrimBot"
            		+ "\n\nEnter `"+Bot.prefix+"help` for a list of commands.");
            MessageEmbed e = eb.build();
            Message m = new MessageBuilder().setEmbed(e).build();
            author.getPrivateChannel().sendMessage(m).queue();
        });
	}

}
