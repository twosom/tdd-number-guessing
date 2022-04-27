package numberguessing;

public class PositiveIntegerGeneratorStub implements PositiveIntegerGenerator {


    private final int[] numbers;
    private int index;

    public PositiveIntegerGeneratorStub(int... numbers) {
        this.numbers = numbers;
        this.index = 0;
    }

    @Override
    public int generateLessThanOrEqualToHundred() {
        int number = numbers[index];
        index = (index + 1) % this.numbers.length;  // numbers 의 길이 이상만큼 올라가지 않도록 수정
        return number;
    }
}
