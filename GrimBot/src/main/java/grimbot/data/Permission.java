package grimbot.data;

public enum Permission {
	// Listed in order of least to most permissions
	BANNED_USER,
	BOT,
	USER,
	APPROVED_BOT,
	APPROVED_USER,
	SERVER_MODERATOR,
	SERVER_ADMIN,
	SERVER_OWNER;
	
	public Permission getPerm(String r) {
		for (Permission perm : values()) {
			if (perm.name().equalsIgnoreCase(r)) {
				return perm;
			}
		}
		return null;
	}
	
	public boolean isAtLeast(Permission perm) {
	    return this.ordinal() >= perm.ordinal();
	}
	
	public boolean isAtMost(Permission perm) {
	    return this.ordinal() <= perm.ordinal();
	}
	
	public boolean isHigherThan(Permission perm) {
	    return this.ordinal() > perm.ordinal();
	}
	
	public boolean isLowerThan(Permission perm) {
	    return this.ordinal() < perm.ordinal();
	}
}
