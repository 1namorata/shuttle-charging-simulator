package com.merickilic.shuttle;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EXAMPLE TESTS - patterns to copy from.
 * These scenarios are NOT in the test plan. Run this class once to check that
 * JUnit is set up correctly, then delete it before submitting.
 */
class ExampleTest {

    private PrintStream silent;

    @BeforeEach
    void setUp() {
        // Simulator prints every assignment; send it nowhere so the console stays clean
        silent = new PrintStream(OutputStream.nullOutputStream());
    }

    // Pattern 1: Arrange - Act - Assert, several checks grouped with assertAll
    @Test
    @DisplayName("Example: availableTime grows over two back-to-back assignments")
    void consecutiveAssignmentsAddUp() {
        Charger charger = new Charger(0);                    // Arrange
        Shuttle first = new Shuttle(1, 1, 0, 4);
        Shuttle second = new Shuttle(2, 1, 0, 6);

        charger.assignShuttle(first, 0);                     // Act
        charger.assignShuttle(second, 4);

        assertAll("charger after two assignments",           // Assert
            () -> assertEquals(10, charger.availableTime),
            () -> assertEquals(2, charger.currentShuttle.id),
            () -> assertFalse(charger.isAvailable(9)));
    }

    // Pattern 2: expecting an exception
    @Test
    @DisplayName("Example: Simulator cannot be built from a null list")
    void nullListThrows() {
        assertThrows(NullPointerException.class, () -> new Simulator(null));
    }

    // Pattern 3: parameterized test - one method, several inputs
    @ParameterizedTest(name = "new charger is free at minute {0}")
    @ValueSource(ints = {1, 50, 1000})
    void newChargerFreeAtAnyLaterTime(int time) {
        assertTrue(new Charger(0).isAvailable(time));
    }

    // Pattern 4: capturing console output
    @Test
    @DisplayName("Example: second shuttle's printed wait is 5 minutes")
    void printedWaitTime() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Simulator sim = new Simulator(
            List.of(new Shuttle(1, 2, 0, 5), new Shuttle(2, 1, 0, 5)),
            new PrintStream(buffer));

        sim.simulateWithKChargers(1);

        String output = buffer.toString();
        assertTrue(output.contains("takes shuttle 2 at minute 5 (wait: 5 mins)"), output);
    }

    // Pattern 5: performance / time limit
    @Test
    @DisplayName("Example: 1000 shuttles are simulated in under 2 seconds")
    void largeInputIsFast() {
        List<Shuttle> many = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            many.add(new Shuttle(i, i % 5, i, 3));
        }
        Simulator sim = new Simulator(many, silent);

        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> sim.simulateWithKChargers(1));
    }
}
