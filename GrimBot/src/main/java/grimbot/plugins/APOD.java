package grimbot.plugins;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

import grimbot.Bot;
import grimbot.Plugin;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APOD extends Plugin{
	
	private static OkHttpClient httpClient = new OkHttpClient();
	private static String nasaKey = Bot.config.getSetting("nasaapikey", "");
	private static String nasaURL = "https://api.nasa.gov/planetary/apod?api_key="+nasaKey;

	public APOD() {
		super("^(apod)$");
	}

	@Override
	public String getPrimaryAlias() {
		return "apod";
	}

	@Override
	public String[] getOtherAliases() {
		return null;
	}

	@Override
	public String getUsage() {
		return "Gets NASA's APOD.";
	}

	@Override
	public String getDescription() {
		return "Fetches the Astronomy Picture of the Day (APOD). This service is provided by NASA at  "
				+ "service at https://apod.nasa.gov/apod/astropix.html and requires use of a valid API key."
				+ "\n"
				+ "To register a new API Key, add the`nasaapikey` to your bot's `config.json` file and next to it add"
				+ "the API Key that you register for through NASA at: https://api.nasa.gov/index.html";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"apod"};
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		if (nasaKey == "") {
			sendMessage(event, "[No NASA API Key was found; please add \"nasaapikey\" to bot config.]");
		} else {
			JSONObject apod = fetch(nasaURL);
	        Message m = buildEmbed(apod);
	        event.getChannel().sendMessage(m).queue();
		}
	}
	
	private Message buildEmbed(JSONObject apod) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(java.awt.Color.BLACK);
		eb.addField("Title", apod.getString("title"), false);
		eb.addField("Explanation", apod.getString("explanation"), false);
		eb.addField("Date", apod.getString("date"), true);
		eb.addField("Copyright", apod.getString("copyright"), true);
        eb.addField("HD Image", apod.getString("hdurl"), false);
        eb.setImage(apod.getString("url"));
        eb.setFooter("Data from NASA", "https://api.nasa.gov/images/logo.png");
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

