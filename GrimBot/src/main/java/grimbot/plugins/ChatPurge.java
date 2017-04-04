package grimbot.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import grimbot.Plugin;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
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
		event.getChannel().sendMessage("Attempting to purge channel history...").queue();
		List<Message> msgs = getHistory(event);
		recordHistory(msgs, event);
		deleteHistory(msgs, event);
        System.out.println("PURGED: #"+event.getChannel().getName());
	}
	
	private List<Message> getHistory(MessageReceivedEvent event) {
		// NOTE: Discord service limits bots to retrieving last 2 weeks of channel history ONLY.
		MessageHistory history = new MessageHistory(event.getChannel());
		history.retrievePast(100).queue();
		try {
			TimeUnit.SECONDS.sleep(1); // Delay to avoid rate limit
		} catch (InterruptedException e) {
			System.out.println("ERROR: getHistory() delay interrupted.");
			e.printStackTrace();
		}
		
		return history.getRetrievedHistory();
	}
	
	private void recordHistory(List<Message> msgs, MessageReceivedEvent event) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date(); //2017-01-30_12-08-43
		String filePath = "purge" + File.separator;
		String fileName = event.getChannel().getId() + "_" + dateFormat.format(date) + "_" + event.getChannel().getName() + ".txt";
		PrintWriter writer;
		try {
			writer = new PrintWriter(filePath + fileName, "UTF-8");
			for (int i = msgs.size() - 1; i >= 0; i--) {
				Message m = msgs.get(i);
				writer.println(m.getCreationTime()
						+ " [MID: " + m.getId() + "] " 
						+ m.getAuthor().getName() + " [UID: " + m.getAuthor().getId() +"]: "
						+ m.getContent());
			}
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Purge output file not found.");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("ERROR: Purge output file encoding not supported.");
			e.printStackTrace();
		}
	}
	
	private void deleteHistory(List<Message> msgs, MessageReceivedEvent event) {
		int count = msgs.size();
		TextChannel channel = (TextChannel) event.getChannel();
		if (count > 1) channel.deleteMessages(msgs).queue();
		else if (count == 1) msgs.get(0).delete().queue();
		else System.out.println("There were no messages to delete.");
	}

}
