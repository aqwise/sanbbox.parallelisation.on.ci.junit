import io.qameta.allure.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Calculator Tests")
@Feature("Arithmetic Operations")
public class CalculatorTest {

    @Test
    @Story("Addition")
    @Description("Test the addition operation of the Calculator")
    @Severity(SeverityLevel.CRITICAL)
    @Link(name = "Calculator Add", url = "http://example.com")
    public void testAdd() {
        Calculator calculator = new Calculator();

        step("Adding 2 and 3", () -> {
            assertEquals(5, calculator.add(2, 3));
        });

        step("Adding -2 and 1", () -> {
            assertEquals(-1, calculator.add(-2, 1));
        });

        step("Adding 0 and 0", () -> {
            assertEquals(0, calculator.add(0, 0));
        });
    }

    @Step("{0}")
    public void step(String message, Runnable code) {
        code.run();
    }
}
