public class Card {
    private final int rank;
    private final int suit;
    private static final String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
    public static final String[] suits = {"Clubs", "Diamonds", "Hearts", "Spades"};
    
    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public int getSuit() {
        return this.suit;
    }
    
    public int getValue() {
        if (rank <= 8) {
            return rank + 2;
        } else if (rank <= 11) {
            return 10;
        } else {
            return 11;
        }
    }
    
    public String getRankString() {
        return ranks[this.rank];
    }
    
    public String getSuitString() {
        return suits[this.suit];
    }
    
    public char getSuitChar() {
        return suits[this.suit].charAt(0);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.rank;
        hash = 97 * hash + this.suit;
        return hash;
    }
    

    
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Card other = (Card) obj;
        if (this.rank != other.rank) {
            return false;
        }
        return this.suit == other.suit;
    }
    
    @Override
    public String toString() {
        return ranks[this.rank] + " of " + suits[this.suit];
    }
}
