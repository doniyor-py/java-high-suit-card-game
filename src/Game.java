import java.io.*;
import java.util.*;

public class Game {
    private Player Player1;
    private Player Player2;
    private int rounds;
    private ArrayList<String> replayData;
    private ArrayList<PlayerScore> highScoresTable;
    private static final String HIGH_SCORE_FILE = "highscores.txt";
    private static final String REPLAY_FILE = "replay.txt";

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String BOLD = "\u001B[1m";

    private boolean blindMode = false;
    private final Scanner sc;
    
    public Game() {
        sc = new Scanner(System.in);

        System.out.println(CYAN + BOLD + "*******************************" + RESET);
        System.out.println(CYAN + BOLD + "*** WELCOME TO HIGHSUIT ***" + RESET);
        System.out.println(CYAN + BOLD + "*******************************" + RESET);
        System.out.println();
        
        int n;
        while (true) {
            System.out.print("How many players? (1-2) >>> ");
            if (sc.hasNextInt()) {
                n = sc.nextInt();
                if (n == 1 || n == 2) break;
            } else {
                sc.next();
            }
            System.out.println(RED + "Invalid input. Enter 1 or 2." + RESET);
        }

        System.out.println();
        int r;
        while (true) {
            System.out.print("How many rounds? (1-3) >>> ");
            if (sc.hasNextInt()) {
                r = sc.nextInt();
                if (r >= 1 && r <= 3) break;
            } else {
                sc.next();
            }
            System.out.println(RED + "Invalid input. Enter 1, 2, or 3." + RESET);
        }
        rounds = r;
        sc.nextLine();

        
        System.out.println();
        
        System.out.print(YELLOW + "Enable Blind Mode? (Y/N) >>> " + RESET);
        String blindChoice = sc.nextLine().trim().toUpperCase();
        blindMode = blindChoice.equals("Y");
        if (blindMode) {
            System.out.println(MAGENTA + "🎭 Blind Mode Activated! Scores hidden until end!" + RESET);
        }
        System.out.println();
        
        if (n == 2) {
            System.out.print("Player 1 name >>> ");
            String name1 = sc.nextLine();
            Player1 = new Player(name1);
            
            System.out.print("Player 2 name >>> ");
            String name2 = sc.nextLine();
            Player2 = new Player(name2);
        } else {
            System.out.print("Player 1 name >>> ");
            String name1 = sc.nextLine();
            Player1 = new Player(name1);
            Player2 = null;
        }
        
        System.out.println();
        
        replayData = new ArrayList<>();
        highScoresTable = new ArrayList<>();
        
        loadHighScores();
    }
    
    public void play() throws IOException {
        for (int round = 1; round <= rounds; round++) {

            System.out.println(GREEN + "****************" + RESET);
            System.out.println(GREEN + "*** Round " + round + " ***" + RESET);
            System.out.println(GREEN + "****************" + RESET);
            System.out.println();
            
            Deck deck = new Deck();
            playRound(Player1, round, deck);
            if (Player2 != null) {
                playRound(Player2, round, deck);
            }
        }
        
        saveReplay();
    }
    
