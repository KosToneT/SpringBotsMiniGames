package Games.tictactoe;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.develop.SpringMiniGames.BotManager;
import com.develop.SpringMiniGames.Bots.Message;
import com.develop.SpringMiniGames.Bots.UserModel;

import Games.Game;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;



public class TicTacToe implements Game{
    public final static int NEED_PLAYER = 2;
    private final static byte FIRST_PLAYER = 1;
    private final static byte SECOND_PLAYER = 2;

    protected UserModel firstUser;
    protected UserModel secondUser;
    protected int size;
    private boolean firstMove = false;
    protected MapTicTacToe map;

    public TicTacToe(){}

    public TicTacToe(UserModel firstUser, UserModel secondUser) {
        this.firstUser = firstUser;
        firstUser.setState(this);
        this.secondUser = secondUser;
        secondUser.setState(this);
        this.size = 3;
        this.map = new MapTicTacToe(size);

        start();
    }

    private void start(){
        BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser, "Начинается сражения века\nВы ходите первыми");
        BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser, "Начинается сражения века\nВы ходите вторыми");
        BotManager.getBot(firstUser.getPlatfrom()).sendPhoto(firstUser, map.getImageMap());
        BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser, "Ваш символ хода - ❌\nЧтобы сходить используйте:\n столбец строка");
    }

    @Override
    public void stop(UserModel user) {
        BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser,"Игра была остановлена");
        BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser, "Игра была остановлена");
        firstUser.setState(null);
        secondUser.setState(null);
    }

    private void motion(int x, int y){
        map.setMatrix(x, y, !firstMove?FIRST_PLAYER:SECOND_PLAYER);
        firstMove = !firstMove;

        BufferedImage img = map.getImageMap();
        BotManager.getBot(firstUser.getPlatfrom()).sendPhoto(firstUser, img);
        BotManager.getBot(secondUser.getPlatfrom()).sendPhoto(secondUser, img);

        byte wined = map.checkWin();
        if(wined==FIRST_PLAYER){
            BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser,"Вы выиграли!!! +1 бал \n");
            BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser,"Вы проиграли :/\n Повезёт в другой раз....\n");
            firstUser.setState(null);
            secondUser.setState(null);
        }else if(wined==SECOND_PLAYER){
            BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser,"Вы выиграли!!! +1 бал \n");
            BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser,"Вы проиграли :/\n Повезёт в другой раз....\n");
            firstUser.setState(null);
            secondUser.setState(null);
        }else if(!map.hasEmpty()){
            BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser,"Ничья, игра окончена");
            BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser,"Ничья, игра окончена");
            firstUser.setState(null);
            secondUser.setState(null);
        }
    }

    @Override
    public boolean newMessage(Message message) {
        String args[] = message.getText().split(" ");
        //x y
        if(args.length>1){

            try {
                int x = Integer.parseInt(args[0]);
                int y = Integer.parseInt(args[1]);
                x--;
                y--;
                if(x>map.getSize()-1||x<0||y>map.getSize()-1||y<0||map.getMatrix(x, y) !=0){
                    BotManager.getBot(message.getUser().getPlatfrom()).sendMessage(message.getUser(), "Неверное значение");
                }else{
                    if(firstMove == firstUser.equals(message.getUser())){
                        BotManager.getBot(message.getUser().getPlatfrom()).sendMessage(message.getUser(), "Второй игрок еще не сходил");
                    }else{
                        motion(x, y);
                        return true;
                    }
                }
            } catch (Exception e) {

            }
                    
        }
        return false;
    }
    
    
    class MapTicTacToe{
        private double width = 300;
        private double height = 300;
        private float stroke;
        private int maxRows;

        private boolean hasEmpty = false;
        private byte[][] matrix;

        public MapTicTacToe(int sizeMap){
            matrix = new byte[sizeMap][sizeMap];
            stroke = 60.f / sizeMap;
            maxRows = sizeMap;
            if(sizeMap>5){
                maxRows = 5;
            }
            
        }

        public byte checkWin(){
            byte win = 0;
            hasEmpty = false;
            for(int i=0; i<matrix.length; i++){
                for(int j=0; j<matrix[i].length; j++){
                    byte sym = matrix[i][j];
                    if(sym==0){
                        hasEmpty = true;
                        continue;
                    }
                    try {
                        for(int k=1; k<maxRows; k++){
                            if(sym==matrix[i][j+k]){
                                if(k==maxRows-1){
                                    win = sym;
                                }
                            }else{
                                break;
                            }
                        }
                    } catch (Exception e) {
                        
                    }
                    try{
                        for(int k=1; k<maxRows; k++){
                            if(sym==matrix[i+k][j+k]){
                                if(k==maxRows-1){
                                    win = sym;
                                }
                            }else{
                                break;
                            }
                        }
                    } catch (Exception e) {
                        
                    }
                    try{              
                        for(int k=1; k<maxRows; k++){
                            if(sym==matrix[i+k][j-k]){
                                if(k==maxRows-1){
                                    win = sym;
                                }
                            }else{
                                break;
                            }
                        }
                    } catch (Exception e) {
                        
                    }
                    try{
                        for(int k=1; k<maxRows; k++){
                            if(sym==matrix[i+k][j]){
                                if(k==maxRows-1){
                                    win = sym;
                                }
                            }else{
                                break;
                            }
                        }
                    } catch (Exception e) {
                        
                    }
                }
            }
            return win;
        }

        public boolean hasEmpty(){
            return hasEmpty;
        }
        public int getSize(){
            return matrix.length;
        }
        public byte getMatrix(int x, int y){
            return matrix[x][y];
        }
        public void setMatrix(int x, int y, byte value){
            matrix[x][y] = value;
        }

        private BufferedImage getImageMap(){
            BufferedImage buf = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = buf.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, (int)width, (int)height);
            drawTicTacToe(g);
            g.dispose();
            return buf;
        }
        private void drawTicTacToe(Graphics2D g){
            int size = (int)(width/(matrix.length+1));
            for(int i=1; i<matrix.length+1; i++){
                for(int j=1; j<matrix[i-1].length+1; j++){
                    g.setColor(Color.WHITE);
                    g.setStroke(new BasicStroke(4f));
                    g.drawRect(size*i, size*j, size, size);
                    if(matrix[i-1][j-1]==2){
                        g.setColor(Color.BLUE);
                        g.setStroke(new BasicStroke(stroke));
                        g.drawOval((int)(size*i+stroke/2), (int)(size*j+stroke/2), (int)(size-stroke), (int)(size-stroke));
                    }
                    if(matrix[i-1][j-1]==1){
                        g.setColor(Color.RED);
                        g.setStroke(new BasicStroke(stroke));
                        g.drawLine((int)(size*i+stroke), (int)(size*j+stroke), (int)(size*(i+1)-stroke), (int)(size*(j+1)-stroke));
                        g.drawLine((int)(size*(i+1)-stroke), (int)(size*j+stroke), (int)(size*(i)+stroke), (int)(size*(j+1)-stroke));
                    }
                }
            }
            g.setColor(Color.WHITE);
            int fontSize = (int)(size/1.5);
            g.setFont(new Font("Default", Font.BOLD, fontSize));
            for(int i=1; i<matrix.length+1; i++){
                int offset = 0;
                if(i<10){
                    offset = size/2;
                }
                g.drawString(""+i, size*i+offset/2,fontSize);
                g.drawString(""+i, offset,(fontSize+size*i));
            }
        }
    }


    @Override
    public Game createGame(UserModel... users) {
        if(users.length == getNeedUser()){
            return new TicTacToe(users[0], users[1]);
        }
        return null;
    }

    @Override
    public int getNeedUser() {
        return NEED_PLAYER;
    }
    
}
