import java.util.List;

public class Card implements Comparable<Card> {
    private int value;

    private static final List<Character> ranks = List.of('6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A');
    private static final List<Suit> suits = List.of(Suit.RUBY, Suit.EMERALD, Suit.SAPPHIRE, Suit.GOLD, Suit.SILVER, Suit.BRONZE);
    private static final List<Integer> primes = List.of(
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 
        31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 
        73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 
        127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 
        179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 
        233, 239, 241, 251
    );

    public static Card createCard(char rank, char suit) {
        Integer r = ranks.indexOf(rank);
        Suit s = Suit.fromEncoding(suit);
        
        if (r == -1 || s == null) {
            return null;
        }

        return new Card(r, s);
    }
    
    public Card(int rank, Suit suit) {
        value = (rank << 3) | suit.getBitwiseRepresentation(); 
    }

    public char getRank() {
        return ranks.get((value >> 3) & 0b1111);
    }

    public Suit getSuit() {
        return suits.get(value & 0b111);
    }
    public int getRankInt() {
        return (value >> 3) & 0b1111;
    }
    
    public int getSuitInt() {
        return value & 0b111;
    }

    public int getValue() {
        return value;
    }

    public int getPrime() {
        int rankIndex = (value >> 3) & 0b1111;
        int suitIndex = value & 0b111;
        int cardIndex = rankIndex * suits.size() + suitIndex;
        return primes.get(cardIndex); 
    }
    public String getValueEncoded(){
        return this.getRank() + "" + this.getSuit().getEncoding();
    }

    public enum Suit {
        RUBY('r'), EMERALD('e'), SAPPHIRE('s'), GOLD('g'), SILVER('v'), BRONZE('b');

        private final char encoding; 

        Suit(char encoding) {
            this.encoding = encoding;
        }

        public char getEncoding() {
            return encoding; 
        }

        public static Suit fromEncoding(char encoding) {
            encoding = Character.toLowerCase(encoding);
            for (Suit suit : Suit.values()) {
                if (suit.getEncoding() == encoding) {
                    return suit;
                }
            }
            return null;
        }

        public int getBitwiseRepresentation() {
            return this.ordinal();
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;

        if(o == null || (this.getClass() != o.getClass())) {
            return false;
        }

        Card oc = (Card) o;
        return (oc.getValue() == this.getValue());
    }

    //Sort A > K > Q > J ... and suits r > e > s ...
    @Override
    public int compareTo(Card o) {
        int rankDifference = Integer.compare(this.getRankInt(), o.getRankInt());
        if(rankDifference != 0) {
            return (-1) * rankDifference;
        }
        return Integer.compare(this.getSuitInt(), o.getSuitInt()); 
    }

    public Card copy() {
        Card copy = new Card(this.getRankInt(), this.getSuit());
        return copy; 
    }
}
