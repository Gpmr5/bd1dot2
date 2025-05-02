import java.util.*;

public class FlashcardApp {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Map<String, String> flashcards = loadSampleFlashcards();
        FlashcardSession session = new FlashcardSession(flashcards);

        int repetitions = 1;
        boolean invert = false;
        String order = "random";

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help":
                    printHelp();
                    return;
                case "--order":
                    if (i + 1 < args.length) {
                        order = args[++i];
                    }
                    break;
                case "--repetitions":
                    if (i + 1 < args.length) {
                        repetitions = Integer.parseInt(args[++i]);
                    }
                    break;
                case "--invertCards":
                    invert = true;
                    break;
            }
        }

        session.setRepetitions(repetitions);
        session.setInverted(invert);
        session.run(order);
    }

    private static void printHelp() {
        System.out.println("--help Show help information");
        System.out.println("--order <order> Sorting type, default is \"random\"");
        System.out.println("[options: \"random\", \"worst-first\", \"recent-mistakes-first\"]");
        System.out.println("--repetitions <num> Set number of times card must be answered correctly (default: 1)");
        System.out.println("--invertCards Reverse questions and answers (default: false) ");
    }

    private static Map<String, String> loadSampleFlashcards() {
        Map<String, String> cards = new LinkedHashMap<>();
        cards.put("France", "Paris");
        cards.put("Germany", "Berlin");
        cards.put("Japan", "Tokyo");
        cards.put("Italy", "Rome");
        cards.put("Spain", "Madrid");
        return cards;
    }
}
