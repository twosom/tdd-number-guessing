package numberguessing.console;

import numberguessing.PositiveIntegerGeneratorStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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
        sut.processInput(String.valueOf(guess));

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
        sut.processInput(String.valueOf(guess));

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
        sut.processInput(String.valueOf(guess));

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
            sut.processInput(String.valueOf(answer));
        }
        String actual = sut.flushOutput();
        assertThat(actual).startsWith("Correct! ");
    }


}