package cars.student;

import cars.engine.Car;
import cars.engine.Vector2;
import cars.engine.World;
import javafx.scene.paint.Color;

import static cars.engine.Vector2.*;
import static java.lang.Math.toRadians;

public class StudentCar extends Car {
    public StudentCar() {
        super(
            Color.RED,           // Cor (JavaFX)
            new Vector2(0, 0),   // Posição inicial
            toRadians(0)               // Ângulo inicial
        );
    }

    /**
     * Deve calcular o steering behavior para esse carro
     * O parâmetro world contém diversos métodos utilitários:
     * - world.getClickPos(): posição do último click (ou null)
     * - world.getMousePos(): posição atual do cursor do mouse
     * - world.getNeighbors([radius]): carros vizinhos (exclui o próprio carro)
     * - world.getSecs(): segundos desde o último quadro
     * - world.getWidth()/getHeight(): dimensões atuais do canvas
     *
     * Métodos úteis do carro:
     * - getDirection(): vetor unitário com a direção do veículo
     * - getPosition(): posição do carro
     */
    @Override
    public Vector2 calculateSteering(final World world) {
        // Exemplo básico (por enquanto, sem steering):
        return vec2();
    }
}
