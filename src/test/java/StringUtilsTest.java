import io.qameta.allure.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("String Utils Tests")
@Feature("Palindrome Functionality")
public class StringUtilsTest {

    @Test
    @Story("Check Palindrome")
    @Description("Test the isPalindrome method of the StringUtils")
    @Severity(SeverityLevel.CRITICAL)
    @Link(name = "StringUtils IsPalindrome", url = "http://example.com")
    public void testIsPalindrome() {
        StringUtils stringUtils = new StringUtils();

        step("Check if 'madam' is a palindrome", () -> {
            assertTrue(stringUtils.isPalindrome("madam"));
        });

        step("Check if 'A man a plan a canal Panama' is a palindrome", () -> {
            assertTrue(stringUtils.isPalindrome("A man a plan a canal Panama"));
        });

        step("Check if 'hello' is not a palindrome", () -> {
            assertFalse(stringUtils.isPalindrome("hello"));
        });

        step("Check if null is not a palindrome", () -> {
            assertFalse(stringUtils.isPalindrome(null));
        });
    }

    @Step("{0}")
    public void step(String message, Runnable code) {
        code.run();
    }
}
