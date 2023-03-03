package Games;

import com.develop.SpringMiniGames.Bots.State;
import com.develop.SpringMiniGames.Bots.UserModel;

public interface Game extends State{
    void stop(UserModel user);
    Game createGame(UserModel ... users);
    int getNeedUser();
}

