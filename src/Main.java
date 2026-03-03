import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner sc = new Scanner(System.in);
        Game game = new Game();
        
        game.play();
        game.finalScore();
        game.highScores();
        
        System.out.print(Game.CYAN + "Would you like to see a replay? (Y/N) >>> " + Game.RESET);
        char ans = sc.next().charAt(0);
        if (ans == 'Y' || ans == 'y') {
            game.replay();
        }
        
        System.out.println("\n" + Game.GREEN + "Thank you for playing HighSuit!" + Game.RESET);
    }
}
