package Games;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.develop.SpringMiniGames.BotManager;
import com.develop.SpringMiniGames.Bots.Message;
import com.develop.SpringMiniGames.Bots.UserModel;

public class SeaBattle implements Game{
    private UserModel first;
    private UserModel second;
    private boolean firstMove = false;

    private Map firstMap = new Map(300);
    private Map secondMap = new Map(300);


    public SeaBattle(){}

    public SeaBattle(UserModel first, UserModel second){
        this.first = first;
        this.second = second;
        first.setState(this);
        second.setState(this);
    }

    @Override
    public int getNeedUser() {
        return 2;
    }

    @Override
    public Game createGame(UserModel... userDataBases) {
        if(userDataBases.length == getNeedUser()){
            return new SeaBattle(userDataBases[0], userDataBases[1]);
        }
        return null;
    }

    @Override
    public boolean newMessage(Message message){
        motion(message.getUser(), message.getText().split(" "));
        return true;
    }

    private void motion(UserModel userDataBase, String args[]) {
        boolean bFirst = first.equals(userDataBase);

        Map map = firstMap;
        Map map1 = secondMap;
        UserModel user = first;
        UserModel user1 = second;

        if(!bFirst){
            map = secondMap;
            map1 = firstMap;
            user = second;
            user1 = first;
        }

        String help = "\n/move столбец строка - чтобы сделать выстрел"+
                      "\n/move random - заполнить карту случайными кораблями"+
                      "\n/move столбец строка горизонтальный - разместить корабль в точке столбец строка если вертикальный 1 тогда будет горизонтальный иначе горизонтальный"+
                      "\n/move 1 показать вашу карту"+
                      "\n/move 2 карта противника"+
                      "\n/move - показать игровую карту";
        if(args.length==0){
            sendFullMap(bFirst);
            return;
        }

        if(args.length==1){
            if(args[0].equals("1")){
                BotManager.getBot(userDataBase.getPlatfrom()).sendPhoto(userDataBase, map.getMap());
                return;
            }
            if(args[0].equals("2")){
                BotManager.getBot(userDataBase.getPlatfrom()).sendPhoto(userDataBase, map.getGeoMap());
                return;
            }
        }
        if(args.length>0&&args[0].toLowerCase().equals("help")){
            BotManager.getBot(userDataBase.getPlatfrom()).sendMessage(userDataBase, help);
            return;
        }

        if(!map.getReady()){//Если корабли не размещенны
            if(args[0].toLowerCase().equals("random")){
                map.randomGenerate();
                BotManager.getBot(userDataBase.getPlatfrom()).sendPhoto(userDataBase, map.getMap());
                BotManager.getBot(userDataBase.getPlatfrom()).sendMessage(userDataBase, "Карта заполненна случайными кораблями");
                BotManager.getBot(userDataBase.getPlatfrom()).sendMessage(userDataBase, "Вы готовы к бою");
                return;
            }else{
                try {
                    int x = Integer.parseInt(args[0])-1;
                    int y = Integer.parseInt(args[1])-1;
                    int horizont =  Integer.parseInt(args[2]);
                    if(map.placeShip(map.nowPlace(), x, y, horizont==1)){
                        BotManager.getBot(userDataBase.getPlatfrom()).sendPhoto(userDataBase, map.getMap());
                        if(map.nowPlace()!=0){
                            BotManager.getBot(userDataBase.getPlatfrom()).sendMessage(userDataBase, "Теперь разместите "+ map.nowPlace()+"- корабль");
                        }else{
                            BotManager.getBot(userDataBase.getPlatfrom()).sendMessage(userDataBase, "Вы готовы к бою");
                            

                            if(firstMap.getReady()&&secondMap.getReady()){
                                BotManager.getBot(first.getPlatfrom()).sendMessage(first, "Вы начинаете первым!!! /move x y");
                            }
                        }
                    }else{
                        BotManager.getBot(userDataBase.getPlatfrom()).sendMessage(userDataBase, "Неверное значение: use /move help");
                    }
                } catch (Exception e) {
                    BotManager.getBot(userDataBase.getPlatfrom()).sendMessage(userDataBase, "Неверное значение попробуйте:\n/move x y 0");
                }
            }
        }else{
            if(firstMove==bFirst){//Если ход текущего игрока
                try {
                    int x = Integer.parseInt(args[0])-1;
                    int y = Integer.parseInt(args[1])-1;
                    boolean fire = map1.fire(x, y);
                    BotManager.getBot(user.getPlatfrom()).sendPhoto(user, map1.getGeoMap());
                    BotManager.getBot(user1.getPlatfrom()).sendPhoto(user1, map1.getMap());
                    if(!fire){
                        firstMove = !firstMove;
                        BotManager.getBot(user.getPlatfrom()).sendMessage(user, "Нет попадания");
                        BotManager.getBot(user1.getPlatfrom()).sendMessage(user1, "Нет попадания");
                        BotManager.getBot(user1.getPlatfrom()).sendMessage(user1, "Теперь ваш ход: use /move столбец строка");
                    }
                } catch (Exception e) {
                    BotManager.getBot(userDataBase.getPlatfrom()).sendMessage(userDataBase, "Неверное значение: use /move help");
                }
            }else{
                BotManager.getBot(userDataBase.getPlatfrom()).sendMessage(userDataBase, "Второй игрок еще не сходил");
            }
        }

        boolean firstWin = secondMap.checkLose();
        boolean secondWin = firstMap.checkLose();

        if((map.getReady()&&map1.getReady())&&(firstWin||secondWin)){
            if(firstWin){
                BotManager.getBot(first.getPlatfrom()).sendMessage(first, "Вы выиграли!!!\n");
                BotManager.getBot(second.getPlatfrom()).sendMessage(second, "Вы проиграли\n");

            }else{
                BotManager.getBot(second.getPlatfrom()).sendMessage(second, "Вы выиграли!!!\n");
                BotManager.getBot(first.getPlatfrom()).sendMessage(first, "Вы проиграли\n");
            }
            BotManager.getBot(first.getPlatfrom()).sendMessage(first, "Игра была остановлена");
            BotManager.getBot(second.getPlatfrom()).sendMessage(second, "Игра была остановлена");
            first.setState(null);
            second.setState(null);
        }        
    }

    
    public void start() {
        String startText = "Игра начинается\n"+
                            "/move help - чтобы показать помощь";

        BotManager.getBot(first.getPlatfrom()).sendMessage(first, startText);
        BotManager.getBot(second.getPlatfrom()).sendMessage(second, startText);

        BotManager.getBot(first.getPlatfrom()).sendPhoto(first, firstMap.getMap());
        BotManager.getBot(second.getPlatfrom()).sendPhoto(second, secondMap.getMap());

    }
    
