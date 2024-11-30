package com.example.neurochat;

public class user {
    private String userId;  // Add userId field
    private String userName;
    private String mail;
    private String profilepic;
    private String status;
    private String lastMessage;
    
    // Constructor with userId
    public user(String userId, String userName, String mail, String profilepic, String status, String lastMessage) {
        this.userId = userId;
        this.userName = userName;
        this.mail = mail;
        this.profilepic = profilepic;
        this.status = status;
        this.lastMessage = lastMessage;
    }

    // Getters and setters for all fields
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
