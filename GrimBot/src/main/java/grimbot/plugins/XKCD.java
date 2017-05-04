package grimbot.plugins;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

import grimbot.Bot;
import grimbot.Plugin;
import grimbot.utilities.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class XKCD extends Plugin {
	
	private static OkHttpClient httpClient = new OkHttpClient();

	public XKCD() {
		super("^xkcd($|\\s+|\\s.+)?");
	}

	@Override
	public String getPrimaryAlias() {
		return "xkcd";
	}

	@Override
	public String[] getOtherAliases() {
		return null;
	}

	@Override
	public String getUsage() {
		return "Fetch an XKCD comic.";
	}

	@Override
	public String getDescription() {
		return "Fetches the current XKCD comic, or a specific comic number (if specified). This service is provided by the "
				+ "author, Randall Munroe, at https://xkcd.com/json.html";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"xkcd", "xkcd 303", "xkcd 627"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"comic #"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
    	String[] cmd = msg.split(" ");
        if (cmd.length == 1) {
        	getComic(event, "today");
        } else {
			if (Util.isInteger(cmd[1])) {
				getComic(event, cmd[1]);
			} else {
				sendMessage(event, "[Bot command was malformed. Type `"+Bot.prefix+"help xkcd` for more information.]");
			}
        }
	}

	private void getComic(MessageReceivedEvent event, String num) {
		String url = "http://xkcd.com/";
		if (!num.equals("today")) {
			url += num + "/";
		}
		url += "info.0.json";
	    Message m = buildEmbed(fetch(url));
	    event.getChannel().sendMessage(m).queue();
	}
	
	private Message buildEmbed(JSONObject xkcd) {
		String num = Integer.toString(xkcd.getInt("num"));
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(java.awt.Color.BLACK);
        eb.addField( "xkcd #"+num+": "+xkcd.getString("safe_title"), "https://xkcd.com/"+num+"/",false);
        eb.setImage(xkcd.getString("img"));
        eb.setFooter("Comic by Randall Munroe", "https://xkcd.com/favicon.ico");
        MessageEmbed me = eb.build();
        return new MessageBuilder().setEmbed(me).build();
	}

	private JSONObject fetch(String url) {
		try {
			Request request = new Request.Builder()
			      .url(url)
			      .build();

			Response response = httpClient.newCall(request).execute();
			return new JSONObject(response.body().string());
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void sendMessage(MessageReceivedEvent event, String message){
		String author = event.getAuthor().getAsMention();
		event.getChannel().sendMessage(author + message).queue();
	}
}

