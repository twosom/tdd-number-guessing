package numberguessing.console;

import numberguessing.PositiveIntegerGeneratorStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static java.lang.String.join;
import static java.lang.String.valueOf;
import static java.util.Arrays.stream;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AppModelTest {

    @DisplayName("AppModel 클래스가 초기화 되고 나면 isCompleted는 false를 반환해야 한다.")
    @Test
    void sut_is_incompleted_when_it_is_initialized() {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        boolean actual = sut.isCompleted();
        assertFalse(actual);
    }

    @DisplayName("sut는 게임 모드 선택 메시지를 알맞게 출력해야 한다.")
    @Test
    void sut_correctly_prints_select_mode_message() {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        String actual = sut.flushOutput();
        String expected = """
                1: Single player game
                2: Multiplayer game
                3: Exit
                Enter selection:\040""";
        assertEquals(expected, actual);
    }

    @DisplayName("sut가 올바르게 종료되어야 한다.")
    @Test
    void sut_correctly_exists() {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.processInput("3");
        boolean actual = sut.isCompleted();
        assertTrue(actual);
    }

    @DisplayName("sut는 싱글 플레이 게임을 시작하는 경우에 메시지를 알맞게 출력해야 한다.")
    @Test
    void sut_correctly_prints_single_player_game_start_message() {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.flushOutput();
        sut.processInput("1");
        String actual = sut.flushOutput();
        String expected = """
                Single player game
                I'm thinking of a number between 1 and 100.
                Enter your guess:\040
                """;
        assertEquals(expected, actual);
    }

    @DisplayName("sut은 싱글 플레이 게임에서 사용자의 추측값이 낮을 경우 알맞은 메시지를 출력해야 한다.")
    @ParameterizedTest(name = "정답이 {0}, 추측이 {1}일 경우")
    @CsvSource({"50, 40", "30, 29", "89, 9"})
    void sut_correctly_prints_too_low_message_in_single_player_game(int answer, int guess) {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(answer));
        sut.processInput("1");
        sut.flushOutput();
        sut.processInput(valueOf(guess));

        String actual = sut.flushOutput();
        String expected = """
                Your guess is too low.
                Enter your guess:\040
                """;
        assertEquals(expected, actual);
    }

    @DisplayName("sut은 싱글 플레이 게임에서 사용자의 추측값이 높을 경우 알맞은 메시지를 출력해야 한다.")
    @ParameterizedTest(name = "정답 : {0}, 추측 : {1}")
    @CsvSource({"50, 60", "80, 81"})
    void sut_correctly_prints_too_high_message_in_single_player_game(int answer, int guess) {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(answer));
        sut.processInput("1");
        sut.flushOutput();
        sut.processInput(valueOf(guess));

        String actual = sut.flushOutput();
        String expected = """
                Your guess is too high.
                Enter your guess:\040
                """;
        assertEquals(expected, actual);
    }


    @DisplayName("sut은 싱글 플레이 게임에서 사용자의 추측값과 정답이 같을 경우 알맞은 메시지를 출력해야 한다.")
    @ParameterizedTest(name = "정답과 추측 모두 {0} 인 경우")
    @ValueSource(ints = {1, 3, 10, 100})
    void sut_correctly_prints_correct_message_in_single_player_game(int answer) {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(answer));
        sut.processInput("1");
        sut.flushOutput();

        int guess = answer;
        sut.processInput(valueOf(guess));

        String actual = sut.flushOutput();
        String expected = "Correct! ";

        assertThat(actual).startsWith(expected);
    }

    @DisplayName("sut은 싱글 플레이 게임에서 사용자가 실패 횟수 만큼 틀린 후에 정답을 맞출 경우 알맞은 메시지를 출력해야 한다.")
    @ParameterizedTest(name = "실패 횟수 : {0}")
    @ValueSource(ints = {1, 10, 100})
    void set_correctly_prints_guess_count_if_single_player_game_finished(int fails) {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.processInput("1");
        for (int i = 0; i < fails; i++) {
            sut.processInput("30");
        }
        sut.flushOutput();
        sut.processInput("50");

        String actual = sut.flushOutput();
        assertThat(actual).contains((fails + 1) + " guesses.\n");
    }

    @DisplayName("sut은 싱글 플레이 게임에서 정답을 한 번에 맞춘 경우 알맞은 메시지를 출력해야 한다.")
    @Test
    void sut_correctly_prints_one_guess_if_single_player_finished() {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.processInput("1");
        sut.flushOutput();
        sut.processInput("50");

        String actual = sut.flushOutput();
        assertThat(actual).contains("1 guess.");
    }

    @DisplayName("sut은 싱글 플레이 게임이 끝난 경우 알맞은 메시지를 출력해야 한다. ")
    @Test
    void sut_prints_select_mode_message_if_single_player_game_finished() {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.processInput("1");
        sut.flushOutput();
        sut.processInput("50");

        String actual = sut.flushOutput();
        assertThat(actual).endsWith("""
                1: Single player game
                2: Multiplayer game
                3: Exit
                Enter selection:\040""");
    }

    @DisplayName("sut은 싱글 플레이 게임이 끝난 경우 모드 선택으로 돌아와야 한다.")
    @Test
    void sut_returns_to_mode_selection_if_single_player_game_finished() {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));

        sut.processInput("1");
        sut.processInput("50");
        sut.processInput("3");

        boolean actual = sut.isCompleted();
        assertTrue(actual);
    }

    @DisplayName("sut은 각각의 게임마다 정답을 생성해야 한다.")
    @ParameterizedTest
    @ValueSource(strings = "100, 10, 1")
    void sut_generates_answer_for_each_game(String source) {
        int[] answers = stream(source.split(","))
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .toArray();
        var sut = new AppModel(new PositiveIntegerGeneratorStub(answers));
        for (int answer : answers) {
            sut.processInput("1");
            sut.flushOutput();
            sut.processInput(valueOf(answer));
        }
        String actual = sut.flushOutput();
        assertThat(actual).startsWith("Correct! ");
    }

    @DisplayName("sut은 멀티플레이어 게임이 셋업될 때 알맞은 메시지를 출력하여야 한다.")
    @Test
    void sut_correctly_prints_multiplayer_game_setup_message() {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.flushOutput();
        sut.processInput("2");

        String actual = sut.flushOutput();
        String expected = """
                Multiplayer game
                Enter player names separated with commas:\040""";
        assertEquals(expected, actual);
    }

    @DisplayName("sut은 멀티플레이어 게임이 시작할 때 알맞은 메시지를 출력하여야 한다.")
    @Test
    void sut_correctly_prints_multiplayer_game_start_message() {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.processInput("2");
        sut.flushOutput();
        sut.processInput("Foo, Bar");

        String actual = sut.flushOutput();
        assertThat(actual).startsWith("I'm thinking of a number between 1 and 100.\n");
    }

    @DisplayName("sut은 멀티플레이어 모드일 때 첫 번째 사용자의 이름을 알맞게 출력해야 한다.")
    @ParameterizedTest(name = "player1 : {0}, player2 : {1}, player3 : {2}")
    @CsvSource({"Foo, Bar, Baz", "Bar, Baz, Foo", "Baz, Foo, Bar"})
    void sut_correctly_prompts_first_player_name(String player1, String player2, String player3) {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.processInput("2");
        sut.flushOutput();
        sut.processInput(join(", ", player1, player2, player3));

        String actual = sut.flushOutput();
        String expected = "Enter %s's guess: ".formatted(player1);
        assertThat(actual).endsWith(expected);
    }

    @DisplayName("sut은 멀티플레이어 모드일 때 두 번째 사용자의 이름을 알맞게 출력해야 한다.")
    @ParameterizedTest(name = "player1 : {0}, player2 : {1}, player3 : {2}")
    @CsvSource({"Foo, Bar, Baz", "Bar, Baz, Foo", "Baz, Foo, Bar"})
    void sut_correctly_prompts_second_player_name(String player1, String player2, String player3) {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.processInput("2");
        sut.processInput(String.join(", ", player1, player2, player3));
        sut.flushOutput();
        sut.processInput("10");

        String actual = sut.flushOutput();

        String expected = "Enter %s's guess: ".formatted(player2);
        assertThat(actual).endsWith(expected);
    }


    @DisplayName("sut은 멀티플레이어 모드일 때 세 번째 사용자의 이름을 알맞게 출력해야 한다.")
    @ParameterizedTest(name = "player1 : {0}, player2 : {1}, player3 : {2}")
    @CsvSource({"Foo, Bar, Baz", "Bar, Baz, Foo", "Baz, Foo, Bar"})
    void sut_correctly_prompts_third_player_name(String player1, String player2, String player3) {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.processInput("2");
        sut.processInput(String.join(", ", player1, player2, player3));
        sut.processInput("90");
        sut.flushOutput();
        sut.processInput("90");

        String actual = sut.flushOutput();
        String expected = "Enter %s's guess: ".formatted(player3);
        assertThat(actual).endsWith(expected);
    }

    @DisplayName("sut은 멀티플레이어 모드일 때 계속 오답 입력 시 첫 번째 사용자의 입력으로 돌아와야 한다.")
    @ParameterizedTest
    @CsvSource({"Foo, Bar, Baz", "Bar, Baz, Foo", "Baz, Foo, Bar"})
    void sut_correctly_rounds_players(String player1, String player2, String player3) {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.processInput("2");
        sut.processInput(String.join(", ", player1, player2, player3));
        sut.processInput("10");
        sut.processInput("10");
        sut.flushOutput();
        sut.processInput("10");

        String actual = sut.flushOutput();
        String expected = "Enter %s's guess: ".formatted(player1);
        assertThat(actual).endsWith(expected);
    }

    @DisplayName("sut은 멀티플레이어 모드에서 정답보다 낮은 오답을 입력 시 마지막 사용자의 이름과 함께 정답보다 낮다는 메시지를 출력해야 한다.")
    @ParameterizedTest(name = "answer = {0}, guess = {1}, fails = {2}, lastPlayer = {3}")
    @CsvSource({"50, 40, 1, Foo", "30, 29, 2, Bar"})
    void sut_correctly_prints_too_low_message_in_multiplayer_game(int answer, int guess, int fails, String lastPlayer) {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(answer));
        sut.processInput("2");
        sut.processInput("Foo, Bar, Baz");
        for (int i = 0; i < fails - 1; i++) {
            sut.processInput(String.valueOf(guess));
        }

        sut.flushOutput();
        sut.processInput(String.valueOf(guess));

        String actual = sut.flushOutput();
        String expected = "%s's guess is too low.\n".formatted(lastPlayer);

        assertThat(actual).startsWith(expected);
    }

    @DisplayName("sut은 멀티플레이어 모드에서 정답보다 높은 오답을 입력 시 마지막 사용자의 이름과 함께 정답보다 높다는 메시지를 출력해야 한다.")
    @ParameterizedTest(name = "answer = {0}, guess = {1}, fails = {2}, lastPlayer = {3}")
    @CsvSource({"50, 60, 1, Foo", "9, 81, 2, Bar"})
    void sut_correctly_prints_too_high_message_in_multiplayer_game(int answer, int guess, int fails, String lastPlayer) {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(answer));
        sut.processInput("2");
        sut.processInput("Foo, Bar, Baz");
        for (int i = 0; i < fails - 1; i++) {
            sut.processInput(String.valueOf(guess));
        }

        sut.flushOutput();
        sut.processInput(String.valueOf(guess));

        String actual = sut.flushOutput();
        String expected = "%s's guess is too high.".formatted(lastPlayer);
        assertThat(actual).startsWith(expected);
    }

    @DisplayName("sut은 멀티플레이어 모드에서 정답을 입력 시 알맞은 메시지를 출력해야 한다.")
    @ParameterizedTest(name = "answer = {0}")
    @ValueSource(ints = {1, 10, 100})
    void sut_correctly_prints_correct_message_in_multiplayer_game(int answer) {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(answer));
        sut.processInput("2");
        sut.processInput("Foo, Bar, Baz");
        sut.flushOutput();
        int guess = answer;
        sut.processInput(String.valueOf(guess));

        String actual = sut.flushOutput();

        assertThat(actual).startsWith("Correct! ");
    }

    @DisplayName("sut은 멀티플레이어 모드에서 정답을 입력 시 알맞은 메시지와 함께 승자를 출력해야 한다.")
    @ParameterizedTest(name = "fails = {0}, winner = {1}")
    @CsvSource({"0, Foo", "1, Bar", "2, Baz", "99, Foo", "100, Bar"})
    void sut_correctly_prints_winner_if_multiplayer_game_finished(int fails, String winner) {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.processInput("2");
        sut.processInput("Foo, Bar, Baz");
        for (int i = 0; i < fails; i++) {
            sut.processInput("30");
        }
        sut.flushOutput();
        sut.processInput("50");

        String actual = sut.flushOutput();
        String expected = "%s wins.\n".formatted(winner);
        assertThat(actual).contains(expected);
    }

    @DisplayName("sut은 멀티플레이어 모드에서 정답으로 인하여 게임이 종료될 시에 모드 선택 메시지가 출력되어야 한다.")
    @Test
    void sut_prints_select_mode_message_if_multiplayer_game_finished() {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        sut.processInput("2");
        sut.processInput("Foo, Bar, Baz");
        sut.flushOutput();
        sut.processInput("50");

        String actual = sut.flushOutput();
        assertThat(actual).endsWith("""
                1: Single player game
                2: Multiplayer game
                3: Exit
                Enter selection:\040""");
    }

    @DisplayName("sut은 멀티플레이어 모드에서 정답으로 인하여 게임이 종료될 시에 모드 선택으로 돌아가야 한다.")
    @Test
    void sut_returns_to_mode_selection_if_multiplayer_game_finished() {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));

        sut.processInput("2");
        sut.processInput("Foo, Bar, Baz");
        sut.processInput("20");
        sut.processInput("50");
        sut.processInput("3");

        boolean actual = sut.isCompleted();
        assertTrue(actual);
    }

}