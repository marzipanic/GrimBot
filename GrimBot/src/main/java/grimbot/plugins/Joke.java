package grimbot.plugins;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import grimbot.Util;
import grimbot.Bot;
import grimbot.Plugin;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Joke extends Plugin {
    private Connection conn = null;
    String jokeTable = "";

	public Joke() {
		super("^(joke|silly)($|\\s+|\\s.+)?");
		conn = Bot.db.connection;
		jokeTable = Bot.db.initializeTable("wow","id int primary key not null, joke text not null");
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
		return "Tells a dad joke.";
	}

	@Override
	public String getDescription() {
		return "Bot responds with a random joke. If provided a `<joke#>`, will tell the corresponding joke. If `<count>` is requested, will report number of jokes known.";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"joke", "joke 42", "joke count", "joke import"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"joke#"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
    	String post = "Joke is on you!";
    	String[] cmd = msg.split(" ");
        if (cmd.length == 1) post = readRandomJoke(jokeTable);
        else if (Util.isInteger(cmd[1])) post = readJoke(jokeTable, Integer.parseInt(cmd[1]));
        else {
        	switch (cmd[1]) {
        		case "add": post = createJoke(jokeTable, msg.split(" ",3)[2]);
        			break;
        		case "update": post = updateJoke(jokeTable, cmd[2], msg.split(" ",4)[3]);
        			break;
        		case "delete": post = deleteJoke(jokeTable, cmd[2]);
        			break;
        		case "import": post = importJokes(jokeTable, "jokes.txt");
    				break;
        		case "count": post = countJokes(jokeTable);
    				break;
        		default: post = "...You speak gibberish. [Bot command was malformed.]";
        			break;
        	}
        }
        event.getChannel().sendMessage(post).queue();
	}
	
	private String createJoke(String table, String joke) {
		String sql = "SELECT id FROM "+table+" ORDER BY id DESC LIMIT 1";
		try {
			// Get highest joke number
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			int newId = rs.getInt(1) + 1;
			System.out.println("Highest Joke #:"+newId);
			s.close();
			
			// Insert joke and assign id 1 higher
			sql = "INSERT INTO "+table+" VALUES (?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
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
		
		String sql = "SELECT joke FROM "+table+" WHERE id = "+num;
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			if (rs.isClosed()) return "There is no joke with that id #.";
			result = "Joke #"+num+": "+rs.getString(1);
			s.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return "I can't think of any jokes right now. [Unable to query database.]";
		}
	}
	
	private String readRandomJoke(String table) {
		String sql = "SELECT id, joke FROM "+table+" ORDER BY RANDOM() LIMIT 1";
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			String result = "Joke #"+rs.getString(1)+": "+rs.getString(2);
			s.close();
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
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while(rs.next()) {
				temp.put(rs.getInt(1), rs.getString(2));
			}
			s.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
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
		HashMap<Integer, String> newJokes = Util.getBotFileAsMap(filename);
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
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			int count = rs.getInt(1);
			s.close();
			return "There are "+count+" jokes in the "+table+" table.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error reading "+table+" table.";
		}
	}
}
	

