package com.penguinsonabeach.tuun.Object;

import com.penguinsonabeach.tuun.Activity.MainActivity;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String messageDate;

    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;

        // Initialize to current date
        messageDate = new MainActivity().getDate();

    }

    public ChatMessage() {

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageToUser(String messageFromUser) {
        this.messageUser = messageFromUser;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

}