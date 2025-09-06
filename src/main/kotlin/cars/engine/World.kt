package cars.engine

class World(
    val secs: Double,
    val width: Double,
    val height: Double,

    val mousePos: Vector2?,
    val clickPos: Vector2?,

    private val current: Car,
    cars: List<Car>,
) {
    val neighbors = cars.filter { it !== current }
}
