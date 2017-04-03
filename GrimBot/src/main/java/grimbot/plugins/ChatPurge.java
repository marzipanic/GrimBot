package grimbot.plugins;

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
import net.dv8tion.jda.core.JDA;
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
		recordHistory(msgs, event.getChannel().getId(), event.getChannel().getName());
		//if (msgs.size() > 0) deleteHistory(msgs);
        event.getChannel().sendMessage("THIS CHANNEL HAS BEEN PURGED!").queue();
	}
	
	private List<Message> getHistory(MessageReceivedEvent event) {
		// NOTE: Discord service limits bots to retrieving last 2 weeks of channel history ONLY.
		MessageHistory history = new MessageHistory(event.getChannel());
		history.retrievePast(100).queue();
		try {
			System.out.println("DELAY TO FETCH MESSAGE HISTORY...");
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		return history.getRetrievedHistory();
	}
	
	private void recordHistory(List<Message> cache, String channelId, String channelName) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date(); //2017-01-30_12-08-43
		String fileName = channelId + "_" + dateFormat.format(date) + "_" + channelName + ".txt";
		PrintWriter writer;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
			System.out.println("CACHE: "+cache);
			for (int i = cache.size() - 1; i >= 0; i--) {
				Message m = cache.get(i);
				System.out.println("MESSAGE "+i+": "+m);
				writer.println(m.getCreationTime()
						+ " [MID: " + m.getId() + "] " 
						+ m.getAuthor().getName() + " [UID: " + m.getAuthor().getId() +"]: "
						+ m.getContent());
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deleteHistory(List<Message> history, JDA jda) {
		/*for (Message m : history) {
			m.deleteMessage();
		}*/
		
		System.out.println("HISTORY PURGED!");
	}

}
