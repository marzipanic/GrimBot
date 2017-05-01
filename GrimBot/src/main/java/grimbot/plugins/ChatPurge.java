package grimbot.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import grimbot.Plugin;
import grimbot.Util;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ChatPurge extends Plugin{
	
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	private static Pattern intervalRegex = Pattern.compile("^(([0-9]|1[0-4])d)?(([0-9]|[1-9][0-9]|[1-2][0-9][0-9]|3[0-2][0-9]|33[0-6])h)?(([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|20[0-1][0-5][0-9]|12060)m)?");
	private static Pattern dayRegex = Pattern.compile("(([0-9]|1[0-4])d){1}");
	private static Pattern hourRegex = Pattern.compile("(([0-9]|[1-9][0-9]|[1-2][0-9][0-9]|3[0-2][0-9]|33[0-6])h){1}");
	private static Pattern minuteRegex = Pattern.compile("(([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|20[0-1][0-5][0-9]|12060)m){1}");
	
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
		return "Deletes chat history from the current channel. Multiple options for the command "
				+ "exist, including: "
				+ "\n\n`all` - deletes all messages within last 2 weeks."
				+ "\n`#` - deletes last # amount of messages."
				+ "\n`#d#h#m` - deletes messages up to # days, # hours, and # minutes, up to 2 weeks."
				+ "\n\nFull channel deletion may be available on a future Discord update. For more information"
				+ "about the current bot limitations, please read here: "
				+ "https://github.com/hammerandchisel/discord-api-docs/issues/208";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"purge", "purge all", "purge 2d14h30m", "purge 20m"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"all | # | #d#h#m"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		if (event.isFromType(ChannelType.PRIVATE)) {
			sendDM(event, "[Cannot purge messages from a direct message channel.]");
		} else {
	    	String[] cmd = msg.split(" ");
	        if (cmd.length == 1) {
	        	purge("100", event);
	        }
	        else {
	        	switch (cmd[1]) {
	        		case "all":
	        			sendDM(event, "[Purging all messages within last 2 weeks from #"+event.getChannel().getName()+"]");
	        			purgeAll(event, buildPurgeFilename(event)+"_ALL");
	        			break;
	        		default: 
	        			if (Util.isInteger(cmd[1])) {
	        				System.out.println("PURGING MESSAGES NUMBER");
	        				purge(cmd[1], event);
	        			} else if (intervalRegex.matcher(cmd[1]).matches()) {
	        				System.out.println("PURGING TIME");
	        				purgeTime(event, cmd[1]);
	        			} else {
	        				sendDM(event, "[Bot command was malformed.]");
	        			}
	        			break;
	        	}
	        }
		}
	}
	
	private void purge(String str, MessageReceivedEvent event) {
		int num = Integer.parseInt(str);
		if (num >= 1 && num <= 100) {
			sendDM(event,"[Purging last "+str+" message(s) from #"+event.getChannel().getName()+"]");
			purgeCount(num, event);
		} else {
			sendDM(event, "[Bot may only purge between 1 and 100 messages at a time.]");
		}
	}
	
	private void purgeCount(int num, MessageReceivedEvent event){
		MessageHistory history = new MessageHistory(event.getChannel());
		history.retrievePast(num).queue(success -> {
			
			// Record Messages
			List<Message> msgs = history.getRetrievedHistory();
			recordPurged(msgs, buildPurgeFilename(event));
			
			// Delete Messages
			if (num > 1 && num <= 100) {
				TextChannel channel = (TextChannel) event.getChannel();
				channel.deleteMessages(msgs).queue();
			} else if (num == 1) {
				msgs.get(0).delete().queue();
			} 
        });
	}
	
	private void purgeTime(MessageReceivedEvent event, String interval) {
		int minutes = parseInterval(interval);
		if (minutes > 0 || minutes < 20161) {
			sendDM(event, "[Purging messages within "+interval+" interval from #"+event.getChannel().getName()+"]");
			OffsetDateTime limit = event.getMessage().getCreationTime().minusMinutes(minutes);
	        purgeDate(event, buildPurgeFilename(event)+"_TIME", limit);
		} else {
			sendDM(event, "[Purge interval was not valid. Interval must be written in the format `#d#h#m`. "
					+"Maximum interval allowed is 2 weeks, specified as `14d`, `336h`, or `20160m`. Minimum "
					+"interval is 1 minute, specified as `1m`.]");
		}
	}
	
	private void purgeDate(MessageReceivedEvent event, String filename, OffsetDateTime limit) {
		MessageHistory history = new MessageHistory(event.getChannel());
		history.retrievePast(5).queue(success -> {
			
			// Record Messages
			List<Message> msgs = getMessagesAfterDate(history, limit);
			recordPurged(msgs,filename);
			
			// Delete Messages
			if (msgs.size() > 1) {
				TextChannel channel = (TextChannel) event.getChannel();
				channel.deleteMessages(msgs).queue(deleted -> {
				purgeDate(event, filename, limit);
			});
			} else if (msgs.size() == 1) {
				msgs.get(0).delete().queue();
			}
        });
	}
	
	private void purgeAll(MessageReceivedEvent event, String filename) {
		MessageHistory history = new MessageHistory(event.getChannel());
		history.retrievePast(5).queue(success -> {
			
			// Record Messages
			List<Message> msgs = history.getRetrievedHistory();
			recordPurged(msgs,filename);
			
			// Delete Messages
			if (msgs.size() > 1) {
				TextChannel channel = (TextChannel) event.getChannel();
				channel.deleteMessages(msgs).queue(deleted -> {
				purgeAll(event, filename);
			});
			} else if (msgs.size() == 1) {
				msgs.get(0).delete().queue();
			}
        });
	}
	
	private void recordPurged(List<Message> msgs, String filename) {
		try {
			File f = new File(filename);
			PrintWriter writer = null;
			
			if (f.exists() && !f.isDirectory()) {
				writer = new PrintWriter(new FileOutputStream(new File(filename), true));
				for (Message m : msgs) {
					writer.println(buildMessageString(m));
				}
				writer.close();
			} else {
				writer = new PrintWriter(filename, "UTF-8");
				for (Message m : msgs) {
					writer.append(buildMessageString(m));
				}
				writer.close();
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private String buildMessageString(Message m) {
		String date = m.getCreationTime().toString();
		return date + " [MID: " + m.getId() + "] " + m.getAuthor().getName() 
		+ " [UID: " + m.getAuthor().getId() +"]: " + m.getContent();
	}
	
	private String buildPurgeFilename(MessageReceivedEvent event) {
		String date = dateFormat.format(new Date()); //2017-01-30_12-08-43
		return "purge" + File.separator + event.getChannel().getId() + "_" 
				+ date + "_" + event.getChannel().getName() + ".txt";
	}
	
	private int parseInterval(String str) {
		// Interval "str" takes the form: #d#h#m
		int days = 0;
		int hours = 0;
		int minutes = 0;
		if (dayRegex.matcher(str).matches()) {
			String[] split = str.split("d");
			str = str.substring(str.indexOf("d")+1);
			days = Integer.parseInt(split[0]);
		}
		if (hourRegex.matcher(str).matches()) {
			String[] split = str.split("h");
			str = str.substring(str.indexOf("h")+1);
			hours = Integer.parseInt(split[0]);
		}
		if (minuteRegex.matcher(str).matches()) {
			String[] split = str.split("m");
			hours = Integer.parseInt(split[0]);
		}
		return days*1440 + hours*60 + minutes;
	}
	
	private List<Message> getMessagesAfterDate(MessageHistory history, OffsetDateTime limit) {
		List<Message> msgs = history.getRetrievedHistory();
		List<Message> pruned = new LinkedList<Message>();
		for (Message m : msgs) { 
			if (m.getCreationTime().isAfter(limit)) {
				pruned.add(m);
			}
		}
		return pruned;
	}
	
	private void sendDM(MessageReceivedEvent event, String message) {
		event.getAuthor().getPrivateChannel().sendMessage(message).queue();
	}
}
