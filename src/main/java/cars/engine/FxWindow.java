package cars.engine;

import cars.student.Setup;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class FxWindow extends Application {
    private static final double INIT_WIDTH = 1024;
    private static final double INIT_HEIGHT = 768;

    private List<Car> cars;
    private Vector2 clickPos = null;  // relative to center
    private Vector2 mousePos = null;  // absolute scene coords

    private Canvas canvas;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Steering behaviors");

        canvas = new Canvas(INIT_WIDTH, INIT_HEIGHT);
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root, INIT_WIDTH, INIT_HEIGHT);

        // Make stage resizable and keep canvas in sync with the scene size
        stage.setResizable(true);
        canvas.widthProperty().bind(Bindings.selectDouble(scene.widthProperty()));
        canvas.heightProperty().bind(Bindings.selectDouble(scene.heightProperty()));

        // Input handlers (use CURRENT canvas size)
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            double cx = canvas.getWidth() / 2.0;
            double cy = canvas.getHeight() / 2.0;
            clickPos = new Vector2(e.getX() - cx, e.getY() - cy);
        });
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            mousePos = new Vector2(e.getX(), e.getY());
        });

        cars = new Setup().createCars();

        stage.setScene(scene);
        stage.show();

        startLoop();
    }

    private void startLoop() {
        GraphicsContext g = canvas.getGraphicsContext2D();

        final long[] prevNanos = { System.nanoTime() };
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double dt = (now - prevNanos[0]) / 1_000_000_000.0;
                // clamp big spikes (e.g., window dragging)
                if (dt > 0.1) dt = 0.1;
                prevNanos[0] = now;

                draw(g);
                update(dt);
            }
        };
        timer.start();
    }

    private void update(final double time) {
        final double width = canvas.getWidth();
        final double height = canvas.getHeight();
        cars.forEach(car -> car.update(new World(
            time, car, cars, mousePos, clickPos, width, height
        )));
    }

    private void draw(GraphicsContext g) {
        final double w = canvas.getWidth();
        final double h = canvas.getHeight();

        // Clear
        g.setFill(Color.rgb(220, 220, 220));
        g.fillRect(0, 0, w, h);

        // Translate origin to the center (dynamic)
        g.save();
        g.translate(w / 2.0, h / 2.0);

        if (clickPos != null) {
            g.setFill(Color.GRAY);
            g.fillOval(clickPos.x - 4, clickPos.y - 4, 8, 8);
        }

        cars.forEach(car -> car.draw(g));
        g.restore();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
