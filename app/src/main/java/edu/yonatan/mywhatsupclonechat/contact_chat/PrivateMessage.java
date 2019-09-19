package edu.yonatan.mywhatsupclonechat.contact_chat;

public class PrivateMessage {


    private String from, message, type;

    public PrivateMessage() {
    }

    public PrivateMessage(String from, String message, String type) {
        this.from = from;
        this.message = message;
        this.type = type;

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "PrivateMessage{" +
                "from='" + from + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
