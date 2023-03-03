package com.develop.SpringMiniGames.Reports;

import com.develop.SpringMiniGames.Bots.Platform;
import com.develop.SpringMiniGames.Bots.State;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; 
    
    public String text;
    long userId;
    Platform platform;
    public String username;
    public String state;
    long timestamp;
    public Report(){}


    public Report(String text, long userId, Platform platform, String username, State state){
        this.text = text;
        this.userId = userId;
        this.platform = platform;
        this.username = username;
        this.state = state==null?"null":state.getClass().getSimpleName();
        timestamp = System.currentTimeMillis();
    }



}
