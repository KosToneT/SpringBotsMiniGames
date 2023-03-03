package com.develop.SpringMiniGames.Bots;

public interface Bot {
    void start();
    void stop();
    boolean isOnline();
    void setMessageObserver(MessageObserver messageObserver);
    void sendMessage(UserModel user, String text);
    void sendPhoto(UserModel user, java.awt.image.BufferedImage photo);
    void setUserDB(UserDB userDB);
    String getBotName();
    Platform getPlatform();
}
