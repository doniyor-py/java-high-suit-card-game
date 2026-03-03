import java.util.*;

public class Deck {
    private ArrayList<Card> deck = new ArrayList<>();
    
    public Deck() {
        for (int i = 0; i < 52; i++) {
            deck.add(new Card(i % 13, i / 13));
        }
        Collections.shuffle(deck);
    }
    
    public Card deal() {
        if (!deck.isEmpty()) {
            Card c = deck.get(0);
            deck.remove(0);
            return c;
        } else {
            return null;
        }
    }
    
    public void returnCard(Card c) {
        deck.add(c);
    }
    
    public int cardsRemaining() {
        return deck.size();
    }
    
    public boolean contains(Card card) {
        return deck.contains(card);
    }
    
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (Card c : deck) {
            out.append(c).append("\n");
        }
        return out.toString();
    }
}