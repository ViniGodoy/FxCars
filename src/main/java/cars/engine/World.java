package cars.engine;

import java.util.List;

import static cars.engine.Vector2.distance;

public final class World {
    private final Car current;
    private final List<Car> cars;
    private final Vector2 mousePos;   // absolute scene coords; may be null
    private final Vector2 clickPos;   // center-relative coords; may be null
    private final double secs;

    // NEW: dynamic canvas dimensions
    private final double width;
    private final double height;

    public World(double secs,
                 Car current,
                 List<Car> cars,
                 Vector2 mousePos,
                 Vector2 clickPos,
                 double width,
                 double height) {
        this.current = current;
        this.cars = cars;
        this.mousePos = mousePos;
        this.clickPos = clickPos;
        this.secs = secs;
        this.width = width;
        this.height = height;
    }

    public Vector2 getMousePos() {
        return mousePos == null ? null : mousePos.clone();
    }

    public Vector2 getClickPos() {
        return clickPos == null ? null : clickPos.clone();
    }

    public List<Car> getNeighbors() {
        return this.getNeighbors(Integer.MAX_VALUE);
    }

    public List<Car> getNeighbors(int radius) {
        return this.cars.stream()
            .filter(c -> c != current)
            .filter(c -> distance(current.getPosition(), c.getPosition()) <= radius)
            .toList();
    }

    public double getSecs() {
        return secs;
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
