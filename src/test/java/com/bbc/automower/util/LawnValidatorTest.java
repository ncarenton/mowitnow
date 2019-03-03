package com.bbc.automower.util;

import com.bbc.automower.domain.Lawn;
import com.bbc.automower.domain.Mower;
import com.bbc.automower.error.Error;
import com.bbc.automower.error.Error.InvalidInt;
import com.bbc.automower.error.Error.InvalidLength;
import com.bbc.automower.error.Error.InvalidOrientation;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static com.bbc.automower.Constants.*;
import static com.bbc.automower.enumeration.Instruction.*;
import static com.bbc.automower.enumeration.Orientation.EAST;
import static com.bbc.automower.enumeration.Orientation.NORTH;
import static io.vavr.API.LinkedSet;
import static io.vavr.API.List;
import static org.apache.commons.io.IOUtils.LINE_SEPARATOR;
import static org.junit.Assert.*;

public class LawnValidatorTest {

    //-------------------------------------------------------------------------
    // Initialization
    //-------------------------------------------------------------------------

    private final LawnValidator validator = new LawnValidator();
    private final ClassLoader classLoader = getClass().getClassLoader();


    //-------------------------------------------------------------------------    
    // Tests
    //-------------------------------------------------------------------------

    @Test
    public void should_validate_lawn() {
        // Given
        List<String> lines = readLines(GOOD_FILE_PATH);
        Lawn lawn = expectedLawn();

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = validator.validate(lines);

        // Then
        assertTrue(errorsOrAutomower.isValid());
        assertSame(lawn, errorsOrAutomower.get());
    }

    @Test
    public void should_be_invalid_when_too_many_elements_in_mower_line() {
        //Given
        List<String> lines = readLines(BAD_MOWER_FILE_PATH_TOO_MANY_ELTS);

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = validator.validate(lines);

        // Then
        assertTrue(errorsOrAutomower.isInvalid());
        assertNotNull(errorsOrAutomower.getError());
        assertEquals(errorsOrAutomower.getError().size(), 1);
        assertEquals(errorsOrAutomower.getError().get(0), InvalidLength.of(2, 4, 3));
    }

    @Test
    public void should_be_invalid_when_bad_mower_line() {
        //Given
        List<String> lines = readLines(BAD_MOWER_FILE_PATH);

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = validator.validate(lines);

        // Then
        assertTrue(errorsOrAutomower.isInvalid());
        assertNotNull(errorsOrAutomower.getError());
        assertEquals(errorsOrAutomower.getError().size(), 3);
        assertEquals(errorsOrAutomower.getError().get(0), InvalidInt.of(2, "X"));
        assertEquals(errorsOrAutomower.getError().get(1), InvalidInt.of(2, "Y"));
        assertEquals(errorsOrAutomower.getError().get(2), InvalidOrientation.of(2, "Z"));
    }

    @Test
    public void should_be_invalid_when_bad_move_line() {
        //Given
        List<String> lines = readLines(BAD_MOVE_FILE_PATH);

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = validator.validate(lines);

        // Then
        assertTrue(errorsOrAutomower.isInvalid());
        assertNotNull(errorsOrAutomower.getError());
        assertEquals(errorsOrAutomower.getError().size(), 1);
        assertEquals(errorsOrAutomower.getError().get(0), Error.InvalidInstruction.of(3, 'A'));
    }

    @Test
    public void should_be_invalid_when_bad_lawn_line() {
        //Given
        List<String> lines = readLines(BAD_LAWN_FILE_PATH);

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = validator.validate(lines);

        // Then
        assertTrue(errorsOrAutomower.isInvalid());
        assertNotNull(errorsOrAutomower.getError());
        assertEquals(errorsOrAutomower.getError().size(), 1);
        assertEquals(errorsOrAutomower.getError().get(0), InvalidLength.of(1, 3, 2));
    }

    private void assertSame(final Lawn lawn1, final Lawn lawn2) {
        assertEquals(lawn1.getHeight(), lawn2.getHeight());
        assertEquals(lawn1.getWidth(), lawn2.getWidth());
        assertEquals(lawn1.getMowers().size(), lawn2.getMowers().size());

        lawn1.getMowers()
                .zipWithIndex()
                .forEach(t -> {
                    Mower mower = lawn2.getMowers().toList().get(t._2);
                    assertEquals(mower.getInstructions(), t._1.getInstructions());
                    assertEquals(mower.getPosition(), t._1.getPosition());
                    assertEquals(mower.getOrientation(), t._1.getOrientation());
                });
    }

    private List<String> readLines(final String file) {
        return Try.withResources(() -> classLoader.getResourceAsStream(file))
                .of(inputStream -> List(
                        IOUtils.toString(inputStream)
                                .split(LINE_SEPARATOR)))
                .getOrElseThrow(() -> new RuntimeException("Error reading file " + file));
    }

    private Lawn expectedLawn() {
        return Lawn.of(5, 5)
                .initialize(
                        LinkedSet(
                                Mower
                                        .of(1, 2, NORTH)
                                        .instructions(List(TURN_LEFT, MOVE_FORWARD, TURN_LEFT, MOVE_FORWARD, TURN_LEFT, MOVE_FORWARD, TURN_LEFT, MOVE_FORWARD, MOVE_FORWARD)),
                                Mower
                                        .of(3, 3, EAST)
                                        .instructions(List(MOVE_FORWARD, MOVE_FORWARD, TURN_RIGHT, MOVE_FORWARD, MOVE_FORWARD, TURN_RIGHT, MOVE_FORWARD, TURN_RIGHT, TURN_RIGHT, MOVE_FORWARD))
                        ));
    }

}
