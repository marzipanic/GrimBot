package grimbot.plugins;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import grimbot.Util;
import grimbot.Bot;
import grimbot.Plugin;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Joke extends Plugin {
	
	private HashMap<Integer, String> wowMap;
    private List<Integer> wowKeys;
    private Connection conn = null;

	public Joke() {
		super("^(joke|silly)($|\\s+|\\s.+)?");
		conn = Bot.db.connection;
		Bot.db.initializeTable("wow","id int primary key not null, joke text not null");
		wowMap = getJokeMap("joke_wow");
        wowKeys = new ArrayList<Integer>(wowMap.keySet());
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
		return new String[] {"joke", "joke 42"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"joke#"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
    	String post = "Joke is on you!";
    	String[] cmd = msg.split(" ");
        if (cmd.length == 1) post = getRandomJoke();
        else if (Util.isInteger(cmd[1])) post = getJoke(wowMap, Integer.parseInt(cmd[1]));
        else {
        	switch (cmd[1]) {
        		case "import": post = importJokes(wowMap, wowKeys, "joke_wow", "jokes.txt");
        			break;
        		case "count": post = countJokes("joke_wow", event);
        			break;
        		default: post = "...You speak gibberish. [Bot command was malformed.]";
        			break;
        	}
        }
        event.getChannel().sendMessage(post).queue();
	}
	
	private String getRandomJoke() {
		// RESTRUCTURE THIS TO SELECT FROM RANDOM JOKE MAP; useful for handling multiple joke sets/themes
		Random rand = new Random();
        Integer i = rand.nextInt(wowKeys.size());
        return String.format("Joke # %d: %s", wowKeys.get(i), wowMap.get(wowKeys.get(i)));
	}
	
	private String getJoke(HashMap<Integer, String> map, Integer i) {
		if (map.get(i) != null) return String.format("Joke # %d: %s", i, map.get(i));
		else return String.format("There is no joke with that number. ...*No joke!*");
	}
	
	private String importJokes(HashMap<Integer, String> map, List<Integer> keys, String table, String filename) {
		try {
			HashMap<Integer, String> newJokes = Util.getBotFileAsMap(filename);
			conn.setAutoCommit(false);
			PreparedStatement query = conn.prepareStatement("insert into "+table+" values(?,?)");
			for (int key : newJokes.keySet()) {
				if (!map.containsKey(key)) {
					System.out.println("ADDDING TO "+table+": joke #"+key);
					query.setInt(1, key);
					query.setString(2, newJokes.get(key));
					query.addBatch();
				} 
			}
			query.executeBatch();
			conn.commit();
			map = newJokes;
			keys = new ArrayList<Integer>(map.keySet());
			return "Jokes have been copied to "+table+" table.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Could not copy jokes to "+table+" table.";
		} 
	}
	
	private String countJokes(String table, MessageReceivedEvent event) {
		try {
			Statement s = conn.createStatement();
			s.setQueryTimeout(30);
			ResultSet rs = s.executeQuery("select count(*) from "+table);
			s.close();
			return "There are "+rs.getFetchSize()+" jokes in the "+table+" table.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error reading "+table+" table.";
		}
	}
	
	private HashMap<Integer, String> getJokeMap(String table) {
		HashMap<Integer, String> temp = new HashMap<Integer, String>();
		try {
			Statement s = conn.createStatement();
			s.setQueryTimeout(30);
			ResultSet rs = s.executeQuery("select * from "+table);
			while(rs.next()) {
				temp.put(rs.getInt(1), rs.getString(2));
			}
			s.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return temp;
	}
}
	

