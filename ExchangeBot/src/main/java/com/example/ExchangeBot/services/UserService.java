package com.example.ExchangeBot.services;

import com.example.ExchangeBot.models.Message;
import com.example.ExchangeBot.models.User;
import com.example.ExchangeBot.repositories.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //Method to create New User
    public boolean createUser(User user){
        String username = user.getUsername();
        //Checking if User is already exist
        if(userRepository.findById(user.getId()).isPresent())
            return false;
        userRepository.save(user);
        return true;
    }
    //Method to find User By id
    public User findUser(Long id){
        User user = userRepository.findUserById(id);
        return user;
    }
    //Method to add Message to the User
    public void saveMessage(User user, Message message) {
        user.addMessageToUser(message);
        userRepository.save(user);
    }
}
