package cars.student

import cars.engine.Car

object Setup {
    /**
     * Retorne uma lista com todos os carros que serão desenhados no exercício
     */
    fun createCars(): List<Car> {
        return listOf(
            StudentCar()
        )
    }
}
