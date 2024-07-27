import io.qameta.allure.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Greeting Service Tests")
@Feature("Greeting Functionality")
public class GreetingServiceTest {

    @Test
    @Story("Greet")
    @Description("Test the greet method of the GreetingService")
    @Severity(SeverityLevel.CRITICAL)
    @Link(name = "GreetingService Greet", url = "http://example.com")
    public void testGreet() {
        GreetingService greetingService = new GreetingService();

        step("Greet World", () -> {
            assertEquals("Hello, World!", greetingService.greet("World"));
        });

        step("Greet Alice", () -> {
            assertEquals("Hello, Alice!", greetingService.greet("Alice"));
        });

        step("Greet Bob", () -> {
            assertEquals("Hello, Bob!", greetingService.greet("Bob"));
        });
    }

    @Step("{0}")
    public void step(String message, Runnable code) {
        code.run();
    }
}
