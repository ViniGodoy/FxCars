package cars.engine;

import java.util.Objects;

/**
 * Represents a vector in 2D coordinate space.
 * This class has two versions of most methods:
 * <ul>
 * <li>A class version, that changes the vector, representing unary operators like +=;</li>
 * <li>A static version, that does not change the given vectors, thus representing binary operators, like +</li>
 * </ul>
 * Class methods returns the vector itself, allowing invocation chaining e.g.:
 * <code>v1.add(v2).normalize();</code>
 */
public final class Vector2 implements Cloneable {
    public double x;
    public double y;

    /**
     * Creates a zero vector.
     */
    public Vector2() {
        this(0, 0);
    }

    /**
     * Creates a vector with the given x and y components.
     *
     * @param x x value.
     * @param y y value.
     */
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Calls the constructor.
     *
     * @see Vector2
     */
    public static Vector2 vec2() {
        return new Vector2();
    }

    /**
     * Calls the constructor.
     *
     * @see Vector2
     */
    public static Vector2 vec2(double x, double y) {
        return new Vector2(x, y);
    }

    /**
     * Creates a unitary vector based in the given angle
     *
     * @return The vector
     * @see Vector2#byAngleSize(double, double)
     */
    public static Vector2 byAngle(double angle) {
        return new Vector2(Math.cos(angle), Math.sin(angle));
    }

    /**
     * Creates a vector based in the given size and angle
     *
     * @return The vector
     */
    public static Vector2 byAngleSize(double angle, double size) {
        return Vector2.byAngle(angle).multiply(size);
    }

    /**
     * Normalizes the given vector.
     *
     * @param vector The vector to normalize.
     * @return A normalized copy of the given vector.
     * @see #normalize()
     */
    public static Vector2 normalize(Vector2 vector) {
        return vector.clone().normalize();
    }

    /**
     * Negates the given vector
     *
     * @param vector Vector to negate
     * @return A negated copy of the given vector.
     */
    public static Vector2 negate(Vector2 vector) {
        return vector.clone().negate();
    }

    /**
     * Adds two vectors together.
     *
     * @param v1 The first vector
     * @param v2 The second vector
     * @return The addition result.
     */
    public static Vector2 add(Vector2 v1, Vector2 v2) {
        return v1.clone().add(v2);
    }

    /**
     * Subtracts two vectors together.
     *
     * @param v1 The first vector
     * @param v2 The second vector
     * @return The subtraction result.
     * @see #subtract(Vector2)
     */
    public static Vector2 subtract(Vector2 v1, Vector2 v2) {
        return v1.clone().subtract(v2);
    }

    /**
     * Calculates the perpendicular vector.
     *
     * @param v1 Vector 1
     * @return The perpendicular vector.
     */
    public static Vector2 perp(Vector2 v1) {
        return new Vector2(-v1.y, v1.x);
    }

    /**
     * Calculate dot product between the perpendicular of v1 and v2.
     * This operation is commonly known as the 2D cross product.
     *
     * @param v1 The first vector
     * @param v2 The second vector
     * @return The perpendicular dot product.
     */
    public static double perpDot(Vector2 v1, Vector2 v2) {
        return perp(v1).dot(v2);
    }

    /**
     * Multiplies the vector by the given scalar
     *
     * @param vector The vector to multiply
     * @param scalar The scalar
     * @return The multiplication result.
     * @see #multiply(double)
     */
    public static Vector2 multiply(Vector2 vector, double scalar) {
        return vector.clone().multiply(scalar);
    }

    /**
     * Elevate each vector component by a given potency.
     *
     * @param vector  Vector to elevate
     * @param potency A scalar factor
     * @return A new vector, with the result.
     */
    public static Vector2 pow(Vector2 vector, double potency) {
        return vector.clone().pow(potency);
    }

    /**
     * Divides the vector by the given scalar.
     *
     * @param vector The vector to divide
     * @param scalar The scalar.
     * @return The division result.
     * @see #divide(double)
     */
    public static Vector2 divide(Vector2 vector, double scalar) {
        return vector.clone().divide(scalar);
    }

    /**
     * Returns a rotated copy of the given vector.
     *
     * @param vector  The vector to rotate
     * @param radians Angle to rate
     * @return The rotated vector
     */
    public static Vector2 rotate(Vector2 vector, double radians) {
        return vector.clone().rotate(radians);
    }

    /**
     * @return The distance between the two vectors
     */
    public static double distanceSqr(Vector2 v1, Vector2 v2) {
        return subtract(v1, v2).sizeSqr();
    }

    public static double distance(Vector2 v1, Vector2 v2) {
        return subtract(v1, v2).size();
    }

