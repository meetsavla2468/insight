package Model;

public class gptMessage_Model {
    private String message;
    private boolean isUser;  // true for user, false for GPT

    public gptMessage_Model(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}
