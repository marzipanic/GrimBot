package discordbot;

import java.io.File;
import java.lang.reflect.Method;
/*import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;*/
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.LoginException;
import org.json.JSONObject;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.*;
import discordbot.commands.EightBallCommand;
import discordbot.commands.HelloCommand;
import discordbot.commands.HelpCommand;
import discordbot.commands.JokeCommand;
import discordbot.commands.onReadyEvent;
import discordbot.commands.AboutCommand;
import discordbot.commands.CoinCommand;
import discordbot.commands.DiceCommand;

public final class Bot {
	
	//static String clientId = "";
	static String prefix = "";
	static JDABuilder builder;
	private List<Plugin> plugins;
	
	public Bot(HashMap<String, String> config) {
		//clientId = config.get("clientId");
		prefix = config.get("prefix");
		builder = new JDABuilder(AccountType.BOT)
				.setToken(config.get("token"))
	        	.setBulkDeleteSplittingEnabled(false)
	        	.setAutoReconnect(true);
	}
	
	public static void Connect() {
		loadPlugins();
		
		//connect();
	}
	
	private static void loadPlugins() {
		// SOURCE: http://www.mkyong.com/java/how-to-use-reflection-to-call-java-method-at-runtime/
		String[] pluginList = {};
		File folder = new File("plugins");
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				System.out.println("File " + files[i].getName());
		    } else if (files[i].isDirectory()) {
		        System.out.println("Directory " + files[i].getName());
		    }
			
		}
		
		/*File file = new File(System.getProperty("user.dir")) + File.separator + "plugins" + File.separator + 
		try {
			Class cls = Class.forName("discordbot.plugins.Hello");
			Object obj = cls.newInstance();
		}*/
		
	}
	
	private static void connect() {
		try {
			builder.buildAsync();
		} catch (LoginException e) {
			System.out.println("\nERROR: Login failed.");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("\nERROR: Illegal Argument when connecting bot.");
			e.printStackTrace();
		} catch (RateLimitedException e) {
        	System.out.println("\nERROR: Bot has been rate limited.");
        }
	}
	
	
	/*public static final String PREFIX = "!";

	public static void main(String[] args) {
        try {
        	
        	// Read in the Config Settings
        	String config = new String(Files.readAllBytes(Paths.get("config/bot.json")));
        	JSONObject configJson = new JSONObject(config);
        	String token = configJson.getString("token");
        	String game = configJson.getString("game");
        	
        	// Bot Instance and Registration of Commands
            JDA jda = new JDABuilder(AccountType.BOT)
                .setToken(token)
                .setBulkDeleteSplittingEnabled(false)
                .setAutoReconnect(true)
                
                // COMMAND REGISTRATION
                //.addListener(new onReadyEvent())
                .addListener(new AboutCommand())
                .addListener(new CoinCommand())
                .addListener(new DiceCommand())
                .addListener(new HelpCommand())
                .addListener(new EightBallCommand())
                .addListener(new HelloCommand())
                .addListener(new JokeCommand())

                
                //blocking vs async. Blocking guarantees that JDA will be completely loaded.
                .buildBlocking();
            
            // Set the "playing <game>" message on the bot's status
            jda.getPresence().setGame(Game.of(game));
        }
        catch (IOException e)
        {
        	System.out.println("Config file not found!");
        }
        catch (LoginException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (RateLimitedException e)
        {
            e.printStackTrace();
        }
    }*/
}
