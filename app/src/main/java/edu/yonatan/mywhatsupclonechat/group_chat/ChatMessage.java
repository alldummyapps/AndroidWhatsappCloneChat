package edu.yonatan.mywhatsupclonechat.group_chat;

public class ChatMessage {

    //props:
    private String title;
    private String body;
    private String time;

    //ctor:
    public ChatMessage(String title, String body, String time) {
        this.title = title;
        this.body = body;
        this.time = time;
    }


    //getters&setters:
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    //toString:
    @Override
    public String toString() {
        return "ChatMessage{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", time='" + time + '\'' +
                '}';
    }


}
