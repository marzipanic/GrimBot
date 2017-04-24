package grimbot.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import grimbot.Plugin;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ChatPurge extends Plugin{

	public ChatPurge() {
		super("^purge($|\\s+|\\s.+)?");
	}

	@Override
	public String getPrimaryAlias() {
		return "purge";
	}

	@Override
	public String[] getOtherAliases() {
		return null;
	}

	@Override
	public String getUsage() {
		return "Delete channel history.";
	}

	@Override
	public String getDescription() {
		return "Saves the chat history for the specified channel to a text file, then deletes that chat history from Discord. For safety, if the channel name is left blank, nothing happens.";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"purge #general"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"text channel name"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		if (event.isFromType(ChannelType.PRIVATE)) {
			event.getChannel().sendMessage("I cannot purge messages from a direct message channel.").queue();
		} else {
			event.getChannel().sendMessage("Purging last 100 messages from this channel.").queue();
			
			MessageHistory history = new MessageHistory(event.getChannel());
			history.retrievePast(100).queue(success -> {
				List<Message> msgs = history.getRetrievedHistory();
				System.out.println("MSGS COUNT: "+msgs.size());
				try {
					// Record purged data in a text file
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
					Date date = new Date(); //2017-01-30_12-08-43
					String filename = "purge" + File.separator + event.getChannel().getId() + "_" + dateFormat.format(date) + "_" + event.getChannel().getName() + ".txt";
					PrintWriter writer = new PrintWriter(filename, "UTF-8");
					writer.println("PURGED MESSAGES:");
					for (Message m : msgs) {
						System.out.println(m.getContent());
						writer.println(
								m.getCreationTime() + " [MID: " + m.getId() + "] " + m.getAuthor().getName() 
								+ " [UID: " + m.getAuthor().getId() +"]: " + m.getContent());
					}
					writer.close();
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				TextChannel channel = (TextChannel) event.getChannel();
				channel.deleteMessages(msgs).queue();
	        });
			System.out.println("PURGED: #"+event.getChannel().getName());
		}
	}
}
