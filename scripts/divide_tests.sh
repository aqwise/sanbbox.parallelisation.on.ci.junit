#!/bin/bash

# Directory where test classes are located
TEST_DIR="src/test/java/com/example"

# Find all test classes
TEST_CLASSES=($(find $TEST_DIR -name "*Test.java" -exec basename {} .java \;))

# Number of chunks
NUM_CHUNKS=$1

# Chunk index (1-based)
CHUNK_INDEX=$2

# Total number of test classes
TOTAL_TESTS=${#TEST_CLASSES[@]}

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
CHUNK_TESTS=${TEST_CLASSES[@]:$START_INDEX:$(( END_INDEX - START_INDEX + 1 ))}

# Join test classes with comma for Maven command
TEST_CLASSES_STR=$(IFS=, ; echo "${CHUNK_TESTS[*]}")

# Run the tests for this chunk
mvn test -Dtest=$TEST_CLASSES_STR
