package discordbot;

//import WoWAPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.security.auth.login.LoginException;
import org.json.JSONObject;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import discordbot.commands.EightBallCommand;
import discordbot.commands.HelloCommand;
import discordbot.commands.HelpCommand;
import discordbot.commands.JokeCommand;
import discordbot.commands.onReadyEvent;
import discordbot.commands.AboutCommand;
import discordbot.commands.CoinCommand;
import discordbot.commands.DiceCommand;

public class Bot {
	
	public static final String PREFIX = "!";

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
    }
}
