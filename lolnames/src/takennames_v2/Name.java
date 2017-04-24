package takennames_v2;

public class Name {
	private String username;
	private int id;
	private long lastActive;
	private long lastChecked;

	public Name() {
		setup(null, -1, -1, -1);
	}

	public Name(String username) {
		setup(username, -1, -1, -1);
	}

	public Name(int id) {
		setup(null, id, -1, -1);
	}

	public Name(String username, int id) {
		setup(username, id, -1, -1);
	}

	public Name(String username, int id, long lastActive, long lastChecked) {
		setup(username, id, lastActive, lastChecked);
	}

	private void setup(String username, int id, long lastActive, long lastChecked) {
		setUsername(username);
		setId(id);
		setLastActive(lastActive);
		setLastChecked(lastChecked);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (username != null && username.length() > 16)
			username = null;
		this.username = username;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		if (id < 0)
			id = -1;
		this.id = id;
	}

	public long getLastActive() {
		return lastActive;
	}

	public void setLastActive(long lastActive) {
		this.lastActive = lastActive;
	}

	public long getLastChecked() {
		return lastChecked;
	}

	public void setLastChecked(long lastChecked) {
		this.lastChecked = lastChecked;
	}

	@Override
	public String toString() {
		return id + ", " + username + ", " + lastActive + ", " + lastChecked;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass())
			return false;
		Name name = (Name) o;
		if (getId() != name.getId() || !getUsername().equals(name.getUsername()))
			return false;
		return true;
	}

	public static Name toName(String info) {
		if (info.length() == 0)
			return null;
		String[] parts = info.split(",");
		return new Name(parts[1].trim(), Integer.parseInt(parts[0].trim()), Long.parseLong(parts[2].trim()),
				Long.parseLong(parts[3].trim()));
	}
}
