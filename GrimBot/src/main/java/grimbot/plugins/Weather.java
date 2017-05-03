package grimbot.plugins;

import java.io.IOException;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grimbot.Bot;
import grimbot.Plugin;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Weather extends Plugin{
	
	private static OkHttpClient httpClient = new OkHttpClient();
	private static String wuKey = Bot.config.getSetting("wuapikey", "");
	private static String wuURL = "http://api.wunderground.com/api/"+wuKey;
	private static Pattern zipRegex = Pattern.compile("(^\\d{5}(-\\d{4})?$)|(^[ABCEGHJKLMNPRSTVXY]{1}\\d{1}[A-Z]{1} *\\d{1}[A-Z]{1}\\d{1}$)");

	public Weather() {
		super("^weather($|\\s+|\\s.+)?");
	}

	@Override
	public String getPrimaryAlias() {
		return "weather";
	}

	@Override
	public String[] getOtherAliases() {
		return null;
	}

	@Override
	public String getUsage() {
		return "Gets weather data for zipcode.";
	}

	@Override
	public String getDescription() {
		return "Fetches weather data for a given zipcode or postal code. All data is provided by the Weather Underground service at: https://www.wunderground.com/";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"weather 90210"};
	}

	@Override
	public String[] getParameters() {
		return new String[] {"zipcode | postal code"};
	}

	@Override
	public void handleMessage(String msg, MessageReceivedEvent event) {
		User author = event.getAuthor();
		String[] params = msg.split(" ", 2);
		if (params.length > 1) {
			if (zipRegex.matcher(params[1]).matches()) {
				if (!event.isFromType(ChannelType.PRIVATE)) {
					event.getChannel().sendMessage(author.getAsMention() + " | Let's speak in private.").queue();
				}
				handleForecast(event, params[1]);
			} else {
				sendMessage(event, "[Zipcode or Postal Code used was not recognized within the US or Canada.]");
			}
		} else {
			sendMessage(event, "[Bot command was missing <zipcode | postal code> parameter. Type `"+Bot.prefix+"help weather` for more information.]");
		}
	}
	
	private void sendForecast(MessageReceivedEvent event, JSONObject geo, JSONArray fc) {
		User author = event.getAuthor();
		author.openPrivateChannel().queue( success -> {
			
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(java.awt.Color.BLACK);
            eb.setThumbnail(fc.getJSONObject(0).getString("icon_url"));
            eb.setFooter("Data from Weather Underground", "https://lh6.ggpht.com/DbtmHUmcZmdm2EyktcY81p91U-"
            		+ "9tCKCdOLn_brEukTpER29z6SXIEveenZkDo4OdOBGz=w30");
            eb.setDescription(parseForecast(geo, fc));
            MessageEmbed e = eb.build();
            Message m = new MessageBuilder().setEmbed(e).build();
            author.getPrivateChannel().sendMessage(m).queue();
        });
	}
	
	private String parseForecast(JSONObject geo, JSONArray fc) {
		String forecast = "__**WEATHER FORECAST**__ "
        		+ "\n**Zipcode:** "+geo.getString("zip")
        		+ "\n**Region:** "+geo.getString("city")+", "+geo.getString("state")+", "+geo.getString("country_name")
        		+ "\n**Timezone:** "+geo.getString("tz_long")
        		+ "\n";
		
		for (int i = 0; i < fc.length(); i++) {
			JSONObject f = fc.getJSONObject(i);
			JSONObject date = f.getJSONObject("date");
    		forecast += "\n *"+date.getString("weekday")+", "+date.getInt("day")+" "+date.getString("monthname")+" "
    				+date.getInt("year")+" ("+date.getString("ampm")+")*"
    				+ "\n**Conditions:** "+f.getString("conditions")
    				+ "\n**Temperature:** "+f.getJSONObject("low").getString("fahrenheit")+"\u00B0 F to "
    						+ f.getJSONObject("high").getString("fahrenheit")+"\u00B0 F"
    				+ "\n**Humidity:** "+f.getInt("avehumidity")+"%"
					+ "\n**Wind:** "+f.getJSONObject("avewind").getString("dir")+" "+f.getJSONObject("avewind").getInt("mph")+" mph";
    		
    		if (!(f.getJSONObject("snow_allday").getInt("in") == 0)) {
    			forecast += "\n**Snow:** "+f.getJSONObject("snow_allday").getInt("in")+" inches";
    		}
    		forecast += "\n";
		}
		forecast += "\n**URL:** "+geo.getString("wuiurl")+"\n";
		return forecast;
	}
	
	private void handleForecast(MessageReceivedEvent event, String zip) {
		JSONObject geolookup = fetch(wuURL+"/geolookup/q/"+zip+".json");
		String request = geolookup.getJSONObject("location").getString("requesturl");
		JSONObject forecast = fetch(wuURL+"/forecast/q/"+request.replace(".html", ".json"));
		sendForecast(event, geolookup.getJSONObject("location"), 
				forecast.getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday"));
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
	
	private void sendDM(MessageReceivedEvent event, String message) {
		event.getAuthor().getPrivateChannel().sendMessage(message).queue();
	}
	
	private void sendMessage(MessageReceivedEvent event, String message){
			String author = event.getAuthor().getAsMention();
			event.getChannel().sendMessage(author + message).queue();
	}
}
