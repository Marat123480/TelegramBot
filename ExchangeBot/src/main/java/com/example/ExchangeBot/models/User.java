package com.example.ExchangeBot.models;

import ch.qos.logback.core.model.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User{
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private List<Message> messages = new ArrayList<>();

    public User(Long id, String username){
        this.id = id;
        this.username = username;
    }
    public void addMessageToUser(Message message) {
        message.setUser(this);
        messages.add(message);
    }
}
