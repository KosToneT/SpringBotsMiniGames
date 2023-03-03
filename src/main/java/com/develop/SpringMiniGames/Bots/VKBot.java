package com.develop.SpringMiniGames.Bots;


import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import com.vk.api.sdk.callback.longpoll.CallbackApiLongPoll;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoUpload;
import com.vk.api.sdk.objects.photos.responses.MessageUploadResponse;

public class VKBot extends CallbackApiLongPoll implements Bot{
    private final String botName = "VK Bot";
    private UserDB userDB = new UserDB();

    private GroupActor actor;
    private MessageObserver messageObserver;
    private Thread thread;
    private Integer ts;
    private boolean online = false;

    public VKBot(int groupid, String TOKEN){
        this(new VkApiClient(new HttpTransportClient()),new GroupActor(groupid, TOKEN));
    }

    private VKBot(VkApiClient client, GroupActor actor){
        super(client, actor);
        this.actor = actor;
    }

    @Override
    public void setUserDB(UserDB userDB) {
        this.userDB = userDB;        
    }

    @Override
    public void start() {
        if(thread!=null){
            stop();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        ts = VKBot.this.getClient().messages().getLongPollServer(actor).execute().getTs();
                        VKBot.this.run();
                    } catch (Exception e) {
                        online = false;
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        break;
                    }
                }
                
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
    public void messageNew(Integer groupId, Message message) {
        try {
            for(Message i:getClient().messages().getLongPollHistory(actor).ts(ts).execute().getMessages().getItems()){
                UserModel userModel = userDB.findByUserIdAndPlatfrom(i.getFromId(), getPlatform());                
                com.develop.SpringMiniGames.Bots.Message messageBuf = new com.develop.SpringMiniGames.Bots.Message(userModel, i.getText(), i.getId());
                messageObserver.notiFy(messageBuf);
            }
            ts = getClient().messages().getLongPollServer(actor).execute().getTs();
            online = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMessageObserver(MessageObserver messageObserver) {
        this.messageObserver = messageObserver;        
    }

    @Override
    public void sendMessage(UserModel user, String text) {
        try {
            getClient().messages().send(actor).message(text).peerId(user.getUserId()).randomId((int)System.currentTimeMillis()).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPhoto(UserModel user, BufferedImage photo) {
        //TODO: neeed do
        try {
                PhotoUpload serverResponse = getClient().photos().getMessagesUploadServer(actor).execute();
                File file = new File("photo.jpg");
                if(file.exists()){
                    file.delete();
                }
                if(!file.exists()){
                    file.createNewFile();
                }
                //File file = File.createTempFile("data", ".jpg");
                ImageIO.write(photo, "jpg", file);
                MessageUploadResponse uploadResponse = getClient().upload().photoMessage(serverResponse.getUploadUrl().toString(), file).execute();
                List<Photo> photoList = getClient().photos().saveMessagesPhoto(actor, uploadResponse.getPhoto())
                                                                .server(uploadResponse.getServer())
                                                                .hash(uploadResponse.getHash())
                                                                .execute();
                Photo photo1 = photoList.get(0); 
                String attachId = "photo" + photo1.getOwnerId() + "_" + photo1.getId();
                getClient().messages().send(actor)
                                .attachment(attachId)
                                .userId(user.getUserId())
                                .randomId((int)System.currentTimeMillis())
                                .execute();
        //bot.messages().send(actor).attachment(value)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean isOnline() {
        return online;
    }

    @Override
    public String getBotName() {
        return botName;
    } 

    @Override
    public Platform getPlatform(){
        return Platform.VK;
    }
}

