package cars.engine

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.*

data class Vector2(val x: Double, val y: Double) {
    constructor() : this(0.0, 0.0)

    // Unary
    operator fun unaryMinus() = Vector2(-x, -y)

    // Basic operations
    operator fun plus(other: Vector2) = Vector2(x + other.x, y + other.y)
    operator fun minus(other: Vector2) = Vector2(x - other.x, y - other.y)

    operator fun times(scalar: Double) = Vector2(x * scalar, y * scalar)
    operator fun times(scalar: Int) = Vector2(x * scalar, y * scalar)

    operator fun div(scalar: Double) = Vector2(x / scalar, y / scalar)
    operator fun div(scalar: Int) = Vector2(x / scalar, y / scalar)

    // Indexed access
    operator fun get(i: Int): Double = when (i) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException()
    }

    // --- Equality with tolerance ---
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vector2) return false
        return q8(x) == q8(other.x) && q8(y) == q8(other.y)
    }

    override fun hashCode(): Int = Objects.hash(q8(x), q8(y))

    val size: Double get() = hypot(x, y)
    val sizeSqr: Double get() = x * x + y * y
    val isUnit: Boolean get() = abs(sizeSqr - 1.0) <= EPS
    val isZero: Boolean get() = sizeSqr <= EPS * EPS
    val angle: Double get() = atan2(y, x)

    override fun toString() = "(%.2f, %.2f)".format(Locale.US, x, y)

    companion object {
        const val EPS: Double = 1e-9
        fun byAngle(angle: Double) = Vector2(cos(angle), sin(angle))
        fun byAngleSize(angle: Double, size: Double) = byAngle(angle) * size
    }
}

// Allow scalar * vector
operator fun Double.times(v: Vector2) = v * this
operator fun Int.times(v: Vector2) = v * this

fun normalize(v: Vector2) = v / v.size

/** Vetor perpendicular (rotaciona +90°) */
fun perp(v: Vector2): Vector2 = Vector2(-v.y, v.x)

/** Limita a magnitude do vetor para no máximo `max` */
fun truncate(v: Vector2, max: Double): Vector2 {
    return if (v.sizeSqr <= max * max) v else normalize(v) * max
}

/** Interpolação linear entre dois vetores */
fun lerp(a: Vector2, b: Vector2, t: Double): Vector2 = a + (b - a) * t.coerceIn(0.0, 1.0)

/** Distância entre dois vetores */
fun distance(a: Vector2, b: Vector2): Double = (b - a).size

/** Rotaciona um vetor por `theta` radianos */
fun rotate(v: Vector2, theta: Double): Vector2 {
    val c = cos(theta)
    val s = sin(theta)
    return Vector2(
        x = v.x * c - v.y * s,
        y = v.x * s + v.y * c
    )
}

fun resize(v: Vector2, size: Double): Vector2 =
    if (v.isZero) Vector2(1.0, 0.0)
    else v * (size / v.size)

private fun q8(v: Double): Long {
    // If you expect only finite values, you can drop this branch.
    if (!v.isFinite()) {
        return when {
            v.isNaN() -> 0L
            v > 0 -> Long.MAX_VALUE
            else -> Long.MIN_VALUE
        }
    }
    val scale = 8
    val bd = BigDecimal.valueOf(v).setScale(scale, RoundingMode.HALF_UP)
    return bd.movePointRight(scale).longValueExact()
}
