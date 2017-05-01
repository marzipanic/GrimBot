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

public class MagicBall extends Plugin{
	private Connection conn = null;
	String defaults = "";
	String answers = "";

	public MagicBall() {
		super("^(8ball|ask|crystalball|magicball)($|\\s+|\\s.+)?");
		conn = Bot.db.conn;
		defaults = Bot.db.initializeTable("defaults","id int primary key not null, response text not null");
		answers = Bot.db.initializeTable("answers","id int primary key not null, response text not null");
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
		return "Divines an answer.";
	}

	@Override
	public String getDescription() {
		
		// TO DO: Update Export Help
		return "Bot responds to a yes or no question. Questions beginning with `who`, `what`, "
				+ "`when`, `where`, `why`, or `how` may recieve uncertain responses. Failure to ask any "
				+ "question at all may result in an unusual response."
				+ "\n\nBot responses may be edited by authorized users with access to the following "
				+ "command paramters:"
				+ "\n`<response #>` - Reads response from `answers` table with given #." 
				+ "\n`add <table> <response text>` - Adds response to table."
				+ "\n`update <table> <response #> <response text>` - Updates response text for the "
				+ "response # in table."
				+ "\n`delete <table> <reponse #>` - Deletes the response (by its #) from the table"
				+ "\n`import <table> <textfile name and extension>` - Imports responses from textfile "
				+ "into the table."
				+ "\n`export <table>` - Exports responses from database to the `exports` folder."
				+ "into textfile. NOT CURRENTLY FUNCTIONAL!"
				+ "\n`count` - Reports how many unique responses are known in all tables."
				+ "\n\nValid <table> parameters for the above commands include:"
				+ "\n`answers` - Table contain answers to yes-or-no questions."
				+ "\n`defaults` - Table contains answers to non yes-or-no questions. ";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"ask Will it rain in Nagrand tomorrow?",
				"ask 42",
				"ask add answers This is an new answer response.",
				"ask update defaults 10 This is an updated default response.",
				"ask delete answers 29",
				"ask import answers imports/answers.txt",
				"ask export defaults",
				"ask count"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"question | add <answer | default>"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		String post = "";
    	String[] splitMsg = msg.split(" ",2);
        if (splitMsg.length == 1) post = readRandomResponse(defaults);
        else { // !ask <params>
        	if (invalidQuestion(splitMsg[1])) {
        		post = "Who, what, where, when, why, how. Ask me a yes or no question instead.";
        	} else {
        		post = handleCommand(splitMsg[1]);
        	}
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
	
	private String handleCommand(String msg) {
		// TO DO: Modify Imports, expand on Exports!
		//!ask <msg>
		String[] params = msg.split(" ");
		String post = "";
		switch (params[0]) {
			case "add": 
				System.out.println("ATTEMPT TO ADD");
				if (params.length >= 3) { // <add> <table> <response text>
					if (isValidTable(params[1])){
						post = createResponse(getTable(params[1]), msg.split(" ",3)[2]);
					} else {
						post = "[Add command was given an invalid <table> parameter; valid <table> parameters include `answers` and `defaults`.]";
					}
				} else {
					post = "[Add command was given too few parameters.]";
				}
				break;
			case "update":
				System.out.println("ATTEMPT TO UPDATE");
				if (params.length >= 4) { // <update> <table> <response #> <response text>
					if (isValidTable(params[1]) && Util.isInteger(params[2])) {
						post = updateResponse(getTable(params[1]), params[2], msg.split(" ",3)[2]);
					} else {
						post = "[Update command was given an invalid <table> or <response #> parameter; valid <table> parameters include `answers` and `defaults`.]";
					}
				} else {
					post = "[Update command was given too few parameters.]";
				}
				break;
			case "delete":
				System.out.println("ATTEMPT TO DELETE");
				if (isValidTable(params[1]) && params.length == 3) { // <delete> <table> <reponse #>
					if (isValidTable(params[1]) && Util.isInteger(params[2])) {
						post = deleteResponse(getTable(params[1]), params[2]);
					} else {
						post = "[Delete command  was given an invalid <table> or <response #> parameter.]";
					}
				} else {
					post = "[Delete command was given too many or too few parameters.]";
				}
				break;
			case "import": 
				System.out.println("ATTEMPT TO IMPORT");
				// Accepts text file must be such that each line contains a new database entry formatted as:
				//    #:response text here
				if (params.length == 3) { // <import> <table> <textfile name and extension>
					if (isValidTable(params[1])){
						post = importResponses(getTable(params[1]), params[2]);
					} else {
						post = "[Import command was given an invalid <table> parameter; valid <table> parameters include `answers` and `defaults`.]";
					}
				} else {
					post = "[Import command was given too many or too few parameters.]";
				}
				break;
			case "count": 
				System.out.println("ATTEMPT TO COUNT");
				post = "I have "+countResponses(answers)+" possible answers for yes-or-no questions and "
						+countResponses(defaults)+" default responses otherwise.";
				break;
			default:
				if (Util.isInteger(params[0])) {
					// TO DO: Adjust to allow read for either table, answers or defaults
					post = readResponse(answers, Integer.parseInt(params[0]));
				} else {
					post = readRandomResponse(answers);
				}
				break;
		}
		return post;
	}
	
	
	private String createResponse(String table, String response) {
		String sql = "SELECT id FROM "+table+" ORDER BY id DESC LIMIT 1";
		try {
			// Get highest response number
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int newId = rs.getInt(1) + 1;
			System.out.println("Highest Response #:"+newId);
			
			// Insert response and assign id 1 higher
			sql = "INSERT INTO "+table+" VALUES (?,?)";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, newId);
			ps.setString(2, response);
			ps.executeUpdate();
			ps.close();
			return "[New response added to "+table+", #"+newId+" "+response+"]";
		} catch (SQLException e) {
			e.printStackTrace();
			return "[Unable to create new entry for table "+table+".]";
		}
	}
	
	private String readResponse(String table, int num) {
		String result = " ";
		System.out.println("Reading Response num: "+num);
		
		String sql = "SELECT response FROM "+table+" WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, num);
			ResultSet rs = ps.executeQuery();
			if (rs.isClosed()) return "[There is no response with that id #.]";
			result = "Response #"+num+": "+rs.getString(1);
			ps.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return "[Database table "+table+" is empty or unavailable.]";
		}
	}
	
