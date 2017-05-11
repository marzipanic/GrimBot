package grimbot.plugins;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import grimbot.Bot;
import grimbot.Plugin;
import grimbot.utilities.Util;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Hello extends Plugin{
	
	private Connection conn = null;
    private String hellos = "";

	public Hello() {
		super("^(hello|hi|hola)($|\\s+|\\s.+)?");
		conn = Bot.database.conn;
		hellos = Bot.database.initializeTable("hellos","id int primary key not null, response text not null");
	}

	@Override
	public String getPrimaryAlias() {
		return "hi";
	}

	@Override
	public String[] getOtherAliases() {
		return new String[] {"hello", "hola"};
	}

	@Override
	public String getUsage() {
		return "Says hello.";
	}

	@Override
	public String getDescription() {
		return "Bot will say hello to the user that greeted it."
				+ "\n\n__Optional Parameters__"
				+ "\n`<#>` - Reads response # from database." 
				+ "\n`add <text>` - Adds new response with text."
				+ "\n`update <#> <text>` - Updates response # with text."
				+ "\n`delete <#>` - Deletes response #."
				+ "\n`import <textfile name and extension>` - Imports responses from textfile."
				+ "\n`count` - Reports number of unique responses known.";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"hi",
				"hi 13",
				"hi add This is a new hello.",
				"hi update 10 This is an updated hello.",
				"hi delete 29",
				"hi import imports/hellos.txt",
				"hi export",
				"hi count"};
	}

	@Override
	public String[] getParameters() {
		return  new String[] {"# | add | update | delete | import | export | count"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		String post = "Hello!";
    	String[] cmd = msg.split(" ");
        if (cmd.length == 1) post = readRandomResponse(hellos);
        else {
        	switch (cmd[1]) {
        		case "add": post = createResponse(hellos, msg.split(" ",3)[2]);
        			break;
        		case "update": post = updateResponse(hellos, cmd[2], msg.split(" ",4)[3]);
        			break;
        		case "delete": post = deleteResponse(hellos, cmd[2]);
        			break;
        		case "import": post = importResponses(hellos, "hellos.txt");
    				break;
        		case "count": post = countResponses(hellos);
    				break;
        		default: 
        			if (Util.isInteger(cmd[1])) {
        				post = readResponse(hellos, Integer.parseInt(cmd[1]));
        			} else {
        				post = "...You speak gibberish. [Bot command was malformed.]";
        			}
        			break;
        	}
        }
        event.getChannel().sendMessage(post).queue();
	}
	
	private String createResponse(String table, String response) {
		String sql = "SELECT id FROM "+table+" ORDER BY id DESC LIMIT 1";
		try {
			// Get highest response number
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int newId = rs.getInt(1) + 1;
			System.out.println("Highest response #:"+newId);
			
			// Insert response and assign id 1 higher
			sql = "INSERT INTO "+table+" VALUES (?,?)";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, newId);
			ps.setString(2, response);
			ps.executeUpdate();
			ps.close();
			return "New response added to "+table+" table; #"+newId+": "+response;
		} catch (SQLException e) {
			e.printStackTrace();
			return "[Unable to query database to add new response.]";
		}
	}
	
	private String readResponse(String table, int num) {
		String result = " ";
		System.out.println("Reading Response #: "+num);
		
		String sql = "SELECT response FROM "+table+" WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, num);
			ResultSet rs = ps.executeQuery();
			if (rs.isClosed()) return "There is no response with that #.";
			result = "Response #"+num+": "+rs.getString(1);
			ps.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return "[Unable to query database to read response; check response count or connection.]";
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
			return "[Unable to query database to read random response; check response count or connection.]";
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
		if (response.equals(null)) return "Please provide a replacement response.";
		
		String sql = "UPDATE "+table+" SET response = ? WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, response);
			ps.setInt(2, num);
			ps.executeUpdate();
			ps.close();
			return "Response #"+num+" has been updated to: "+response;
		} catch (SQLException e) {
			e.printStackTrace();
			return "[Unable to query database to update.]";
		}
	}
	
	private String deleteResponse(String table, String numString) {
		if (!Util.isInteger(numString)) return "That is not a valid number.";
		int num = Integer.parseInt(numString);
		
		String sql = "DELETE FROM "+table+" WHERE id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, num);
			ps.executeUpdate();
			ps.close();
			return "Response #"+num+" was removed.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "[Unable to query database.]";
		}
	}
	
	private String importResponses(String table, String filename) {
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
			return "Responses have been copied to "+table+" table.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Could not copy responses to "+table+" table.";
		} 
	}
	
	private String countResponses(String table) {
		String sql = "SELECT COUNT(*) FROM "+table;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int count = rs.getInt(1);
			ps.close();
			return "There are "+count+" responses in the "+table+" table.";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error reading "+table+" table.";
		}
	}
}
