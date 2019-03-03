package com.bbc.automower;

import org.junit.Before;
import org.junit.Test;

import static com.bbc.automower.Constants.BAD_LAWN_FILE_PATH;
import static com.bbc.automower.Constants.GOOD_FILE_PATH;
import static com.bbc.automower.Main.main;
import static com.bbc.automower.TestAppender.clear;
import static com.bbc.automower.TestAppender.messages;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        assertNotNull(messages);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "Line 1: the line contains 3 elements instead of 2");
    }

    private void assertThatMowersHavePrintedTheirPositions() {
        assertNotNull(messages);
        assertEquals(messages.size(), 2);
        assertEquals(messages.get(0), "1 3 N");
        assertEquals(messages.get(1), "5 1 E");
    }

}
