import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalcPrimes {

    private static List<Character> suits = List.of('r', 'e', 's', 'g', 'v', 'b');
    private static Map<Character, int[]> primes = Map.ofEntries(
        Map.entry('6', new int[]{2, 3, 5, 7, 11, 13}),
        Map.entry('7', new int[]{17, 19, 23, 29, 31, 37}),
        Map.entry('8', new int[]{41, 43, 47, 53, 59, 61}),
        Map.entry('9', new int[]{67, 71, 73, 79, 83, 89}),
        Map.entry('T', new int[]{97, 101, 103, 107, 109, 113}),
        Map.entry('J', new int[]{127, 131, 137, 139, 149, 151}),
        Map.entry('Q', new int[]{157, 163, 167, 173, 179, 181}),
        Map.entry('K', new int[]{191, 193, 197, 199, 211, 223}),
        Map.entry('A', new int[]{227, 229, 233, 239, 241, 251})
    );

    private static HashMap<Long, Integer> resultsMap = new HashMap<>(); 

    public static void main(String[] args) {
        String filename; 
        if(args.length == 0) {
            filename = "wideDeckHandOrder.txt";
        } else {
            filename = args[0];
        }

        readHands(filename);

    }

    public static void readHands(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            List<String> hands = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                hands.add(line.split("\t\t")[0]);
            }

            parseLines(hands);
            
            saveMapToFile(resultsMap, "primeProducts.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseLines(List<String> hands) {
        int totalSize = hands.size(); 
        for(int i = 0; i < totalSize; i++) {
            //i doubles as hand rank
            long prime = parseHandProduct(hands.get(i));
            resultsMap.put(prime, i); 

            if ((i + 1) % (totalSize / 10) == 0) {
                System.out.println("Calculated: " + (i + 1) * 100 / totalSize + "%");
            }
        }
    }

    public static long parseHandProduct(String hand) {
        long product = 1L;
        for(int i = 0; i < hand.length(); i += 2) {
            if(suits.indexOf(hand.charAt(i + 1)) < 0) {
                System.out.println(hand);
            }
            product *= primes.get(hand.charAt(i))[suits.indexOf(hand.charAt(i + 1))];
        }
        return product; 
    }

    public static void saveMapToFile(Map<Long, Integer> map, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            System.out.println("Started saving...");
            long startTime = System.nanoTime();
            oos.writeObject(map);
            long endTime = System.nanoTime(); 
            double duration = (endTime - startTime) / 1e6;
            System.out.println("Saving complete in : " + duration + " milliseconds");
        }
    }
}