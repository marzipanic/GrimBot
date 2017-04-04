package grimbot;

// Permission Levels
public class Permission {
	
	public enum Perm {
		// Listed in order of least to most permissions
		BANNED_USER,
		BOT,
		USER,
		APPROVED_BOT,
		APPROVED_USER,
		SERVER_MODERATOR,
		SERVER_ADMIN,
		SERVER_OWNER,
		CREATOR;
		
		public Perm getPerm(String r) {
			for (Perm perm : values()) {
				if (perm.name().equalsIgnoreCase(r)) {
					return perm;
				}
			}
			return null;
		}
		
		public boolean isAtLeast(Perm perm) {
		    return this.ordinal() >= perm.ordinal();
		}
		
		public boolean isAtMost(Perm perm) {
		    return this.ordinal() <= perm.ordinal();
		}
		
		public boolean isHigherThan(Perm perm) {
		    return this.ordinal() > perm.ordinal();
		}
		
		public boolean isLowerThan(Perm perm) {
		    return this.ordinal() < perm.ordinal();
		}
	}
}
