package grimbot.plugins;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import grimbot.Util;
import grimbot.Bot;
import grimbot.Plugin;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Joke extends Plugin {
    private Connection conn = null;
    String jokes = "";

	public Joke() {
		super("^(joke|silly)($|\\s+|\\s.+)?");
		conn = Bot.database.conn;
		jokes = Bot.database.initializeTable("wow","id int primary key not null, joke text not null");
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
		return "Bot responds with a random joke. If provided a `joke #`, will tell the corresponding joke."
				+ " If `count` is requested, will report number of jokes known."
				+ "\n\nBot responses may be edited by authorized users with with the following "
				+ "command paramaters:"
				+ "\n`<response #>` - Reads response from `jokes` table with given #." 
				+ "\n`add <response text>` - Adds response to the `jokes` table."
				+ "\n`update <response #> <response text>` - Updates response text for the "
				+ "response # in table."
				+ "\n`delete <reponse #>` - Deletes response (by its #) from the `jokes` table."
				+ "\n`import <textfile name and extension>` - Imports responses from textfile "
				+ "into the `jokes` table."
				+ "\n`export` - Exports responses from database to the `exports` folder."
				+ "into textfile. NOT CURRENTLY FUNCTIONAL!"
				+ "\n`count` - Reports how many unique hello responses are known.";
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
        if (cmd.length == 1) post = readRandomJoke(jokes);
        else {
        	switch (cmd[1]) {
        		case "add": post = createJoke(jokes, msg.split(" ",3)[2]);
        			break;
        		case "update": post = updateJoke(jokes, cmd[2], msg.split(" ",4)[3]);
        			break;
        		case "delete": post = deleteJoke(jokes, cmd[2]);
        			break;
        		case "import": post = importJokes(jokes, "jokes.txt");
    				break;
        		case "count": post = countJokes(jokes);
    				break;
        		default: 
        			if (Util.isInteger(cmd[1])) {
        				post = readJoke(jokes, Integer.parseInt(cmd[1]));
        			} else {
        				post = "...You speak gibberish. [Bot command was malformed.]";
        			}
        			break;
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
	
	// Placeholders for handling multiple joke tables.
	private boolean isValidTable(String table) {
		if (table.equals("jokes")) return true;
		else return false;
	}
	
	// Placeholders for handling multiple joke tables.
	private String getTable(String table) {
		if (table.equals("jokes")) return jokes;
		else return null;
	}
	
}
	

