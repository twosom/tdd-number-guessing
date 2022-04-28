package numberguessing.console;

import numberguessing.PositiveIntegerGenerator;

public class AppModel {

    public static final String SELECT_MODE_MESSAGE = """
            1: Single player game
            2: Multiplayer game
            3: Exit
            Enter selection:\040""";


    private final PositiveIntegerGenerator generator;
    private int answer;
    private String output;
    private boolean completed;
    private boolean singlePlayerMode;
    private int tries;

    public AppModel(PositiveIntegerGenerator generator) {
        this.completed = false;
        this.output = SELECT_MODE_MESSAGE;
        this.generator = generator;
        this.singlePlayerMode = false;
        this.tries = 0;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public String flushOutput() {
        return output;
    }

    public void processInput(String input) {
        if (singlePlayerMode) {
            processSinglePlayerGame(input);
        } else {
            processModeSelection(input);
        }
    }

    private void processSinglePlayerGame(String input) {
        tries++;
        int guess = Integer.parseInt(input);
        if (guess < answer) {
            this.output = """
                    Your guess is too low.
                    Enter your guess:\040
                    """;
        } else if (guess > answer) {
            this.output = """
                    Your guess is too high.
                    Enter your guess:\040
                    """;
        } else {
            this.output = "Correct! " + tries + (tries == 1 ? " guess.\n" : " guesses.\n") + SELECT_MODE_MESSAGE;
            this.singlePlayerMode = false;
        }
    }

    private void processModeSelection(String input) {
        if (input.equals("1")) {
            this.output = """
                    Single player game
                    I'm thinking of a number between 1 and 100.
                    Enter your guess:\040
                    """;
            this.singlePlayerMode = true;
            answer = generator.generateLessThanOrEqualToHundred();
        } else {
            completed = true;
        }
    }
}
