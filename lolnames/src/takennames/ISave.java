package takennames;

public interface ISave {
	/**
	 * Saves the information to the specified path.
	 * 
	 * @param path
	 *            path to save, or where the program is running if null
	 */
	public boolean save(String path);
	/**
	 * Loads the information at the specified path.
	 * 
	 * @param path
	 *            path load from, or where the program is running if null
	 */
	public void load(String path);
	/**
	 * Clears the information at the location path. May not clear if the
	 * filetype doesn't match what the program wants (extension).
	 * 
	 * @param path
	 *            path with the file to clear, or where the program is running
	 *            if null
	 */
	public void clear(String path);
}
