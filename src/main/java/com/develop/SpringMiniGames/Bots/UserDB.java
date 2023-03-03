package com.develop.SpringMiniGames.Bots;

import java.util.HashMap;

public class UserDB {
    private final HashMap<Long, UserModel> databaseTemp = new HashMap<>();
    private UserRepo userRepo;

    public UserDB(){}

    public UserDB(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    public UserModel findByUserIdAndPlatfrom(int id, Platform platform){
        UserModel user = null;
        for(UserModel i:userRepo.findByUserIdAndPlatfrom(id, platform)){
            user = i;
        }

        
        if(user==null){
            user = new UserModel(id, platform);
            userRepo.save(user);
            return user;            
        }else{
            Long databaseID = user.getId();
            if(databaseTemp.containsKey(databaseID)){
                user = databaseTemp.get(databaseID);
                return user;
            }else{
                databaseTemp.put(databaseID, user);
                return user;
            }
        }
    }
}
