package com.bbc.automower.domain;

import com.bbc.automower.enumeration.Instruction;
import com.bbc.automower.enumeration.Orientation;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.Test;

import static com.bbc.automower.enumeration.Instruction.*;
import static com.bbc.automower.enumeration.Orientation.*;
import static io.vavr.API.List;
import static io.vavr.API.Tuple;
import static org.junit.Assert.*;

public class MowerTest {

    @Test
    public void should_add_instructions() {
        //Given
        Mower mower = Mower.of(1, 2, NORTH);
        List<Instruction> instructions = List(MOVE_FORWARD);

        //Action
        Mower newMower = mower.instructions(instructions);

        //Asserts
        assertSameUuidAndDifferentInstances(mower, newMower);
        assertEquals(newMower.getInstructions(), instructions);
        assertEquals(newMower.getPosition(), mower.getPosition());
        assertEquals(newMower.getOrientation(), mower.getOrientation());
    }

    @Test
    public void should_turn_left() {
        testTurnLeft(NORTH, WEST);
        testTurnLeft(WEST, SOUTH);
        testTurnLeft(SOUTH, EAST);
        testTurnLeft(EAST, NORTH);
    }

    @Test
    public void should_turn_right() {
        testTurnRight(WEST, NORTH);
        testTurnRight(SOUTH, WEST);
        testTurnRight(EAST, SOUTH);
        testTurnRight(NORTH, EAST);
    }

    @Test
    public void should_move_forward() {
        testMoveForwardWhenNorth();
        testMoveForwardWhenSouth();
        testMoveForwardWhenEast();
        testMoveForwardWhenWest();
    }

    @Test
    public void should_be_equal_if_same_id() throws Exception {
        //Given
        Mower mower = Mower.of(1, 2, NORTH);

        //Action
        Mower newMower = mower.moveForward();

        //Asserts
        assertFalse(mower == newMower); //different instances
        assertEquals(mower.getUuid(), newMower.getUuid());
        assertNotEquals(
                Tuple(mower.getPosition(), mower.getOrientation()),
                Tuple(newMower.getPosition(), newMower.getOrientation())
        );
    }

    @Test
    public void should_execute_next_instruction() {
        //Given
        Mower mower = Mower.of(1, 2, NORTH)
                .instructions(List(TURN_LEFT, TURN_RIGHT));

        //Action
        Option<Mower> maybeMower1 = mower.executeInstruction();

        //Asserts
        assertTrue(maybeMower1.isDefined());
        maybeMower1.forEach(
                mower1 -> {
                    assertSameUuidAndDifferentInstances(mower, mower1);
                    assertEquals(mower1.getOrientation(), WEST);
                    assertEquals(mower1.getPosition(), Position.of(1, 2));
                    assertEquals(mower1.getInstructions().size(), mower.getInstructions().size() - 1);
                }
        );
    }

    @Test
    public void should_return_none_when_trying_to_execute_next_instruction_without_instruction() {
        //Given
        Mower mower = Mower.of(1, 2, NORTH);

        //Action
        Option<Mower> maybeMower1 = mower.executeInstruction();

        //Asserts
        assertTrue(maybeMower1.isEmpty());
    }

    @Test
    public void should_remove_next_instruction_when_no_action() {
        //Given
        Mower mower = Mower.of(1, 2, NORTH)
                .instructions(List(TURN_LEFT, TURN_RIGHT));

        //Action
        Mower mower1 = mower.removeInstruction();

        //Asserts
        assertEquals(mower1.getInstructions(), List(TURN_RIGHT));
    }

    // Private methods
    //-------------------------------------------------------------------------

    private void testMoveForwardWhenNorth() {
        //Given
        Mower mower = Mower.of(5, 5, NORTH);

        //Test
        testMoveForward(mower, 5, 6);
    }

    private void testMoveForwardWhenSouth() {
        //Given
        Mower mower = Mower.of(5, 5, SOUTH);

        //Test
        testMoveForward(mower, 5, 4);
    }

    private void testMoveForwardWhenEast() {
        //Given
        Mower mower = Mower.of(5, 5, EAST);

        //Test
        testMoveForward(mower, 6, 5);
    }

    private void testMoveForwardWhenWest() {
        //Given
        Mower mower = Mower.of(5, 5, WEST);

        //Test
        testMoveForward(mower, 4, 5);
    }

    private void testTurnLeft(final Orientation initial, final Orientation expected) {
        //Given
        Mower mower = Mower.of(5, 5, initial);

        //Action
        Mower newMower = mower.turnLeft();

        //Asserts
        assertSameUuidAndDifferentInstances(mower, newMower);
        assertEquals(expected, newMower.getOrientation());
    }

    private void testTurnRight(final Orientation initial, final Orientation expected) {
        //Given
        Mower mower = Mower.of(5, 5, initial);

        //Action
        Mower newMower = mower.turnRight();

        //Asserts
        assertSameUuidAndDifferentInstances(mower, newMower);
        assertEquals(expected, newMower.getOrientation());
    }

    private void testMoveForward(final Mower mower, int expectedX, int expectedY) {
        //Action
        Mower newMower = mower.moveForward();

        //Asserts
        assertSameUuidAndDifferentInstances(mower, newMower);
        assertEquals(expectedX, newMower.getPosition().getX());
        assertEquals(expectedY, newMower.getPosition().getY());
    }

    private void assertSameUuidAndDifferentInstances(final Mower mower1, final Mower mower2) {
        assertNotSame(mower1, mower2); //Different instances because Mower is immuable
        assertEquals(mower1, mower2); //Same business object (UUID)
    }

}
