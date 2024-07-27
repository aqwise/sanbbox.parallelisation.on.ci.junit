import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestClassSorter {

    public static void main(String[] args) throws IOException {
        String testClassesDir = args.length > 0 ? args[0] : "target/test-classes";
        System.out.println("Scanning directory: " + testClassesDir);

        List<String> testClasses = getTestClasses(testClassesDir);

        System.out.println("Total test classes found: " + testClasses.size());
        System.out.println("Sorted test classes:");
        testClasses.forEach(System.out::println);
    }

    private static List<String> getTestClasses(String testClassesDir) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(testClassesDir))) {
            List<String> testClassPaths = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(path -> path.endsWith("Test.class")).toList();

            System.out.println("Test class files found:");
            testClassPaths.forEach(System.out::println);

            return testClassPaths.stream()
                    .map(TestClassSorter::convertToClassName)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    private static String convertToClassName(String filePath) {
        String relativePath = new File(filePath).getPath().replace("\\", "/");
        String className = relativePath
                .replace("target/test-classes/", "")
                .replace(".class", "")
                .replace("/", ".");
        System.out.println("Converted " + filePath + " to " + className);
        return className;
    }
}
