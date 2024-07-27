
public class Main {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        System.out.println("Sum: " + calculator.add(2, 3));

        GreetingService greetingService = new GreetingService();
        System.out.println(greetingService.greet("World"));

        StringUtils stringUtils = new StringUtils();
        System.out.println("Is 'madam' a palindrome? " + stringUtils.isPalindrome("madam"));
    }
}
