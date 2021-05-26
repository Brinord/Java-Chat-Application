import java.net.*;
import java.io.*;


public class Clients  {

	//Generate socket for client
	private Socket socket;
	//Generate server string and user string
	private String server, username;
	//GUI made for client to connect to chatbox
	private GUI clientGUI;
    private ObjectInputStream inputStream;		
    private ObjectOutputStream outputStream;		
    //Port number
    private int portNumber;

    //Client constructor method
    Clients(String server, int portNumber, String username, GUI clientGUI) {
        this.server = server;
        this.portNumber = portNumber;
        this.username = username;
        
        this.clientGUI = clientGUI;
    }
   

    //Client Thread class for when client inputs a message
    class ClientServer extends Thread {
        public void run() {
            while(true) {
                try {
                    String userText = (String) inputStream.readObject();
                    clientGUI.appendUserMessage(userText);
                    }
                catch(IOException error) {
                	//When user logs out, visual text to display them disconnecting from server.
                    display("You have disconnected from the server.");
                    if(clientGUI != null)
                        clientGUI.connectionFault();
                    break;
                }
                catch(ClassNotFoundException classError) {
                }
            }
        }
    }

 
    //Start method
    public boolean start() {
        //Attempt to start client connection
        try {
            socket = new Socket(server, portNumber);
        }
        //Catch unsuccesful attempt to connect to server.
        catch(Exception error) {
            display("Connection Unsuccessful to server: :" + error);
            return false;
        }
        //When client succeeds display a message to show that they are connected.
        String message = "Connection Successful to Address: " + socket.getInetAddress() + ":" + socket.getPort();
        display(message);
        // Get input/output stream
        try
        {
            inputStream  = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException errorStream) {
            display("Error creating Object Stream: " + errorStream);
            return false;
        }

        new ClientServer().start();
        try
        {
            outputStream.writeObject(username);
        }
        catch (IOException errorStream) {
            display("Error occurred during Login: " + errorStream);
            disconnect();
            return false;
        }
        return true;
    }
    
    //Send message method
    void sendMessage(ChatMessage message) {
        try {
            outputStream.writeObject(message);
        }
        catch(IOException error) {
            display("Error occurred during Writing:  " + error);
        }
    }
    
    //Display client message
    private void display(String message) {    
            clientGUI.appendUserMessage(message + "\n");		
    }
    
    //Disconnect client from server, close all sockets.
    private void disconnect() {
        try{
            if(socket != null) socket.close();
        }catch(Exception socketError) {}
        try {
            if(inputStream != null) inputStream.close();
        }
        catch(Exception inputError) {} 
        try {
            if(outputStream != null) outputStream.close();
        }
        catch(Exception outputError) {} 

    }

    
  
}