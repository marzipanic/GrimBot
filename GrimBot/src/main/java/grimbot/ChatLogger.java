package grimbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import grimbot.utilities.Util;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
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
		ex.schedule(new Runnable() {
		    public void run() {
		        System.out.println("Asynchronous Backup...");
		        backupLogs();
		    }
	    }, 15, TimeUnit.SECONDS);
	}
	
	public void onMessageReceived(final MessageReceivedEvent event) {
		System.out.println("MESSAGE RECEIVED!");
		if (event.isFromType(ChannelType.TEXT)) {
			String name = event.getChannel().getId()+"_"+event.getChannel().getName();
			String log = buildMessageString(event.getMessage());
			if (!logs.containsKey(name)) {
				logs.put(name, createLog(name));
			} 
			writeLog(logs.get(name),log);
		}
	}
	
	public void onMessageUpdate(final MessageUpdateEvent event) {
		System.out.println("MESSAGE UPDATED!");
		if (event.isFromType(ChannelType.TEXT)) {
			String logID = event.getChannel().getId();
			String log = buildMessageUpdateString(event.getMessage());
			System.out.println("MESSAGE UPDATE: "+ log);
			if (!logs.containsKey(logID)) {
				logs.put(logID, createLog(logID+"_"+event.getChannel().getName()));
			} 
			writeLog(logs.get(logID),log);
		}
	}
	
	/* INACTIVE
	public void onMessageDeleteEvent(final MessageDeleteEvent event) {
	 
		System.out.println("MESSAGE DELETED!");
		if (event.isFromType(ChannelType.TEXT)) {
			String logName = event.getChannel().getId()+"_"+event.getChannel().getName();
			String log = buildMessageDeleteString(event);
			System.out.println("MESSAGE DELETE: "+ log);
			if (logs.containsKey(logName)) {
				log += "\n"+logs.get(logName);
			}
			logs.put(logName, log);
		}
	}*/
	
	public void onReactionReceived(final MessageReactionAddEvent event) {
		System.out.println("REACTION RECEIVED!");
		String logID = event.getChannel().getId();
		String log = buildReactionString(event);
		System.out.println("REACTION: "+ log);
		if (!logs.containsKey(logID)) {
			logs.put(logID, createLog(logID+"_"+event.getChannel().getName()));
		} 
		writeLog(logs.get(logID),log);
	}
	
	public void onReactionRemoved(final MessageReactionRemoveEvent event) {
		System.out.println("REACTION REMOVED!");
		String logID = event.getChannel().getId();
		String log = buildReactionDeleteString(event);
		System.out.println("REACTION DELETE: "+ log);
		if (!logs.containsKey(logID)) {
			logs.put(logID, createLog(logID+"_"+event.getChannel().getName()));
		} 
		writeLog(logs.get(logID),log);
	}
	
	private void backupLogs() {
		System.out.println("BACKING UP!");
		for (String name : logs.keySet()) {
			PrintWriter writer = logs.get(name);
			writer.close();
			logs.put(name, createLog(name));
		}
		logs.clear();
	}
	
	private void writeLog(PrintWriter writer, String log) {
		writer.append(log + "\r\n");
		writer.flush();
	}
	
	private PrintWriter createLog(String logName) {
		System.out.println("CREATE LOG: "+logName);
		String filename = path+logName+".txt";
		try {
			File f = new File(filename);
			if (f.exists() && !f.isDirectory()) {
				OffsetDateTime now = OffsetDateTime.now();
				filename = path+logName+"_"+now.format(df)+".txt";
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
