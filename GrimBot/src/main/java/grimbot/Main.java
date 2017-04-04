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
		Config config = new Config("config.json");
		Bot primary = new Bot(config);
		
		// NOTE: GrimBot must be invited to a server via a Discord web invitation
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
	
}
