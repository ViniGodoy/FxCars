package cars.student;

import cars.engine.Car;

import java.util.List;

public class Setup {
    /**
     * Retorne uma lista com todos os carros que serão desenhados no exercício.
     */
    public List<Car> createCars() {
        return List.of(new StudentCar());
    }
}
