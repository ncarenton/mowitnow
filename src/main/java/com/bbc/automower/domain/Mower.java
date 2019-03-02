package com.bbc.automower.domain;

import com.bbc.automower.enumeration.Instruction;
import com.bbc.automower.enumeration.Orientation;
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.*;
import lombok.EqualsAndHashCode.Include;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static com.bbc.automower.enumeration.Orientation.*;
import static io.vavr.API.*;
import static java.util.UUID.randomUUID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Slf4j
public class Mower implements Movable<Mower> {

    @Include
    UUID uuid;

    @Wither(AccessLevel.PRIVATE)
    Position position;

    @Wither(AccessLevel.PRIVATE)
    Orientation orientation;

    @Wither(AccessLevel.PRIVATE)
    List<Instruction> instructions;

    public static Mower of(final int x, final int y, @NonNull final Orientation orientation) {
        return new Mower(x, y, orientation);
    }

    private Mower(final int x, final int y, @NonNull final Orientation orientation) {
        uuid = randomUUID();
        instructions = List();
        position = Position.of(x, y);
        this.orientation = orientation;
    }

    public Mower instructions(final List<Instruction> instructions) {
        return withInstructions(this.instructions.appendAll(instructions));
    }

    public Option<Mower> executeInstruction() {
        return instructions.headOption()
                .peek(instruction -> log.debug("Execute instruction {} on mower {}", instruction.getLabel(), uuid))
                .map(instruction -> instruction.apply(this))
                .map(Mower::removeInstruction);
    }

    @Override
    public Mower turnRight() {
        return withOrientation(orientation.right());
    }

    @Override
    public Mower turnLeft() {
        return withOrientation(orientation.left());
    }

    @Override
    public Mower moveForward() {
        return withPosition(
                Match(orientation).of(
                        Case($(EAST), position::incrementX),
                        Case($(SOUTH), position::decrementY),
                        Case($(NORTH), position::incrementY),
                        Case($(WEST), position::decrementX),
                        Case($(), position)));
    }

    public Mower removeInstruction() {
        return withInstructions(instructions.pop());
    }

    public Mower printPosition() {
        log.info("{} {} {}", position.getX(), position.getY(), orientation.getLabel());
        return this;
    }

}