    private void playRound(Player player, int round, Deck deck) {
        StringBuilder roundReplay = new StringBuilder();

        player.setHand(deck);
        
        System.out.println("-------------------------");
        System.out.println("Player: " + player.getName() + "    Round: " + round);
        System.out.println("-------------------------");

        roundReplay.append("-------------------------\n");
        roundReplay.append("Player: ").append(player.getName()).append("    Round: ").append(round).append("\n");
        roundReplay.append("-------------------------\n\n");
        roundReplay.append("Initial hand:\n");
        for (Card c : player.getHand().getCards()) {
            roundReplay.append(c).append("\n");
        }
        
        player.printHand();
        
        int maxScore = player.getHand().getMaxSuitScore();
        String maxSuit = player.getHand().getMaxSuit();
        System.out.println("\nYour maximum suit score is " + maxScore + " in " + maxSuit);
        System.out.println();

        if (player.isComputer()) {
            player.nominateBonusComputer();
        } else {
            player.nominateBonus(sc);
        }
        System.out.println();
        
        roundReplay.append("\nBonus suit: ").append(Card.suits[getSuitIndex(player.getBonus())]).append("\n");

        ArrayList<Card> beforeDrop = new ArrayList<>(player.getHand().getCards());
        
        if (player.isComputer()) {
            player.dropCardsComputer(deck);
        } else {
            player.dropCards(deck, sc);
        }

        roundReplay.append("\nCards dropped: ");
        ArrayList<Card> droppedCards = new ArrayList<>();
        for (Card c : beforeDrop) {
            if (!player.getHand().getCards().contains(c)) {
                droppedCards.add(c);
            }
        }
        if (droppedCards.isEmpty()) {
            roundReplay.append("(no cards)\n");
        } else {
            for (int i = 0; i < droppedCards.size(); i++) {
                roundReplay.append(droppedCards.get(i));
                if (i < droppedCards.size() - 1) roundReplay.append(", ");
            }
            roundReplay.append("\n");
        }

        player.printHand();

        roundReplay.append("\nFinal hand:\n");
        for (Card c : player.getHand().getCards()) {
            roundReplay.append(c).append("\n");
        }
        
        int finalMaxScore = player.getHand().getMaxSuitScore();
        String finalMaxSuit = player.getHand().getMaxSuit();

        if (!blindMode) {
            System.out.println("\nYour maximum suit score is " + finalMaxScore + " in " + finalMaxSuit);
        } else {
            System.out.println(MAGENTA + "\n🎭 Score hidden (Blind Mode)" + RESET);
        }
        
        int roundScore = player.calculateRoundScore(blindMode);
        
        
        if (!blindMode) {

            System.out.println(YELLOW + "Score for this round is " + roundScore + RESET);
        } else {
            System.out.println(MAGENTA + "🎭 Round score hidden" + RESET);
        }
        System.out.println();
        
        roundReplay.append("\nScore: ").append(roundScore).append("\n\n");
        
        if (!player.isComputer()) {
            System.out.println("Press Enter key to continue...");
            sc.nextLine();
        } else {
            System.out.println("Press Enter key to continue...");
            sc.nextLine();
        }
        System.out.println();
        
        replayData.add(roundReplay.toString());
    }
    
    private int getSuitIndex(char suitChar) {
        switch (suitChar) {
            case 'C': return 0;
            case 'D': return 1;
            case 'H': return 2;
            case 'S': return 3;
            default: return 0;
        }
    }
    
    public void finalScore() {
        System.out.println(BOLD + CYAN + "FINAL SCORES" + RESET);
        System.out.println(CYAN + "------------" + RESET);
        
        ArrayList<Player> list = new ArrayList<>();
        list.add(Player1);
        if (Player2 != null) list.add(Player2);
        Collections.sort(list);
        
        for (Player p : list) {
            System.out.println(p);
        }
        if (Player2 != null) {
            Player top = list.get(0);
            Player second = list.get(1);

            if (top.getTotalScore() > second.getTotalScore()) {
                System.out.println(GREEN + BOLD + "🏆 Winner: " + Player1.getName() + " 🏆" + RESET);
            } else if (Player2.getTotalScore() > Player1.getTotalScore()) {
                System.out.println(GREEN + BOLD + "🏆 Winner: " + Player2.getName() + " 🏆" + RESET);
            } else {
                System.out.println(YELLOW + BOLD + "🤝 It's a tie!" + RESET);
            }
        }
        System.out.println();
        displayScoreGraph();
    }
    
    public void highScores() throws IOException {
        double avgScore1 = (double) Player1.getTotalScore() / rounds;
        highScoresTable.add(new PlayerScore(Player1.getName(), avgScore1));
        
        if (Player2 != null) {
            double avgScore2 = (double) Player2.getTotalScore() / rounds;
            highScoresTable.add(new PlayerScore(Player2.getName(), avgScore2));
        }
        
        Collections.sort(highScoresTable, (a, b) -> Double.compare(b.score, a.score));
        if (highScoresTable.size() > 5) {
            highScoresTable = new ArrayList<>(highScoresTable.subList(0, 5));
        }
        saveHighScores();

        System.out.println(BOLD + MAGENTA + "HIGH SCORE TABLE" + RESET);
        System.out.println(MAGENTA + "----------------" + RESET);
        for (int i = 0; i < highScoresTable.size(); i++) {
            PlayerScore ps = highScoresTable.get(i);
            String medal = "";
            if (i == 0) medal = "🥇 ";
            else if (i == 1) medal = "🥈 ";
            else if (i == 2) medal = "🥉 ";
            System.out.printf("%s%.2f %s\n", medal, ps.score, ps.name);
        }
        System.out.println();

        manageHighScores();
    }
    
