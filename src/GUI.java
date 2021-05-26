import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI extends JFrame implements ActionListener, KeyListener {
	private static final long serialVersionUID = 1L;
	// Client Class Object
    private Clients client;
	// Default Port Number
    private int defaultPort;
    private String defaultHost;
    // Field holding data regarding UserID and User Text Message
    private JLabel userField;
    // Standard Text Field
    private JTextField textField;
    // Text Field Used to hold Server Data and Port Data
    private JTextField serverField, portField;
    // Buttons Generated for functions: User Login, User Logout and Checking how many users are in.
    private JButton login, logout, userList;
    // Text Area Generated for the Chatroom
    private JTextArea textArea;
    // Boolean flag to indicate whether the user has connected.
    private boolean connected;

    // GUI Constructor Class, taking input default port and host
    GUI(String userHost, int userPort) {

        super("Chatbox");
        defaultPort = userPort;
        defaultHost = userHost;
        
        //Action Listener on standby until user presses the button.
        //Login Button Generated.
        login = new JButton("Login");
        login.addActionListener(this);
        //Logout button Generated.
        logout = new JButton("Logout");
        logout.addActionListener(this);
        logout.setEnabled(false);		// User unable to press logout until logged in.
        //User List Button Generated.
        userList = new JButton("User List");
        userList.addActionListener(this);
        userList.setEnabled(false);		// User unable to see the user list until logged in.

        JPanel northPanel = new JPanel();
        northPanel.add(login);
        northPanel.add(logout);
        northPanel.add(userList);
        add(northPanel, BorderLayout.NORTH);

        // The Lower Half Of the Chatbox
        JPanel southPanel = new JPanel(new GridLayout(3,1));
        JPanel southLayout = new JPanel(new GridLayout(1,3));
        // Textfields generated for functions: Server field and Port field.
        serverField = new JTextField(userHost);
        portField = new JTextField("" + userPort);
        //Label asking for port number
        southLayout.add(new JLabel("Port Number:  "));
        southLayout.add(portField);
       //Label asking for Server IP Address
        southLayout.add(new JLabel("Server IP:  "));
        southLayout.add(serverField);
        southLayout.add(new JLabel(""));
        southPanel.add(southLayout);

        // Userfield asking for user ID.
        userField = new JLabel("Enter your ID below:");
        southPanel.add(userField);
        textField = new JTextField("");
        //Text field for user to write their ID
        southPanel.add(textField);
        add(southPanel, BorderLayout.SOUTH);

        // Main Text Area for Clients to text to eachother
        textArea = new JTextArea(40, 40);
        JPanel centerLayout = new JPanel(new GridLayout(1,1));
        centerLayout.add(new JScrollPane(textArea));
        textArea.setEditable(false);
        add(centerLayout, BorderLayout.WEST);
        
  
        textArea.addKeyListener(this);
        serverField.addKeyListener(this);
        portField.addKeyListener(this);
        userField.addKeyListener(this);
        textField.addKeyListener(this);
        
        setSize(600, 600);
        setVisible(true);
        //Requests input focus.
        textField.requestFocus();
        //Close method for user exit.
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    //Append function for User Message
    void appendUserMessage(String str) {
        textArea.append(str);
        //Sets text position
        textArea.setCaretPosition(textArea.getText().length() - 1);
    }
    //Fault Tolerance: Upon user disconnect, text fields will reset allowing for user to reconnect back into the chatroom.
    void connectionFault() {
        login.setEnabled(true);
        logout.setEnabled(false);
        userList.setEnabled(false);
        userField.setText("Enter User ID:");
        // Port field is reset.
        portField.setText("" + defaultPort);
        serverField.setText(defaultHost);
        // Both fields are now editable, allowing the user to reconnect
        serverField.setEditable(true);
        portField.setEditable(true);
        textField.removeActionListener(this);
        connected = false;
    }


    //
    public void actionPerformed(ActionEvent e) {
        Object keyPress = e.getSource();
        
        // If user presses user list
        if(keyPress == userList) {
            client.sendMessage(new ChatMessage(ChatMessage.USERLIST, ""));
            return; 
        }
        // If the user presses logout
        if(keyPress == logout) {
            client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
            return;
        }
        // If the user is connected
        if(connected) {
            // Send Message function used to send message to chatbox.
            client.sendMessage(new ChatMessage(ChatMessage.USERMESSAGE, textField.getText()));
            //Reset text field to blank after message has been sent.
            textField.setText("");
            return;
        }

        //If user presses login
        if(keyPress == login) {
            // Username field for user.
            String username = textField.getText().trim();
            // If the username length = 0, login will not work until user inputs a name.
            if(username.length() == 0)
                return;
            // If server length = 0, will not connect.
            String server = serverField.getText().trim();
            if(server.length() == 0)
                return;
            // If port number = 0
            String portNumber = portField.getText().trim();
            if(portNumber.length() == 0)
                return;
            int port = 0;
            try {
                port = Integer.parseInt(portNumber);
            }
            catch(Exception error) {
                return;  
            }

            // Generate the client consisting of: Server IP, Port Number, Username
            client = new Clients(server, port, username, this);
            //Client connected
            if(!client.start())
                return;
            textField.setText("");
            //Field changed from enter ID, to change message below.
            userField.setText("Enter your message below");
            // Login button set to disabled, as user has already logged in.
            login.setEnabled(false);
            // User can now logout
            logout.setEnabled(true);
            // User can now be able to press the user list.
            userList.setEnabled(true);
            // Port field disabled as the user is already connected.
            portField.setEditable(false);
            // Action Listener used to wait for user input.
            textField.addActionListener(this);
            // Server Field disabled as the user is already connected.
            serverField.setEditable(false);
            // User is now connected.
            connected = true;

 
        }
        
       
    }

    // Main Method
    public static void main(String[] args) {
    	// Default port: 9999
        new GUI("",9999);
    }

    
    // Default Standard KeyListener Code for User Input.
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()) {
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}