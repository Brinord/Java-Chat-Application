import java.io.Serializable;


public class ChatMessage implements Serializable {
	static final int USERLIST = 0, USERMESSAGE = 1, LOGOUT = 2;
	//Serialization used to convert object to stream, allowing for user to type in the chatbox.
	//Specific serialVersionUID is required for the user to be able to chat without disconnecting
	//ID: 1112122200L allows for user to connect to chatbox.
    protected static final long serialVersionUID = 1112122200L;
    //Constant values used for Switch Case.
    private int messageType;
    private String message;

    //Send Message constructor
    ChatMessage(int messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    //Getter method
    int getMessageType() {
        return messageType;
    }
    String getMessage() {
        return message;
    }
}