package com.develop.SpringMiniGames.Bots;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractSaveTable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id; 
}
