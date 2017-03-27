# GrimBot
A Java-based chat bot built for Discord that provides chat management functionality, data storage, and operates as an information broker for services provided by the following services:
* [Google Drive API](https://developers.google.com/drive/)
* [Battle.Net API](https://dev.battle.net/)
* [Riot Games API](https://developer.riotgames.com/)
* [Weather Underground API](https://www.wunderground.com/weather/api/)

# Setup
To run GrimBot, you first need to acquire API keys for the services listed above. To register for those API keys, create an account with each of the services linked below, login, then create a new API key for each.

Once you've downloaded this repo, update `config.json` file located in the config folder so that it contains your API key information. Save the file and remember not to share your API keys with anyone!

# Running the Program
Currently, GrimBot is in development stages, so a runnable jar file has not been provided. However, you can download this bot project, drop it into your Eclipse Neon.2 IDE workspace (or the workspace foor another Java IDE), and open it there. You can run the bot directly via the IDE, or export a runnable jar file after configuring it as listed above.

# Project Notes
GrimBot was started mid-February 2017 as a personal project, but has evolved into a more serious undertaking since then. The following is a record of progress prior to GitHub upload:
* **February 18-19:** Created basic bot, verified Discord connectivity, and built basic functions
* **February 25-26:** Changed Discord API Wrapper choice, re-wrote bot
* **March 4-5:** Broke functions up into "modules"; expanded and added features to include 5 new modules with file I/O
* **March 18-19:** Made plan to re-implement bot using "reflection" techniques; investigated Battle.Net API wrappers; may need to write one personally capable of handling OAuth flow
* **March 26:** Setting up GitHub Repo, recording current progress, implementing reflection

# Project Goals
Current goals for GrimBot are constantly evolving, but currently include:
* Implement reflection technique to load new features at runtime
* Explore and implement Google Drive API to dynamically store cloud backups of server logs and bot data
* Explore and implement Battle.Net API to call game data
* Explore and implement Riot Games API to call game data
* Explore and implement Weather Underground API to call weather data

# Project Challenges
Many of GrimBot's challenges likely occur due to its author's newness at writing chat bots. So far, known challenges include:
* Lack of Java Wrappers for desired API; will require additional work to write custom wrapper to parse data
* Lack of time! Good grief, there is never enough of it.
* Lack of experience. Marzipanic spends a lot of time reading about best practices, so much research goes into this.

