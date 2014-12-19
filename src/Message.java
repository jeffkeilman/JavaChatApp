public class Message {
	private String msgtxt;
	private String target;
	
	public Message(String msgtxt, String target)
	{
		// overload constructor. Forms a message of type String
		// with a String "target" whom the message will
		// be sent to on the server
		
		this.msgtxt = msgtxt;
		this.target = target;
	}
	
	public Message(String msgtxt)
	{
		// constructor. Forms a message meant simply for the server
		// to do some processing
		
		this.msgtxt = msgtxt;
	}
	
	public String GetMessage()
	{
		// gets just the text from a message
		return msgtxt;
	}
	
	public String GetTarget()
	{
		// gets the target
		return target;
	}
}
