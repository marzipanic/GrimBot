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

public class MagicBall extends Plugin{
	private Connection conn = null;
	String defaultTable = "";
	String answerTable = "";
	
	/*HashMap<Integer, String> map;
    List<Integer> keys;
    
    HashMap<Integer, String> mapDefault;
    List<Integer> keysDefault;*/

	public MagicBall() {
		super("^(8ball|ask|crystalball|magicball)($|\\s+|\\s.+)?");
		conn = Bot.db.conn;
		defaultTable = Bot.db.initializeTable("defaults","id int primary key not null, response text not null");
		answerTable = Bot.db.initializeTable("answers","id int primary key not null, response text not null");
		
		/*map = Util.getBotFileAsMap("8ball.txt");
        keys = new ArrayList<Integer>(map.keySet());
        //System.out.println(map.toString());
        mapDefault = Util.getBotFileAsMap("8balldefault.txt");
        keysDefault = new ArrayList<Integer>(mapDefault.keySet());*/
	}

	@Override
	public String getPrimaryAlias() {
		return "ask";
	}

	@Override
	public String[] getOtherAliases() {
		return new String[] {"8ball", "crystalball", "magicball"};
	}

	@Override
	public String getUsage() {
		return "Consults a crystal ball";
	}

	@Override
	public String getDescription() {
		return "Bot responds to a yes or no question. Questions beginning with `who`, `what`, `when`, `where`, `why`, or `how` will not be answered. Failure to ask any question at all may result in an unusual response.";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"ask Will it rain in Numbani tomorrow?",
				"ask Does Mageroyal taste pleasant?",
				"ask Is my starship going to survive its next encounter?"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"question"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		String post = "";
    	String[] cmd = msg.split(" ",2);
        if (cmd.length == 1) post = readRandomResponse(defaultTable);
        else if (invalidQuestion(cmd[1])) post = "Who, what, where, when, why, how. Ask me a yes or no question instead.";
        else {
        	String table = cmd[1].split(" ",2)[0].toLowerCase();
        	String params = cmd[1].split(" ",2)[1];
        	
        	// Pull database name for queries
        	if (table == "default" || table == "answer") {
        		String command = params.split(" ",2)[0];
        		String response = params.split(" ",2)[1] == null ? "" : params.split(" ",2)[1];
        		if (table == "answer") {
        			post = handleCommand(answerTable, command, response);
        		}
        		else post = handleCommand(defaultTable, command, response);
        	}
        	else post = readRandomResponse(answerTable);
        }
        event.getChannel().sendMessage(post).queue();
	}
	
	private boolean invalidQuestion(String sentence) {
		String test = sentence.toLowerCase();
		if (test.indexOf("who") == 0) return true;
		if (test.indexOf("what") == 0) return true;
		if (test.indexOf("where") == 0) return true;
		if (test.indexOf("when") == 0) return true;
		if (test.indexOf("why") == 0) return true;
		if (test.indexOf("how") == 0) return true;
		return false;
	}
	
	/*
	private String getDefaultAnswer(String table) {
		Random rand = new Random();
        Integer i = rand.nextInt(keysDefault.size());
        return String.format(mapDefault.get(keysDefault.get(i)));
        // return String.format("Answer # %d: %s", keys.get(i), map.get(keys.get(i)));
	}
	
	private String getRandomAnswer() {
		Random rand = new Random();
        Integer i = rand.nextInt(keys.size());
        return String.format(map.get(keys.get(i)));
        // return String.format("Answer # %d: %s", keys.get(i), map.get(keys.get(i)));
	}*/
	
