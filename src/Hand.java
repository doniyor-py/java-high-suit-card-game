import java.util.*;

public class Hand {
    private ArrayList<Card> cards;
    
    public Hand() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(int index) {
        if (index >= 0 && index < cards.size()) {
            cards.remove(index);
        }
    }

    public Card getCard(int index) {
        if (index >= 0 && index < cards.size()) {
            return cards.get(index);
        }
        return null;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public int size() {
        return cards.size();
    }

    public void clear() {
        cards.clear();
    }

    public int[] calculateSuitScores() {
        int[] suitScores = new int[4];
        for (Card c : cards) {
            suitScores[c.getSuit()] += c.getValue();
        }
        return suitScores;
    }

    public int getMaxSuitScore() {
        int[] suitScores = calculateSuitScores();
        int maxScore = 0;
        for (int score : suitScores) {
            if (score > maxScore) {
                maxScore = score;
            }
        }
        return maxScore;
    }

    public String getMaxSuit() {
        int[] suitScores = calculateSuitScores();
        int maxScore = 0;
        int maxSuit = 0;
        for (int i = 0; i < 4; i++) {
            if (suitScores[i] > maxScore) {
                maxScore = suitScores[i];
                maxSuit = i;
            }
        }
        return Card.suits[maxSuit];
    }
    
    public String getMaxSuit(char bonus) {
        return Card.suits[getMaxSuitIndex(bonus)];
    }


    public int getMaxSuitIndex() {
        int[] suitScores = calculateSuitScores();
        int maxScore = 0;
        int maxSuit = 0;
        for (int i = 0; i < 4; i++) {
            if (suitScores[i] > maxScore) {
                maxScore = suitScores[i];
                maxSuit = i;
            }
        }
        return maxSuit;
    }
    
    public int getMaxSuitIndex(char bonus) {
        int[] suitScores = calculateSuitScores();
        int maxScore = 0;

        for (int score : suitScores) {
            if (score > maxScore) maxScore = score;
        }

        int bonusIndex = bonusCharToSuitIndex(bonus);

        if (bonusIndex >= 0 && suitScores[bonusIndex] == maxScore) {
            return bonusIndex;
        }


        return getMaxSuitIndex();
    }

    
    private int bonusCharToSuitIndex(char bonus) {
        switch (Character.toUpperCase(bonus)) {
            case 'C': return 0;
            case 'D': return 1;
            case 'H': return 2;
            case 'S': return 3;
            default: return -1;
        }
    }


    public void displayHand() {
        System.out.println("\nYour current hand:");
        for (int i = 0; i < cards.size(); i++) {
            System.out.printf("%c: %s\n", i + 65, cards.get(i));
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card).append("\n");
        }
        return sb.toString();
    }
}
