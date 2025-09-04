package cars.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

import static cars.engine.Vector2.*;
import static java.lang.Math.toRadians;

public abstract class Car implements Cloneable {
    private final Color color;
    private final double mass;
    private final double maxForce;
    private final double maxSpeed;
    private Vector2 position;
    private Vector2 velocity;

    // Store last clamped steering (pre-dt) for debug drawing
    private Vector2 lastSteering = vec2();

    public Car(Color color, Vector2 position, double orientation, double mass, double maxForce, double maxSpeed) {
        this.color = color;
        this.position = position;
        this.velocity = Vector2.byAngle(orientation);
        this.mass = mass;
        this.maxForce = maxForce;
        this.maxSpeed = maxSpeed;
    }

    public Car(Color color, Vector2 position, double orientation, double mass) {
        this(color, position, orientation, mass, 350, 500);
    }

    public Car(Color color, Vector2 position, double orientation) {
        this(color, position, orientation, 1, 350, 500);
    }

    // ---- Accessors ----
    public Vector2 getPosition() { return position.clone(); }
    public Vector2 getVelocity() { return velocity.clone(); }
    public Vector2 getDirection() { return velocity.isZero() ? byAngle(0) : normalize(velocity); }
    public double getMass() { return mass; }
    public double getMaxForce() { return maxForce; }
    public double getMaxSpeed() { return maxSpeed; }
    private Vector2 getLastSteering() { return lastSteering == null ? null : lastSteering.clone(); }

    public abstract Vector2 calculateSteering(World world);

    void update(World world) {
        final var steeringForce = calculateSteering(world);
        if (steeringForce == null) {
            lastSteering = vec2();
            return;
        }

        lastSteering = truncate(steeringForce, maxForce);

        final var impulse = multiply(lastSteering, world.getSecs()); // F * dt
        final var acceleration = impulse.divide(mass);              // (F*dt)/m
        velocity = truncate(add(velocity, acceleration), maxSpeed);
        position = multiply(velocity, world.getSecs()).add(position);

        final var w = world.getWidth() / 2.0;
        final var h = world.getHeight() / 2.0;
        if (position.x < -(w + 20)) position.x =  w;
        if (position.x >  (w + 20)) position.x = -w;
        if (position.y < -(h + 20)) position.y =  h;
        if (position.y >  (h + 20)) position.y = -h;
    }

    // -----------------------------------------------------
    // Drawing
    // -----------------------------------------------------
    void draw(GraphicsContext g) {
        g.save();
        g.translate(position.x, position.y);
        g.rotate(Math.toDegrees(velocity.getAngle()));

        g.save();
        g.scale(-0.5, 0.5);
        drawF1Car80px(g, color);
        g.restore();

        g.restore(); // back to world space

        drawDebugArrows(g);
    }

    // ----- F1 car, authored at 80px long (drawn above with scale 0.5) -----
    private static void drawF1Car80px(GraphicsContext g, Color bodyColor) {
        final var L = 80.0;
        final var W = 36.0;
        final var halfL = L / 2.0;
        final var halfW = W / 2.0;

        final var wingColor = Color.DARKGRAY;
        final var tireColor = Color.BLACK;

        final Paint bodyGrad = new LinearGradient(
            -halfL, 0, halfL, 0, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, bodyColor.darker()),
            new Stop(0.5, bodyColor),
            new Stop(1.0, bodyColor.darker())
        );

        g.setFill(tireColor);
        final var tireW = 12;
        final var tireH = 7;
        final var frontX = halfL * 0.55;
        final var rearX  = -halfL * 0.55;
        final var tireY = halfW;
        g.fillRoundRect(frontX - tireW / 2, -tireY,     tireW, tireH, 3, 3);
        g.fillRoundRect(frontX - tireW / 2,  tireY - 7, tireW, tireH, 3, 3);
        g.fillRoundRect(rearX  - tireW / 2, -tireY,     tireW, tireH, 3, 3);
        g.fillRoundRect(rearX  - tireW / 2,  tireY - 7, tireW, tireH, 3, 3);

        g.setFill(bodyGrad);
        g.fillRoundRect(-halfL * 0.45, -W * 0.22, L * 0.70, W * 0.44, 6, 6);
        g.fillRoundRect(-halfL * 0.45, -W * 0.10, L * 0.50, W * 0.20, 10, 10);

        g.setFill(bodyColor.darker());
        g.fillRoundRect(-L * 0.10, -W * 0.34, L * 0.30, W * 0.20, 6, 6);
        g.fillRoundRect(-L * 0.10,  W * 0.14, L * 0.30, W * 0.20, 6, 6);

        g.setFill(Color.rgb(25, 25, 32, 0.9));
        g.fillOval(-L * 0.12, -W * 0.18, L * 0.28, W * 0.36);

        g.setFill(wingColor);
        g.fillRoundRect(halfL - 10, -W * 0.70, 12, W * 1.40, 6, 6);
        g.fillRoundRect(-halfL - 4, -W * 0.50, 8, W * 0.98, 6, 6);

        g.setStroke(Color.rgb(255, 255, 255, 0.28));
        g.setLineWidth(2);
        g.strokeLine(-halfL, 0, halfL, 0);
    }

    // ----- Debug arrows -----
    private void drawDebugArrows(GraphicsContext g) {
        drawArrow(g, velocity, Color.BLUE);
        drawArrow(g, lastSteering, Color.ORANGERED);
    }

    private void drawArrow(GraphicsContext g, Vector2 vector, Color color) {
        if (Vector2.isZero(vector)) return;

        final var origin = add(position, multiply(getDirection(), 12));
        final var tip = add(origin, multiply(vector, 0.2));
        final var dir = subtract(tip, origin);

        g.save();
        g.setGlobalAlpha(0.25);
        g.setStroke(color);
        g.setLineWidth(3);

        g.strokeLine(origin.x, origin.y, tip.x, tip.y);

        final var angle = dir.getAngle();
        final var a = toRadians(30);
        final var p1 = subtract(tip, byAngleSize(angle - a, 8));
        final var p2 = subtract(tip, byAngleSize(angle + a, 8));
        g.strokeLine(tip.x, tip.y, p1.x, p1.y);
        g.strokeLine(tip.x, tip.y, p2.x, p2.y);

        g.setGlobalAlpha(1.0);
        g.restore();
    }

    @Override
    public Car clone() {
        try {
            final var other = (Car) super.clone();
            other.position = position.clone();
            other.velocity = velocity.clone();
            other.lastSteering = lastSteering == null ? null : lastSteering.clone();
            return other;
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }
}
