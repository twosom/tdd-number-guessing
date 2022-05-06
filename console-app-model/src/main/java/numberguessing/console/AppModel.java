package numberguessing.console;

import numberguessing.PositiveIntegerGenerator;

import java.util.stream.Stream;

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
    private final StringBuffer outputBuffer;
    private boolean completed;
    private Processor processor;

    public AppModel(PositiveIntegerGenerator generator) {
        this.completed = false;
        this.outputBuffer = new StringBuffer(SELECT_MODE_MESSAGE);
        this.generator = generator;
        this.processor = this::processModeSelection;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public String flushOutput() {
        String buffer = this.outputBuffer.toString();
        this.outputBuffer.setLength(0);
        return buffer;
    }

    public void processInput(String input) {
        this.processor = this.processor.run(input);
    }

    private Processor processModeSelection(String input) {
        if (input.equals("1")) {
            this.outputBuffer.append("""
                    Single player game
                    I'm thinking of a number between 1 and 100.
                    Enter your guess:\040
                    """);
            int answer = generator.generateLessThanOrEqualToHundred();
            return getSinglePlayerProcessor(answer, 1);
        } else if (input.equals("2")) {
            this.outputBuffer.append("""
                    Multiplayer game
                    Enter player names separated with commas:\040""");

            return startMultiplayerGameProcessor();
        } else {
            completed = true;
            return null;
        }
    }

    private Processor startMultiplayerGameProcessor() {
        return input -> {
            Object[] players = Stream.<String>of(input.split(","))
                    .map(String::trim)
                    .toArray();
            this.outputBuffer.append("I'm thinking of a number between 1 and 100.\n");
            int answer = generator.generateLessThanOrEqualToHundred();
            return getMultiplayerGameProcessor(players, answer, 1);
        };
    }

    private Processor getMultiplayerGameProcessor(Object[] players, int answer, int tries) {
        Object player = players[(tries - 1) % players.length];
        this.outputBuffer.append("Enter %s's guess: ".formatted(player));
        return input -> {
            int guess = Integer.parseInt(input);
            if (guess < answer) {
                this.outputBuffer.append("%s's guess is too low.\n".formatted(player));
                return getMultiplayerGameProcessor(players, answer, tries + 1);
            } else if (guess > answer) {
                this.outputBuffer.append("%s's guess is too high.\n".formatted(player));
                return getMultiplayerGameProcessor(players, answer, tries + 1);
            } else {
                this.outputBuffer.append("Correct! ");
                this.outputBuffer.append("%s wins.\n".formatted(player));
                this.outputBuffer.append(SELECT_MODE_MESSAGE);
                return this::processModeSelection;
            }
        };
    }

    private Processor getSinglePlayerProcessor(int answer, int tries) {
        return input -> {
            int guess = Integer.parseInt(input);
            if (guess < answer) {
                this.outputBuffer.append("""
                        Your guess is too low.
                        Enter your guess:\040
                        """);
                return this.getSinglePlayerProcessor(answer, tries + 1);
            } else if (guess > answer) {
                this.outputBuffer.append("""
                        Your guess is too high.
                        Enter your guess:\040
                        """);
                return this.getSinglePlayerProcessor(answer, tries + 1);
            } else {
                this.outputBuffer.append("Correct! ")
                        .append(tries)
                        .append(tries == 1 ? " guess.\n" : " guesses.\n")
                        .append(SELECT_MODE_MESSAGE);
                return this::processModeSelection;
            }
        };
    }
}
