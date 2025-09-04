package cars.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static cars.engine.Vector2.*;

public abstract class Car implements Cloneable {
    private final Color color;
    private final double mass;
    private final double maxForce;
    private final double maxSpeed;
    private Vector2 position;
    private Vector2 velocity;

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

    public Vector2 getPosition() {
        return position.clone();
    }

    public Vector2 getVelocity() {
        return velocity.clone();
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getMaxForce() {
        return maxForce;
    }

    public Vector2 getDirection() {
        return velocity.isZero() ? byAngle(0) : normalize(velocity);
    }

    public abstract Vector2 calculateSteering(World world);

    void update(World world) {
        // Same simplified physics
        final var steeringForce = truncate(calculateSteering(world), maxForce).multiply(world.getSecs());
        final var acceleration = divide(steeringForce, mass);
        velocity = truncate(add(velocity, acceleration), maxSpeed);
        position = multiply(velocity, world.getSecs()).add(position);

        // Dynamic screen wrap using the *current* canvas size
        final var w = world.getWidth() / 2.0;
        final var h = world.getHeight() / 2.0;
        if (position.x < -(w + 20)) position.x = w;
        if (position.x > (w + 20)) position.x = -w;
        if (position.y < -(h + 20)) position.y = h;
        if (position.y > (h + 20)) position.y = -h;
    }

    void draw(GraphicsContext g) {
        g.save();
        g.translate(position.x, position.y);
        g.rotate(Math.toDegrees(velocity.getAngle()));
        g.scale(-0.5, 0.5);

        final double L = 80;       // total length
        final double W = 36;       // max width across tires
        final double halfL = L / 2.0;
        final double halfW = W / 2.0;

        // Base colors
        final var detailColor = this.color.darker().darker();
        final var tireColor = Color.BLACK;

        // ----- Tires -----
        final var tireW = 10.0;
        final var tireH = 6.0;
        
        // front axle
        final var frontX =  halfL * 0.55;
        g.setFill(tireColor);
        g.fillRoundRect(frontX - tireW / 2, -halfW, tireW, tireH, 3, 3);
        g.fillRoundRect(frontX - tireW / 2,  halfW - tireH, tireW, tireH, 3, 3);
        // rear axle
        final var rearX = -halfL * 0.55;
        g.fillRoundRect(rearX - tireW / 2, -halfW, tireW, tireH, 3, 3);
        g.fillRoundRect(rearX - tireW / 2,  halfW - tireH, tireW, tireH, 3, 3);

        // ----- Nose + Chassis -----
        g.setFill(this.color);
        // long central body
        g.fillRect(-halfL * 0.5, -W * 0.20, L * 0.7, W * 0.40);
        // nose cone
        g.fillRect(-halfL, -W * 0.08, L * 0.5, W * 0.16);

        // ----- Cockpit -----
        g.setFill(detailColor);
        g.fillOval(-L * 0.15, -W * 0.15, L * 0.30, W * 0.30);

        // ----- Front wing -----
        g.setFill(detailColor);
        g.fillRect(halfL - 10, -W * 0.6, 12, W * 1.2);

        // ----- Rear wing -----
        g.setFill(detailColor);
        g.fillRect(-halfL - 2, -W * 0.45, 6, W * 0.9);

        // ----- Highlight line down the center -----
        g.setStroke(Color.rgb(255, 255, 255, 0.3));
        g.setLineWidth(2);
        g.strokeLine(-halfL, 0, halfL, 0);

        g.restore();
    }

    @Override
    public Car clone() {
        try {
            final var other = (Car) super.clone();
            other.position = position.clone();
            other.velocity = velocity.clone();
            return other;
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }
}
