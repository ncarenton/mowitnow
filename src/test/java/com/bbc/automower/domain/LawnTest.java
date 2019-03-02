package com.bbc.automower.domain;

import io.vavr.collection.Set;
import org.junit.Test;

import static com.bbc.automower.enumeration.Instruction.MOVE_FORWARD;
import static com.bbc.automower.enumeration.Instruction.TURN_LEFT;
import static com.bbc.automower.enumeration.Orientation.NORTH;
import static com.bbc.automower.enumeration.Orientation.SOUTH;
import static io.vavr.API.*;
import static org.junit.Assert.assertEquals;

public class LawnTest {

    @Test(expected=IllegalArgumentException.class)
    public void should_throw_illegalargumentexception_when_x_is_negative() {
        Lawn.of(-1, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void should_throw_illegalargumentexception_when_y_is_negative() {
        Lawn.of(1, -1);
    }

    @Test
    public void should_initialize_lawn() {
        //Given
        Lawn lawn = Lawn.of(5, 5);
        Set<Mower> mowers = Set(Mower
                .of(1, 2, NORTH)
                .instructions(List(TURN_LEFT, MOVE_FORWARD, TURN_LEFT, MOVE_FORWARD, TURN_LEFT, MOVE_FORWARD, TURN_LEFT, MOVE_FORWARD, MOVE_FORWARD)));

        //Action
        Lawn newLawn = lawn.initialize(mowers);

        //Asserts
        assertEquals(newLawn.getMowers(), mowers);
        assertEquals(newLawn.getWidth(), lawn.getWidth());
        assertEquals(newLawn.getHeight(), lawn.getHeight());
    }

    @Test
    public void should_execute_instructions() {
        //Given
        Lawn lawn = Lawn.of(5, 5)
                .initialize(
                        LinkedSet(Mower
                                .of(1, 2, NORTH)
                                .instructions(List(TURN_LEFT, MOVE_FORWARD, TURN_LEFT, MOVE_FORWARD, TURN_LEFT, MOVE_FORWARD, TURN_LEFT, MOVE_FORWARD, MOVE_FORWARD))));

        //Action
        Lawn newLawn = lawn.execute();

        //Assert
        assertEquals(newLawn.getWidth(), lawn.getWidth());
        assertEquals(newLawn.getHeight(), lawn.getHeight());

        assertEquals(newLawn.getMowers().size(), 1);
        assertEquals(newLawn.getMowers().head().getOrientation(), NORTH);
        assertEquals(newLawn.getMowers().head().getPosition(), Position.of(1, 3));
    }

    @Test
    public void should_do_nothing_when_mower_without_instruction() {
        //Given
        Lawn lawn = Lawn.of(5, 5)
                .initialize(
                        Set(Mower.of(1, 2, NORTH)));

        //Action
        Lawn newLawn = lawn.execute();

        //Asserts
        assertEquals(newLawn, lawn);
    }

    @Test
    public void should_do_nothing_when_mower_try_to_go_outside() {
        //Given
        Lawn lawn = Lawn.of(5, 5)
                .initialize(
                        Set(
                                Mower.of(0, 0, SOUTH)
                                        .instructions(List(MOVE_FORWARD))));

        //Action
        Lawn newLawn = lawn.execute();

        //Asserts
        assertEquals(newLawn, lawn);
    }

}