	private String readRandomResponse(String table) {
		String sql = "SELECT id, response FROM "+table+" ORDER BY RANDOM() LIMIT 1";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			//String result = "Response #"+rs.getString(1)+": "+rs.getString(2);
			String result = rs.getString(2);
			ps.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return "[Database table "+table+" is empty or unavailable.]";
		}
	}
	
	private HashMap<Integer, String> readResponseMap(String table) {
		HashMap<Integer, String> temp = new HashMap<Integer, String>();
		
		String sql = "SELECT id, response FROM "+table;
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
	
	private String updateResponse(String table, String numString, String response) {
		if (!Util.isInteger(numString)) return "That is not a valid number.";
		int num = Integer.parseInt(numString);
		if (response.equals(null)) return "[Please provide response text.]";
		
		String sql = "UPDATE "+table+" SET response = ? WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, response);
			ps.setInt(2, num);
			ps.close();
			return "[Response #"+num+" in "+table+" has been updated to: "+response+"]";
		} catch (SQLException e) {
			e.printStackTrace();
			return "[Unable to query database.]";
		}
	}
	
	private String deleteResponse(String table, String numString) {
		if (!Util.isInteger(numString)) {
			return "That is not a valid number.";
		}
		int num = Integer.parseInt(numString);
		
		String sql = "DELETE FROM "+table+" WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, num);
			ps.executeUpdate();
			ps.close();
			return "[Response #"+num+" has been removed.]";
		} catch (SQLException e) {
			e.printStackTrace();
			return "[Unable to query database.]";
		}
	}
	
	private String importResponses(String table, String filename) {
		System.out.println("Importing "+filename+" to "+table);
		HashMap<Integer, String> newResponses = Util.getBotFileAsMap("imports"+File.separator+filename);
		HashMap<Integer, String> oldResponses = readResponseMap(table);
		
		String sql = "INSERT INTO "+table+" VALUES(?,?)";
		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
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
			return "[Responses have been copied to "+table+" table.]";
		} catch (SQLException e) {
			e.printStackTrace();
			return "[Could not copy responses to "+table+" table.]";
		} 
	}
	
	private String countResponses(String table) {
		String sql = "SELECT COUNT(*) FROM "+table;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int count = rs.getInt(1);
			ps.close();
			return count+"";
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private boolean isValidTable(String table) {
		if (table.equals("defaults") || table.equals("answers")) return true;
		else return false;
	}
	
	private String getTable(String table) {
		if (table.equals("answers")) return answers;
		else if (table.equals("defaults")) return defaults;
		else return null;
	}

}
