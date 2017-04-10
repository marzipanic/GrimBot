# GrimBot
A Java-based chat bot built for Discord that provides chat management functionality, data storage, and (will eventually operate as an information broker for services provided by the following:
* [Google Drive API](https://developers.google.com/drive/)
* [Battle.Net API](https://dev.battle.net/)
* [Riot Games API](https://developer.riotgames.com/)
* [Weather Underground API](https://www.wunderground.com/weather/api/)

GrimBot is written in Java and utilizes Maven to fetch dependencies, which include:
* [sqlite-jdbc](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc) - A library for creating and maintaining an embedded SQLite database.
* [JDA](https://github.com/DV8FromTheWorld/JDA) - An API wrapper for Discord.
* [Google OAuth Client](https://developers.google.com/api-client-library/java/google-oauth-java-client/) - A library to handle OAuth connections to Google accounts.

# Setup
To run GrimBot, you first need to acquire API keys for the services listed above. To register for those API keys, create an account with each of the services linked below, login, then create a new API key for each.

Once you've downloaded this repo, update `config.json.example` file located in the config folder by removing the ".example" extension. You will then need to open the file and replace each of the items listed below with appropriate values:
* **token:** This field should contain your Discord Bot API Key, which you can register for via the [Discord Developer Site](https://discordapp.com/login?redirect_to=/developers/applications/me).
* **prefix:**  Choose a command prefix, such as `!`; useful for avoiding collisions with other bots.
* **game:** An optional field that will set the "playing [...]" status message on the bot.
* **permissions:** An integer representing the standard permission set that should be given to your bot. To generate this integer, use the [Discord Permissions Calculator](https://discordapi.com/permissions.html).

Once you've updated these values, save the file and remember not to share your API keys with anyone!

# Running the Program
Currently, GrimBot is in development stages, so a runnable jar file has not been provided. However, you can download this bot project, drop it into your Eclipse Neon.2 IDE workspace (or the workspace for another Java IDE), and open it there. You can run the bot directly via the IDE, or export a runnable jar file after configuring it as listed above.

# Project Notes
GrimBot was started mid-February 2017 as a personal project, but has evolved into a more serious undertaking since then. The following is a record of progress prior to GitHub upload:
* **February 18-19:** Created basic bot, verified Discord connectivity, and built basic functions
* **February 25-26:** Changed Discord API Wrapper choice, re-wrote bot
* **March 4-5:** Broke functions up into "modules"; expanded and added features to include 5 new modules with file I/O
* **March 18-19:** Made plan to re-implement bot using "reflection" techniques; investigated Battle.Net API wrappers; may need to write one personally capable of handling OAuth flow
* **March 26:** Setting up GitHub Repo, recording current progress, implementing reflection
* 

# Immediate Project To-Do List
Current goals for GrimBot are constantly evolving, but currently include:
* Refactor modules using textfile storage (MagicBall, Hello, ChatPurge) to use new SQLite database
* Refactor textfile import methods to handle easy, human-readable text files
* Refactor ChatPurge module to wait() and respect 2 week message-limit on Bot fetches in Discord
* Build skeleton into ChatPurge to handle full-channel purging (future API feature for Discord)
* Code forward logging module
* Explore and implement Google Drive API to dynamically store cloud backups of server logs and bot data

# Project Challenges
Many of GrimBot's challenges likely occur due to its author's newness at writing chat bots. So far, known challenges include:
* Lack of Java Wrappers for desired API; may require custom wrappers to parse data
* Lack of time! Good grief, there is never enough of it.
* Lack of experience. Marzipanic spends a lot of time reading about best practices, so much research goes into this.