	private String handleCommand(String table, String cmd, String res) {
		String post = "";
		switch (cmd) {
			case "add": 
				post = createResponse(table, res);
				break;
			case "update":
				String[] params = res.split(" ",2);
				post = updateResponse(table, params[0], params[1]);
				break;
			case "delete":
				post = deleteResponse(table, res);
				break;
			case "import": 
				// Accepts text file must be such that each line contains a new database entry formatted as:
				//    #:response text here
				// To issue this command successfully, must specify the name of the text file used, including
				// its extension. For example, to import lines from answer.txt into the answer table, enter:
				//    !ask answer import answers.txt
				post = importResponses(table, res);
				break;
			case "count": post = countResponses(table);
				break;
			default: post = "...You speak gibberish. [Paramater for <action> was missing or malformed.]";
				break;
		}
		return post;
	}
	
	
	private String createResponse(String table, String response) {
		String sql = "SELECT id FROM "+table+" ORDER BY id DESC LIMIT 1";
		try {
			// Get highest response number
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, table);
			ResultSet rs = ps.executeQuery();
			int newId = rs.getInt(1) + 1;
			System.out.println("Highest Response #:"+newId);
			
			// Insert response and assign id 1 higher
			sql = "INSERT INTO ? VALUES (?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, table);
			ps.setInt(2, newId);
			ps.setString(3, response);
			ps.executeUpdate();
			ps.close();
			return "New response added to "+table+", #"+newId+" "+response;
		} catch (SQLException e) {
			e.printStackTrace();
			return "I'm can't do that right now. [Unable to query database for "+table+".]";
		}
	}
	
	private String readResponse(String table, int num) {
		String result = " ";
		System.out.println("Response num: "+num);
		
		String sql = "SELECT response FROM ? WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, table);
			ps.setInt(2, num);
			ResultSet rs = ps.executeQuery();
			if (rs.isClosed()) return "There is no response with that id #.";
			result = "Joke #"+num+": "+rs.getString(1);
			ps.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return "I can't think of any responses right now. [Unable to query database for "+table+".]";
		}
	}
	
	private String readRandomResponse(String table) {
		String sql = "SELECT id, response FROM ? ORDER BY RANDOM() LIMIT 1";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, table);
			ResultSet rs = ps.executeQuery();
			String result = "Response #"+rs.getString(1)+": "+rs.getString(2);
			ps.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return "Sorry, I can't recall that right now. [Unable to query database for "+table+".]";
		}
	}
	
	private HashMap<Integer, String> readResponseMap(String table) {
		HashMap<Integer, String> temp = new HashMap<Integer, String>();
		
		String sql = "SELECT id, response FROM ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, table);
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
	
	private String updateResponse(String table, String numString, String response) {
		if (!Util.isInteger(numString)) return "That is not a valid number.";
		int num = Integer.parseInt(numString);
		if (response.equals(null)) return "Please provide response text.";
		
		String sql = "UPDATE ? SET response = ? WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, table);
			ps.setString(2, response);
			ps.setInt(3, num);
			ps.close();
			return "Response #"+num+" in "+table+" has been updated to: "+response;
		} catch (SQLException e) {
			e.printStackTrace();
			return "I can't do that right now. [Unable to query database.]";
		}
	}
	
	private String deleteResponse(String table, String numString) {
		if (!Util.isInteger(numString)) return "That is not a valid number.";
		int num = Integer.parseInt(numString);
		
		String sql = "DELETE FROM ? WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, table);
			ps.setInt(2, num);
			ps.executeUpdate();
			ps.close();
			return "Response #"+num+" has been erased from my memory.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "I can't do that right now. [Unable to query database.]";
		}
	}
	
	private String importResponses(String table, String filename) {
		HashMap<Integer, String> newResponses = Util.getBotFileAsMap(filename);
		HashMap<Integer, String> oldResponses = readResponseMap(table);
		
		String sql = "INSERT INTO ? VALUES(?,?)";
		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, table);
			for (int key : newResponses.keySet()) {
				if (!oldResponses.containsKey(key)) {
					System.out.println("ADDING TO "+table+": response #"+key);
					ps.setInt(1, key);
					ps.setString(2, newResponses.get(key));
					ps.addBatch();
				} 
			}
			ps.executeBatch();
			conn.commit();
			ps.close();
			return "Responses have been copied to "+table+" table.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Could not copy responses to "+table+" table.";
		} 
	}
	
	private String countResponses(String table) {
		String sql = "SELECT COUNT(*) FROM ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, table);
			ResultSet rs = ps.executeQuery(sql);
			int count = rs.getInt(1);
			ps.close();
			return "There are "+count+" responses in the "+table+" table.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error reading "+table+" table.";
		}
	}

}
