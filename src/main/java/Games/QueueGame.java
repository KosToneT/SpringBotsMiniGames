package Games;

import java.util.LinkedList;

import com.develop.SpringMiniGames.BotManager;
import com.develop.SpringMiniGames.Bots.Message;
import com.develop.SpringMiniGames.Bots.UserModel;


public class QueueGame implements Game {
    private final LinkedList<UserModel> findList = new LinkedList<>();
    private Game game;

    public QueueGame(Game game){
        this.game = game;
    }

    public void addList(UserModel userDataBase) {     
        if(findList.indexOf(userDataBase)==-1){
            findList.addLast(userDataBase);
            userDataBase.setState(this);
            BotManager.getBot(userDataBase.getPlatfrom()).sendMessage(userDataBase, "Ищем соперника!!!!");
        }
        if(findList.size()>=game.getNeedUser()){
            UserModel users[] = new UserModel[game.getNeedUser()];
            for(int i=0; i<users.length; i++){
                users[i] = findList.pop();
            }
            
            game.createGame(users);     
        }
    }

    @Override
    public void stop(UserModel userDataBase) {
        findList.remove(userDataBase);
        BotManager.getBot(userDataBase.getPlatfrom()).sendMessage(userDataBase, "Поиск остановлен!!!");
        userDataBase.setState(null);
    }


    @Override
    public int getNeedUser() {
        return 0;
    }

   
    @Override
    public boolean newMessage(Message message) {
        if(message.getText().equals("/stop")){
            stop(message.getUser());
            return true;
        }
        return false;
    }

    @Override
    public Game createGame(UserModel... users) {
        return null;
    }

}
