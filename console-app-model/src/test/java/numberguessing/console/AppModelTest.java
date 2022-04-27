package numberguessing.console;

import numberguessing.PositiveIntegerGeneratorStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class AppModelTest {

    @DisplayName("AppModel 클래스가 초기화 되고 나면 isCompleted는 false를 반환해야 한다.")
    @Test
    void sut_is_incompleted_when_it_is_initialized() {
        var sut = new AppModel(new PositiveIntegerGeneratorStub(50));
        boolean actual = sut.isCompleted();
        assertFalse(actual);
    }

}