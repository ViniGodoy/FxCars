package cars.engine

import cars.engine.Vector2.Companion.byAngle
import cars.engine.Vector2.Companion.byAngleSize
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop

abstract class Car(
    private val color: Color,
    private var _position: Vector2,
    orientation: Double = 0.0,
    val mass: Double = 1.0,
    val maxForce: Double = 350.0,
    val maxSpeed: Double = 500.0
) : Cloneable {
    private var _velocity = byAngle(orientation)
    private var lastSteering = Vector2()

    // ---- Accessors ----
    val position: Vector2 get() = _position
    val velocity: Vector2 get() = _velocity
    val direction: Vector2 get() = if (_velocity.isZero) byAngle(0.0) else normalize(_velocity)

    abstract fun calculateSteering(world: World): Vector2

    fun update(world: World) {
        val steeringForce = calculateSteering(world)
        lastSteering = truncate(steeringForce, maxForce)

        val impulse = lastSteering * world.secs
        val acceleration = impulse / mass
        _velocity = truncate(_velocity + acceleration, maxSpeed)
        _position += _velocity * world.secs

        val w = world.width / 2.0
        if (_position.x < -(w + 20)) _position = _position.copy(x = w)
        else if (_position.x > (w + 20)) _position = _position.copy(x = -w)

        val h = world.height / 2.0
        if (_position.y < -(h + 20)) _position = _position.copy(y = h)
        else if (_position.y > (h + 20)) _position = _position.copy(y = -h)
    }

    // -----------------------------------------------------
    // Drawing
    // -----------------------------------------------------
    fun draw(g: GraphicsContext) {
        g.save()
        g.translate(_position.x, _position.y)
        g.rotate(Math.toDegrees(_velocity.angle))

        g.save()
        g.scale(-0.5, 0.5)
        drawF1Car80px(g, color)
        g.restore()

        g.restore() // back to world space

        drawDebugArrows(g)
    }

    // ----- Debug arrows -----
    private fun drawDebugArrows(g: GraphicsContext) {
        drawArrow(g, _velocity, Color.BLUE)
        drawArrow(g, lastSteering, Color.ORANGERED)
    }

    private fun drawArrow(g: GraphicsContext, vector: Vector2?, color: Color?) {
        if (vector?.isZero ?: true) return

        val origin = _position + direction * 12.0
        val tip = origin + vector * 0.2
        val dir = tip - origin

        g.save()
        g.globalAlpha = 0.25
        g.stroke = color
        g.lineWidth = 3.0

        g.strokeLine(origin.x, origin.y, tip.x, tip.y)

        val angle = dir.angle
        val a = Math.toRadians(30.0)
        val p1 = tip - byAngleSize(angle - a, 8.0)
        val p2 = tip - byAngleSize(angle + a, 8.0)
        g.strokeLine(tip.x, tip.y, p1.x, p1.y)
        g.strokeLine(tip.x, tip.y, p2.x, p2.y)

        g.globalAlpha = 1.0
        g.restore()
    }

    public override fun clone() = super.clone() as Car

    companion object {
        // ----- F1 car, authored at 80px long (drawn above with scale 0.5) -----
        private fun drawF1Car80px(g: GraphicsContext, bodyColor: Color) {
            val l = 80.0
            val w = 36.0
            val halfL = l / 2.0
            val halfW = w / 2.0

            val wingColor = Color.DARKGRAY
            val tireColor = Color.BLACK

            val bodyGrad = LinearGradient(
                -halfL, 0.0, halfL, 0.0, false, CycleMethod.NO_CYCLE,
                Stop(0.0, bodyColor.darker()),
                Stop(0.5, bodyColor),
                Stop(1.0, bodyColor.darker())
            )

            g.fill = tireColor
            val tireW = 12
            val tireH = 7
            val frontX = halfL * 0.55
            val rearX = -halfL * 0.55
            g.fillRoundRect(frontX - tireW / 2, -halfW, tireW.toDouble(), tireH.toDouble(), 3.0, 3.0)
            g.fillRoundRect(frontX - tireW / 2, halfW - 7, tireW.toDouble(), tireH.toDouble(), 3.0, 3.0)
            g.fillRoundRect(rearX - tireW / 2, -halfW, tireW.toDouble(), tireH.toDouble(), 3.0, 3.0)
            g.fillRoundRect(rearX - tireW / 2, halfW - 7, tireW.toDouble(), tireH.toDouble(), 3.0, 3.0)

            g.fill = bodyGrad
            g.fillRoundRect(-halfL * 0.45, -w * 0.22, l * 0.70, w * 0.44, 6.0, 6.0)
            g.fillRoundRect(-halfL * 0.45, -w * 0.10, l * 0.50, w * 0.20, 10.0, 10.0)

            g.fill = bodyColor.darker()
            g.fillRoundRect(-l * 0.10, -w * 0.34, l * 0.30, w * 0.20, 6.0, 6.0)
            g.fillRoundRect(-l * 0.10, w * 0.14, l * 0.30, w * 0.20, 6.0, 6.0)

            g.fill = Color.rgb(25, 25, 32, 0.9)
            g.fillOval(-l * 0.12, -w * 0.18, l * 0.28, w * 0.36)

            g.fill = wingColor
            g.fillRoundRect(halfL - 10, -w * 0.70, 12.0, w * 1.40, 6.0, 6.0)
            g.fillRoundRect(-halfL - 4, -w * 0.50, 8.0, w * 0.98, 6.0, 6.0)

            g.stroke = Color.rgb(255, 255, 255, 0.28)
            g.lineWidth = 2.0
            g.strokeLine(-halfL, 0.0, halfL, 0.0)
        }
    }
}
