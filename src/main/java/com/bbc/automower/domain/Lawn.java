package com.bbc.automower.domain;

import io.vavr.Tuple2;
import io.vavr.collection.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

import static io.vavr.API.LinkedSet;
import static io.vavr.API.Tuple;


@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class Lawn {

    int width;
    int height;

    @Wither(AccessLevel.PRIVATE)
    Set<Mower> mowers;

    public static Lawn of(final int width, final int height) {
        return new Lawn(width, height);
    }

    private Lawn(final int width, final int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Arguments must be >= 0");
        }

        this.width = width;
        this.height = height;
        mowers = LinkedSet();
    }

    public Lawn initialize(final Set<Mower> mowers) {
        return withMowers(this.mowers.addAll(mowers));
    }

    public Lawn execute() {
        return mowers.foldLeft(
                this,
                (l,m) ->  l.executeInstructions(m)
                        .map2(Mower::printPosition)
                        ._1
        );
    }

    private Tuple2<Lawn, Mower> executeInstructions(final Mower mower) {
        return mower.executeInstruction()
                .map(m -> move(mower, m))
                .peek(lm -> log.debug("Mower {} at position {}", lm._2.getUuid(), lm._2.getPosition()))
                .map(lm -> lm._1.executeInstructions(lm._2))
                .getOrElse(() -> Tuple(this, mower));
    }

    private Tuple2<Lawn, Mower> move(final Mower source, final Mower target) {
        final Mower mower = inside(target.getPosition()) && available(source, target.getPosition()) ?
                target :
                source.removeInstruction();

        return Tuple(
                withMowers(mowers
                        .remove(source)
                        .add(mower)),
                mower);
    }

    private boolean inside(final Position position) {
        return position.getX() >= 0 && position.getX() <= width
                && position.getY() >= 0 && position.getY() <= height;
    }

    private boolean available(final Mower mower, final Position position) {
        Predicate<Mower> equalsToMower = mower::equals;

        return ! mowers
                .filter(equalsToMower.negate()) //Java 11 : use Predicate.not(mower::equals)
                .map(Mower::getPosition)
                .contains(position);
    }

}
