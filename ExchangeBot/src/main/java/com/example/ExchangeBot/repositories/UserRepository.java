package com.example.ExchangeBot.repositories;

import com.example.ExchangeBot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    //Adding method to get User by id
    User findUserById(Long id);
}
