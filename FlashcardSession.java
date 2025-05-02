import java.io.*;
import java.util.*;

public class FlashcardSession {
    private final Map<String, String> flashcards;
    private Map<String, Integer> mistakeCount = new HashMap<>();
    private List<String> recentMistakes = new ArrayList<>();
    private boolean inverted = false;
    private int repetitions = 1;

    private static final String MISTAKE_FILE = "mistakes.txt";
    private static final String RECENT_FILE = "recent.txt";

    public FlashcardSession(Map<String, String> flashcards) {
        this.flashcards = flashcards;
        loadState();
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public void run(String order) {
        List<String> questions;

        switch (order) {
            case "worst-first":
                questions = new ArrayList<>(flashcards.keySet());
                questions.sort((q1, q2) -> Integer.compare(
                        mistakeCount.getOrDefault(q2, 0),
                        mistakeCount.getOrDefault(q1, 0)
                ));
                break;

            case "recent-mistakes-first":
                questions = new ArrayList<>(recentMistakes);
                Collections.reverse(questions);
                break;

            case "random":
            default:
                questions = new ArrayList<>(flashcards.keySet());
                Collections.shuffle(questions);
                break;
        }

        Map<String, Integer> correctCount = new HashMap<>();
        Scanner scanner = new Scanner(System.in);

        for (String q : questions) {
            String question = inverted ? flashcards.get(q) : q;
            String answer = inverted ? q : flashcards.get(q);
            correctCount.putIfAbsent(q, 0);

            while (correctCount.get(q) < repetitions) {
                System.out.print("Q: " + question + " -> ");
                String userAnswer = scanner.nextLine().trim();

                if (userAnswer.equalsIgnoreCase(answer)) {
                    System.out.println("Correct!");
                    correctCount.put(q, correctCount.get(q) + 1);
                } else {
                    System.out.println("Incorrect! Answer is: " + answer);
                    mistakeCount.put(q, mistakeCount.getOrDefault(q, 0) + 1);
                    recentMistakes.add(q);
                    break;
                }
            }
        }

        System.out.println("Session completed.");
        saveState();
    }

    private void saveState() {
        try (PrintWriter writer = new PrintWriter("mistakes.txt")) {
            for (Map.Entry<String, Integer> entry : mistakeCount.entrySet()) {
                writer.println(entry.getKey() + "|" + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Failed to save mistakes.txt");
        }
    
        try (PrintWriter writer = new PrintWriter("recent.txt")) {
            for (String question : recentMistakes) {
                writer.println(question);
            }
        } catch (IOException e) {
            System.err.println("Failed to save recent.txt");
        }
    }
    

    @SuppressWarnings("unchecked")
    private void loadState() {
        mistakeCount = new HashMap<>();
        recentMistakes = new ArrayList<>();
    
        // Load mistake counts
        try (Scanner scanner = new Scanner(new File("mistakes.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2 && flashcards.containsKey(parts[0])) {
                    mistakeCount.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        } catch (IOException e) {
            
        }
    
        // Load recent mistakes, only keep existing questions
        try (Scanner scanner = new Scanner(new File("recent.txt"))) {
            while (scanner.hasNextLine()) {
                String question = scanner.nextLine();
                if (flashcards.containsKey(question)) {
                    recentMistakes.add(question);
                }
            }
        } catch (IOException e) {
            
        }
    
        for (String q : flashcards.keySet()) {
            mistakeCount.putIfAbsent(q, 0);
        }
    }
    
    

}
