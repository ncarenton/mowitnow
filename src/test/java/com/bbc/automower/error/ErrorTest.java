package com.bbc.automower.error;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.junit.Test;

import static com.bbc.automower.error.Error.*;
import static io.vavr.API.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ErrorTest {

    @Test
    public void should_format_errors() {
        //Assertions
        assertEquals(EmptyFile.of().text(), "Empty file");
        assertEquals(FileNotFound.of("filename").text(), "File filename not found");
        assertEquals(InvalidInstruction.of(1, 'X').text(), "Line 1: X cannot be cast to com.bbc.automower.enumeration.Instruction");
        assertEquals(InvalidOrientation.of(1, "X").text(), "Line 1: X cannot be cast to com.bbc.automower.enumeration.Orientation");
        assertEquals(InvalidLength.of(1, 2, 3).text(), "Line 1: the line contains 2 elements instead of 3");
        assertEquals(InvalidInt.of(1, "X").text(), "Line 1: X is not a numeric");
        assertEquals(InvalidSizeList.of(List("123"), 2).text(), "Bad number of elements for list List(123) : expected 2 elements");
    }

    @Test
    public void should_be_invalid() {
        //Given
        Error e = () -> "toto";

        //Action
        Validation<Error, Object> errorOrSomething = e.asInvalid();

        //Assertions
        assertTrue(errorOrSomething.isInvalid());
        assertEquals(errorOrSomething.getError(), e);
    }

    @Test
    public void should_be_invalid_seq() {
        //Given
        Error e = () -> "toto";

        //Action
        Validation<Seq<Error>, Object> errorOrSomething = e.asInvalidSeq();

        //Assertions
        assertTrue(errorOrSomething.isInvalid());
        assertEquals(1, errorOrSomething.getError().size());
        assertEquals(errorOrSomething.getError().get(0), e);
    }

}