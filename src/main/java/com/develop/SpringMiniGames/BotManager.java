package com.develop.SpringMiniGames;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Controller;

import com.develop.SpringMiniGames.Bots.Bot;
import com.develop.SpringMiniGames.Bots.Message;
import com.develop.SpringMiniGames.Bots.MessageListener;
import com.develop.SpringMiniGames.Bots.MessageObserver;
import com.develop.SpringMiniGames.Bots.Platform;
import com.develop.SpringMiniGames.Bots.TGBot;
import com.develop.SpringMiniGames.Bots.UserDB;
import com.develop.SpringMiniGames.Bots.UserModel;
import com.develop.SpringMiniGames.Bots.UserRepo;
import com.develop.SpringMiniGames.Bots.VKBot;
import com.develop.SpringMiniGames.Reports.Report;
import com.develop.SpringMiniGames.Reports.ReportRepo;

import Games.PrimeNumber;
import Games.QueueGame;
import Games.SeaBattle;
import Games.Chess.ChessPlayable;
import Games.tictactoe.TicTacToe;
import Games.tictactoe.TicTacToe15;
import Games.tictactoe.TicTacToe35;


@Controller
public class BotManager {
    private static BotManager botManager;
    
    private final UserDB userDB;

    private final LinkedList<Bot> bots = new LinkedList<>();
    
    private ReportRepo reportRepo;


    public BotManager(UserRepo userRepo, ReportRepo reportRepo){
        this.userDB = new UserDB(userRepo);
        this.reportRepo = reportRepo;

        bots.add(new VKBot(216029096, "vk1.a.PxOTKqt8B5HyqTUmyQhfWESgBDslePM75D16P8AyuCD-9b8PBqbVw2Vju4JYnUOYdFY5VerR8vEPHAHCigJUR9zeSxIT6KK26yvIsKiGcDVPHkQqaSGMLBEvUkFYetWskkqlLxMpDRyrtkT2csTYkd_jhdxcjQIIVqMF37ZcWpNOdJmb9OCnegOYvazjyeJm"));
        bots.add(new TGBot("5711386065:AAEME2MKLQuqT3_nRQ5O5uXno9zjqFOPuHw", "MiniGames"));
        
        MessageObserver messageObserver = new MessageObserver();
        initMessageObserver(messageObserver);

        for(Bot bot:bots){
            bot.setMessageObserver(messageObserver);
            bot.setUserDB(userDB);
            bot.start();
        }

        botManager = this;
    }

