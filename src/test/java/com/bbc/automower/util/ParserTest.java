package com.bbc.automower.util;

import com.bbc.automower.domain.Lawn;
import com.bbc.automower.domain.Mower;
import com.bbc.automower.error.Error;
import com.bbc.automower.error.Error.FileNotFound;
import com.bbc.automower.error.Error.InvalidInt;
import com.bbc.automower.error.Error.InvalidLength;
import com.bbc.automower.error.Error.InvalidOrientation;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.junit.Test;

import static com.bbc.automower.enumeration.Instruction.*;
import static com.bbc.automower.enumeration.Orientation.EAST;
import static com.bbc.automower.enumeration.Orientation.NORTH;
import static io.vavr.API.LinkedSet;
import static io.vavr.API.List;
import static org.junit.Assert.*;

public class ParserTest {

    //-------------------------------------------------------------------------    
    // Variables
    //-------------------------------------------------------------------------

    public final static String GOOD_FILE_PATH = "automower.txt";
    private final static String BAD_MOWER_FILE_PATH = "automower-bad-mower.txt";
    private final static String BAD_MOWER_FILE_PATH_TOO_MANY_ELTS = "automower-bad-mower-too-many-elements.txt";
    private final static String BAD_MOVE_FILE_PATH = "automower-bad-move.txt";
    private final static String BAD_LAWN_FILE_PATH = "automower-bad-lawn.txt";


    //-------------------------------------------------------------------------
    // Initialization
    //-------------------------------------------------------------------------

    private final Parser parser = new Parser();


    //-------------------------------------------------------------------------    
    // Tests
    //-------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void sould_throw_illegalargumentexception_when_filename_is_null() {
        // Action
        parser.parse(null);
    }

    @Test
    public void should_be_invalid_when_file_not_found() {
        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = parser.parse("src/main/resources/META-INF/config/dzedze");

        // Then
        assertTrue(errorsOrAutomower.isInvalid());
        assertNotNull(errorsOrAutomower.getError());
        assertEquals(errorsOrAutomower.getError().size(), 1);
        assertEquals(errorsOrAutomower.getError().get(0), FileNotFound.of("src/main/resources/META-INF/config/dzedze"));
    }

    @Test
    public void should_parse_file_in_file_system() {
        // Given
        Lawn lawn = expectedLawn();

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = parser.parse("src/main/resources/META-INF/config/" + GOOD_FILE_PATH);

        // Then
        assertTrue(errorsOrAutomower.isValid());
        assertSame(lawn, errorsOrAutomower.get());
    }

    @Test
    public void should_parse_file_in_classpath() {
        // Given
        Lawn lawn = expectedLawn();

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = parser.parse(GOOD_FILE_PATH);

        // Then
        assertTrue(errorsOrAutomower.isValid());
        assertSame(lawn, errorsOrAutomower.get());
    }

    @Test
    public void should_be_invalid_when_file_with_too_many_elements_in_mower_line() {
        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = parser.parse(BAD_MOWER_FILE_PATH_TOO_MANY_ELTS);

        // Then
        assertTrue(errorsOrAutomower.isInvalid());
        assertNotNull(errorsOrAutomower.getError());
        assertEquals(errorsOrAutomower.getError().size(), 1);
        assertEquals(errorsOrAutomower.getError().get(0), InvalidLength.of(2, 4, 3));
    }

    @Test
    public void should_be_invalid_when_file_with_bad_mower_line() {
        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = parser.parse(BAD_MOWER_FILE_PATH);

        // Then
        assertTrue(errorsOrAutomower.isInvalid());
        assertNotNull(errorsOrAutomower.getError());
        assertEquals(errorsOrAutomower.getError().size(), 3);
        assertEquals(errorsOrAutomower.getError().get(0), InvalidInt.of(2, "X"));
        assertEquals(errorsOrAutomower.getError().get(1), InvalidInt.of(2, "Y"));
        assertEquals(errorsOrAutomower.getError().get(2), InvalidOrientation.of(2, "Z"));
    }

    @Test
    public void should_be_invalid_when_file_with_bad_move_line() {
        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = parser.parse(BAD_MOVE_FILE_PATH);

        // Then
        assertTrue(errorsOrAutomower.isInvalid());
        assertNotNull(errorsOrAutomower.getError());
        assertEquals(errorsOrAutomower.getError().size(), 1);
        assertEquals(errorsOrAutomower.getError().get(0), Error.InvalidInstruction.of(3, 'A'));
    }

    @Test
    public void should_be_invalid_when_file_with_bad_lawn_line() {
        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = parser.parse(BAD_LAWN_FILE_PATH);

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
