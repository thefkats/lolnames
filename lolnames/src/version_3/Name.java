package version_3;

public class Name implements Comparable<Name> {
	private int id;
	private String uname;
	private long lastActive;
	private long lastChecked;
	private boolean saved;

	public Name(String info) {
		String[] parts = info.split(",");
		if (parts.length == 2)
			setup(Integer.parseInt(parts[0].trim()), parts[1].trim(), -1, -1, false);
		else if (parts.length == 3)
			setup(Integer.parseInt(parts[0].trim()), parts[1].trim(), Long.parseLong(parts[2].trim()), -1, false);
		else if (parts.length == 4)
			setup(Integer.parseInt(parts[0].trim()), parts[1].trim(), Long.parseLong(parts[2].trim()), Long.parseLong(parts[3].trim()), true);
	}

	public Name(int id) {
		setup(id, null, -1, -1, false);
	}

	public Name(int id, String uname) {
		setup(id, uname, -1, -1, false);
	}

	public Name(int id, String uname, long lastActive, long lastChecked, boolean saved) {
		setup(id, uname, lastActive, lastChecked, saved);
	}

	private void setup(int id, String uname, long lastActive, long lastChecked, boolean saved) {
		this.id = id;
		setUname(uname);
		setLastActive(lastActive);
		setLastChecked(lastChecked);
		setSaved(saved);
	}

	public int getId() {
		return id;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
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

	public boolean isSaved() {
		return saved;
	}
	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	@Override
	public String toString() {
		return id + "," + (uname == null ? "-" : uname) + "," + lastActive + "," + lastChecked;
	}
	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass())
			return false;
		Name n = (Name) o;
		return n.getId() == id && n.isSaved() == saved;
	}
	@Override
	public int compareTo(Name o) {
		if (o == null)
			throw new NullPointerException("null pointer");// TODO
		return id - o.getId();
	}

}
