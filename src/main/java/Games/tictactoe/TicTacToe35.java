package Games.tictactoe;

import com.develop.SpringMiniGames.Bots.UserModel;

import Games.Game;

public class TicTacToe35 extends TicTacToe{
    public TicTacToe35(){}
    public TicTacToe35(UserModel first, UserModel second){
        this.firstUser = first;
        this.secondUser = second;
        this.size = 35;
        this.map = new MapTicTacToe(size);
    }

    @Override
    public Game createGame(UserModel... users) {
        if(users.length == getNeedUser()){
            return new TicTacToe35(users[0], users[1]);
        }
        return null;
    }
}
