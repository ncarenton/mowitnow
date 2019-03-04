package com.bbc.automower.enumeration;

import com.bbc.automower.domain.Mower;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.bbc.automower.enumeration.Instruction.*;
import static io.vavr.API.None;
import static io.vavr.API.Some;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class InstructionTest {

    @Mock
    private Mower mower;

    @Test
    public void should_get_by_label() {
        assertThat(getByLabel('F')).isEqualTo(Some(MOVE_FORWARD));
        assertThat(getByLabel('R')).isEqualTo(Some(TURN_RIGHT));
        assertThat(getByLabel('L')).isEqualTo(Some(TURN_LEFT));
        assertThat(getByLabel('X')).isEqualTo(None());
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