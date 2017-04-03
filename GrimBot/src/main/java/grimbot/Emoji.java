package grimbot;

public class Emoji {

	// Listed: https://www.webpagefx.com/tools/emoji-cheat-sheet/
	public enum DiscordEmoji {
		// ADD LIST OF standard Discord Emoji
		OCTOCAT(":octocat:");
		
		private String code;
		
		private DiscordEmoji(String c) {
			code = c;
		}
		
		public String getCode(String code) {
			return code;
		}
	}
	
	public enum CustomEmoji {
		// ADD LIST OF standard Discord Emoji
		OCTOCAT(":octocat:");
		
		private String code;
		
		private CustomEmoji(String c) {
			code = c;
		}
		
		public String getCode(String code) {
			return code;
		}
	}
}