    @Override
    public void stop(UserModel userDataBase) {
        BotManager.getBot(first.getPlatfrom()).sendMessage(first, "Игра была остановлена");
        BotManager.getBot(second.getPlatfrom()).sendMessage(second, "Игра была остановлена");
        first.setState(null);
        second.setState(null);
    }

    public void sendFullMap(boolean first){
        Map  firstMap = this.firstMap;
        Map secondMap = this.secondMap;
        UserModel user = this.first;
        if(!first){
            firstMap = this.secondMap;
            secondMap = this.firstMap;
            user = this.second;
        }
        int size = firstMap.getWidth()*2;

        BufferedImage image = new BufferedImage(size, size/2, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, size, size);
        firstMap.drawFullMap(g, 0,0);
        secondMap.drawGeoMap(g, firstMap.getWidth(),0);
        g.dispose();
        
        BotManager.getBot(user.getPlatfrom()).sendPhoto(user, image);
    }

    class Map{
        private int matrix[][] = new int[10][10];
        private int size = 10;
        private int width;
        //private boolean ready = false;
        private int place = 0;
        // /private int state[] = {0,1,2,3};//none, ship, drowned, miss

        private int ship[] = {4,3,3,2,2,2,1,1,1,1,0};
    
        public Map(int width){
            this.width = width;
            size = width/(matrix.length+1);
        }
        
