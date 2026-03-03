import java.util.*;

public class Player implements Comparable<Player> {
    private String name;
    private int score;
    private int totalScore;
    private char bonus;
    private Hand hand; 
    
    public Player(String name, int score) {
        this.name = name;
        this.score = score;
        this.totalScore = 0;
        hand = new Hand();
    }
    
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.totalScore = 0;
        hand = new Hand();
    }
    
    public void nominateBonus(Scanner sc) {
        while (true) {
            System.out.print("Please nominate your bonus suit? (C/D/H/S) >>> ");
            String in = sc.nextLine().trim().toUpperCase();
            if (in.length() == 1) {
                char c = in.charAt(0);
                if (c == 'C' || c == 'D' || c == 'H' || c == 'S') {
                    bonus = c;
                    return;
                }
            }
            System.out.println(Game.RED + "Invalid suit. Enter C, D, H, or S." + Game.RESET);
        }
    }    

    
    public void nominateBonusComputer() {
        int maxSuit = hand.getMaxSuitIndex();
        bonus = Card.suits[maxSuit].charAt(0);
        System.out.println("Computer chooses " + Card.suits[maxSuit] + " as the bonus suit");
    }
    
    void setHand(Deck deck) {
        hand.clear();
        for (int i = 0; i < 5; i++) {
            Card c = deck.deal();
            if (c !=  null) hand.addCard(c);
        }
    }
    
    void printHand() {
        hand.displayHand();
    }
    
    public void dropCards(Deck deck, Scanner sc) {
        while (true) {
            System.out.print("\nPlease nominate the cards to be dropped? >>> ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.isEmpty()) {
                return;
            }

            ArrayList<Integer> indices = new ArrayList<>();
            for (char c : input.toCharArray()) {
                if (c >= 'A' && c <= 'E') {
                    int index = c - 'A';
                    if (index < hand.size() && !indices.contains(index)) {
                        indices.add(index);
                    }
                }
            }

            if (indices.size() > 4) {
                System.out.println(Game.RED + "You can drop up to 4 cards only." + Game.RESET);
                continue;
            }

            Collections.sort(indices, Collections.reverseOrder());

            for (int index : indices) {
                hand.removeCard(index);
            }
            for (int i = 0; i < indices.size(); i++) {
                Card dealt = deck.deal();
                if (dealt != null) hand.addCard(dealt); 
            }
            return;
        }
    }

    
    public void dropCardsComputer(Deck deck) {
        int maxSuit = hand.getMaxSuitIndex();
        ArrayList<Integer> indicesToDrop = new ArrayList<>();
        for (int i = 0; i < hand.size(); i++) {
            if (hand.getCard(i).getSuit() != maxSuit) {
                indicesToDrop.add(i);
            }
            if (indicesToDrop.size() >= 4) break;
        }
        System.out.print("Computer drops: ");
        if (indicesToDrop.isEmpty()) {
            System.out.println("(no cards)");
        } else {
            for (int i = 0; i < indicesToDrop.size(); i++) {
                System.out.print(hand.getCard(indicesToDrop.get(i)));
                if (i < indicesToDrop.size() - 1) System.out.print(", ");
            }
            System.out.println();
        }
        Collections.sort(indicesToDrop, Collections.reverseOrder());
        for (int index : indicesToDrop) {
            hand.removeCard(index);
        }
        for (int i = 0; i < indicesToDrop.size(); i++) {
            hand.addCard(deck.deal());
        }
    }
    
    public int calculateRoundScore() {
        return calculateRoundScore(false);
    }

    public int calculateRoundScore(boolean blindMode) {
        int maxScore = hand.getMaxSuitScore();
        String maxSuit = hand.getMaxSuit(bonus);

        if (maxSuit.charAt(0) == bonus) {
            maxScore += 5;
            if (!blindMode) System.out.println("Bonus suit matches for a 5-point bonus!");
        } else {
            if (!blindMode) System.out.println("Bonus suit does not match – no bonus awarded.");
        }

        this.score = maxScore;
        this.totalScore += maxScore;
        return maxScore;
    }

    
    public String getName() {
        return name;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getTotalScore() {
        return totalScore;
    }
    
    public void resetScore() {
        this.score = 0;
    }
    
    public void resetTotalScore() {
        this.totalScore = 0;
    }
    
    public char getBonus() {
        return bonus;
    }
    
    public Hand getHand() {
        return hand;
    }
    
    public boolean isComputer() {
        return name.equalsIgnoreCase("Computer");
    }
    
    @Override
    public int compareTo(Player p) {
        return p.getTotalScore() - this.getTotalScore(); 
    }
    
    @Override
    public String toString() {
        return name + " : " + totalScore + " points";
    }
}
