package cars.engine

class World(
    val secs: Double,
    val width: Double,
    val height: Double,

    val mousePos: Vector2?,
    val clickPos: Vector2?,

    private val current: Car,
    private val cars: List<Car>,
) {
    fun getNeighbors() =
        cars.filter { it !== current }

    fun getNeighbors(radius: Int = Int.MAX_VALUE) =
        getNeighbors().filter { distance(current.position, it.position) <= radius }
}
