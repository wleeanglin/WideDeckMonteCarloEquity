import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;

    public Deck(boolean shuffle){
        this.cards = new ArrayList<>(); 
        for(Card.Suit suit : Card.Suit.values()) {
            for(int i = 0; i < 9; i++){
                cards.add(new Card(i, suit));
            }
        }
        if(shuffle) { shuffle(); } 
    }

    public void shuffle() { 
        Collections.shuffle(cards);
    }

    public Card deal() {
        if(!cards.isEmpty()) {
            return cards.remove(cards.size() - 1);
        }
        return null;
    }

    public int cardsRemaining() {
        return cards.size();
    }

    public ArrayList<Card> board() {
        ArrayList<Card> board = new ArrayList<Card>(); 
        for(int i = 0; i < 5; i++) {
            board.add(this.deal()); 
        }
        return board; 
    }

    public void debug(int lineCount){
        int i = 0;
        for(Card c : cards) {
            System.out.printf("%s ", c.getValueEncoded());
            i++;
            if (i >= lineCount) {
                i = 0;
                System.out.println();
            }
        }
        System.out.println();
    }

    public void debugPrimes(){
        for(Card c : cards) {
            System.out.printf("%s : %d\n", c.getValueEncoded(), c.getPrime());
        }
        System.out.println();
    }

    public Deck copy() {
        Deck copy = new Deck(false);
        copy.cards = deepCopy(this.cards);
        return copy;
    }

    private ArrayList<Card> deepCopy(ArrayList<Card> original) {
        ArrayList<Card> copy = new ArrayList<>();
        for (Card card : original) {
            copy.add(card.copy());
        }
        return copy;
    }

    public void remove(Card c) {
        cards.remove(c);
    }
}

