package com.bbc.automower.domain;

import io.vavr.collection.Set;
import org.junit.Test;

import static com.bbc.automower.enumeration.Instruction.MOVE_FORWARD;
import static com.bbc.automower.enumeration.Instruction.TURN_LEFT;
import static com.bbc.automower.enumeration.Orientation.NORTH;
import static com.bbc.automower.enumeration.Orientation.SOUTH;
import static io.vavr.API.*;
import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(newLawn.getMowers()).isEqualTo(mowers);
        assertThat(newLawn.getWidth()).isEqualTo(lawn.getWidth());
        assertThat(newLawn.getHeight()).isEqualTo(lawn.getHeight());
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
        assertThat(newLawn.getWidth()).isEqualTo(lawn.getWidth());
        assertThat(newLawn.getHeight()).isEqualTo(lawn.getHeight());

        assertThat(newLawn.getMowers()).hasSize(1);
        assertThat(newLawn.getMowers().head().getOrientation()).isSameAs(NORTH);
        assertThat(newLawn.getMowers().head().getPosition()).isEqualTo(Position.of(1, 3));
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
        assertThat(newLawn).isEqualTo(lawn);
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
        assertThat(newLawn).isEqualTo(lawn);
    }

}
