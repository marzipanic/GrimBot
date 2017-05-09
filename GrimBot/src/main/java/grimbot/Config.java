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
	static private String configFile = "";
	static private final Pattern tokenRegex = Pattern.compile("^(\\w|\\p{Punct}){59}");
	
	Config(String f) {
		configFile = f;
		loadConfig();
	}
	
	public String getSetting(String option, String defaultSetting) {
		String setting = defaultSetting;
		try {
			setting = config.getString(option).toString();
		} catch (Exception e) {
			System.out.println("ERROR: Could not find setting for "+option);
		}
		return setting;
	}
	
	public void updateSetting(String option, String setting) {
		try {
			config.remove(option);
			config.put(option, setting);
			saveConfig();
			System.out.println("UPDATED CONFIG: setting "+option+" to "+setting);
		} catch (Exception e) {
			System.out.println("ERROR: Could not find setting for "+option);
		}
	}
	
	private void saveConfig() {
		try {
			PrintWriter writer = null;
			writer = new PrintWriter(configFile, "UTF-8");
			writer.println(config.toString(5));
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	// Read in basic config settings
	private static void loadConfig() {
    	config = getJSON(configFile);
    	if (config != null) {
    		if (tokenIsValid()) System.out.println("SUCCESS: Config file "+configFile+" has been loaded.");
    		else System.out.println("ERROR: Config file is missing [token]; please create a new "
    				+ "Bot User through your Discord account, then paste the token for it in "+configFile+".");
    	} else createConfigFile(configFile);
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
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			System.out.println("ERROR: Config file encoding was not supported.");
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("ERROR: Config file could not be found; a new config file has been created for you as "+filename
				+ "\nTo run this bot, you must update the following config properties:"
				+ "\n'token': Create a new App and Bot User through the Discord Developer website, then paste the bot token here.");
	}
}
