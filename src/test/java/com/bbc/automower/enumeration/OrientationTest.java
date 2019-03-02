package com.bbc.automower.enumeration;

import org.junit.Test;

import static com.bbc.automower.enumeration.Orientation.*;
import static io.vavr.API.None;
import static io.vavr.API.Some;
import static org.junit.Assert.assertEquals;

public class OrientationTest {
    
    @Test
    public void should_move_right() {
        assertEquals(NORTH.right(), EAST);
        assertEquals(EAST.right(),  SOUTH);
        assertEquals(SOUTH.right(), WEST);
        assertEquals(WEST.right(),  NORTH);
    }
    
    @Test
    public void should_move_left() {
        assertEquals(NORTH.left(), WEST);
        assertEquals(WEST.left(),  SOUTH);
        assertEquals(SOUTH.left(), EAST);
        assertEquals(EAST.left(),  NORTH);
    }

    @Test
    public void should_get_label() {
        assertEquals(NORTH.getLabel(), "N");
        assertEquals(WEST.getLabel(), "W");
        assertEquals(SOUTH.getLabel(), "S");
        assertEquals(EAST.getLabel(), "E");
    }

    @Test
    public void should_get_by_label() {
        assertEquals(getByLabel("N"), Some(NORTH));
        assertEquals(getByLabel("W"), Some(WEST));
        assertEquals(getByLabel("S"), Some(SOUTH));
        assertEquals(getByLabel("E"), Some(EAST));
        assertEquals(getByLabel("UNKNOWN"), None());
    }
    
}
