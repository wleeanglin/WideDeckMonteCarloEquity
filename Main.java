import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    ArrayList<Hand> hands = new ArrayList<>();
    ArrayList<Card> board = new ArrayList<>();

    public static void main(String[] args) {
        Main m = new Main(); 
        m.run();  
    }

    public void run(){
        Deck d = new Deck(true);
        EvalLogic e  = new EvalLogic(); 
        printHelp();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                String line = null;
                line = reader.readLine();
                switch(line){
                    case "hand":
                        Hand h = getHand(reader, d);
                        Optional.ofNullable(h).ifPresent(hands::add);
                        break;
                    case "board":
                        board = getBoard(reader, d);
                        Collections.sort(board);
                        break;
                    case "clear":
                        clear();
                        break;
                    case "sim":
                        int sims = 0; 
                        while(true) {
                            line = reader.readLine(); 
                            try {
                                sims = Integer.parseInt(line); 
                                break;
                            } catch (NumberFormatException pe) {
                                pe.printStackTrace();
                            }
                        }
                        Collections.sort(board);
                        HashMap<Hand, Double> handEquity = monteCarloSim(hands, board, sims, d, e);
                        printMonteCarloResults(handEquity); 
                        clear();
                        break;
                    case "testsim":
                        d = new Deck(true); 
                        for(int i = 0; i < 2; i++) {
                            hands.add(new Hand(d));
                        }
                        HashMap<Hand, Double> randomEquity = monteCarloSim(hands, new ArrayList<Card>(), 10000, d, e);
                        printMonteCarloResults(randomEquity);
                        clear();
                        break; 
                    case "exit":
                        System.exit(1); 
                    case "help":
                        printHelp();
                    default:
                        System.err.println(line + ": command not found");
                        break;
                }
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public Card parse(String input){
        return Card.createCard(input.charAt(0), input.charAt(1)); 
    }

    public Hand getHand(BufferedReader reader, Deck d) throws IOException{
        ArrayList<Card> cards = new ArrayList<>();
        String line = null;

        while(cards.size() < 2) {
            line = reader.readLine();


            if(line == null || line.equals("exit")) { 
                return null;
            }

            Card c = parse(line);
            if(c != null){
                cards.add(c); 
                d.remove(c);
            }
        }

        Hand h = new Hand();
        for(Card c : cards) {
            h.addCard(c);
        }
        return h;
    }

    public ArrayList<Card> getBoard(BufferedReader reader, Deck d) throws IOException {
        ArrayList<Card> board = new ArrayList<>();
        String line = null;

        while(board.size() < 5) {
            line = reader.readLine();
            if(line == null || line.equals("exit")) { 
                return board;
            }

            Card c = parse(line);
            if(c != null){
                board.add(c); 
                d.remove(c);
            }
        }
        
        return board;
    }

    public void printBoard(ArrayList<Card> board){
        for(Card card: board) {
            System.out.printf("%s", card.getValueEncoded());
        }
        System.out.println();
    }

    public void printResults(ArrayList<Result> results, ArrayList<Card> board) {
        System.out.printf("On board: ");
        printBoard(board);
        for(Result result : results) {
            result.printResult(); 
        }
    }

    public void printMonteCarloResults(HashMap<Hand, Double> monteCarloResults) {
        for(Hand h : monteCarloResults.keySet()) {
            System.out.println(h.toString() + " has equity " + monteCarloResults.get(h));
        }
    }

    public void clear() {
        hands = new ArrayList<>();
        board = new ArrayList<>();
    }

    public void printHelp() {
        System.out.println("Enter one of the following commands;");
        System.out.println("    -hand: Enter a new hand");
        System.out.println("    -board: Enter a new board");
        System.out.println("    -clear: reset");
        System.out.println("    -sim x: Monte carlo with x iterations");
        System.out.println("    -exit: End program");
        System.out.println("    -help: Show this message");
        System.out.println("    -testsim: random 2 hand no board 10000 montecarlo sim");
    }

    public HashMap<Hand, Double> monteCarloSim(ArrayList<Hand> hands, ArrayList<Card> board, int sims, Deck d, EvalLogic e) {
        HashMap<Hand, Integer> winnerCount = new HashMap<>();

        //Initialise
        for(int i = 0; i < hands.size(); i++){
            winnerCount.put(hands.get(i), 0); 
        }

        for(int i = 0; i < sims; i++) {
            Deck tempDeck = d.copy(); 
            tempDeck.shuffle();
            ArrayList<Card> tempBoard = board.stream().map(Card::copy)
                .collect(Collectors.toCollection(ArrayList::new));; 
            for(int j = 0; j < 5 - board.size(); j++) {
                tempBoard.add(tempDeck.deal()); 
            }
            ArrayList<Result> result = e.calcWinner(hands, tempBoard);
            for (Result r : result) {
                if (r.getRank() == 1) {
                    winnerCount.put(r.getHand(), winnerCount.get(r.getHand()) + 1);
                }
            }
        }

        //Divide each winner count by the number of sims to get equity
        return (HashMap<Hand, Double>) winnerCount.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> (double) entry.getValue() / sims
                ));
    }
}