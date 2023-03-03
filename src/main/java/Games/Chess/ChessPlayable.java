package Games.Chess;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.develop.SpringMiniGames.BotManager;
import com.develop.SpringMiniGames.Bots.Message;
import com.develop.SpringMiniGames.Bots.UserModel;

import Games.Game;

public class ChessPlayable implements Game{
    private final Chess chess = new Chess(50, 50,500,500);

    private UserModel firstUser;
    private UserModel secondUser;

    private boolean firstMove = true;

    public ChessPlayable(){}

    public ChessPlayable(UserModel first, UserModel second){
        this.firstUser = first;
        firstUser.setState(this);
        this.secondUser = second;
        secondUser.setState(this);
        start();
    }

    private void start(){
        BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser, "Начинается сражения века\nВы ходите первыми");
        sendMap(firstUser);
        BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser, "Начинается сражения века\nВы ходите вторыми");
        sendMap(secondUser);
    }

    private void sendMap(UserModel userModel){
        BufferedImage buf = new BufferedImage(600,600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = buf.createGraphics();
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0,600,600);
        chess.draw(g2d);
        g2d.dispose();
        BotManager.getBot(userModel.getPlatfrom()).sendPhoto(userModel, buf);
    }

    private void sendText(UserModel userModel, String text){
        BotManager.getBot(userModel.getPlatfrom()).sendMessage(userModel, text);
    }

    private void checkWin(){
        boolean hasBlackKing = false;
        boolean hasWhiteKing = false;
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(!hasWhiteKing && chess.table[i][j].getClass().equals(WHITE_KING.class)){
                    hasWhiteKing = true;
                }
                if(!hasBlackKing && chess.table[i][j].getClass().equals(BLACK_KING.class)){
                    hasBlackKing = true;
                }
            }
        }
        if(hasBlackKing!=hasWhiteKing){
            tellWin(hasWhiteKing);
        }
    }
    private void tellWin(boolean firstWin){
        if(firstWin){
            BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser,"Вы выиграли!!! +1 бал \n");
            BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser,"Вы проиграли :/\n Повезёт в другой раз....\n");
            firstUser.setState(null);
            secondUser.setState(null);
        }else{
            BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser,"Вы выиграли!!! +1 бал \n");
            BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser,"Вы проиграли :/\n Повезёт в другой раз....\n");
            firstUser.setState(null);
            secondUser.setState(null);
        }
    }

    @Override
    public boolean newMessage(Message message) {
        String args[] = message.getText().split(" ");
        if(args.length == 2 && args[0].length() == 2 && args[1].length() == 2){
            char moveFromChar[] = args[0].toUpperCase().toCharArray();
            int moveFrom[] = {moveFromChar[0]-'A', 8-(moveFromChar[1]-'0')};

            char moveToChar[] = args[1].toUpperCase().toCharArray();
            int moveTo[] = {moveToChar[0]-'A', 8-(moveToChar[1]-'0')};

            if(message.getUser().equals(firstUser) != firstMove){
                sendText(message.getUser(), "Сейчас ходит другой игрок");
                return true;
            }

            if(chess.table[moveFrom[0]][moveFrom[1]].isBlack()!= message.getUser().equals(firstUser)){
                Point fromPos = new Point(moveFrom[0], moveFrom[1]);
                Point toPos = new Point(moveTo[0], moveTo[1]);
                if(chess.move(fromPos, toPos)){
                    sendMap(firstUser);
                    sendMap(secondUser);
                    firstMove = !firstMove;
                }else{
                    sendText(message.getUser(), "Не возможный ход");
                }
                


            }else{
                sendText(message.getUser(), "Нельзя двигать чужие фигуры");
                
            }
            checkWin();
            return true;
        }else{
            sendText(message.getUser(), "Неверный аргумент");
        }
        return false;
    }

    @Override
    public void stop(UserModel user) {
        BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser,"Игра была остановлена");
        BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser, "Игра была остановлена");
        firstUser.setState(null);
        secondUser.setState(null);
    }

    @Override
    public Game createGame(UserModel... users) {
        if(users.length == getNeedUser()){
            return new ChessPlayable(users[0], users[1]);
        }
        return null;
    }

    @Override
    public int getNeedUser() {
        return 2;
    }
    
}
