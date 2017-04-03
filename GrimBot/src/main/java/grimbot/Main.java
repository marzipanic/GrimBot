package grimbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.json.JSONObject;

public class Main {
	
	public static void main(String[] args) {
		
		// Introduce program
		Intro();
		
		// Load GrimBot
		HashMap<String, String> config = LoadConfig("config.json");
		Bot primary = new Bot(config);
		
		// Connect GrimBot
		// Note that GrimBot must be invited to servers via a Discord web invitation
		primary.Connect();
	}
	
	// Print friendly welcome message to console
	public static void Intro(){
		System.out.println("-----===== GrimBot =====-----"
				+ "\nWritten by: Miriam Feldhausen (Marzipanic)"
				+ "\nCreated: February 2017"
				+ "\nVersion: 0.1 Alpha"
				+ "\n"
				+ "\nLoading config files...");
	}

	// Read in basic config settings
	public static HashMap<String, String> LoadConfig(String filename) {
    	HashMap<String,String> config = new HashMap<String, String>();
		try {
			String configFile = new String(Files.readAllBytes(Paths.get(filename)));
			JSONObject configJson = new JSONObject(configFile);
			
			for (String key : configJson.keySet()) {
				config.put(key, (String)(configJson.get(key)));
				System.out.println("KEY: "+key+",     VALUE: "+(String)(configJson.get(key)));
			}
			
			// Check validity of config
			if (!(config.containsKey("token") && config.containsKey("clientId"))) {
				System.out.println("ERROR: Config file is missing [token] or [clientId].");
			} else {
				System.out.println("SUCCESS: Config file has been loaded.");
				return config;
			}
			
		} catch (IOException e) {
			System.out.println("ERROR: Config file could not be read.");
			e.printStackTrace();
		}
		
		return null;
	}
	
}
