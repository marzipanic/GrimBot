package grimbot.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import grimbot.Bot;
import grimbot.Plugin;
import grimbot.utilities.Util;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Joke extends Plugin {
	
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	private static final String configModOption = "jokes_mod";
    private static final String configModDefault = "owner";
    private static Connection conn = null;
    private static String jokeTable = ""; // table used to store jokes
    private static String modTag = ""; // tag used to determine default admin privileges

	public Joke() {
		super("^(joke|silly)($|\\s+|\\s.+)?");
		conn = Bot.database.conn;
		jokeTable = Bot.database.initializeTable("wow","id int primary key not null, joke text not null");
		modTag = initializePermissions();
	}

	@Override
	public String getPrimaryAlias() {
		return "joke";
	}

	@Override
	public String[] getOtherAliases() {
		return new String[] {"silly"};
	}

	@Override
	public String getUsage() {
		return "Tells a joke.";
	}

	@Override
	public String getDescription() {
		return "Bot responds with a random joke. Users may also query the bot using the following parameters:"
				+ "\n`<#>` - Reads response # from database." 
				+ "\n\nUsers with the `"+modTag+"` role may addtionally query the bot with the following parameters:"
				+ "\n`<#>` - Reads response # from database." 
				+ "\n`add <response text>` - Adds response to the `jokes` table."
				+ "\n`update <#> <text>` - Updates response # with given text."
				+ "\n`delete <#>` - Deletes response by #."
				+ "\n`import <filename and extension>` - Imports responses from textfile."
				+ "\n`export` - Exports responses as a textfile."
				+ "\n`count` - Reports number of responses known.";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"joke",
				"joke 13",
				"joke add This is a new joke.",
				"joke update 10 This is an updated joke.",
				"joke delete 29",
				"joke import imports/jokes.txt",
				"joke export",
				"joke count"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"# | add | update | delete | import | export | count"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
    	String post = "Joke is on you!";
    	String[] cmd = msg.split(" ");
        if (cmd.length == 1) post = readRandomJoke(jokeTable);
        else if (Util.isInteger(cmd[1])) {
			post = readJoke(jokeTable, Integer.parseInt(cmd[1]));
        }
        else {
        	if (isMod(event)) {
	        	switch (cmd[1]) {
	        		case "add": post = createJoke(jokeTable, msg.split(" ",3)[2]);
	        			break;
	        		case "update": post = updateJoke(jokeTable, cmd[2], msg.split(" ",4)[3]);
	        			break;
	        		case "delete": post = deleteJoke(jokeTable, cmd[2]);
	        			break;
	        		case "mod": post = setModLevel(event, cmd[2]);
	    				break;
	        		case "import": post = importJokes(jokeTable, "jokes.txt");
	    				break;
	        		case "export": post = exportJokes(jokeTable);
						break;
	        		case "count": post = countJokes(jokeTable);
	    				break;
	        		default: 
	        			post = "...You speak gibberish. [Bot command was malformed.]";
	        			break;
	        	}
        	} else {
        		post = "[Only Administrators or members with the "+modTag+" role may use that command.]";
        	}
        }
        event.getChannel().sendMessage(post).queue();
	}
	
	private String createJoke(String table, String joke) {
		String sql = "SELECT id FROM "+table+" ORDER BY id DESC LIMIT 1";
		try {
			// Get highest joke number
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int newId = rs.getInt(1) + 1;
			System.out.println("Highest Joke #:"+newId);
			
			// Insert joke and assign id 1 higher
			sql = "INSERT INTO "+table+" VALUES (?,?)";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, newId);
			ps.setString(2, joke);
			ps.executeUpdate();
			ps.close();
			return "New joke added, #"+newId+" "+joke;
		} catch (SQLException e) {
			e.printStackTrace();
			return "I'm can't do that right now. [Unable to query database.]";
		}
	}
	
	private String readJoke(String table, int num) {
		String result = " ";
		System.out.println("Joke num: "+num);
		
		String sql = "SELECT joke FROM "+table+" WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, num);
			ResultSet rs = ps.executeQuery();
			if (rs.isClosed()) return "There is no joke with that id #.";
			result = "Joke #"+num+": "+rs.getString(1);
			ps.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return "I can't think of any jokes right now. [Unable to query database.]";
		}
	}
	
	private String readRandomJoke(String table) {
		String sql = "SELECT id, joke FROM "+table+" ORDER BY RANDOM() LIMIT 1";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			String result = "Joke #"+rs.getString(1)+": "+rs.getString(2);
			ps.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return "Sorry, I can't recall that right now. [Unable to query database.]";
		}
	}
	
	private HashMap<Integer, String> readJokeMap(String table) {
		HashMap<Integer, String> temp = new HashMap<Integer, String>();
		
		String sql = "SELECT id, joke FROM "+table;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				temp.put(rs.getInt(1), rs.getString(2));
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	private String updateJoke(String table, String numString, String joke) {
		if (!Util.isInteger(numString)) return "That is not a valid number.";
		int num = Integer.parseInt(numString);
		if (joke.equals(null)) return "Please provide a joke.";
		
		String sql = "UPDATE "+table+" SET joke = ? WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, joke);
			ps.setInt(2, num);
			ps.executeUpdate();
			ps.close();
			return "Joke #"+num+" has been updated to: "+joke;
		} catch (SQLException e) {
			e.printStackTrace();
			return "I can't do that right now. [Unable to query database.]";
		}
	}
	
	private String deleteJoke(String table, String numString) {
		if (!Util.isInteger(numString)) return "That is not a valid number.";
		int num = Integer.parseInt(numString);
		
		String sql = "DELETE FROM "+table+" WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, num);
			ps.executeUpdate();
			ps.close();
			return "Joke #"+num+" has been erased from my memory.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "I can't do that right now. [Unable to query database.]";
		}
	}
	
	private String setModLevel(MessageReceivedEvent event, String tag) {
		Member author = event.getMember();
		if (author.hasPermission(Permission.ADMINISTRATOR)) {
			if (tag.isEmpty()) {
				tag = configModDefault;
			}
			if (Bot.config.getSetting(configModOption, "").isEmpty()) {
				Bot.config.updateSetting(configModOption, tag);
			} else {
				Bot.config.updateSetting(configModOption, tag);
			}
			return "[Jokes mod role has been set to `"+tag+"`]";
		} else {
			return "[Jokes plugin mod role may only be set by a Server Administrator.]";
		}
	}
	
	private String importJokes(String table, String filename) {
		HashMap<Integer, String> newJokes = Util.getBotFileAsMap("imports"+File.separator+filename);
		HashMap<Integer, String> oldJokes = readJokeMap(table);
		
		String sql = "INSERT INTO "+table+" VALUES(?,?)";
		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (int key : newJokes.keySet()) {
				if (!oldJokes.containsKey(key)) {
					System.out.println("ADDING TO "+table+": joke #"+key);
					ps.setInt(1, key);
					ps.setString(2, newJokes.get(key));
					ps.addBatch();
				} 
			}
			ps.executeBatch();
			conn.commit();
			ps.close();
			return "Jokes have been copied to "+table+" table.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Could not copy jokes to "+table+" table.";
		} 
	}
	
	private String exportJokes(String table) {
		System.out.println("EXPORTING...");
		String sql = "SELECT joke FROM "+table;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			String filename = buildExportFilename(table);
			PrintWriter export = createExportFile(filename);
			while (rs.next()) {
				export.append(rs.getString(1)+"\r\n");
			}
			ps.close();
			export.close();
			return "[The "+table+" table has been exported to the `exports` folder.]";
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return "[Error attempting to export "+table+" table.]";
	}
	
	private String countJokes(String table) {
		String sql = "SELECT COUNT(*) FROM "+table;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int count = rs.getInt(1);
			ps.close();
			return "There are "+count+" jokes in the "+table+" table.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error reading "+table+" table.";
		}
	}
	
	private PrintWriter createExportFile(String filename) {
		PrintWriter writer = null;
		try {
			File f = new File(filename);
			if (f.exists() && !f.isDirectory()) {
				writer = new PrintWriter(new FileOutputStream(new File(filename), true));
			} else {
				writer = new PrintWriter(filename, "UTF-8");
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return writer;
	}
	
	private String buildExportFilename(String table) {
		String date = dateFormat.format(new Date()); //2017-01-30_12-08-43
		return "exports"+File.separator+"export_jokes_" + table 
				+ "_" + date + ".txt";
	}
	
	private String initializePermissions() {
		if (Bot.config.getSetting(configModOption, "").isEmpty()) {
			Bot.config.updateSetting(configModOption, configModDefault);
			return "owner";
		} else {
			return Bot.config.getSetting(configModOption, "");
		}
	}
	
	private boolean isMod(MessageReceivedEvent event) {
		Member author = event.getMember();
		List<Role> roles = author.getRoles();
		boolean mod = false;
		for (Role r : roles) {
			if (r.getName().equals(modTag)) {
				mod = true;
			}
		}
		
		if (author.hasPermission(Permission.ADMINISTRATOR) || mod) {
			return true;
		} 
		return false;
	}
	
	// Placeholders for handling multiple joke tables.
	private boolean isValidTable(String table) {
		if (table.equals("jokes")) return true;
		else return false;
	}
	
	// Placeholders for handling multiple joke tables.
	private String getTable(String table) {
		if (table.equals("jokes")) return jokeTable;
		else return null;
	}
	
}
	

