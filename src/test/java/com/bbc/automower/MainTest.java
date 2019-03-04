package com.bbc.automower;

import org.junit.Before;
import org.junit.Test;

import static com.bbc.automower.Constants.BAD_LAWN_FILE_PATH;
import static com.bbc.automower.Constants.GOOD_FILE_PATH;
import static com.bbc.automower.Main.main;
import static com.bbc.automower.TestAppender.clear;
import static com.bbc.automower.TestAppender.messages;
import static org.assertj.core.api.Assertions.assertThat;

public class MainTest {

    @Before
    public void setUp() {
        clear();
    }

    @Test
    public void should_execute_automower_program() {
        // Given
        String[] args = new String[]{};

        // Action
        main(args);

        // Asserts
        assertThatMowersHavePrintedTheirPositions();
    }

    @Test
    public void should_execute_automower_program_with_file() {
        // Given
        String[] args = new String[] {GOOD_FILE_PATH};

        // Action
        main(args);

        // Asserts
        assertThatMowersHavePrintedTheirPositions();
    }

    @Test
    public void should_print_errors() {
        // Given
        String[] args = new String[] {BAD_LAWN_FILE_PATH};

        // Action
        main(args);

        // Asserts
        assertThat(messages)
                .isNotNull()
                .hasSize(1)
                .containsExactly("Line 1: the line contains 3 elements instead of 2");
    }

    private void assertThatMowersHavePrintedTheirPositions() {
        assertThat(messages)
                .isNotNull()
                .hasSize(2)
                .containsExactly("1 3 N", "5 1 E");
    }

}
