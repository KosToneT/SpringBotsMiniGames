package com.develop.SpringMiniGames.Bots;

public class Message {
    private UserModel user;
    private String text;
    private int id;
    
    public Message(UserModel user, String text, int id) {
        this.user = user;
        this.text = text;
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }
    
    
}
