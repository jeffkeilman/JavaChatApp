import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * A Client class that connects to the server. It sends the messages,
 * typed into the ChatWindow to the Server.
 */
public class Client {

    BufferedReader in;
    PrintWriter out;
    FriendsList lol = new FriendsList(this);
    
    public Client() {
        // Layout GUI
    }
    
    public void SendMessage(Message message)
    {
    	System.out.println(message.GetTarget());
    	out.println(message.GetMessage() + "+/+" + message.GetTarget());
    	// out.println(message); <-- might be able to add later. Object types for each message type? 
    }
    
    public void SendMessage(Message message, String messageType)
    {
    	out.println(messageType + " " + message.GetMessage());
    }

    /**
     * Prompt for and return the address of the server.
     */
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            lol,
            "Enter IP Address of the Server:",
            "Welcome to the Chatter",
            JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Prompt for and return the desired screen name.
     */
    private String getName() {
        return JOptionPane.showInputDialog(
            lol,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Connects to the server then enters the processing loop.
     */
    private void run() throws IOException {

        // Make connection and initialize streams
        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                out.println(getName());
            } else if (line.startsWith("NAMEACCEPTED")) {
            	lol.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            	lol.setTitle("Chat Program"); 
            	lol.setVisible(true);
            } else if (line.startsWith("MESSAGE")) {
                lol.chat.DisplayArea.append(line.substring(8) + "\n");
                lol.chat.MessagePane.setText("");
            } else if (line.startsWith("SEARCHAPPROVED")) {
            	String displayName;
            	displayName = JOptionPane.showInputDialog(
                        	  lol,
                        	  "Enter your friend's display name:",
                        	  "Screen name selection",
                        	  JOptionPane.PLAIN_MESSAGE);
            	lol.friends.add(new Friend(line.substring(15), displayName));
            	for (Friend x: lol.friends)
            	{
            		System.out.println(x.GetName());
            	}
            	lol.PopulateList();
            } else if (line.startsWith("SEARCHDENIED")) {
            	JOptionPane.showMessageDialog(null, "Name not registered!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void SearchNames(String name)
    {
    	out.println("SEARCH " + name);
    }

    /**
     * Runs the client
     */
    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.run();
    }
}