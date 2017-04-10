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
	
	private HashMap<Integer, String> map;
    private List<Integer> keys;
    private Connection conn = null;

	public Joke() {
		super("^(joke|silly)($|\\s+|\\s.+)?");
		map = Util.getBotFileAsMap("jokes.txt");
        keys = new ArrayList<Integer>(map.keySet());
        conn = Bot.db.connection;
        Bot.db.initializeTable("wow","id int primary key not null, joke text not null");
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
        else if (Util.isInteger(cmd[1])) post = getJoke(Integer.parseInt(cmd[1]));
        else if (cmd[1].equals("copy")) {
        	copyToDB();
        	post = "Copied jokes to the database.";
        }
        else if (cmd[1].equals("count")) {
        	//post = "You doubt me? I know " + map.size() + " clever jokes.";
        	post = countJokes(event);
        }
        else {
        	System.out.println(cmd[1]);
        	post = "...You speak gibberish. [Bot command was malformed. Type `!help joke` for more info.]";
        }
        event.getChannel().sendMessage(post).queue();
	}
	
	private String getRandomJoke() {
		Random rand = new Random();
        Integer i = rand.nextInt(keys.size());
        return String.format("Joke # %d: %s", keys.get(i), map.get(keys.get(i)));
	}
	
	private String getJoke(Integer i) {
		if (map.get(i) != null) return String.format("Joke # %d: %s", i, map.get(i));
		else return String.format("There is no joke with that number. ...*No joke!*");
	}
	
	private void copyToDB() {
		try {
			Statement s = conn.createStatement();
			s.setQueryTimeout(30);
			// UPDATE THIS TO PULL RESULTSET JUST ONCE
			for (int key : map.keySet()) {
				ResultSet rs = s.executeQuery("select * from joke_wow where id = "+key);
				if (!rs.next()) {
					System.out.println("ADDING JOKE: "+key);
					PreparedStatement query = conn.prepareStatement("insert into joke_wow values(?,?)");
					query.setInt(1, key);
					query.setString(2, map.get(key));
					query.executeUpdate();
				} 
			}
			s.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private String countJokes(MessageReceivedEvent event) {
		try {
			Statement s = conn.createStatement();
			s.setQueryTimeout(30);
			ResultSet rs = s.executeQuery("select count(*) from joke_wow");
			s.close();
			return "There are "+rs.getFetchSize()+" jokes in the database.";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error reading joke database.";
		}
	}
}
	

