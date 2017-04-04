package grimbot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class Config {
	
	static private JSONObject config;
	static private final Pattern tokenRegex = Pattern.compile("^(\\w|\\p{Punct}){59}");
	
	Config(String f) {
		LoadConfig(f);
	}
	
	public String getGame() {
		return config.getString("game").toString();
	}
	
	public String getToken() {
		return config.getString("token").toString();
	}
	
	public String getPrefix() {
		return config.getString("prefix").toString();
	}
	
	
	// Read in basic config settings
	private static void LoadConfig(String filename) {
    	config = getJSON(filename);
    	if (config != null) {
    		if (tokenIsValid()) System.out.println("SUCCESS: Config file "+filename+" has been loaded.");
    		else System.out.println("ERROR: Config file is missing [token]; please create a new "
    				+ "Bot User through your Discord account, then paste the token for it in "+filename+".");
    	} else createConfigFile(filename);
	}
	
	private static JSONObject getJSON(String filename) {
		String file = null;
		try {
			 file = new String(Files.readAllBytes(Paths.get(filename)));
		} catch (IOException e) {return null;}
		return new JSONObject(file);
	}
	
	private static boolean tokenIsValid() {
		if (config.has("token")) {
			if ((tokenRegex).matcher(config.get("token").toString()).matches()) {
				return true;
			}
		}
		return false;
	}
	
	private static void createConfigFile(String filename) {
		config = new JSONObject();
		config.append("token", "YOUR TOKEN HERE");
		config.append("prefix", "!");
		config.append("game", "BOT GAME STATUS HERE");
		config.append("version", "0.1-alpha");
		config.append("permissions", "PERMISSION LEVEL HERE");
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(filename, "UTF-8");
			writer.print(config);
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Config file could not be found.");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("ERROR: Config file encoding was not supported.");
			e.printStackTrace();
		}
		
		System.out.println("ERROR: Config file could not be found; a new config file was created as "+filename
				+ "\nTo run this bot, you must update the AT MINIMUM the following config properties:"
				+ "\n'token': Create a new App and Bot User through the Discord Developer website, then paste the bot user token here.");
	}
}
