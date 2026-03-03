import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.Field;

public class HighSuitTest {
    private static void setPrivateField(Object obj, String fieldName, Object value) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field '" + fieldName + "' on " 
                    + obj.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    private static Card c(int rank, int suit) {
        return new Card(rank, suit);
    }

    @Test
    public void cardValue_NumberCards() {
        assertEquals(2, c(0, 0).getValue());   // "2"
        assertEquals(3, c(1, 0).getValue());   // "3"
        assertEquals(10, c(8, 0).getValue());  // "10"
    }

    @Test
    public void cardValue_FaceCardsAndAce() {
        assertEquals(10, c(9, 0).getValue());   // Jack
        assertEquals(10, c(10, 0).getValue());  // Queen
        assertEquals(10, c(11, 0).getValue());  // King
        assertEquals(11, c(12, 0).getValue());  // Ace
    }

    @Test
    public void cardSuitCharMatchesSuitString() {
        assertEquals('C', c(0, 0).getSuitChar());
        assertEquals('D', c(0, 1).getSuitChar());
        assertEquals('H', c(0, 2).getSuitChar());
        assertEquals('S', c(0, 3).getSuitChar());
    }

    @Test
    public void cardEqualsAndHashCodeContract() {
        Card a = c(12, 2);  // Ace Hearts
        Card b = c(12, 2);  // Ace Hearts
        Card diffSuit = c(12, 3); // Ace Spades
        Card diffRank = c(11, 2); // King Hearts

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, diffSuit);
        assertNotEquals(a, diffRank);
    }

    @Test
    public void cardToStringContainsRankAndSuit() {
        String s = c(12, 2).toString(); // "Ace of Hearts"
        assertTrue(s.contains("Ace"));
        assertTrue(s.contains("Hearts"));
    }

    @Test
    public void handCalculateSuitScoresCorrect() {
        Hand h = new Hand();
        h.addCard(c(0, 0)); // 2 of Clubs
        h.addCard(c(8, 0)); // 10 of Clubs

        h.addCard(c(12, 2)); // Ace of Hearts

        int[] scores = h.calculateSuitScores();
        assertEquals(12, scores[0]); // Clubs
        assertEquals(0,  scores[1]); // Diamonds
        assertEquals(11, scores[2]); // Hearts
        assertEquals(0,  scores[3]); // Spades
    }

    @Test
    public void handMaxSuitScoreAndSuitName() {
        Hand h = new Hand();

        h.addCard(c(12, 2)); // Ace Hearts
        h.addCard(c(6,  2)); // 8 Hearts

        h.addCard(c(8,  3)); // 10 Spades

        assertEquals(19, h.getMaxSuitScore());
        assertEquals("Hearts", h.getMaxSuit());
    }

    @Test
    public void handMaxSuitIndexMatchesSuitName() {
        Hand h = new Hand();
        h.addCard(c(7,  1)); // 9 Diamonds
        h.addCard(c(10, 1)); // Queen Diamonds

        assertEquals(19, h.getMaxSuitScore());
        assertEquals(1,  h.getMaxSuitIndex());   // Diamonds index = 1
        assertEquals("Diamonds", h.getMaxSuit()); // Suit string
    }

    @Test
    public void handTieFavoringBonusSuitWinsTie() {
        Hand h = new Hand();
        h.addCard(c(12, 2));
        h.addCard(c(6,  2));
        h.addCard(c(7,  1));
        h.addCard(c(10, 1));
        h.addCard(c(0, 0));

        assertEquals(19, h.getMaxSuitScore());
        assertEquals("Hearts",   h.getMaxSuit('H'));
        assertEquals("Diamonds", h.getMaxSuit('D'));
    }

    @Test
    public void handTieFavoringDoesNotOverrideWhenNotTied() {
        Hand h = new Hand();
        h.addCard(c(12, 2)); // Ace Hearts (11)
        h.addCard(c(6,  2)); // 8 Hearts (8)
        h.addCard(c(6,  1));  // 8 Diamonds (8)
        h.addCard(c(10, 1));  // Queen Diamonds (10)

        h.addCard(c(0, 0));

        assertEquals(19, h.getMaxSuitScore());
        assertEquals("Hearts", h.getMaxSuit('D')); 
    }

    @Test
    public void handRemoveInvalidIndexDoesNotBreak() {
        Hand h = new Hand();
        h.addCard(c(0, 0));

        h.removeCard(-1);
        h.removeCard(999);

        assertEquals(1, h.size());
    }

    @Test
    public void deckDealReturnsNonNullInitially() {
        Deck deck = new Deck();
        assertNotNull(deck.deal());
    }

    @Test
    public void deckDeals52UniqueCards() {
        Deck deck = new Deck();
        HashSet<Card> seen = new HashSet<>();

        for (int i = 0; i < 52; i++) {
            Card card = deck.deal();
            assertNotNull(card);
            assertFalse("Duplicate dealt: " + card, seen.contains(card));
            seen.add(card);
        }

        assertEquals(52, seen.size());
    }

    @Test
    public void deckExhaustionAfter52Deals() {
        Deck deck = new Deck();
        for (int i = 0; i < 52; i++) {
            assertNotNull(deck.deal());
        }
        
        try {
            Card extra = deck.deal();
            assertNull("Expected null when deck is empty (or an exception), but got: " + extra, extra);
        } catch (RuntimeException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void playerSetHandDealsExactly5Cards() {
        Deck deck = new Deck();
        Player p = new Player("Test");
        p.setHand(deck);

        assertEquals(5, p.getHand().size());
    }

    @Test
    public void dealtHandHasNoDuplicateCards() {
        Deck deck = new Deck();
        Player p = new Player("Test");
        p.setHand(deck);

        HashSet<Card> seen = new HashSet<>();
        for (Card c : p.getHand().getCards()) {
            assertFalse("Duplicate in hand: " + c, seen.contains(c));
            seen.add(c);
        }
        assertEquals(5, seen.size());
    }

    @Test
    public void twoPlayersFromSameDeckHaveNoOverlappingCards() {
        Deck deck = new Deck();

        Player p1 = new Player("P1");
        Player p2 = new Player("P2");

        p1.setHand(deck);
        p2.setHand(deck);

        HashSet<Card> p1Cards = new HashSet<>(p1.getHand().getCards());
        for (Card c : p2.getHand().getCards()) {
            assertFalse("Same card appears in both hands: " + c, p1Cards.contains(c));
        }
    }

    @Test
    public void playerRoundScoreAddsBonusWhenMaxSuitMatches() {
        Player p = new Player("Test");
        setPrivateField(p, "bonus", 'H');
        p.getHand().clear();
        p.getHand().addCard(c(12, 2)); // Ace Hearts 11
        p.getHand().addCard(c(6,  2)); // 8 Hearts 8

        p.getHand().addCard(c(0, 0));
        p.getHand().addCard(c(1, 0));
        p.getHand().addCard(c(2, 0));

        int score;
        try {
            score = p.calculateRoundScore(false);
        } catch (NoSuchMethodError e) {
            score = p.calculateRoundScore();
        }

        assertEquals(24, score);
    }

    @Test
    public void playerRoundScoreNoBonusWhenMaxSuitDoesNotMatch() {
        Player p = new Player("Test");
        setPrivateField(p, "bonus", 'D');

        p.getHand().clear();
        p.getHand().addCard(c(12, 2)); // Ace Hearts 11
        p.getHand().addCard(c(6,  2)); // 8 Hearts 8
        p.getHand().addCard(c(0, 0));
        p.getHand().addCard(c(1, 0));
        p.getHand().addCard(c(2, 0));

        int score;
        try {
            score = p.calculateRoundScore(false);
        } catch (NoSuchMethodError e) {
            score = p.calculateRoundScore();
        }

        assertEquals(19, score);
    }

    @Test
    public void playerTotalScoreAccumulatesAcrossRounds() {
        Player p = new Player("Test");
        setPrivateField(p, "bonus", 'H');

        p.getHand().clear();
        p.getHand().addCard(c(12, 2));
        p.getHand().addCard(c(6,  2));
        p.getHand().addCard(c(0, 0));
        p.getHand().addCard(c(1, 0));
        p.getHand().addCard(c(2, 0));

        int r1;
        try { r1 = p.calculateRoundScore(false); }
        catch (NoSuchMethodError e) { r1 = p.calculateRoundScore(); }

        p.getHand().clear();
        p.getHand().addCard(c(8,  3));  // 10 Spades
        p.getHand().addCard(c(10, 3));  // Queen Spades (10)
        p.getHand().addCard(c(0, 0));
        p.getHand().addCard(c(1, 0));
        p.getHand().addCard(c(2, 0));

        int r2;
        try { r2 = p.calculateRoundScore(false); }
        catch (NoSuchMethodError e) { r2 = p.calculateRoundScore(); }

        assertEquals(r1 + r2, p.getTotalScore());
    }
}
