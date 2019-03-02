package com.bbc.automower.enumeration;

import com.bbc.automower.domain.Mower;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.bbc.automower.enumeration.Instruction.*;
import static io.vavr.API.None;
import static io.vavr.API.Some;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class InstructionTest {

    @Mock
    private Mower mower;

    @Test
    public void should_get_by_label() {
        assertEquals(getByLabel('F'), Some(MOVE_FORWARD));
        assertEquals(getByLabel('R'), Some(TURN_RIGHT));
        assertEquals(getByLabel('L'), Some(TURN_LEFT));
        assertEquals(getByLabel('X'), None());
    }

    @Test
    public void should_execute_move_forward_when_forward() {
        //Action
        MOVE_FORWARD.apply(mower);

        //Verify
        verify(mower).moveForward();
    }

    @Test
    public void should_execute_turn_right_when_right() {
        //Action
        TURN_RIGHT.apply(mower);

        //Verify
        verify(mower).turnRight();
    }

    @Test
    public void should_execute_turn_left_when_left() {
        //Action
        TURN_LEFT.apply(mower);

        //Verify
        verify(mower).turnLeft();
    }

}