package cars.student

import cars.engine.Car
import cars.engine.Vector2
import cars.engine.World
import cars.engine.distance
import cars.engine.randomAngle
import cars.engine.randomPosition
import javafx.scene.paint.Color

object Setup {
    /**
     * Retorne uma lista com todos os carros que serão desenhados no exercício
     */
    fun createCars(): List<Car> {


        // Wanders around. Ignores the pursuer
        val wanderer: Car = object: Car(color=Color.ORANGE) {
            private val wander = Wander(car=this, speed=200.0)
            override fun calculateSteering(world: World): Vector2 = wander.force()
        }


        // Pursuit the wanderer
        val pursuer: Car = object : Car(
            position = randomPosition(),
            color=Color.RED
        ) {
            override fun calculateSteering(world: World) = pursuit(this, wanderer)
        }

        // Approaches the mouse click.
        val approacher: Car = object : Car(
            position=randomPosition(),
            orientation = randomAngle(),
            color=Color.NAVY
        ) {
            override fun calculateSteering(world: World) = world.clickPos
                ?.let { arrive(this, it) }
                ?: Vector2()
        }


        // Flees from all cars closer than 300 pixels, otherwise wanders
        val coward: Car = object : Car(
            position = randomPosition(),
            color = Color.GREEN,
        ) {
            private val wander = Wander(car=this, speed=200.0)

            override fun calculateSteering(world: World) =
                world.neighbors
                    .filter { distance(this, it) < 300.0 }
                    .fold(Vector2()) { a, r -> a + flee(this, r.position) }
                    .let { if (it.size > 10) it else wander.force() }
        }

        return listOf(wanderer, pursuer, coward, approacher)
    }
}
