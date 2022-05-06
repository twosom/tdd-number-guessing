package numberguessing.console;

import numberguessing.PositiveIntegerGenerator;

public class AppModel {

    public static final String SELECT_MODE_MESSAGE = """
            1: Single player game
            2: Multiplayer game
            3: Exit
            Enter selection:\040""";

    @FunctionalInterface
    interface Processor {

        Processor run(String input);
    }


    private final PositiveIntegerGenerator generator;
    private String output;
    private boolean completed;
    private Processor processor;

    public AppModel(PositiveIntegerGenerator generator) {
        this.completed = false;
        this.output = SELECT_MODE_MESSAGE;
        this.generator = generator;
        this.processor = this::processModeSelection;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public String flushOutput() {
        return output;
    }

    public void processInput(String input) {
        this.processor = this.processor.run(input);
    }

    private Processor processModeSelection(String input) {
        if (input.equals("1")) {
            this.output = """
                    Single player game
                    I'm thinking of a number between 1 and 100.
                    Enter your guess:\040
                    """;
            int answer = generator.generateLessThanOrEqualToHundred();
            return getSinglePlayerProcessor(answer, 1);
        } else {
            completed = true;
            return null;
        }
    }

    private Processor getSinglePlayerProcessor(int answer, int tries) {
        return input -> {
            int guess = Integer.parseInt(input);
            if (guess < answer) {
                AppModel.this.output = """
                        Your guess is too low.
                        Enter your guess:\040
                        """;
                return AppModel.this.getSinglePlayerProcessor(answer, tries + 1);
            } else if (guess > answer) {
                AppModel.this.output = """
                        Your guess is too high.
                        Enter your guess:\040
                        """;
                return AppModel.this.getSinglePlayerProcessor(answer, tries + 1);
            } else {
                AppModel.this.output = "Correct! " + tries + (tries == 1 ? " guess.\n" : " guesses.\n") + SELECT_MODE_MESSAGE;
                return AppModel.this::processModeSelection;
            }
        };
    }
}