        public int getWidth(){
            return width;
        }

        private void placeShip1(int x, int y) throws Exception{
            if(!hasSpace(x, y))
                throw new Exception("Нет места для корабля");
            matrix[x][y] = 1;
        }
        private void placeShip2(int x, int y, boolean horizont) throws Exception{
            if(!(hasSpace(x, y)))
                throw new Exception("Нет места для корабля");
            if(horizont){
                if(!(hasSpace(x+1, y)))
                    throw new Exception("Нет места для корабля");
                matrix[x][y] = 1;
                matrix[x+1][y] = 1;
            }else{
                if(!(hasSpace(x, y+1)))
                    throw new Exception("Нет места для корабля");
                matrix[x][y] = 1;
                matrix[x][y+1] = 1;
            }
        }

        private void placeShip3(int x, int y, boolean horizont) throws Exception{
            if(!(hasSpace(x, y)))
                throw new Exception("Нет места для корабля");
            if(horizont){
                if(!(hasSpace(x+1, y)&&hasSpace(x+2, y)))
                    throw new Exception("Нет места для корабля");
                matrix[x][y] = 1;
                matrix[x+1][y] = 1;
                matrix[x+2][y] = 1;
            }else{
                if(!(hasSpace(x, y+1)&&hasSpace(x, y+2)))
                    throw new Exception("Нет места для корабля");
                matrix[x][y] = 1;
                matrix[x][y+1] = 1;
                matrix[x][y+2] = 1;
            }
        }

        private void placeShip4(int x, int y, boolean horizont) throws Exception{
            if(!(hasSpace(x, y)))
                throw new Exception("Нет места для корабля");
            if(horizont){
                if(!(hasSpace(x+1, y)&&hasSpace(x+2, y)&&hasSpace(x+3, y)))
                    throw new Exception("Нет места для корабля");
                matrix[x][y] = 1;
                matrix[x+1][y] = 1;
                matrix[x+2][y] = 1;
                matrix[x+3][y] = 1;
            }else{
                if(!(hasSpace(x, y+1)&&hasSpace(x, y+2)&&hasSpace(x+3, y)))
                    throw new Exception("Нет места для корабля");
                matrix[x][y] = 1;
                matrix[x][y+1] = 1;
                matrix[x][y+2] = 1;
                matrix[x][y+3] = 1;
            }
        }

        private void drawMiss(Graphics2D g, int x, int y){
            g.setColor(Color.GRAY);
            g.fillRect(x, y, size, size);
        }

        private void drawDrawing(Graphics2D g, int x, int y){
            g.setColor(Color.GREEN);
            g.fillRect(x, y, size, size);
        }
    
        private void drawDrowned(Graphics2D g, int x,int y){
            g.setColor(Color.RED);
            g.fillRect(x, y, size, size);
    
        }
    
        private void drawCell(Graphics2D g, int x, int y){
            g.setColor(Color.white);
            g.drawRect(x, y, size, size);
        }
    
