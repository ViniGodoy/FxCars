package cars.student

import cars.engine.Car
import cars.engine.RND
import cars.engine.Vector2
import cars.engine.Vector2.Companion.byAngleSize
import cars.engine.resize
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.min

/**
 * Move-se em direção ao alvo, na velocidade desejada.
 * @param car Veículo que irá se mover
 * @param target Alvo.
 * @param speed Velocidade desejada do movimento. Por padrão a velocidade máxima do veículo.
 * @return Steering force
 */
fun seek(car: Car, target: Vector2, speed: Double = car.maxSpeed): Vector2 {
    val desiredVelocity = resize(target - car.position, speed)
    return desiredVelocity - car.velocity
}

/**
 * Foge da direção do alvo o mais rápido que puder, desde que este esteja no raio definido pela panicDistance..
 * @param car Veículo que irá fugir
 * @param target Alvo.
 * @param panicDistance Raio em que a fuga ocorre.
 * @param speed Velocidade desejada do movimento. Por padrão a velocidade máxima do veículo.
 * @return Steering force
 */
fun flee(car: Car, target: Vector2, panicDistance: Double = Double.MAX_VALUE, speed: Double = car.maxSpeed): Vector2 {
    var desiredVelocity = resize(car.position - target, speed)
    val size = desiredVelocity.size
    if (size > panicDistance) {
        return Vector2()
    }

    //Não precisamos renormalizar com resize, já que já calculamos size
    desiredVelocity *= car.maxForce / size
    return desiredVelocity - car.velocity
}

/**
 * Faz com que o carro desacelere até o alvo
 * @param car Quem realizará o arrive
 * @param target Destino
 * @param deceleration Fator de desaceleração
 * @param stopDistance Distância em que considerará que chegou.
 * @return Steering force
 */
fun arrive(car: Car, target: Vector2, deceleration: Double = 1.0, stopDistance: Double = 5.0): Vector2 {
    val toTarget = target - car.position
    val dist = toTarget.size
    if (dist < stopDistance) {
        return Vector2()
    }

    val speed = min(dist / deceleration, car.maxSpeed)
    val desiredVelocity = target * (speed / dist)
    return desiredVelocity - car.velocity
}

/**
 * Tenta interceptar outro veículo, usando para isso sua posição futura.
 *
 * @param pursuer Perseguidor
 * @param evader Veículo fugitivo
 * @return A steering force do perseguidor.
 */
fun pursuit(pursuer: Car, evader: Car): Vector2 {
    val toEvader = evader.position - pursuer.position

    // Se o fugitivo está diretamente a frente vindo em nossa direção
    // Podemos só fazer um seek para cima dele
    val isAhead = toEvader.dot(pursuer.direction) > 0
    val isFacing = pursuer.direction.dot(evader.direction) < acos(Math.toRadians(18.0))
    if (isAhead && isFacing) {
        return seek(pursuer, evader.position)
    }

    // Se ele está indo para outra direção, estimamos sua posição futura
    val lookAheadTime = toEvader.size / (pursuer.maxSpeed * evader.speed)
    val futurePosition = evader.position + evader.velocity * lookAheadTime
    return seek(pursuer, futurePosition)
}

/**
 * Direciona o carro para um alvo posicionado a em um círculo a sua frente.
 * O alvo se mexe alguns graus sobre esse círculo para a esquerda ou direita a cada frame (jitter).
 */
class Wander constructor(
    private val car: Car,
    private val distance: Double = 120.0,
    radius: Double = 90.0,
    jitter: Double = 15.0,
    speed: Double? = 300.0,
) {
    private val radius: Double
    private val jitter: Double
    private var angle: Double
    private val speed: Double

    init {
        this.radius = abs(radius)
        this.jitter = abs(jitter)
        this.angle = RND.nextDouble(2 * Math.PI)
        this.speed = speed ?: car.maxSpeed
    }

    /**
     * @return A posição do alvo, após aplicada sua movimentação aleatória.
     */
    fun target(): Vector2 {
        angle += Math.toRadians(RND.nextDouble(-jitter, jitter))

        val circleOffset = car.direction * distance
        val targetPosInCircle = byAngleSize(angle, radius)
        return car.position + circleOffset + targetPosInCircle
    }

    /**
     * @return A steering force calculada, isto é, um seek até a posição calculada do alvo.
     * @see .target
     * @see seek
     */
    fun force(): Vector2 {
        return seek(car, target())
    }
}
