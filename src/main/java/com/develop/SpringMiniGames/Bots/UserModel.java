package com.develop.SpringMiniGames.Bots;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Transient;


@Entity
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; 

    private Integer userId;
    private String name = "Unknown";
    private int win = 0;
    private Platform platfrom;
    private String role = "";

    @Transient
    private State state; 


    public UserModel(){}

    public UserModel(Integer userId, Platform platfrom) {
        this.userId = userId;
        this.platfrom = platfrom;
    }

    @Override
    public boolean equals(Object arg0) {
        if(arg0 instanceof UserModel user){
            return this.userId == user.userId && this.platfrom == user.platfrom;
        }
        return false;
    }

    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public Platform getPlatfrom() {
        return platfrom;
    }

    public void setPlatfrom(Platform platfrom) {
        this.platfrom = platfrom;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
