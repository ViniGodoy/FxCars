package cars.student

import cars.engine.Car
import cars.engine.Vector2
import cars.engine.World
import javafx.scene.paint.Color

class StudentCar : Car(
    color = Color.RED,
    position = Vector2()
) {
    /**
     * Deve calcular o steering behavior para esse carro
     *
     * O parâmetro world contém as propriedades do mundo:
     * - world.clickPos: posição do último click (ou null)
     * - world.mousePos: posição atual do cursor do mouse
     * - world.neighbors: carros vizinhos
     * - world.secs: segundos desde o último quadro
     * - world.width/height: dimensões atuais do canvas
     *
     * Métodos úteis do carro:
     * - direction: vetor unitário com a direção do veículo
     * - position: posição do carro
     */
    override fun calculateSteering(world: World): Vector2 {
        return Vector2()
    }
}
