public class Friend {
	private String name;
	private String displayName;
	
	public Friend(String name, String displayName)
	{
		// constructor. Has name of the friend,
		// stored in the server. Has display name
		// for displaying within the FriendsList class.
		this.name = name;
		this.displayName = displayName;
	}
	
	public String GetName()
	{
		// return server name
		return name;
	}
	
	public String GetDisplay()
	{
		// return display name
		return displayName;
	}
}