    private void initMessageObserver(MessageObserver messageObserver){
        initCommand(messageObserver);
        //State handler
        messageObserver.add(new MessageListener(){
            @Override
            public boolean newMessage(Message message){
                if(message.getUser().getState()==null){
                    //BotManager.getBot(message.getUser().getPlatfrom()).sendMessage(message.getUser(), "state" + message.getUser().getState());
                    return false;
                }
                
                return message.getUser().getState().newMessage(message);
            }
        });
        
        initMiniGames(messageObserver);
    }
    private void initCommand(MessageObserver messageObserver){
        //help
        messageObserver.add(new MessageListener() {
            @Override
            public boolean newMessage(Message message) {
                if(message.getText().toLowerCase().equals("/help")){
                    UserModel user = message.getUser();
                    String text = ""+
                                "\n/help - Помощь"+
                                "\n/mm - Меню"+
                                "\n/primenumber - Игра лучшее простое число"+
                                "\n/tictactoe - Игра крестики нолики"+
                                "\n/tictactoe15 - Игра крестики нолики 15х15"+
                                "\n/tictactoe35 - Игра крестики нолики 33х33"+
                                "\n/seabattle - Игра морской бой"+
                                "\n/chess - Игра шахматы"+
                                "\n/stop - Остановить игру, поиск"+
                                "\n/report - Сообщить об ошибке"+
                                "\n/m - Написать сообщение противнику   "+
                                "\n/username 'name' - Устанавливает ваше имя"+
                                "\n/move чтобы ходить в игре";
                    BotManager.getBot(user.getPlatfrom()).sendMessage(user, text);
                    return true;
                }
                return false;
                
            }
        });
        //username 'name'
        messageObserver.add(new MessageListener() {
            @Override
            public boolean newMessage(Message message) {
                if(message.getText().toLowerCase().split(" ")[0].equals("/username")){
                    int spaceIndex = message.getText().indexOf(' ');
                    UserModel user = message.getUser();
                    if(spaceIndex!= -1){
                        String nickname = message.getText().substring(spaceIndex);
                        user.setName(nickname);
                        String text = "Ваш ник: " + user.getName() + " установлен.";
                        BotManager.getBot(user.getPlatfrom()).sendMessage(user, text);
                    }else{
                        BotManager.getBot(user.getPlatfrom()).sendMessage(user, "Неверный никнейм");
                        
                    }
                    return true;
                }
                return false;
                
            }
        });

        //mm
        messageObserver.add(new MessageListener() {
            @Override
            public boolean newMessage(Message message) {
                if(message.getText().toLowerCase().equals("/mm")){
                    UserModel user = message.getUser();
                    String text = "О вас: \n"+ 
                                    "\t🎰Вами выигранно: " +  user.getWin()+"\n"+
                                    (user.getState()!=null?"\t🕹Вы сейчас играете в:"+user.getState().getClass().getName()+"\n":"") +
                                    "\t✅Ник: " + user.getName() + "\n"+
                                    ((user.getRole().length()>0)?"\t🎮Роль: "+user.getRole()+"\n":"")+
                                    "\t🎆UID: "+ user.getId()+
                                    //"Профиль приватный: " + (userDataBase.getPrivateProfile()?"Да":"Нет") + "\n"+
                                    "";
                    BotManager.getBot(user.getPlatfrom()).sendMessage(user, text);
                    return true;
                }
                return false;
                
            }
        });

        //report
        messageObserver.add(new MessageListener() {
            @Override
            public boolean newMessage(Message message) {
                if(message.getText().toLowerCase().split(" ")[0].equals("/report")){
                    int spaceIndex = message.getText().indexOf(' ');
                    UserModel user = message.getUser();
                    if(spaceIndex!= -1){
                        Report report = new Report(message.getText().substring(spaceIndex), user.getId(), user.getPlatfrom(), user.getName(), user.getState());
                        reportRepo.save(report);
                        BotManager.getBot(user.getPlatfrom()).sendMessage(user, "Ваш репорт был отправлен, спасибо за вашу отзывчивость!");
                    }else{
                        BotManager.getBot(user.getPlatfrom()).sendMessage(user, "Неверный ввод");
                        
                    }
                    return true;
                }
                return false;
            }
        });
    }



    private void initMiniGames(MessageObserver messageObserver){
        //TicTacToe queue
        messageObserver.add(new MessageListener() {
            QueueGame queue = new QueueGame(new TicTacToe());
            @Override
            public boolean newMessage(Message message) {
                if(message.getText().toLowerCase().equals("/tictactoe")){
                    queue.addList(message.getUser());
                    return true;
                }
                return false;
            }
        });
        //TicTacToe15 queue
        messageObserver.add(new MessageListener() {
            QueueGame queue = new QueueGame(new TicTacToe15());
            @Override
            public boolean newMessage(Message message) {
                if(message.getText().toLowerCase().equals("/tictactoe15")){
                    queue.addList(message.getUser());
                    return true;
                }
                return false;
            }
        });
        //TicTacToe35 queue
        messageObserver.add(new MessageListener() {
            QueueGame queue = new QueueGame(new TicTacToe35());
            @Override
            public boolean newMessage(Message message) {
                if(message.getText().toLowerCase().equals("/tictactoe35")){
                    queue.addList(message.getUser());
                    return true;
                }
                return false;
            }
        });
        //PrimeNumber queue
        messageObserver.add(new MessageListener() {
            QueueGame queue = new QueueGame(new PrimeNumber());
            @Override
            public boolean newMessage(Message message) {
                if(message.getText().toLowerCase().equals("/primenumber")){
                    queue.addList(message.getUser());
                    return true;
                }
                return false;
            }
        });
        //SeaBattle queue
        messageObserver.add(new MessageListener() {
            QueueGame queue = new QueueGame(new SeaBattle());
            @Override
            public boolean newMessage(Message message) {
                if(message.getText().toLowerCase().equals("/seabattle")){
                    queue.addList(message.getUser());
                    return true;
                }
                return false;
            }
        });
        //Chess queue
        messageObserver.add(new MessageListener() {
            QueueGame queue = new QueueGame(new ChessPlayable());
            @Override
            public boolean newMessage(Message message) {
                if(message.getText().toLowerCase().equals("/chess")){
                    queue.addList(message.getUser());
                    return true;
                }
                return false;
            }
        });
    
    
    }

    public List<Bot> getBots(){
        return bots;
    }

    public static Bot getBot(Platform platfrom){
        for(Bot bot:botManager.bots){
            if(bot.getPlatform()==platfrom){
                return bot;
            }
        }
        return null;
    }
}
