package com.bbc.automower.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PositionTest {
    
    private final Position position = Position.of(2, 5);
    
    @Test
    public void should_increment_x() {
        //Action
        Position newPosition = position.incrementX();

        //Asserts
        assertThatPositionIsEqualToExpected(newPosition,3, 5);
    }
    
    @Test
    public void should_decrement_x() {
        //Action
        Position newPosition = position.decrementX();

        //Asserts
        assertThatPositionIsEqualToExpected(newPosition, 1, 5);
    }
    
    @Test
    public void should_increment_y() {
        //Action
        Position newPosition = position.incrementY();

        //Asserts
        assertThatPositionIsEqualToExpected(newPosition, 2, 6);
    }
    
    @Test
    public void should_decrement_y() {
        //Action
        Position newPosition = position.decrementY();

        //Asserts
        assertThatPositionIsEqualToExpected(newPosition, 2, 4);
    }
    

    // Private methods
    //-------------------------------------------------------------------------
    
    private void assertThatPositionIsEqualToExpected(final Position position, int expectedX, int expectedY) {
        assertThat(position).isNotSameAs(this.position); // Not same references -> position is immuable
        assertThat(position.getX()).isEqualTo(expectedX);
        assertThat(position.getY()).isEqualTo(expectedY);
    }
}
