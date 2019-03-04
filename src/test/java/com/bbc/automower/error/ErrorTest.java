package com.bbc.automower.error;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.junit.Test;

import static com.bbc.automower.error.Error.*;
import static io.vavr.API.List;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class ErrorTest {

    @Test
    public void should_format_errors() {
        //Assertions
        assertThat(EmptyFile.of().text()).isEqualTo("Empty file");
        assertThat(FileNotFound.of("filename").text()).isEqualTo("File filename not found");
        assertThat(InvalidInstruction.of(1, 'X').text()).isEqualTo("Line 1: X cannot be cast to com.bbc.automower.enumeration.Instruction");
        assertThat(InvalidOrientation.of(1, "X").text()).isEqualTo("Line 1: X cannot be cast to com.bbc.automower.enumeration.Orientation");
        assertThat(InvalidLength.of(1, 2, 3).text()).isEqualTo("Line 1: the line contains 2 elements instead of 3");
        assertThat(InvalidInt.of(1, "X").text()).isEqualTo("Line 1: X is not a numeric");
        assertThat(InvalidSizeList.of(List("123"), 2).text()).isEqualTo("Bad number of elements for list List(123) : expected 2 elements");
    }

    @Test
    public void should_be_invalid() {
        //Given
        Error e = () -> "an error";

        //Action
        Validation<Error, Object> errorOrSomething = e.asInvalid();

        //Assertions
        assertThat(errorOrSomething.isInvalid()).isTrue();
        assertThat(errorOrSomething.getError()).isEqualTo(e);
    }

    @Test
    public void should_be_invalid_seq() {
        //Given
        Error e = () -> "an error";

        //Action
        Validation<Seq<Error>, Object> errorOrSomething = e.asInvalidSeq();

        //Assertions
        assertThat(errorOrSomething.isInvalid()).isTrue();
        assertThat(errorOrSomething.getError())
                .hasSize(1)
                .containsExactly(e);
    }

}