import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreetingServiceTest {

    @Test
    public void testGreet() {
        GreetingService greetingService = new GreetingService();
        assertEquals("Hello, World!", greetingService.greet("World"));
        assertEquals("Hello, Alice!", greetingService.greet("Alice"));
        assertEquals("Hello, Bob!", greetingService.greet("Bob"));
    }
}
