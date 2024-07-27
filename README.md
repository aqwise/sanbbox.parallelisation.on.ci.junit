### README.md

# CI with Parallel Test Execution and Allure Reporting

This project demonstrates how to set up a continuous integration (CI) pipeline using GitHub Actions for parallel test execution and Allure reporting. The solution includes steps to generate and publish Allure reports to GitHub Pages.

## Setup Instructions

### Prerequisites

- A GitHub repository
- Maven installed locally for building the project
- JDK 21

### Creating a Personal Access Token (PAT)

To enable GitHub Actions to publish to GitHub Pages, you need to create a Personal Access Token (PAT) with the necessary permissions.

1. **Generate a Personal Access Token:**
    - Go to [GitHub Settings](https://github.com/settings/tokens).
    - Click on "Generate new token".
    - Select the scopes `repo` and `workflow`.
    - Click "Generate token" and copy the generated token.

2. **Add the PAT as a Secret:**
    - Go to your GitHub repository.
    - Click on the `Settings` tab.
    - In the sidebar, under `Secrets and variables`, click on `Actions`.
    - Click on `New repository secret`.
    - Add a name for your secret (e.g., `GH_PAGES_TOKEN`).
    - Paste your PAT in the `Value` field.
    - Click `Add secret`.

### Project Structure

The project includes a simple Java project with Maven, JUnit tests, and Allure reporting integration.

```
.
├── .github
│   └── workflows
│       └── ci.yml
├── scripts
│   └── divide_tests.sh
├── src
│   └── main
│       └── java
│           └── com
│               └── example
│                   ├── Calculator.java
│                   ├── GreetingService.java
│                   ├── Main.java
│                   ├── StringUtils.java
│                   └── TestClassSorter.java
│   └── test
│       └── java
│           └── com
│               └── example
│                   ├── CalculatorTest.java
│                   ├── GreetingServiceTest.java
│                   └── StringUtilsTest.java
├── .gitignore
├── pom.xml
└── README.md
```

### `pom.xml` Configuration

Ensure your `pom.xml` is configured with the necessary dependencies and plugins for JUnit, Allure, and the Maven Surefire Plugin.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-app</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.7.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.7.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.qameta.allure</groupId>
            <artifactId>allure-junit5</artifactId>
            <version>2.13.9</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <encoding>${project.build.sourceEncoding}</encoding>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.22.2</version>
                <configuration>
                    <failIfNoTests>false</failIfNoTests>
                    <properties>
                        <property>
                            <name>listener</name>
                            <value>io.qameta.allure.junit5.AllureJunit5</value>
                        </property>
                    </properties>
                </configuration>
            </plugin>
            <plugin>
                <groupId>io.qameta.allure</groupId>
                <artifactId>allure-maven</artifactId>
                <version>2.13.9</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>report</goal>
                            <goal>aggregate</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.0.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>java</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <mainClass>com.example.TestClassSorter</mainClass>
                    <arguments>
                        <argument>target/test-classes</argument>
                    </arguments>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### GitHub Actions Workflow

Create the workflow file `.github/workflows/ci.yml` to define the CI pipeline:

```yaml
name: CI

on: [push, pull_request]

jobs:
  test-job-unix:
    runs-on: ubuntu-latest

    strategy:
      matrix:
        chunk: [1, 2]

    steps:
      - name: Checkout repository
        uses: actions/checkout@v2

      - name: Set up JDK 21
        uses: actions/setup-java@v2
        with:
          distribution: 'temurin'
          java-version: '21'

      - name: Cache Maven packages
        uses: actions/cache@v2
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: |
            ${{ runner.os }}-maven-

      - name: Build with Maven
        run: mvn --batch-mode --update-snapshots clean install -DskipTests

      - name: Make divide_tests.sh executable
        run: chmod +x scripts/divide_tests.sh

      - name: Run tests for chunk ${{ matrix.chunk }}
        run: ./scripts/divide_tests.sh 2 ${{ matrix.chunk }}

      - name: Archive Allure Results
        uses: actions/upload-artifact@v2
        with:
          name: allure-results-${{ matrix.chunk }}
          path: target/allure-results

  merge-and-publish:
    runs-on: ubuntu-latest
    needs: test-job-unix
    steps:
      - name: Checkout repository
        uses: actions/checkout@v2

      - name: Download Allure Results for Chunk 1
        uses: actions/download-artifact@v2
        with:
          name: allure-results-1
          path: allure-results/1

      - name: Download Allure Results for Chunk 2
        uses: actions/download-artifact@v2
        with:
          name: allure-results-2
          path: allure-results/2

      - name: Merge Allure Results
        run: |
          mkdir -p target/allure-results
          cp -r allure-results/1/* target/allure-results/
          cp -r allure-results/2/* target/allure-results/

      - name: Generate Allure Report
        run: mvn allure:aggregate -Dallure.results.directory=target/allure-results

      - name: Build Allure Report
        run: mvn allure:report -Dallure.results.directory=target/allure-results -Dallure.report.directory=target/site/allure-maven-plugin

      - name: Archive Allure Report
        uses: actions/upload-artifact@v2
        with:
          name: allure-report
          path: target/site/allure-maven-plugin

      - name: Deploy Allure Report
        uses: peaceiris/actions-gh-pages@v3
        with:
          github_token: ${{ secrets.GH_PAGES_TOKEN }}
          publish_dir: ./target/site/allure-maven-plugin
```

### Scripts

Create the `divide_tests.sh` script to divide test classes into chunks:

```sh
#!/bin/bash

# Number of chunks
NUM_CHUNKS=$1

# Chunk index (1-based)
CHUNK_INDEX=$2

# Get the list of test classes
TEST_CLASSES=$(mvn -q exec:java -Dexec.mainClass=com.example.TestClassSorter)

# Debug: Print the raw test classes output
echo "Raw test

 classes output:"
echo "$TEST_CLASSES"

# Convert newline-separated string to array
IFS=$'\n' read -rd '' -a TEST_CLASSES_ARRAY <<<"$TEST_CLASSES"

# Total number of test classes
TOTAL_TESTS=${#TEST_CLASSES_ARRAY[@]}

# Number of tests per chunk
TESTS_PER_CHUNK=$(( (TOTAL_TESTS + NUM_CHUNKS - 1) / NUM_CHUNKS ))

# Calculate start and end index for this chunk
START_INDEX=$(( (CHUNK_INDEX - 1) * TESTS_PER_CHUNK ))
END_INDEX=$(( START_INDEX + TESTS_PER_CHUNK - 1 ))

# Ensure end index does not exceed total number of tests
if [ $END_INDEX -ge $TOTAL_TESTS ]; then
  END_INDEX=$(( TOTAL_TESTS - 1 ))
fi

# Extract the test classes for this chunk
CHUNK_TESTS=(${TEST_CLASSES_ARRAY[@]:$START_INDEX:$(( END_INDEX - START_INDEX + 1 ))})

# Join test classes with comma for Maven command
TEST_CLASSES_STR=$(IFS=, ; echo "${CHUNK_TESTS[*]}")

# Debug messages
echo "Total test classes found: ${TEST_CLASSES_ARRAY[*]}"
echo "Test classes for chunk $CHUNK_INDEX: ${CHUNK_TESTS[*]}"
echo "Running Maven command: mvn test -Dtest=${TEST_CLASSES_STR}"

# Run the tests for this chunk
mvn test -Dtest="${TEST_CLASSES_STR}"
```

### Conclusion

This setup ensures that your CI pipeline can run tests in parallel, aggregate the results, generate an Allure report, and publish the report to GitHub Pages. Follow the instructions above to configure your repository and secrets correctly. If you encounter any issues, double-check the configuration and ensure the PAT has the correct permissions.