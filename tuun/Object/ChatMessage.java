package com.penguinsonabeach.tuun.Object;

import com.penguinsonabeach.tuun.Activity.MainActivity;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String messageDate;
    private String messagePhoto;
    private String messageUserId;
    private String messageRead;

    public ChatMessage(String messageText, String messageUser, String messagePhoto) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messagePhoto = messagePhoto;

        // Initialize to current date
        messageDate = new MainActivity().getDate();

        messageRead = "Unread";

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

    public String getMessagePhoto() {
        return messagePhoto;
    }

    public void setMessagePhoto(String messagePhoto) {
        this.messagePhoto = messagePhoto;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(String messageUserId) {
        this.messageUserId = messageUserId;
    }

    public String getMessageRead() {
        return messageRead;
    }

    public void setMessageRead(String messageRead) {
        this.messageRead = messageRead;
    }

}