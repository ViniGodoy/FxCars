package cars.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.math.sqrt

class Vector2Test {

    private fun assertVec(v: Vector2, x: Double, y: Double, tol: Double = 1e-12) {
        v.x shouldBe x.plusOrMinus(tol)
        v.y shouldBe y.plusOrMinus(tol)
    }

    // ---------- Core construction & basics ----------

    @Test
    fun `constructors and getters`() {
        val z = Vector2()
        assertVec(z, 0.0, 0.0)

        val v = Vector2(3.0, 4.0)
        assertVec(v, 3.0, 4.0)
        v.size shouldBe 5.0.plusOrMinus(1e-12)
        v.sizeSqr shouldBe 25.0.plusOrMinus(1e-12)
    }

    @Test
    fun `indexing get and set`() {
        val v = Vector2(7.0, -2.0)
        v[0] shouldBe 7.0.plusOrMinus(0.0)
        v[1] shouldBe (-2.0).plusOrMinus(0.0)
        shouldThrow<IndexOutOfBoundsException> { v[2] }
    }

    // ---------- Operators ----------

    @Test
    fun `unary and binary operators`() {
        val a = Vector2(1.0, 2.0)
        val b = Vector2(3.0, 5.0)

        assertVec(-a, -1.0, -2.0)
        assertVec(a + b, 4.0, 7.0)
        assertVec(b - a, 2.0, 3.0)

        assertVec(a * 2.0, 2.0, 4.0)
        assertVec(a / 2.0, 0.5, 1.0)

        // Double * Vector2 extension
        assertVec(2.0 * a, 2.0, 4.0)
    }

    // ---------- Predicates & equality ----------

    @Test
    fun `isZero and isUnit`() {
        val u = Vector2.byAngle(0.0) // (1,0)
        u.isUnit shouldBe true
        u.isZero shouldBe false

        val tiny = Vector2(Vector2.EPS / 2.0, 0.0)
        tiny.isZero shouldBe true
    }

    @Test
    fun `epsilon equals and hashCode consistency`() {
        val a = Vector2(1.0, 1.0)
        val b = Vector2(1.0 + Vector2.EPS / 2.0, 1.0 - Vector2.EPS / 2.0)
        (a == b) shouldBe true
        a.hashCode() shouldBe b.hashCode()

        val c = Vector2(1.0 + Vector2.EPS * 10, 1.0)
        (a == c) shouldBe false
        a.hashCode() shouldNotBe c.hashCode()
    }

    // ---------- Factories & helpers (class) ----------

    @Test
    fun `byAngle and byAngleSize`() {
        val v = Vector2.byAngle(PI / 4)
        v.size shouldBe 1.0.plusOrMinus(1e-12)

        val w = Vector2.byAngleSize(PI / 4, 5.0)
        w.size shouldBe 5.0.plusOrMinus(1e-12)
    }

    // ---------- Top-level helpers (below class) ----------

    @Test
    fun `top-level normalize returns normalized clone without mutating original`() {
        val v = Vector2(0.0, 10.0)
        val n = normalize(v)
        // result
        n.size shouldBe 1.0.plusOrMinus(1e-12)
        assertVec(n, 0.0, 1.0)
        // original unchanged
        assertVec(v, 0.0, 10.0)
    }

    @Test
    fun `perp rotates +90 degrees`() {
        val v = Vector2(2.0, 3.0)
        val p = perp(v)
        assertVec(p, -3.0, 2.0)
    }

    @Test
    fun `truncate returns clone if short enough, or max-length vector if too long, and does not mutate input`() {
        val short = Vector2(3.0, 4.0) // len 5
        val t1 = truncate(short, 6.0)
        assertVec(t1, 3.0, 4.0)
        t1 shouldBeSameInstanceAs short
        // input unchanged
        assertVec(short, 3.0, 4.0)

        val long = Vector2(10.0, 0.0)
        val t2 = truncate(long, 5.0)
        assertVec(t2, 5.0, 0.0)
        t2 shouldNotBeSameInstanceAs long
        // input should remain unchanged by a non-mutating helper
        assertVec(long, 10.0, 0.0)
    }

    @Test
    fun `lerp clamps t and interpolates linearly`() {
        val a = Vector2(0.0, 0.0)
        val b = Vector2(10.0, 10.0)
        assertVec(lerp(a, b, 0.5), 5.0, 5.0)
        // clamp below 0
        assertVec(lerp(a, b, -1.0), 0.0, 0.0)
        // clamp above 1
        assertVec(lerp(a, b, 2.0), 10.0, 10.0)
    }

    @Test
    fun `distance computes Euclidean distance`() {
        val a = Vector2(1.0, 1.0)
        val b = Vector2(4.0, 5.0)
        distance(a, b) shouldBe sqrt(3.0 * 3.0 + 4.0 * 4.0).plusOrMinus(1e-12)
    }

    @Test
    fun `rotate returns a new rotated vector`() {
        val r90 = rotate(Vector2(1.0, 0.0), PI / 2)
        assertVec(r90, 0.0, 1.0, tol = 1e-9)

        val r180 = rotate(Vector2(2.0, 3.0), PI)
        assertVec(r180, -2.0, -3.0, tol = 1e-9)
    }

    @Test
    fun `resize returns resized clone without mutating original`() {
        val v = Vector2(3.0, 4.0) // size 5
        val r = resize(v, 20.0)
        r.size shouldBe 20.0.plusOrMinus(1e-12)
        // direction preserved
        assertVec(r, 12.0, 16.0)
        // original unchanged
        v.size shouldBe 5.0.plusOrMinus(1e-12)
        assertVec(v, 3.0, 4.0)
    }

    // ---------- toString ----------

    @Test
    fun `toString prints two decimals (locale tolerant)`() {
        val s = Vector2(3.14159, 2.71828).toString()
        // Allow , or . as decimal separator depending on default locale
        s shouldBe "(3.14, 2.72)"
    }
}
