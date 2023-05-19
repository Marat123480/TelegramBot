package com.example.ExchangeBot.config;

import lombok.Data;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

@Configuration
@Data
@PropertySource("classpath:application.properties")
public class BotConfig {

    @Value("${bot.name}")
    String botName;
    @Value("${bot.key}")
    String token;


}
