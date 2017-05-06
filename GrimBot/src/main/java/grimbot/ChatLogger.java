package grimbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChatLogger extends ListenerAdapter {
	
	private static final String path = "chatlogs" + File.separator;
	private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
	private static final ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
			

	private GoogleDrive gdrive;
	private boolean cloudEnabled = false;
	private HashMap<String, PrintWriter> logs = new HashMap<String, PrintWriter>();
	
	public ChatLogger(String key) {
		System.out.println("CONSTRUCTOR FOR CHATLOGGER");
		if (key != null) {
			cloudEnabled = true;
			gdrive = new GoogleDrive(key);
		}
		InitializeLogBackups();
	}
	
	private void InitializeLogBackups() {
		System.out.println("Backups handled...");
		ex.scheduleAtFixedRate(new Runnable() {
		    public void run() {
		        System.out.println("Asynchronous Backup...");
		        backupLogs();
		    }
	    }, 1, 24, TimeUnit.HOURS);
	}
	
	public void onMessageReceived(final MessageReceivedEvent event) {
		System.out.println("MESSAGE RECEIVED!");
		if (event.isFromType(ChannelType.TEXT)) {
			String name = event.getChannel().getId()+"_"+event.getChannel().getName();
			String log = buildMessageString(event.getMessage());
			writeLog(name, log);
		}
	}
	
	public void onMessageUpdate(final MessageUpdateEvent event) {
		System.out.println("MESSAGE UPDATED!");
		if (event.isFromType(ChannelType.TEXT)) {
			String name = event.getChannel().getId()+"_"+event.getChannel().getName();
			String log = buildMessageUpdateString(event.getMessage());
			writeLog(name, log);
		}
	}
	
	public void onMessageDelete(final MessageDeleteEvent event) {
	 
		System.out.println("MESSAGE DELETED!");
		if (event.isFromType(ChannelType.TEXT)) {
			String name = event.getChannel().getId()+"_"+event.getChannel().getName();
			String log = buildMessageDeleteString(event);
			writeLog(name, log);
		}
	}
	
	public void onMessageReactionAdd(final MessageReactionAddEvent event) {
		System.out.println("REACTION RECEIVED!");
		String name = event.getChannel().getId()+"_"+event.getChannel().getName();
		String log = buildReactionString(event);
		writeLog(name, log);
	}
	
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		System.out.println("REACTION REMOVED!");
		String name = event.getChannel().getId()+"_"+event.getChannel().getName();
		String log = buildReactionDeleteString(event);
		writeLog(name, log);
	}
	
	private void backupLogs() {
		System.out.println("BACKING UP!");
		HashMap<String, PrintWriter> newLogs = new HashMap<String, PrintWriter>();
		for (String name : logs.keySet()) {
			System.out.println("Backup: "+name);
			PrintWriter writer = logs.get(name);
			writer.close();
			newLogs.put(name, createLog(name));
		}
		logs = newLogs;
	}
	
	private void writeLog(String name, String log) {
		if (!logs.containsKey(name)) {
			logs.put(name, createLog(name));
		} 
		PrintWriter writer = logs.get(name);
		writer.append(log + "\r\n");
		writer.flush();
	}
	
	private PrintWriter createLog(String name) {
		System.out.println("CREATE LOG: "+name);
		OffsetDateTime now = OffsetDateTime.now();
		String filename = path+name+"_"+now.format(df)+".txt";
		try {
			File f = new File(filename);
			if (f.exists() && !f.isDirectory()) {
				filename = path+name+"_"+now.format(df)+"_duplicate.txt";
			}
			return new PrintWriter(filename, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String buildMessageString(Message m) {
		String date = m.getCreationTime().toString();
		return date + " [MID: " + m.getId() + "] " + m.getAuthor().getName() 
		+ " [UID: " + m.getAuthor().getId() +"]: " + m.getContent();
	}
	
	private String buildMessageUpdateString(Message m) {
		OffsetDateTime now = OffsetDateTime.now();
		return now.toString() + " UPDATED [MID: " + m.getId() + "] " + m.getAuthor().getName() 
		+ " [UID: " + m.getAuthor().getId() +"]: " + m.getContent();
	}
	
	private String buildMessageDeleteString(MessageDeleteEvent event) {
		OffsetDateTime now = OffsetDateTime.now();
		return now.toString() + "DELETED MESSAGE [MID: " + event.getMessageId() + "]";
	}
	
	private String buildReactionString(MessageReactionAddEvent event) {
		OffsetDateTime now = OffsetDateTime.now();
		return now.toString() + " [MID: "+event.getMessageId()+"] "+event.getUser().getName()+" [UID: "+event.getUser().getId()+"]"
				+" REACTION TO [MID: "+event.getReaction().getMessageId()+"] BY "+event.getReaction().toString();
	}
	
	private String buildReactionDeleteString(MessageReactionRemoveEvent event) {
		OffsetDateTime now = OffsetDateTime.now();
		return now.toString() + " DELETED REACTION [MID: "+event.getMessageId();
	}
	
	private class GoogleDrive {
		private String key;
		
		private GoogleDrive (String key) {
			this.key = key;
			initializeDirectory();
		}
		
		private void initializeDirectory() {
			
		}
		
		private void buildDirectory() {
			
		}
		
		private void writeLog() {
			
		}
		
	}
}