        public void drawFullMap(Graphics2D g, int x, int y){
            for(int i=1;i<matrix.length+1; i++){
                for(int j=1; j<matrix[i-1].length+1; j++){
                    switch (matrix[i-1][j-1]){
                        case 0:
                            drawCell(g, x+i*size, y+j*size);
                            break;
                        case 1:
                            drawDrawing(g, x+i*size,y+ j*size);
                            break;
                        case 2:
                            drawDrowned(g, x+i*size, y+j*size);
                            break;
                        case 3:
                            drawMiss(g, x+i*size, y+j*size);
                            break;
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
                g.drawString(""+i, x+size*i+offset/2,y+fontSize);
                g.drawString(""+i, x+offset,y+(fontSize+size*i));
            }
    
        }
    
        public void drawGeoMap(Graphics2D g, int x, int y){
            for(int i=1;i<matrix.length+1; i++){
                for(int j=1; j<matrix[i-1].length+1; j++){
                    switch (matrix[i-1][j-1]){
                        case 2:
                            drawDrowned(g, x+i*size, y+j*size);
                            break;
                        case 3:
                            drawMiss(g, x+i*size, y+j*size);
                            break;                            
                    }
                    drawCell(g, x+i*size, y+j*size);
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
                g.drawString(""+i, x+size*i+offset/2,y+fontSize);
                g.drawString(""+i, x+offset,y+(fontSize+size*i));
            }
        }

        private boolean hasSpace(int x, int y){
            if(x<0||y<0||x>matrix.length||y>matrix.length)return false;
            int count = 0;
            for(int i=-1; i<2;i++){
                for(int j=-1; j<2; j++){
                    try {
                        count+= matrix[x+i][y+j];
                    } catch (Exception e) {
                        
                    }
                }
            }
            return count == 0;
        }

        public boolean placeShip(int type, int x, int y, boolean horizont){
            if(type==1){
                try {
                    placeShip1(x, y);
                    place++;
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }else if(type==2){
                try {
                    placeShip2(x, y, horizont);
                    place++;
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }else if(type==3){
                try {
                    placeShip3(x, y, horizont);
                    place++;
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }else if(type==4){
                try {
                    placeShip4(x, y, horizont);
                    place++;
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
    
            return false;
        }
      
        public BufferedImage getMap(){
            BufferedImage buf = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = buf.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, width);
            drawFullMap(g,0,0);
            g.dispose();
            return buf;
        }

        public BufferedImage getGeoMap(){
            BufferedImage buf = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = buf.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, width);
            drawGeoMap(g,0,0);
            g.dispose();
            return buf;
        }

        public boolean getReady(){
            return nowPlace()==0;
        }
        
        public int getCount(){
            return place;
        }

        public int nowPlace(){
            return ship[place];
        }
        public int nextPlace(){
            return ship[place+1];
        }

        public void randomGenerate(){
            while(nowPlace()!=0){
                placeShip(nowPlace(), (int)(Math.random()*matrix.length), (int)(Math.random()*matrix.length), Math.random()>0.5);
            }
        }

        private int leftLiveCellShip(int x, int y, int x1, int y1){
            int live = 0;
            for(int i=-1; i<2; i++){
                for(int j=-1; j<2; j++){
                    try {
                        if((i!=0||j!=0) && !(x1==x+i&&y1 == y+j)  && (matrix[x+i][y+j]==1 || matrix[x+i][y+j]==2)){
                            live += leftLiveCellShip(x+i, y+j, x, y);
                        }
                    } catch (Exception e) {
                    }
                }
            }
            try {
                if(matrix[x][y]==1)
                    live++;
            } catch (Exception e) {
                
            }
            return live;
        }

        private void fillShipWater(int x, int y, int x1, int y1){
            for(int i=-1; i<2; i++){
                for(int j=-1; j<2; j++){
                    try {
                        if((i!=0||j!=0) && !(x1==x+i&&y1 == y+j)  && (matrix[x+i][y+j]==1 || matrix[x+i][y+j]==2)){
                            fillShipWater(x+i, y+j, x, y);
                        }
                    } catch (Exception e) {
                    }
                }
            }
            try {
                for(int i=-1; i<2; i++){
                    for(int j=-1; j<2; j++){
                        try {
                            if(!(matrix[x+i][y+j]==1 || matrix[x+i][y+j]==2)){
                                matrix[x+i][y+j] = 3;
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (Exception e) {
                
            }
        }
        public boolean fire(int x, int y)throws Exception{
            if(matrix[x][y]==2||matrix[x][y]==3) throw new Exception("Вы уже сюда стреляли");

            if(matrix[x][y]==1){
                matrix[x][y] = 2;


                if(leftLiveCellShip(x, y, x, y)==0){
                    fillShipWater(x, y, x, y);
                }
                return true;
            }else{
                matrix[x][y] = 3;
                return false;
            }
        }
        public boolean checkLose(){
            for(int i=0; i<matrix.length; i++){
                for(int j=0; j<matrix[i].length; j++){
                    if(matrix[i][j]==1){
                        return false;
                    }
                }
            }
            return true;
        }
    
    
        
    }


}
