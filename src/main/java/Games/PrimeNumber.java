package Games;

import java.math.BigInteger;
import java.awt.image.BufferedImage;

import com.develop.SpringMiniGames.BotManager;
import com.develop.SpringMiniGames.Bots.Message;
import com.develop.SpringMiniGames.Bots.UserModel;

public class PrimeNumber implements Game{

    private UserModel firstUser;
    private UserModel secondUser;

    BigInteger firstNumber;
    BigInteger secondNumber;
    BigInteger firstPrimeNumber;
    BigInteger secondPrimeNumber;


    public PrimeNumber(){}

    public PrimeNumber(UserModel firstUser, UserModel secondUser){
        this.firstUser = firstUser;
        firstUser.setState(this);
        this.secondUser = secondUser;
        secondUser.setState(this);
        start();
    } 

    private void start(){
        BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser, "Начинается сражения века\nВы ходите первыми");
        BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser, "Начинается сражения века\nВы ходите вторыми");
    }

    private boolean motion(){
        if((firstNumber != null || firstPrimeNumber != null) && (secondNumber != null || secondPrimeNumber != null)){
            int firstWin = -2;
            
            if(firstPrimeNumber != null){
                firstWin = secondPrimeNumber == null?-1:firstPrimeNumber.compareTo(secondPrimeNumber);
                // if(secondPrimeNumber != null){
                //     firstWin = firstPrimeNumber.compareTo(secondPrimeNumber);
                // }else{
                //     firstWin = -1;
                // }
            }else{
                firstWin = secondPrimeNumber != null ? -1: firstNumber.compareTo(secondNumber);
                // if(secondPrimeNumber != null){
                //     firstWin = 1;
                // }else{
                //     firstWin = firstNumber.compareTo(secondNumber);
                // }
            }



            if(firstWin==1){
                BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser,"Вы выиграли!!! +1 бал \n Число соперника: "+ (secondPrimeNumber==null?secondNumber:secondPrimeNumber));
                BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser,"Вы проиграли :/\n Повезёт в другой раз....\n Число соперника: "+ (firstPrimeNumber==null?firstNumber:firstPrimeNumber));
            }
            if(firstWin==-1){
                BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser,"Вы выиграли!!! +1 бал \n Число соперника: "+ (firstPrimeNumber==null?firstNumber:firstPrimeNumber));
                BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser,"Вы проиграли :/\n Повезёт в другой раз....\n Число соперника: "+ (secondPrimeNumber==null?secondNumber:secondPrimeNumber));
            }


            if(firstWin==0){
                BotManager.getBot(firstUser.getPlatfrom()).sendMessage(firstUser, "Ничья");
                BotManager.getBot(secondUser.getPlatfrom()).sendMessage(secondUser, "Ничья");
            }

            firstUser.setState(null);
            secondUser.setState(null);
            return true;
        }else{
            return false;
        }

    }

    @Override
    public boolean newMessage(Message message) {
        try {
            BigInteger bigInteger = new BigInteger(message.getText());
            if(bigInteger.isProbablePrime(100)){
                BotManager.getBot(message.getUser().getPlatfrom()).sendMessage(message.getUser(), "Ваше число простое");
                if(firstUser.equals(message.getUser())){
                    firstPrimeNumber = bigInteger;
                }else{
                    secondPrimeNumber = bigInteger;
                }
            }else{
                BotManager.getBot(message.getUser().getPlatfrom()).sendMessage(message.getUser(), "Ваше число не простое");
                if(firstUser.equals(message.getUser())){
                    firstNumber = bigInteger;
                }else{
                    secondNumber = bigInteger;
                }
            }

            if(!motion()){
                BotManager.getBot(message.getUser().getPlatfrom()).sendMessage(message.getUser(), "Ожидаем хода соперника...");
                
            }
            return true;

        } catch (Exception e) {
            BotManager.getBot(message.getUser().getPlatfrom()).sendMessage(message.getUser(), "Это не число");
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
            return new PrimeNumber(users[0], users[1]);
        }
        return null;
    }

    @Override
    public int getNeedUser() {
        return 2;
    }
}
