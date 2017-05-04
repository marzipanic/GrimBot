# GrimBot
![GrimBot Banner](https://github.com/marzipanic/GrimBot/blob/master/GrimBotChannelBanner.png?raw=true)
A Java-based chat bot built for Discord that provides chat management functionality, data storage, and (eventually) operate as an information broker for services such as:
* [Google Drive API](https://developers.google.com/drive/)
* [Weather Underground API](https://www.wunderground.com/weather/api/)

# Status 
[![GitHub version](https://badge.fury.io/gh/marzipanic%2FGrimBot.svg)](https://badge.fury.io/gh/marzipanic%2FGrimBot)
[![Dependency Status](https://www.versioneye.com/user/projects/590a66d39e070f003f7cb1f2/badge.svg?style=flat)](https://www.versioneye.com/user/projects/590a66d39e070f003f7cb1f2)

GrimBot uses Maven to fetch dependencies, which include:
* [sqlite-jdbc](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc) - Library to create and maintain an embedded SQLite database.
* [JDA](https://github.com/DV8FromTheWorld/JDA) - A Java API wrapper for Discord.
* [Google OAuth Client](https://developers.google.com/api-client-library/java/google-oauth-java-client/) - Library to handle OAuth connections to Google accounts.
* [OKHttp](http://square.github.io/okhttp/) - Library to handle HTTP requests.

# Current Features
GrimBot is being built with Server Administrators and Plugin Developers in mind. Some of its features currently include:
* **Regex Identification:** All plugins must specify a unique Regex pattern that will help the bot's `ChatListener.java` class match the appropriate plugin to the appropriate chat command. Test your regex at [RegexPlanet](http://www.regexplanet.com/advanced/java/index.html).
* **Reflection:** Plugins written for this bot by others may be dropped into the `plugins` folder, no need to recompile the code! Simply restarting the bot will load them up.
* **Robust Help Functions:** The abstract `Plugin.java` class helps plugin writers to easily include information about the plugin into the bot's normal help commands.
* **SQLite Database:** Plugin authors can connect to the SQLite Database built into this bot in order to add persistent storage to their plugin.
* **Complimentary Plugins:** To help you get started, several example plugins, such as `Hello.java`, `Dice.java`, and `Joke.java` have been included. Note: the `ChatPurge.java` module provides the ability to delete the last 100 messages in chat, up to 2 weeks past (specific cap on Discord Bots); *permissions are not yet handled, so any user can run this command!*

# Setup
To run GrimBot, you first need to create a Discord Bot User and acquire a "token" for it. Follow [this tutorial](https://github.com/reactiflux/discord-irc/wiki/Creating-a-discord-bot-&-getting-a-token) to complete the process.

Next, download or clone this repository, then update `config.json.example` file located in the config folder by removing the ".example" extension. You will then need to open the file and replace each of the items listed below with appropriate values:
* **token:** This field should contain your Discord Bot API Key, which you received when you registered at the [Discord Developer Site](https://discordapp.com/login?redirect_to=/developers/applications/me) when you setup your Bot User above.
* **prefix:**  Choose a command prefix, such as `!`; this is useful for avoiding collisions with other bots.
* **game:** An optional field that will set the "playing [...]" status message on the bot.
* **permissions:** An integer representing the standard permission set that should be given to your bot. To generate this integer, use the [Discord Permissions Calculator](https://discordapi.com/permissions.html).

Once you've updated these values, save the file and remember not to share your API keys with anyone! As a heads up, you may also wish to register for and add keys to your config in order to use the following plugins:

| Plugin | Description | API URL | config key |
| --- | --- | --- | --- |
| `APOD.java` | Fetch NASA's Astronomy Picture of the Day | [CLICK](https://api.nasa.gov/) | "nasaapikey" |
| `Weather.java` | Fetch forecasts from Weather Underground | [CLICK](https://www.wunderground.com/weather/api/) | "wuapikey" |

# Running the Program
Currently, GrimBot is in development stages, so a runnable jar file has not been provided. However, you can download this bot project, drop it into your Eclipse Neon.2 IDE workspace (or the workspace for another Java IDE), and open it there. You can run the bot directly via the IDE, or export a runnable jar file after configuring it as listed above.

To get GrimBot onto your server, you'll need to return to the [Discord Developer Site](https://discordapp.com/login?redirect_to=/developers/applications/me) where you must create a new Bot User.

# Project Challenges
Many of GrimBot's challenges likely occur due to its author's newness at writing chat bots. So far, known challenges include:
* Lack of Java Wrappers for desired API; may require custom wrappers to parse data
* Lack of time! Good grief, there is never enough of it.
* Lack of experience. Marzipanic spends a lot of time reading about best practices, so much research goes into this.

# Project To-Do List
Goals for GrimBot are constantly evolving, but immediately include:
* Develop Permissions handling into GrimBot, keeping it simple for Plugin authors.
* Extract `Help` functions from the ChatListener class
* Build plugin embeds for help at bot start up, rather than just the help text
* Begin work on integration of [Battle.Net API](https://dev.battle.net/)
* Begin work on integration of [Riot Games API](https://developer.riotgames.com/)

# Project Notes
GrimBot was started mid-February 2017 as a personal project, but has evolved into a more serious undertaking since then. The following is a record of progress prior to GitHub upload:
* **February 18-19:** Created basic bot, verified Discord connectivity, and built basic functions
* **February 25-26:** Changed Discord API Wrapper choice, re-wrote bot
* **March 4-5:** Broke functions up into "modules"; expanded and added features to include 5 new modules with file I/O
* **March 18-19:** Made plan to re-implement bot using "reflection" techniques; investigated Battle.Net API wrappers; may need to write one personally capable of handling OAuth flow
* **March 26:** Set up GitHub repo.

# Copyright
This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
