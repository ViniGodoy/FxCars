package cars.engine

import cars.engine.FxWindow.Companion.INITIAL_HEIGHT
import cars.engine.FxWindow.Companion.INITIAL_WIDTH
import java.util.random.RandomGenerator

val RND: RandomGenerator = RandomGenerator.getDefault()

fun randomPosition(w: Double=INITIAL_WIDTH, h: Double=INITIAL_HEIGHT): Vector2 {
    val hw = w / 2.0;
    val hh = h / 2.0;
    return Vector2(RND.nextDouble(-hw, hw), RND.nextDouble(-hh, hh))
}

fun randomAngle() = RND.nextDouble(0.0, 2 * Math.PI)