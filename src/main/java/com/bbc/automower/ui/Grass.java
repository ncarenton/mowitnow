package com.bbc.automower.ui;

import com.bbc.automower.domain.Lawn;
import com.bbc.automower.domain.MowerEvent;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.bbc.automower.ui.UIUtil.getPoint2D;

@Slf4j
public class Grass {
    private static final int TILE_SIZE = 100;

    private static final Color COLOR_MOWER = Color.BLACK;
    private static final Color COLOR_RAW_LAWN = Color.GREEN;
    private static final Color COLOR_MOWED_LAWN = Color.LIGHTGREEN;

    private final Point2D grid;
    private final Map<Point2D, Rectangle> cells = new HashMap<>();

    private final List<MowerEvent> events;


    public Grass(final Lawn lawn) {
        events = lawn.getHistory();
        grid = getPoint2D(lawn);
        initCells();
        initMowers();
    }

    public Grass fill(final Stage stage) {
        stage.setTitle("Mowitnow");
        stage.setWidth(TILE_SIZE * (grid.getX() + 1));
        stage.setHeight(TILE_SIZE * (grid.getY() + 1));

        Group root = new Group();
        stage.setScene(new Scene(root));

        cells.forEach((coord, rectangle) -> root.getChildren().add(rectangle));
        return this;
    }

    public Grass execute() {
        Executors
                .newSingleThreadExecutor()
                .execute(() -> {
                    events
                            .forEach(event -> {
                                try {
                                    Thread.sleep(500);
                                    handleEvent(event);
                                } catch (Exception e) {
                                    log.error(e.getMessage(), e);
                                }
                            });
                });

        return this;
    }

    private void initMowers() {
        events
                .groupBy(MowerEvent::getBefore)
                .values()
                .map(Traversable::headOption)
                .flatMap(Function.identity())
                .map(MowerEvent::getBefore)
                .forEach(mower ->
                        cells
                                .get(getPoint2D(mower))
                                .setFill(COLOR_MOWER));
    }

    private void initCells() {
        IntStream.rangeClosed(0, (int) grid.getX())
                .forEach(x ->
                        IntStream.rangeClosed(0, (int) grid.getY())
                                .forEach(y -> {
                                    Rectangle rect = new Rectangle(TILE_SIZE * x, TILE_SIZE * (grid.getY() - y), TILE_SIZE, TILE_SIZE);
                                    rect.setFill(COLOR_RAW_LAWN);
                                    cells.put(new Point2D(x, y), rect);
                                }));
    }

    private void handleEvent(final MowerEvent event) {
        log.debug("[Mower {}] instruction {} : {} => {}", event.getBefore().getUuid(), event.getInstruction().getLabel(), event.getBefore().getLocation(), event.getAfter().getLocation());
        cells.get(getPoint2D(event.getBefore())).setFill(COLOR_MOWED_LAWN);
        cells.get(getPoint2D(event.getAfter())).setFill(COLOR_MOWER);
    }
}
