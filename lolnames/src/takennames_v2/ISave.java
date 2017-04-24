package takennames_v2;

public interface ISave {
	/**
	 * Saves the information to the specified path.
	 * 
	 * @param path
	 *            path to save, or where the program is running if null
	 */
	public void save();

	/**
	 * Loads the information at the specified path.
	 * 
	 * @param path
	 *            path load from, or where the program is running if null
	 */
	public void load();
}
