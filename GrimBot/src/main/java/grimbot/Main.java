package grimbot;

import grimbot.data.SQLiteJDBC;

public class Main {
	
	public static void main(String[] args) {
		
		// Introduce program
		Intro();
		
		// Load GrimBot
		SQLiteJDBC db = new SQLiteJDBC();
		Config config = new Config("config.json");
		Bot primary = new Bot(config, db);
		
		// NOTE: GrimBot must be invited to a server via a Discord web invitation
		primary.Connect();
	}
	
	// Print friendly welcome message to console
	private static void Intro(){
		System.out.println("-----===== GrimBot =====-----"
				+ "\nWritten by: Miriam Feldhausen (Marzipanic)"
				+ "\nCreated: February 2017"
				+ "\nVersion: 0.1 Alpha"
				+ "\n"
				+ "\nLoading up GrimBot..."
				+ "\n");
	}
	
}
