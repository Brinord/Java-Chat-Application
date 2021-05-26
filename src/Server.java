import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
    // boolean flag to indicate whether server is still online.
    private boolean serverOnline;
    // Array list generated for every client connected.
    private ArrayList<ClientThread> clientList;
    // Generated user ID for every connection made by client
    private static int userID;
    // Port Number for the Server to listen to
    private int portNumber;
    //Standard Date Time Formatting.
    private SimpleDateFormat dateTime;



    //Server Constructor method, with port number as input.
    public Server(int portNumber) {
        this.portNumber = portNumber;
        // Generate client list
        clientList = new ArrayList<ClientThread>();
        dateTime = new SimpleDateFormat("HH:mm:ss");
    }
    
    
    //Synchronized method to add onto each message.
    private synchronized void chatMessage(String message) {
        // Message contains time
        String chatTime = dateTime.format(new Date());
        String appendedMessage = chatTime + " " + message + "\n";
        //For loop to get every client, done in reverse order.
        for(int i = clientList.size(); --i >= 0;) {
            ClientThread client = clientList.get(i);
            // Check to see if client has disconnected and remove them from the list if they have. (AFK Checker)
            if(!client.messageWrite(appendedMessage)) {
                clientList.remove(i);
            }
        }
    }
    
    // Remove Method for when client logs out.
    synchronized void remove(int id) {
    	// For loop to find client ID.
        for(int i = 0; i < clientList.size(); ++i) {
            ClientThread client = clientList.get(i);
            // found it
            if(client.uniqueID == id) {
                clientList.remove(i);
                return;
            }
        }
    }

    //For When Server is online.
    public void online() {
        serverOnline = true;
		// Create Socket Server with Port Number.
        try
        {
            // the socket used by the server
            ServerSocket serverSocket = new ServerSocket(portNumber);

            // While loop, keeping server online until user closes the server
            while(serverOnline)
            {
                Socket socket = serverSocket.accept();  	// accept connection
                // Break loop to close the server.
                if(!serverOnline)
                    break;
                // Multithreading for each client accepted
                ClientThread client = new ClientThread(socket);
                // Client is added into client list
                clientList.add(client);	
                // Start thread for client
                client.start();
            }
            // Try method for when server is closed.
            try {
                serverSocket.close();
                // For loop to disconnect every client connected.
                for(int i = 0; i < clientList.size(); ++i) {
                	//Get every client from list.
                    ClientThread clientClose = clientList.get(i);
                    try {
                        clientClose.sInput.close();
                        clientClose.sOutput.close();
                        clientClose.socket.close();
                    }
                    catch(IOException IOError) {
                    }
                }
            }
            catch(Exception error) {
            }
        }
        catch (IOException IOError) {
        }
    }
 


    //Main Method
    public static void main(String[] args) {
        // Default Port Number
        int portNumber = 0; 
        if (args.length > 0) {
            try {
                portNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }
        }
        // Create a new server
        Server server = new Server(portNumber);
        //Server is now online.
        server.online();
    }

    // Thread Class For Each Client
    class ClientThread extends Thread {
        Socket socket;
        // Date of user connecting
        String date;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        // Client unique ID
        int uniqueID;
        // the Username of the Client
        String username;
        // User Message
        ChatMessage userMessage;
      
        public void run() {
            boolean serverOnline = true;
            //Run while server is online
            while(serverOnline) {
                try {
                    userMessage = (ChatMessage) sInput.readObject();
                }
                catch (IOException error) {
                    break;
                }
                catch(ClassNotFoundException classError) {
                    break;
                }
                //Message string 
                String message = userMessage.getMessage();

                // Switch case
                switch(userMessage.getMessageType()) {
                    case ChatMessage.USERMESSAGE:
                        chatMessage(username + ": " + message);
                        break;
                    case ChatMessage.LOGOUT:
                        serverOnline = false;
                        break;
                    case ChatMessage.USERLIST:
                        messageWrite("List of users connected at time: " + dateTime.format(new Date()) + "\n");
                        // For loop to get all the users.
                        for(int i = 0; i < clientList.size(); ++i) {
                            ClientThread client = clientList.get(i);
                            if (i==0)
                            	messageWrite((i+1) + ") " + client.username + " - Coordinator " + " IP: " + socket.getInetAddress() + " Port: " + socket.getPort() +  "\n");
                            else
                            	messageWrite((i+1) + ") " + client.username +  " IP: " + socket.getInetAddress() + " Port: " + socket.getPort() + "\n");
                        }
                        break;
                }
            }
            //Case for when client is logged out, remove client from list.
            remove(uniqueID);
            logout();
        }

        // Constructor method for each client connected.
        ClientThread(Socket socket) {
            // Increment user ID by one
            uniqueID = ++userID;
            this.socket = socket;
            //Get InputStream and OutputStream
            try
            {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput  = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            }
            catch (IOException error) {
                return;
            }
            catch (ClassNotFoundException error) {
            }
        }
        
        // Close every connection.
        private void logout() {
            try {
                if(sOutput != null) sOutput.close();
            }
            catch(Exception error) {}
            try {
                if(sInput != null) sInput.close();
            }
            catch(Exception error) {};
            try {
                if(socket != null) socket.close();
            }
            catch (Exception error) {} 
        }

             
        private boolean messageWrite(String msg) {
            //Check to see if client is still connected
            if(!socket.isConnected()) {
                logout();
                return false;
            }
            try {
                sOutput.writeObject(msg);
            }
            catch(IOException e) {
            }
            return true;
        }




    }
}