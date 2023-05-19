package com.example.ExchangeBot.services;

import com.example.ExchangeBot.config.BotConfig;
import com.example.ExchangeBot.config.CurrencyInfo;

import com.example.ExchangeBot.models.Message;
import com.example.ExchangeBot.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.DecimalFormat;
import java.util.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    private UserService userService;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    @Autowired
    public TelegramBot(BotConfig config, UserService userService){
        this.config = config;
        this.userService = userService;
    }
    public TelegramBot(BotConfig config) {
        this.config = config;
    }
    @Override
    //Method to response after Users messages
    public void onUpdateReceived(Update update) {
        //Checking if User send message
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    sendMessage(chatId, """
                            Welcome to the ExchangeBot.\s
                            Rules how to use it!\s
                            Write number and then currency.\s
                            The bot only accepts integers\s
                            Example:\s
                            2000 USD\s
                            Don't write anything after currency""");
                    break;
                default:
                    //Method to prepare response to the User
                    startCommandReceived(chatId, update.getMessage().getText());
                    //Method to Add User and message to the Database
                    addToDataBase(chatId, update);
                    break;
            }
            //Or User send voice message
        } else if (update.hasMessage() && update.getMessage().hasVoice()) {
            //Method to send message to the user
            sendMessage(update.getMessage().getChatId(), "I don't understand your command. Please try again.");
        }
    }

    //Method to get from the sentence only Digits
    private Double getNumber(String text) {
        Double value = Double.parseDouble(text.replaceAll("[^0-9]", ""));
        return value;
    }

    //Method to Add User and message to the Database
    private void addToDataBase(Long chatId, Update update){
        User user = new User(chatId, update.getMessage().getChat().getUserName());
        Message message = new Message(update.getMessage().getText());
        //Method to add messages to the User
        user.addMessageToUser(message);
        //Method if User exist
        if(!userService.createUser(user)){
            //Method for finding User by Id
            user = userService.findUser(user.getId());
            //Method to add messages to the User
            user.addMessageToUser(message);
            //Method to commit data to the DB
            userService.saveMessage(user, message);
        }
    }
    //Method to prepare text to the User
    private void startCommandReceived(Long chatId, String text) {
        String answer;
        //Checkin that User send correct text
        if (checkText(text)) {
            //Getting digitals from User's message
            Double value = getNumber(text);
            //Getting letter from User's message
            String currency = text.replaceAll("[^a-zA-Z$]", "");
            if (currency.equals("$")) currency = "USD";
            //Getting rate of the currency
            Double rate = CurrencyInfo.chooseCurrency(currency);
            if(currency.equals("KZT")){
                answer = df.format(calculateTheAmount(currency, value, rate)) + " " + "USD";
            }else{
                answer = df.format(calculateTheAmount(currency, value, rate)) + " " + "KZT";
            }
        } else {
            answer = "I don't understand your command. Please try again.";
        }
        //Method to send answer to the User
        sendMessage(chatId, answer);
    }

    //Method to check Users message correctness
    private boolean checkText(String text) {
        if (text.matches(".*[a-zA-Z$].*") && text.matches(".*[0-9].*")) {
            return true;
        }
        return false;
    }

    //Method to calculate Amount in different currency
    private Double calculateTheAmount(String currency, Double amount, Double rate) {
        if (currency.equals("KZT"))
            return amount / CurrencyInfo.chooseCurrency("USD");
        else
            return amount * rate;
    }

    //Metod to send message to the User
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

}
