package com.bbc.automower.util;

import com.bbc.automower.domain.Lawn;
import com.bbc.automower.domain.Mower;
import com.bbc.automower.error.Error;
import com.bbc.automower.error.Error.InvalidInstruction;
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
import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(errorsOrAutomower.isValid()).isTrue();
        assertSame(errorsOrAutomower.get(), lawn);
    }

    @Test
    public void should_be_invalid_when_too_many_elements_in_mower_line() {
        //Given
        List<String> lines = readLines(BAD_MOWER_FILE_PATH_TOO_MANY_ELTS);

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = validator.validate(lines);

        // Then
        assertThat(errorsOrAutomower.isInvalid()).isTrue();
        assertThat(errorsOrAutomower.getError())
                .isNotNull()
                .hasSize(1)
                .containsExactly(InvalidLength.of(2, 4, 3));
    }

    @Test
    public void should_be_invalid_when_bad_mower_line() {
        //Given
        List<String> lines = readLines(BAD_MOWER_FILE_PATH);

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = validator.validate(lines);

        // Then
        assertThat(errorsOrAutomower.isInvalid()).isTrue();
        assertThat(errorsOrAutomower.getError())
                .isNotNull()
                .hasSize(3)
                .containsExactly(
                        InvalidInt.of(2, "X"),
                        InvalidInt.of(2, "Y"),
                        InvalidOrientation.of(2, "Z"));
    }

    @Test
    public void should_be_invalid_when_bad_move_line() {
        //Given
        List<String> lines = readLines(BAD_MOVE_FILE_PATH);

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = validator.validate(lines);

        // Then
        assertThat(errorsOrAutomower.isInvalid()).isTrue();
        assertThat(errorsOrAutomower.getError())
                .isNotNull()
                .hasSize(1)
                .containsExactly(InvalidInstruction.of(3, 'A'));
    }

    @Test
    public void should_be_invalid_when_bad_lawn_line() {
        //Given
        List<String> lines = readLines(BAD_LAWN_FILE_PATH);

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = validator.validate(lines);

        // Then
        assertThat(errorsOrAutomower.isInvalid()).isTrue();
        assertThat(errorsOrAutomower.getError())
                .isNotNull()
                .hasSize(1)
                .containsExactly(InvalidLength.of(1, 3, 2));
    }

    private void assertSame(final Lawn lawn1, final Lawn lawn2) {
        assertThat(lawn1.getHeight()).isEqualTo(lawn2.getHeight());
        assertThat(lawn1.getWidth()).isEqualTo(lawn2.getWidth());
        assertThat(lawn1.getMowers().size()).isEqualTo(lawn2.getMowers().size());

        lawn1.getMowers()
                .zipWithIndex()
                .forEach(t -> {
                    Mower mower = lawn2.getMowers().toList().get(t._2);
                    assertThat(mower.getInstructions()).isEqualTo(t._1.getInstructions());
                    assertThat(mower.getPosition()).isEqualTo(t._1.getPosition());
                    assertThat(mower.getOrientation()).isEqualTo(t._1.getOrientation());
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
