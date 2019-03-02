package com.bbc.automower.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class Position {
    
    int x;
    int y;

    public Position incrementX() {
        return Position.of(x + 1, y);
    }
    
    public Position decrementX() {
        return Position.of(x - 1, y);
    }
    
    public Position incrementY() {
        return Position.of(x, y + 1);
    }
    
    public Position decrementY() {
        return Position.of(x, y - 1);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

}