    private void loadHighScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    double score = Double.parseDouble(parts[1]);
                    highScoresTable.add(new PlayerScore(name, score));
                }
            }
        } catch (IOException e) {
        }
    }
    
    private void saveHighScores() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(HIGH_SCORE_FILE))) {
            for (PlayerScore ps : highScoresTable) {
                writer.println(ps.name + "," + ps.score);
            }
        }
    }
    
    private void saveReplay() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REPLAY_FILE))) {
            for (String data : replayData) {
                writer.print(data);
            }
        }
    }

    public void replay() throws IOException {
        System.out.println(GREEN + "\n=== GAME REPLAY ===" + RESET);
        try (BufferedReader reader = new BufferedReader(new FileReader(REPLAY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("No replay data available.");
        }
    }

    private void manageHighScores() throws IOException {
        
        System.out.println(YELLOW + "\n=== HIGH SCORE MANAGEMENT ===" + RESET);
        System.out.println("1. Keep all scores");
        System.out.println("2. Delete all high scores");
        System.out.println("3. Remove a specific score");
        System.out.print("Choose option (1-3) >>> ");
        
        int choice = 1;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            choice = 1;
        }
        
        switch (choice) {
            case 2:
                System.out.print(RED + "⚠️  Are you sure you want to delete ALL high scores? (Y/N) >>> " + RESET);
                String confirm = sc.nextLine().trim().toUpperCase();
                if (confirm.equals("Y")) {
                    highScoresTable.clear();
                    saveHighScores();
                    System.out.println(GREEN + "✓ All high scores deleted!" + RESET);
                } else {
                    System.out.println("Cancelled.");
                }
                break;
                
            case 3:
                if (highScoresTable.isEmpty()) {
                    System.out.println("No scores to remove.");
                    break;
                }
                
                System.out.println("\nCurrent High Scores:");
                for (int i = 0; i < highScoresTable.size(); i++) {
                    PlayerScore ps = highScoresTable.get(i);
                    System.out.printf("%d. %.2f %s\n", i + 1, ps.score, ps.name);
                }
                
                System.out.print("\nEnter number to remove (1-" + highScoresTable.size() + ") >>> ");
                try {
                    int removeIndex = Integer.parseInt(sc.nextLine().trim()) - 1;
                    if (removeIndex >= 0 && removeIndex < highScoresTable.size()) {
                        PlayerScore removed = highScoresTable.remove(removeIndex);
                        saveHighScores();
                        System.out.println(GREEN + "✓ Removed: " + removed.name + " (" + removed.score + ")" + RESET);
                    } else {
                        System.out.println("Invalid number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
                break;
                
            default:
                System.out.println(GREEN + "✓ High scores kept intact." + RESET);
                break;
        }
        System.out.println();
    }
    
    private void displayScoreGraph() {
        System.out.println(BOLD + BLUE + "📊 SCORE COMPARISON GRAPH" + RESET);
        System.out.println(BLUE + "=".repeat(50) + RESET);
        
        int p1Score = Player1.getTotalScore();
        int p2Score = Player2 != null ? Player2.getTotalScore() : 0;
        int maxScore = Math.max(p1Score, p2Score);
        
        if (maxScore == 0) maxScore = 1;
        
        int p1Bars = (int) ((p1Score / (double) maxScore) * 40);
        System.out.print(Player1.getName() + ": ");
        System.out.print(GREEN);
        for (int i = 0; i < p1Bars; i++) {
            System.out.print("█");
        }
        System.out.println(" " + p1Score + RESET);
        
        if (Player2 != null) {
            int p2Bars = (int) ((p2Score / (double) maxScore) * 40);
            System.out.print(Player2.getName() + ": ");
            System.out.print(MAGENTA);
            for (int i = 0; i < p2Bars; i++) {
                System.out.print("█");
            }
            System.out.println(" " + p2Score + RESET);
        }
        
        System.out.println();
    }

    private void printSoundEffect(String sound) {
        System.out.println(YELLOW + sound + RESET);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    

    private static class PlayerScore {
        String name;
        double score;
        
        PlayerScore(String name, double score) {
            this.name = name;
            this.score = score;
        }
    }
}
