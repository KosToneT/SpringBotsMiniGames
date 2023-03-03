package Games.Chess;

import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;


import javax.imageio.ImageIO;


public class Chess{
    Figure table[][];

    Point sizeRect;
    
    int posX;
    int posY;
    
    int width;
    int height;

    Color colorSchema[] = {new Color(235,235,208), new Color(119,148, 85)};

    Point activeFigure = null;


    public Chess(int posX, int posY, int width, int height){
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        
        table = new Figure[8][8];
        sizeRect = new Point(width/table.length, height/table.length);
        fill();
        // for(int i=0; i<table.length; i++){
        //     for(int j=0; j<table[i].length; j++){
        //         table[i][j] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.VOID);
        //     }
        // }
        // table[3][7] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.WHITE_QUEEN);
    }

    private Chess(){

    }

    public void fill(){
        for(int i=0; i<table.length; i++){
            for(int j=0; j<table[i].length; j++){
                table[i][j] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.VOID);
            }
        }

        for(int i=0; i<8; i++){
            table[i][1] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.BLACK_PAWNS);
            table[i][6] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.WHITE_PAWNS);
        }

        table[0][0] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.BLACK_ROOKS);
        table[7][0] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.BLACK_ROOKS);
        table[0][7] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.WHITE_ROOKS);
        table[7][7] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.WHITE_ROOKS);

        table[1][0] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.BLACK_KNIGHTS);
        table[6][0] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.BLACK_KNIGHTS);
        table[1][7] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.WHITE_KNIGHTS);
        table[6][7] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.WHITE_KNIGHTS);

        table[2][0] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.BLACK_BISHOPS);
        table[5][0] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.BLACK_BISHOPS);
        table[2][7] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.WHITE_BISHOPS);
        table[5][7] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.WHITE_BISHOPS);

        table[3][0] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.BLACK_QUEEN);
        table[4][0] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.BLACK_KING);
        table[3][7] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.WHITE_QUEEN);
        table[4][7] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.WHITE_KING);


    }
    

    public boolean canMove(Point pos, Point step){
        int movex = pos.x + step.x;
        int movey = pos.y + step.y;
        if(movex >=0 && movey >= 0 && movex < table.length && movey < table[movex].length){
            
        }else{
            return false;
        }

        Figure figure = table[pos.x][pos.y];
        Figure stepFigure = table[pos.x+step.x][pos.y+step.y];
        
        if(figure.isJump() && (stepFigure.isVoid() || figure.isBlack() != stepFigure.isBlack())){
            return true;
        }

        if(!stepFigure.isVoid() && figure.isBlack() == stepFigure.isBlack()){
            return false;
        }

        if(step.x == 0){
            for(int i=1; i<Math.abs(step.y); i++){
                if(!table[pos.x][pos.y + i*step.y/Math.abs(step.y)].isVoid()){
                    return false;
                }
            }
        }

        if(step.y == 0){
            for(int i=1; i<Math.abs(step.x); i++){
                if(!table[pos.x+i*step.x/Math.abs(step.x)][pos.y].isVoid()){
                    return false;
                }
            }
        }

        if(step.y!=0 && step.x != 0){
            for(int i=1; i<Math.abs(step.x); i++){
                if(!table[pos.x + i*step.x/Math.abs(step.x)][pos.y + i*step.y/Math.abs(step.y)].isVoid()){
                    return false;
                }
            }
        }

        return true;
    }

    public boolean move(Point pos, Point pos1){
        activeFigure = pos;
        if(table[pos.x][pos.y].isVoid())return false;
        Point step = new Point(pos1.x - pos.x, pos1.y - pos.y);
        if(!canMove(pos, step))return false;
        step = new Point(pos.x - pos1.x, pos.y - pos1.y);
        if(table[pos1.x][pos1.y].isVoid()){
            for(Point i:table[pos.x][pos.y].getSteps()){
                if(i.equals(step)){
                    table[pos.x][pos.y].touch();;
                    var buf = table[pos.x][pos.y];        
                    table[pos.x][pos.y] = table[pos1.x][pos1.y];
                    table[pos1.x][pos1.y] = buf;
                    return true;
                }
            }
        }else{
            for(Point i:table[pos.x][pos.y].getHack()){
                if(i.equals(step)){
                    table[pos.x][pos.y].touch();
                    table[pos1.x][pos1.y] = table[pos.x][pos.y];
                    table[pos.x][pos.y] = FigureBuilder.getFigure(FigureBuilder.EnumFigure.VOID);
                    return true;
                }
            }
        }
        return false;
    }

    public Point toCellPoint(Point p){
        int x = (p.x-posX) / (sizeRect.x);
        int y = (p.y-posY) / (sizeRect.y);
        return new Point(x,y);
    }


    public void drawAbailableStep(Point pos, Graphics2D g){
        int i = pos.x;
        int j = pos.y;
        //Point[] steps = table[i][j].getSteps();

        for(Point step:table[i][j].getSteps()){
            try {
                step = new Point(-step.x, -step.y);
                Point pos1 = new Point(i, j);
                if(canMove(pos1, step) && table[i+step.x][j+step.y].isVoid()){
                    g.setColor(Color.DARK_GRAY);
                    g.fillOval((i+step.x)*sizeRect.x+sizeRect.x/4, (j+step.y)*sizeRect.y+sizeRect.y/4, sizeRect.x/2, sizeRect.y/2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(Point step:table[i][j].getHack()){
            try {
                step = new Point(-step.x, -step.y);
                Point pos1 = new Point(i, j);
                if(canMove(pos1, step) && !table[i+step.x][j+step.y].isVoid()){
                    g.setColor(Color.DARK_GRAY);
                    g.fillOval((i+step.x)*sizeRect.x+sizeRect.x/4, (j+step.y)*sizeRect.y+sizeRect.y/4, sizeRect.x/2, sizeRect.y/2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void draw(Graphics2D g){
        g.translate(posX, posY);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 8*width, 8*width);

        for(int i=0; i<table.length; i++){
            for(int j=0; j<table[i].length; j++){
                g.setColor(colorSchema[(i+j)&1]);
                g.fillRect(i*sizeRect.x, j*sizeRect.y, sizeRect.x, sizeRect.y);
                table[i][j].draw(g, i*sizeRect.x, j*sizeRect.y, sizeRect.x, sizeRect.y);
            }
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Default", Font.BOLD, 30));
        for(int i=1; i<9; i++){
            g.drawString(""+(9-i),-20, i*sizeRect.y);
            g.drawString(""+(char)(i-1+'a'), i*sizeRect.x-20,  sizeRect.y*8+25);
        }

        if(activeFigure!=null){
            drawAbailableStep(activeFigure, g);
        }
    }

    @Override
    public Chess clone(){
        Chess chess = new Chess();
        
        chess.sizeRect = sizeRect;
        chess.posX = posX;
        chess.posY = posY;
        chess.width = width;
        chess.height = height;
        chess.colorSchema = colorSchema.clone();

        chess.table = new Figure[table.length][table[0].length];
        for(int i=0; i<table.length; i++){
            for(int j=0; j<table[i].length; j++){
                chess.table[i][j] = table[i][j];
            }
        }
        return chess;
    }

    
}


class FigureBuilder {
    enum EnumFigure{
        VOID,
        WHITE_KING,
        WHITE_QUEEN,
        WHITE_ROOKS,
        WHITE_BISHOPS,
        WHITE_KNIGHTS,
        WHITE_PAWNS,
        BLACK_KING,
        BLACK_QUEEN,
        BLACK_ROOKS,
        BLACK_BISHOPS,
        BLACK_KNIGHTS,
        BLACK_PAWNS
    }
    public static Figure getFigure(EnumFigure type){
        if(type == EnumFigure.WHITE_KING){
            return new WHITE_KING();
        }
        else if(type == EnumFigure.WHITE_QUEEN){
            return new WHITE_QUEEN();
        }
        else if(type == EnumFigure.WHITE_ROOKS){
            return new WHITE_ROOKS();
        }
        else if(type == EnumFigure.WHITE_BISHOPS){
            return new WHITE_BISHOPS();
        }
        else if(type == EnumFigure.WHITE_KNIGHTS){
            return new WHITE_KNIGHTS();
        }
        else if(type == EnumFigure.WHITE_PAWNS){
            return new WHITE_PAWNS();
        }
        else if(type == EnumFigure.BLACK_KING){
            return new BLACK_KING();
        }
        else if(type == EnumFigure.BLACK_QUEEN){
            return new BLACK_QUEEN();
        }
        else if(type == EnumFigure.BLACK_ROOKS){
            return new BLACK_ROOKS();
        }
        else if(type == EnumFigure.BLACK_BISHOPS){
            return new BLACK_BISHOPS();
        }
        else if(type == EnumFigure.BLACK_KNIGHTS){
            return new BLACK_KNIGHTS();
        }
        else if(type == EnumFigure.BLACK_PAWNS){
            return new BLACK_PAWNS();
        }
        return new VOID();
    }

}

abstract class Figure implements behaviorFigure{
    public boolean isJump(){
        return false;
    }
    public boolean isVoid(){
        return false;
    }
    public void touch(){
        
    }
    @Override
    public Point[] getHack() {
        return getSteps();
    }
}
interface behaviorFigure{
    void draw(Graphics2D g, int x, int y, int width, int height);
    Point[] getSteps();
    Point[] getHack();
    boolean isBlack();
    void touch();
}

class VOID extends Figure{
    public void draw(Graphics2D g, int x, int y, int width, int height){

    }
    @Override
    public boolean isVoid(){
        return true;
    }
    @Override
    public Point[] getSteps() {
        return new Point[0];
    }

    @Override
    public Point[] getHack() {
        return new Point[0];
    }
    @Override
    public boolean isBlack(){
        return false;
    }
    
}


class WHITE_KING extends Figure{
    static BufferedImage sprite;
    static{
        try {
            sprite = (BufferedImage)ImageIO.read(new File("src\\Sprite\\WHITE_KING.png"));
        } catch (Exception e) {}
    }
    public void draw(Graphics2D g, int x, int y, int width, int height){
        g.drawImage(sprite, x, y, width, height, null);
    }
    @Override
    public Point[] getSteps() {
        Point steps[] = new Point[8];
        int steps_count = 0;
        for(int i=-1; i<2;i++){
            for(int j=-1; j<2;j++){
                if(i==0 && j ==0){

                }else{
                    steps[steps_count++] = new Point(i, j);
                }
            }   
        }
        return steps;
    }
    @Override
    public boolean isBlack(){
        return false;
    }
}
class WHITE_QUEEN extends Figure{
    static BufferedImage sprite;
    static{
        try {
            sprite = (BufferedImage)ImageIO.read(new File("src\\Sprite\\WHITE_QUEEN.png"));
        } catch (Exception e) {}
    }
    public void draw(Graphics2D g, int x, int y, int width, int height){
        g.drawImage(sprite, x, y, width, height, null);
    }
    @Override
    public Point[] getSteps() {
        Point steps[] = new Point[8*7];
        int steps_count = 0;
        for(int i=1; i<8;i++){
            steps[steps_count++] = new Point(i, 0);
            steps[steps_count++] = new Point(0, i);
            steps[steps_count++] = new Point(-i, 0);
            steps[steps_count++] = new Point(0, -i);

            steps[steps_count++] = new Point(i, i);
            steps[steps_count++] = new Point(-i, -i);
            steps[steps_count++] = new Point(i, -i);
            steps[steps_count++] = new Point(-i, i);
        }
        return steps;
    }
    @Override
    public boolean isBlack(){
        return false;
    }
}
class WHITE_ROOKS extends Figure{
    static BufferedImage sprite;
    static{
        try {
            sprite = (BufferedImage)ImageIO.read(new File("src\\Sprite\\WHITE_ROOKS.png"));
        } catch (Exception e) {}
    }
    public void draw(Graphics2D g, int x, int y, int width, int height){
        g.drawImage(sprite, x, y, width, height, null);
    }
    @Override
    public Point[] getSteps() {
        Point steps[] = new Point[4*7];
        int steps_count = 0;
        for(int i=1; i<8;i++){
            steps[steps_count++] = new Point(i, 0);
            steps[steps_count++] = new Point(0, i);
            steps[steps_count++] = new Point(-i, 0);
            steps[steps_count++] = new Point(0, -i);
        }
        return steps;
    }
    @Override
    public boolean isBlack(){
        return false;
    }
}
class WHITE_BISHOPS extends Figure{
    static BufferedImage sprite;
    static{
        try {
            sprite = (BufferedImage)ImageIO.read(new File("src\\Sprite\\WHITE_BISHOPS.png"));
        } catch (Exception e) {}
    }
    public void draw(Graphics2D g, int x, int y, int width, int height){
        g.drawImage(sprite, x, y, width, height, null);
    }
    @Override
    public Point[] getSteps() {
        Point steps[] = new Point[4*7];
        int steps_count = 0;
        for(int i=1; i<8;i++){
            steps[steps_count++] = new Point(i, i);
            steps[steps_count++] = new Point(-i, -i);
            steps[steps_count++] = new Point(i, -i);
            steps[steps_count++] = new Point(-i, i);
        }
        return steps;
    }
    @Override
    public boolean isBlack(){
        return false;
    }
}
class WHITE_KNIGHTS extends Figure{
    static BufferedImage sprite;
    static{
        try {
            sprite = (BufferedImage)ImageIO.read(new File("src\\Sprite\\WHITE_KNIGHTS.png"));
        } catch (Exception e) {}
    }
    public void draw(Graphics2D g, int x, int y, int width, int height){
        g.drawImage(sprite, x, y, width, height, null);
    }
    @Override
    public Point[] getSteps() {
        Point steps[] = new Point[8];
        int steps_count = 0;
        steps[steps_count++] = new Point(2, 1);
        steps[steps_count++] = new Point(2, -1);
        steps[steps_count++] = new Point(1, 2);
        steps[steps_count++] = new Point(-1, 2);

        steps[steps_count++] = new Point(-2, 1);
        steps[steps_count++] = new Point(-2, -1);
        steps[steps_count++] = new Point(1, -2);
        steps[steps_count++] = new Point(-1, -2);

        return steps;
    }

    @Override
    public boolean isBlack(){
        return false;
    }
    @Override
    public boolean isJump(){
        return true;
    }
}
class WHITE_PAWNS extends Figure{
    static BufferedImage sprite;
    static{
        try {
            sprite = (BufferedImage)ImageIO.read(new File("src\\Sprite\\WHITE_PAWNS.png"));
        } catch (Exception e) {}
    }
    boolean touch = false;
    public void draw(Graphics2D g, int x, int y, int width, int height){
        g.drawImage(sprite, x, y, width, height, null);
    }


    @Override
    public void touch() {
        touch = true;
    }



    @Override
    public Point[] getSteps() {
        if(!touch){
           
            Point steps[] = new Point[2];
            int steps_count = 0;

            steps[steps_count++] = new Point(0, 1);
            steps[steps_count++] = new Point(0, 2);
            return steps;
        }
        Point steps[] = new Point[1];
        int steps_count = 0;

        steps[steps_count++] = new Point(0, 1);

        return steps;
    }
    @Override
    public Point[] getHack() {
        Point steps[] = new Point[2];
        int steps_count = 0;
        steps[steps_count++] = new Point(1, 1);
        steps[steps_count++] = new Point(-1, 1);
        return steps;
    }
    @Override
    public boolean isBlack(){
        return false;
    }
}
class BLACK_KING extends Figure{
    static BufferedImage sprite;
    static{
        try {
            sprite = (BufferedImage)ImageIO.read(new File("src\\Sprite\\BLACK_KING.png"));
        } catch (Exception e) {}
    }
    public void draw(Graphics2D g, int x, int y, int width, int height){
        g.drawImage(sprite, x, y, width, height, null);
    }
    @Override
    public Point[] getSteps() {
        Point steps[] = new Point[8];
        int steps_count = 0;
        for(int i=-1; i<2;i++){
            for(int j=-1; j<2;j++){
                if(i==0 && j ==0){

                }else{
                    steps[steps_count++] = new Point(i, j);
                }
            }   
        }
        return steps;
    }
    @Override
    public boolean isBlack(){
        return true;
    }
}
class BLACK_QUEEN extends Figure{
    static BufferedImage sprite;
    static{
        try {
            sprite = (BufferedImage)ImageIO.read(new File("src\\Sprite\\BLACK_QUEEN.png"));
        } catch (Exception e) {}
    }
    public void draw(Graphics2D g, int x, int y, int width, int height){
        g.drawImage(sprite, x, y, width, height, null);
    }
    @Override
    public Point[] getSteps() {
        Point steps[] = new Point[8*7];
        int steps_count = 0;
        for(int i=1; i<8;i++){
            steps[steps_count++] = new Point(i, 0);
            steps[steps_count++] = new Point(0, i);
            steps[steps_count++] = new Point(-i, 0);
            steps[steps_count++] = new Point(0, -i);

            steps[steps_count++] = new Point(i, i);
            steps[steps_count++] = new Point(-i, -i);
            steps[steps_count++] = new Point(i, -i);
            steps[steps_count++] = new Point(-i, i);
        }
        return steps;
    }
    @Override
    public boolean isBlack(){
        return true;
    }
}
class BLACK_ROOKS extends Figure{
    static BufferedImage sprite;
    static{
        try {
            sprite = (BufferedImage)ImageIO.read(new File("src\\Sprite\\BLACK_ROOKS.png"));
        } catch (Exception e) {}
    }
    public void draw(Graphics2D g, int x, int y, int width, int height){
        g.drawImage(sprite, x, y, width, height, null);
    }
    @Override
    public Point[] getSteps() {
        Point steps[] = new Point[4*7];
        int steps_count = 0;
        for(int i=1; i<8;i++){
            steps[steps_count++] = new Point(i, 0);
            steps[steps_count++] = new Point(0, i);
            steps[steps_count++] = new Point(-i, 0);
            steps[steps_count++] = new Point(0, -i);
        }
        return steps;
    }
    @Override
    public boolean isBlack(){
        return true;
    }
}
class BLACK_BISHOPS extends Figure{
    static BufferedImage sprite;
    static{
        try {
            sprite = (BufferedImage)ImageIO.read(new File("src\\Sprite\\BLACK_BISHOPS.png"));
        } catch (Exception e) {}
    }
    public void draw(Graphics2D g, int x, int y, int width, int height){
        g.drawImage(sprite, x, y, width, height, null);
    }
    @Override
    public Point[] getSteps() {
        Point steps[] = new Point[4*7];
        int steps_count = 0;
        for(int i=1; i<8;i++){
            steps[steps_count++] = new Point(i, i);
            steps[steps_count++] = new Point(-i, -i);
            steps[steps_count++] = new Point(i, -i);
            steps[steps_count++] = new Point(-i, i);
        }
        return steps;
    }
    @Override
    public boolean isBlack(){
        return true;
    }
}
class BLACK_KNIGHTS extends Figure{
    static BufferedImage sprite;
    static{
        try {
            sprite = (BufferedImage)ImageIO.read(new File("src\\Sprite\\BLACK_KNIGHTS.png"));
        } catch (Exception e) {}
    }
    public void draw(Graphics2D g, int x, int y, int width, int height){
        g.drawImage(sprite, x, y, width, height, null);
    }
    @Override
    public Point[] getSteps() {
        Point steps[] = new Point[8];
        int steps_count = 0;
        steps[steps_count++] = new Point(2, 1);
        steps[steps_count++] = new Point(2, -1);
        steps[steps_count++] = new Point(1, 2);
        steps[steps_count++] = new Point(-1, 2);

        steps[steps_count++] = new Point(-2, 1);
        steps[steps_count++] = new Point(-2, -1);
        steps[steps_count++] = new Point(1, -2);
        steps[steps_count++] = new Point(-1, -2);

        return steps;
    }

    @Override
    public boolean isBlack(){
        return true;
    }
    @Override
    public boolean isJump(){
        return true;
    }
}
class BLACK_PAWNS extends Figure{
    static BufferedImage sprite;
    static{
        try {
            sprite = (BufferedImage)ImageIO.read(new File("src\\Sprite\\BLACK_PAWNS.png"));
        } catch (Exception e) {}
    }
    boolean touch = false;
    public void draw(Graphics2D g, int x, int y, int width, int height){
        g.drawImage(sprite, x, y, width, height, null);
    }

    @Override
    public void touch() {
        touch = true;
    }

    @Override
    public Point[] getSteps() {
        if(!touch){
            Point steps[] = new Point[2];
            int steps_count = 0;

            steps[steps_count++] = new Point(0, -1);
            steps[steps_count++] = new Point(0, -2);
            return steps;
        }
        Point steps[] = new Point[1];
        int steps_count = 0;

        steps[steps_count++] = new Point(0, -1);

        return steps;
    }
    @Override
    public Point[] getHack() {
        Point steps[] = new Point[2];
        int steps_count = 0;
        steps[steps_count++] = new Point(1, -1);
        steps[steps_count++] = new Point(-1, -1);
        return steps;
    }
    @Override
    public boolean isBlack(){
        return true;
    }
}