import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * This is a multi-threaded chat server. It takes on several connections and relays messages
 * when fed an address (name) that is appended to the end of a message. The server will store
 * several printwriters that allows it to keep track of current users, and forward messages appropriately.
 * There is also a list of corresponding names (both lists are synchronized by index). 
 * 
 * There are several preset headers message that can be sent to a Client and a FriendsList in order to get information.
 * SUBMITNAME will get a new username from a the FriendsList class. NAMEACCEPTED informs
 * the FriendsList class that the name was accepted. MESSAGE informs the client that the text they are recieving is 
 * a message to be displayed.
 */
public class Server {

    /**
     * The port that the server listens on.
     */
    private static final int PORT = 9001;

    /**
     * The set of all names of clients in the chat room.  Maintained
     * so that we can check that new clients are not registering name
     * already in use.
     */
    private static ArrayList<String> names = new ArrayList<String>();

    /**
     * The set of all the print writers for all the clients.  This
     * set is kept so we can easily broadcast messages.
     */
    private static ArrayList<PrintWriter> writers = new ArrayList<PrintWriter>();

    /**
     * The application main method, which just listens on a port and
     * spawns handler threads.
     */
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * A handler thread class.  
     * New handlers are given with each 
     * new client and help to broadcast messages.
     */
    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {

                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Request a name from this FriendsList. Keep asking
                // until a unique name is given.
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }

                // Name adding was successful. Add a printwriter to
                // the list.
                out.println("NAMEACCEPTED");
                writers.add(out);

                while (true) {
                    String input = in.readLine();
                    
                    if (input == null) {
                        return;
                    }
                    
                    if (input.startsWith("SEARCH"))
                    {
                    	if (names.contains(input.substring(7)))
                    	{
                    		out.println("SEARCHAPPROVED " + input.substring(7));
                    	} else
                    	{
                    		out.println("SEARCHDENIED");
                    	}
                    }
                    else
                    {
	                    String[] container = input.split(Pattern.quote("+/+"));
	                    System.out.println(container[0]);
	                    System.out.println(names.indexOf(container[1]));
	                    System.out.println(names.indexOf(name));
	                    PrintWriter temp1 = writers.get(names.indexOf(container[1]));
	                    PrintWriter temp2 = writers.get(names.indexOf(name));
	                
	                    temp1.println("MESSAGE " + name + ": " + container[0]);
	                    temp2.println("MESSAGE " + name + ": " + container[0]);
	                    
	                    temp1.flush();
	                    temp2.flush();
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // Delete client from list! It is closing.
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}