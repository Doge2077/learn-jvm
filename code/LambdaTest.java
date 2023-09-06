public class LambdaTest {

    public static interface Test {
        String showTestNumber(Integer param);
    }

    public static void main(String[] args) {
        Test test = param -> "Test number is " + param;
        System.out.println(test.showTestNumber(114514));
    }
}