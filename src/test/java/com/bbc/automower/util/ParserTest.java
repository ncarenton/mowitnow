package com.bbc.automower.util;

import com.bbc.automower.domain.Lawn;
import com.bbc.automower.domain.Mower;
import com.bbc.automower.error.Error;
import com.bbc.automower.error.Error.FileNotFound;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.bbc.automower.Constants.GOOD_FILE_PATH;
import static com.bbc.automower.enumeration.Instruction.*;
import static com.bbc.automower.enumeration.Orientation.EAST;
import static com.bbc.automower.enumeration.Orientation.NORTH;
import static io.vavr.API.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParserTest {

    //-------------------------------------------------------------------------
    // Initialization
    //-------------------------------------------------------------------------

    @Mock
    private Validator<Lawn> validator;

    @InjectMocks
    private Parser<Lawn> parser;


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
        assertThat(errorsOrAutomower.isInvalid()).isTrue();
        assertThat(errorsOrAutomower.getError())
                .isNotNull()
                .hasSize(1)
                .containsExactly(FileNotFound.of("src/main/resources/META-INF/config/dzedze"));
    }

    @Test
    public void should_parse_file_in_file_system() {
        // Given
        Lawn lawn = expectedLawn();

        // When
        when(validator.validate(any())).thenReturn(Valid(lawn));

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = parser.parse("src/main/resources/META-INF/config/" + GOOD_FILE_PATH);

        // Then
        assertThat(errorsOrAutomower.isValid()).isTrue();
        assertThat(errorsOrAutomower.get()).isSameAs(lawn);

        //Verify
        verify(validator).validate(any());
    }

    @Test
    public void should_parse_file_in_classpath() {
        // Given
        Lawn lawn = expectedLawn();

        // When
        when(validator.validate(any())).thenReturn(Valid(lawn));

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = parser.parse(GOOD_FILE_PATH);

        // Then
        assertThat(errorsOrAutomower.isValid()).isTrue();
        assertThat(errorsOrAutomower.get()).isSameAs(lawn);

        //Verify
        verify(validator).validate(any());
    }

    @Test
    public void should_be_invalid_when_validator_returns_invalid() {
        //Given
        Validation<Seq<Error>, Lawn> invalid = Invalid(Seq(Error.EmptyFile.of()));

        // When
        when(validator.validate(any())).thenReturn(invalid);

        // Action
        Validation<Seq<Error>, Lawn> errorsOrAutomower = parser.parse(GOOD_FILE_PATH);

        // Then
        assertThat(errorsOrAutomower).isSameAs(invalid);

        //Verify
        verify(validator).validate(any());
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
