package com.develop.SpringMiniGames.Bots;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class TGBot extends TelegramLongPollingBot implements Bot{
    private final String botName = "TG Bot";
    private UserDB userDB = new UserDB();


    private final String TOKEN;
    private final String botUserName;
    private MessageObserver messageObserver;
    private Thread thread;
    private boolean online = false;

    public TGBot(String TOKEN, String botUserName){
        this.TOKEN = TOKEN;
        this.botUserName = botUserName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            org.telegram.telegrambots.meta.api.objects.Message message = update.getMessage();
            UserModel userModel = userDB.findByUserIdAndPlatfrom(message.getFrom().getId().intValue(), getPlatform());
            com.develop.SpringMiniGames.Bots.Message messageBuf = new com.develop.SpringMiniGames.Bots.Message(userModel, message.getText(), message.getMessageId());
            messageObserver.notiFy(messageBuf);
        }
        online = true; 
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public void start() {
        if(thread!=null){
            stop();
        }
        thread = new Thread(()->{
        
            try {
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
                telegramBotsApi.registerBot(this);

            } catch (Exception e) {
                online = false;
            }

            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                
            }
            

        });
        thread.start();

        
    }

    @Override
    public void stop() {
        if(thread!=null){
            thread.interrupt();
        }
        
    }

    @Override
    public void setMessageObserver(MessageObserver messageObserver) {
        this.messageObserver = messageObserver; 
    }

    @Override
    public void sendMessage(UserModel user, String text) {
        try {
            SendMessage sendMessage = new SendMessage(user.getUserId()+"", text);
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPhoto(UserModel user, BufferedImage photo) {
        if(photo.getType()!=BufferedImage.TYPE_INT_RGB)throw new RuntimeException("No correct Format image need to be rgb.");
        try {
            
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(photo, "jpeg", os);              
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            SendPhoto sendPhoto = SendPhoto.builder().photo(new InputFile(is, "jpg")).chatId(user.getUserId()+"").build();
            this.execute(sendPhoto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isOnline() {
        return online;
    }

    @Override
    public void setUserDB(UserDB userDB) {
        this.userDB = userDB;
    }

    @Override
    public String getBotName() {
        return botName;
    }
    
    @Override
    public Platform getPlatform(){
        return Platform.TG;
    }
}