    /**
     * Truncates the vector to the given size.
     *
     * @param vector Vector to be truncated
     * @param size   The maximum vector size
     * @return The truncated vector
     */
    public static Vector2 truncate(Vector2 vector, double size) {
        if (vector.sizeSqr() <= size * size) {
            return vector.clone();
        }
        return normalize(vector).multiply(size);
    }

    /**
     * Reflects the vector over the normal
     *
     * @param ray    ray vector
     * @param normal normal
     * @return The reflected vector
     */
    public static Vector2 reflex(Vector2 ray, Vector2 normal) {
        return subtract(ray, normal.multiply(2 * ray.dot(normal)));
    }

    /**
     * Returns a resized copy of v.
     *
     * @param v       The vector to resize.
     * @param newSize The new size
     * @return The resized copy of v.
     */
    public static Vector2 resize(Vector2 v, float newSize) {
        return v.clone().resize(newSize);
    }

    /**
     * Returns true if the vector is zero or null.
     * @param v The vector to test
     * @return True if zero or null.
     */
    public static boolean isZero(Vector2 v) {
        return v == null || v.isZero();
    }

    /**
     * Changes all vector components
     *
     * @param x New x value
     * @param y New y value
     * @return This vector.
     */
    public Vector2 set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * @return the size of this vector squared.
     */
    public double sizeSqr() {
        return x * x + y * y;
    }

    /**
     * @return the size of this vector
     */
    public double size() {
        return Math.sqrt(sizeSqr());
    }

    /**
     * Changes this Vector2 size. Equivalent to v.normalize().multiply(size)
     *
     * @param newSize The new size.
     * @return The resized vector.
     */
    public Vector2 resize(double newSize) {
        return this.normalize().multiply(newSize);
    }

    /**
     * @return Turns this vector into a unitary vector.
     * @see Vector2#normalize(Vector2)
     */
    public Vector2 normalize() {
        return isZero() ? this : divide(size());
    }

    /**
     * Adds another vector to this vector.
     *
     * @param other The vector to add.
     * @return This vector after addition.
     * @see Vector2#add(Vector2, Vector2)
     */
    public Vector2 add(Vector2 other) {
        x += other.x;
        y += other.y;
        return this;
    }

    /**
     * Negates this vector, reversing it's side.
     *
     * @return This vector after negation.
     * @see Vector2#negate(Vector2)
     */
    public Vector2 negate() {
        return set(-x, -y);
    }

    /**
     * Subtracts the other vector from this one.
     *
     * @param other The vector to subtract to.
     * @return This vector after subtraction.
     * @see Vector2#subtract(Vector2, Vector2)
     */
    public Vector2 subtract(Vector2 other) {
        x -= other.x;
        y -= other.y;
        return this;
    }

    /**
     * Multiplies this vector by the given scalar.
     *
     * @param scalar The scalar.
     * @return This vector after multiplication.
     * @see Vector2#multiply(Vector2, double)
     */
    public Vector2 multiply(double scalar) {
        return set(x * scalar, y * scalar);
    }

    /**
     * Elevate each component by the given potency.
     *
     * @param potency The potency to elevate to.
     * @return The vector after the operation.
     */
    public Vector2 pow(double potency) {
        return set(Math.pow(x, potency), Math.pow(y, potency));
    }

    /**
     * Divides this vector by the given scalar.
     *
     * @param scalar The scalar.
     * @return This vector divided.
     * @see Vector2#divide(Vector2, double)
     */
    public Vector2 divide(double scalar) {
        return multiply(1.0f / scalar);
    }

    /**
     * Calculates the dot product between this vector an the given one.
     *
     * @param other A vector.
     * @return The dot product.
     */
    public double dot(Vector2 other) {
        return x * other.x +
            y * other.y;
    }

    /**
     * Rotate the vector
     *
     * @param radians Angle to rotate
     * @return This vector, rotated
     */
    public Vector2 rotate(double radians) {
        var s = Math.sin(radians);
        var c = Math.cos(radians);

        var newX = x * c - y * s;
        var newY = x * s + y * c;

        x = newX;
        y = newY;
        return this;
    }

    /**
     * @return True if this is a unitary (normal) vector.
     */
    public boolean isUnit() {
        return Math.abs(sizeSqr() - 1.0) < 1e-9;
    }

    /**
     * @return True if this is the zero vector.
     */
    public boolean isZero() {
        return sizeSqr() < 1e-12;
    }

    /**
     * @return This Vector angle around
     */
    double getAngle() {
        return Math.atan2(y, x);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        final var other = (Vector2) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public Vector2 clone() {
        try {
            return (Vector2) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Vector2(this.x, this.y);
        }
    }

    @Override
    public String toString() {
        return String.format("v(%.2f, %.2f)", x, y);
    }
}
