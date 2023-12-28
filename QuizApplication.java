import java.util.Scanner;
import java.util.concurrent.*;

public class QuizApplication {
    private static final int QUESTION_COUNT = 10;  // Number of questions in the quiz
    private static final int TIME_LIMIT_SECONDS = 30;  // Time limit for each question in seconds

    private static String[] questions = {
            "What is the capital of France?",
            "Which planet is known as the Red Planet?",
            "What is the largest mammal in the world?",
            "Who wrote 'Romeo and Juliet'?",
            "What is the powerhouse of the cell?",
            "What is the capital of Australia?",
            "Who painted the Mona Lisa?",
            "What is the currency of Japan?",
            "What is the tallest mountain in the world?",
            "Who invented the telephone?"
    };

    private static String[][] options = {
            {"A. Berlin", "B. Madrid", "C. Paris", "D. Rome"},
            {"A. Venus", "B. Mars", "C. Jupiter", "D. Saturn"},
            {"A. Elephant", "B. Blue Whale", "C. Giraffe", "D. Dolphin"},
            {"A. Charles Dickens", "B. William Shakespeare", "C. Jane Austen", "D. Mark Twain"},
            {"A. Nucleus", "B. Mitochondria", "C. Ribosome", "D. Chloroplast"},
            {"A. Sydney", "B. Melbourne", "C. Canberra", "D. Perth"},
            {"A. Vincent van Gogh", "B. Pablo Picasso", "C. Leonardo da Vinci", "D. Michelangelo"},
            {"A. Yen", "B.  Euro", "C. Dollar", "D. Rupee"},
            {"A. Mount Kilimanjaro", "B. Mount Everest", "C. Mount McKinley", "D. Mount Fuji"},
            {"A. Thomas Edison", "B. Nikola Tesla", "C. Alexander Graham Bell", "D. Isaac Newton"}
    };

    private static char[] correctAnswers = {'C', 'B', 'B', 'B', 'B', 'C','C','A','B','C'};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        int score = 0;
        long totalTimeTaken = 0;

        for (int i = 0; i < QUESTION_COUNT; i++) {
            System.out.println("\nQuestion " + (i + 1) + ": " + questions[i]);
            for (String option : options[i]) {
                System.out.println(option);
            }

            Future<Character> userInput = executor.submit(() -> getUserAnswer(scanner));

            try {
                long questionStartTime = System.currentTimeMillis();
                char userAnswer = userInput.get(TIME_LIMIT_SECONDS, TimeUnit.SECONDS);
                long questionEndTime = System.currentTimeMillis();

                totalTimeTaken += (questionEndTime - questionStartTime);

                if (userAnswer == correctAnswers[i]) {
                    System.out.println("Correct!\n");
                    score++;
                } else {
                    System.out.println("Incorrect! The correct answer is " + correctAnswers[i] + ".\n");
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                System.out.println("Time's up! Moving to the next question.");
                userInput.cancel(true);
            }
        }

        System.out.println("Quiz completed! Your final score is: " + score + "/" + QUESTION_COUNT);
        System.out.println("Total time taken: " + totalTimeTaken / 1000 + " seconds");
        scanner.close();
        executor.shutdown();
    }

    private static char getUserAnswer(Scanner scanner) {
        long startTime = System.currentTimeMillis();

        while (true) {
            long elapsedTime = System.currentTimeMillis() - startTime;

            if (elapsedTime >= TIME_LIMIT_SECONDS * 1000) {
                return ' ';  // Timeout
            }

            if (scanner.hasNext()) {
                String input = scanner.next().toUpperCase();
                if (input.length() == 1 && input.charAt(0) >= 'A' && input.charAt(0) <= 'D') {
                    return input.charAt(0);
                }
            }
        }
    }
}
