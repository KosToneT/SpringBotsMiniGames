package Games.tictactoe;

import com.develop.SpringMiniGames.Bots.UserModel;

import Games.Game;

public class TicTacToe15 extends TicTacToe{
    public TicTacToe15(){}
    public TicTacToe15(UserModel first, UserModel second){
        this.firstUser = first;
        this.secondUser = second;
        this.size = 15;
        this.map = new MapTicTacToe(size);
    }

    @Override
    public Game createGame(UserModel... users) {
        if(users.length == getNeedUser()){
            return new TicTacToe15(users[0], users[1]);
        }
        return null;
    }
}
