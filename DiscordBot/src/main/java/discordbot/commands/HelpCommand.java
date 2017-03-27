package discordbot.commands;

//import java.util.Date;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class HelpCommand extends ListenerAdapter {
	
	public void onMessageReceived(MessageReceivedEvent event) {
	    User author = event.getAuthor();
	    //DateFormat date = new SimpleDateFormat("EEE dd MMM yyyy");
	    //DateFormat time = new SimpleDateFormat("hh:mm:aa aa");
	    //Date d = new Date();
	
	
	    if((event.isFromType(ChannelType.TEXT) || event.isFromType(ChannelType.PRIVATE)) && !author.isBot()) {
	    	String msg = event.getMessage().getContent();
	        if(msg.startsWith("!help")) {
	        	if (!event.isFromType(ChannelType.PRIVATE)) event.getChannel().sendMessage(author.getAsMention() + " | I'll tell you more in private.").queue();
	            String[] cmd = msg.split(" ", 2);
	            author.openPrivateChannel().queue( success -> {
	                EmbedBuilder eb = new EmbedBuilder();
	                eb.setColor(java.awt.Color.BLACK);
	                if(cmd.length == 1) {
	                //eb.setFooter(author.getName() + " | " + date.format(d) + " at " + time.format(d), author.getAvatarUrl());
		                eb.setDescription("**`!help`** - Sends a list of available commands."
		                		+ "\n**`!about`** - Introduces self."
		                		+ "\n**`!ask <yes/no question>`** - Divines an answer."
		                		+ "\n**`!coin`** - Flips a coin."
		                		+ "\n**`!hello`** - Says hello."
		                		+ "\n**`!joke`** - Tells a joke."
		                		+ "\n**`!roll <#d#>`** - Rolls a die."
		                		+ "\n\nEnter !help <command> for details." );
		                eb.setFooter("Contact Mordavrin to request new commands.", event.getJDA().getUserById("140901708493619200").getEffectiveAvatarUrl());
	                } else eb.setDescription(getDescription(cmd[1]));
		            MessageEmbed e = eb.build();
	                MessageBuilder mb = new MessageBuilder();
	                mb.setEmbed(e);
	                Message m = mb.build();
	                author.getPrivateChannel().sendMessage(m).queue();
	            });
	        }
	    }
	}
	
	public String getDescription(String cmd) {
		String description;
    	switch(cmd) {
	    	case "about": description = describeAbout();
	    		break;
	    	case "ask": description = describeAsk();
				break;
	    	case "coin ": description = describeCoin();
				break;
	    	case "hello": description = describeHello();
				break;
	    	case "joke": description = describeJoke();
				break;
	    	case "roll": description = describeRoll();
				break;
	    	default: description = "I don't understand. [Command was malformed or does not exist.]";
				break;
		}
    	return description;
	}
	
	public String describeAbout(){
		return "**Command Usage:** `!about`"
        		+ "\n**Aliases:** `!info`"
        		+ "\n**Module:** AboutCommand"
        		+ "\n**Details:** Bot sends a direct message containing IC and OOC information about himself."
        		+ "\n**Examples:** "
        		+ "\n`!about`";
	}
	
	public String describeAsk(){
		return "**Command Usage:** `!ask <question>`"
        		+ "\n**Aliases:** `!8ball`"
        		+ "\n**Module:** EightBallCommand"
        		+ "\n**Details:** Bot responds to a yes or no question. Questions beginning with `who`, `what`, `when`, `where`, `why`, or `how` will not be answered. Failure to ask any question at all may result in an unusual response."
        		+ "\n**Examples:** "
        		+ "\n`!ask Will it rain in Brill tomorrow?`"
        		+ "\n`!ask Does Mageroyal taste pleasant?`"
        		+ "\n`!ask Is that potion going to explode?`";
	}
	
	public String describeCoin(){
		return "**Command Usage:** `!coin`"
        		+ "\n**Aliases:** *no aliases*"
        		+ "\n**Module:** CoinCommand"
        		+ "\n**Details:** Bot flips a coin and reports `heads` or `tails`."
        		+ "\n**Examples:** "
        		+ "\n`!coin`";
	}
	
	public String describeHello(){
		return "**Command Usage:** `!hello`"
        		+ "\n**Aliases:** `!hi`"
        		+ "\n**Module:** HelloCommand"
        		+ "\n**Details:** Bot responds with a greeting...or other cognizant statement."
        		+ "\n**Examples:** "
        		+ "\n`!hello`";
	}
	
	public String describeJoke(){
		return "**Command Usage:** `!joke <optional joke# | count>"
        		+ "\n**Aliases:** *no aliases*"
        		+ "\n**Module:** JokeCommand"
        		+ "\n**Details:** Bot responds with a random joke. If provided a `<joke#>`, will tall the corresponding joke. If `<count>` is requested, will report number of jokes known."
        		+ "\n**Examples:** "
        		+ "\n`!joke`"
        		+ "\n`!joke 42`"
        		+ "\n`!joke count`";
	}
	
	public String describeRoll(){
		return "**Command Usage:** `!roll <optional #die + d + #faces>`"
        		+ "\n**Aliases:** `!dice`"
        		+ "\n**Module:** DiceCommand"
        		+ "\n**Details:** Bot responds with a random roll from 1 to 100. Will roll specific die if given `<#die + d + #faces>`, where 0 < #die < 20 and 0 < #faces < 1000000."
        		+ "\n**Examples:** "
        		+ "\n`!roll`"
        		+ "\n`!roll d20`"
        		+ "\n`!roll 3d9000`";
	}
	
	public String describeChar(){
		return "**Command Usage:** !"
        		+ "\n**Aliases:** `!`"
        		+ "\n**Module:** "
        		+ "\n**Details:** "
        		+ "\n**Examples:** "
        		+ "\n``"
        		+ "\n``"
        		+ "\n``";
	}
}
