package cars.engine

import cars.student.Setup
import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage

class FxWindow : Application() {
    private var cars: List<Car> = listOf()
    private var clickPos: Vector2? = null // relative to center
    private var mousePos: Vector2? = null // absolute scene coords
    private var canvas: Canvas? = null

    override fun start(stage: Stage) {
        stage.title = "Steering behaviors"

        canvas = Canvas(INIT_WIDTH, INIT_HEIGHT)

        val root = Pane(canvas)
        val scene = Scene(root, INIT_WIDTH, INIT_HEIGHT)

        // Make stage resizable and keep canvas in sync with the scene size
        stage.isResizable = true
        canvas!!.widthProperty().bind(Bindings.selectDouble(scene.widthProperty()))
        canvas!!.heightProperty().bind(Bindings.selectDouble(scene.heightProperty()))

        // Input handlers (use CURRENT canvas size)
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED) {
            val cx = canvas!!.width / 2.0
            val cy = canvas!!.height / 2.0
            clickPos = Vector2(it.x - cx, it.y - cy)
        }
        scene.addEventFilter(MouseEvent.MOUSE_MOVED) {
            mousePos = Vector2(it.x, it.y)
        }

        cars = Setup.createCars()

        stage.scene = scene
        stage.show()

        startLoop()
    }

    private fun startLoop() {
        val g = canvas!!.graphicsContext2D

        val prevNanos = longArrayOf(System.nanoTime())
        val timer: AnimationTimer = object : AnimationTimer() {
            override fun handle(now: Long) {
                var dt = (now - prevNanos[0]) / 1000000000.0
                // clamp big spikes (e.g., window dragging)
                if (dt > 0.1) dt = 0.1
                prevNanos[0] = now

                draw(g)
                update(dt)
            }
        }
        timer.start()
    }

    private fun update(time: Double) {
        val width = canvas!!.width
        val height = canvas!!.height
        cars.forEach {
            it.update(
                World(
                    secs = time,
                    width = width,
                    height = height,
                    mousePos = mousePos,
                    clickPos = clickPos,
                    current = it,
                    cars = cars
                )
            )
        }
    }

    private fun draw(g: GraphicsContext) {
        val w = canvas!!.width
        val h = canvas!!.height

        // Clear
        g.fill = Color.rgb(220, 220, 220)
        g.fillRect(0.0, 0.0, w, h)

        // Translate origin to the center (dynamic)
        g.save()
        g.translate(w / 2.0, h / 2.0)

        clickPos?.let {
            g.fill = Color.GRAY
            g.fillOval(it.x - 4, it.y - 4, 8.0, 8.0)
        }

        cars.forEach { it.draw(g) }
        g.restore()
    }

    companion object {
        private const val INIT_WIDTH = 1024.0
        private const val INIT_HEIGHT = 768.0
    }
}
