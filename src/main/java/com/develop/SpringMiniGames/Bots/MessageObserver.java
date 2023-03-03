package com.develop.SpringMiniGames.Bots;

import java.util.LinkedList;

public class MessageObserver {
    private final LinkedList<MessageListener> listListeners = new LinkedList<MessageListener>();

    public void add(MessageListener messageListener){
        listListeners.add(messageListener);
    }

    public void remove(MessageListener messageListener){
        listListeners.remove(messageListener);
    }

    public void notiFy(Message message){
        for(MessageListener i:listListeners){
            if(i.newMessage(message)){
                break;
            }
        }
    }
}
