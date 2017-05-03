package grimbot.plugins;

import java.util.Random;

import grimbot.Plugin;
import grimbot.utilities.Util;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Dice extends Plugin {
	public Dice() {
		super("^(roll|die|dice)($|\\s+|\\s.+)?");
	}

	@Override
	public String getPrimaryAlias() {
		return "roll";
	}

	@Override
	public String[] getOtherAliases() {
		return new String[] {"dice", "die"};
	}

	@Override
	public String getUsage() {
		return "Rolls a die.";
	}

	@Override
	public String getDescription() {
		return "Bot responds with a random roll from 1 to 100. Will roll specific die if given "
				+ "`<die# + d + face#>`, where 0 < die# < 20 and 0 < face# < 1000000.";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"roll", "roll d6", "roll 20d1000000"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"die# + d + face#"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		String post = event.getAuthor().getName();
    	String[] cmd = msg.split(" ",2);
        if (cmd.length == 1) post += getRandom100Roll();
        else post += getRoll(cmd[1]);
        event.getChannel().sendMessage(post).queue();
	}
	
	private String getRandom100Roll() {
		Random rand = new Random();
		int i = rand.nextInt(100) + 1;
        return " rolls a lucky :game_die: and gets "+i+ " out of 100.";
	}
	
	private int getDie(String[] roll) {
		int die;
		if (roll.length == 1 || roll[0].isEmpty()) die = 1;
		else {
			if (roll[0] != "" && Util.isInteger(roll[0])) die = Integer.parseInt(roll[0]);
			else die = 0;
		}
		if (die < 1 || die > 20) die = 0;
		return die;
	}
	
	private int getFaces(String faces) {
		int result = 0;
		if (Util.isInteger(faces)) result = Integer.parseInt(faces);
		if (result < 1 || result > 1000000) result = 0;
		return result;
	}
	
	private String getRollResult(int die, int faces) {
		String post = " rolls "+die+" :game_die: "+faces+" and gets:";
		if (die > 0 && faces > 0) {
			Random rand = new Random();
			int sum = 0;
			for (int i = 1; i <= die; i++) {
				int r = rand.nextInt(faces)+1;
				post += " "+r;
				sum += r;
				if (i < die) post += " +";
			}
			if (die != 1) post +=" = "+sum+"";
		}
		else post = " needs a new set of dice. [Roll was malformed or out of range. Type `!help roll` for more info.]";
		return post;
	}
	
	private String getRoll(String roll) {
		String[] temp = roll.split("d");
		int die = getDie(temp);
		int faces = getFaces(temp[temp.length-1]);
		return getRollResult(die, faces);
	}
}
