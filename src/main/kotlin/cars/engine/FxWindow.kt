package cars.engine

import cars.student.Setup
import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlin.math.min

class FxWindow : Application() {
    private lateinit var canvas: Canvas
    private lateinit var cars: List<Car>

    private var clickPos: Vector2? = null
    private var mousePos: Vector2? = null

    private var debugMode = true
    private lateinit var debugLabel: Label

    override fun start(stage: Stage) {
        cars = Setup.createCars()

        stage.title = "Steering behaviors"
        canvas = Canvas(INIT_WIDTH, INIT_HEIGHT)

        // Create label
        debugLabel = Label()
        debugLabel.textFill = Color.BLACK
        debugLabel.style = "-fx-font-size: 10px; -fx-background-color: rgba(255,255,255,0.0);"
        updateDebugLabel()

        // StackPane makes label float over canvas
        val root = StackPane(canvas, debugLabel)
        StackPane.setAlignment(debugLabel, Pos.BOTTOM_LEFT)   // pin top-left
        debugLabel.translateX = 10.0   // small padding
        debugLabel.translateY = -10.0

        val scene = Scene(root, INIT_WIDTH, INIT_HEIGHT)

        // Make stage resizable and keep canvas in sync with the scene size
        stage.isResizable = true
        canvas.widthProperty().bind(Bindings.selectDouble(scene.widthProperty()))
        canvas.heightProperty().bind(Bindings.selectDouble(scene.heightProperty()))

        scene.addEventFilter(MouseEvent.MOUSE_CLICKED) {
            clickPos = Vector2(
                x= it.x - canvas.width / 2.0,
                y= it.y - canvas.height / 2.0
            )
        }
        scene.addEventFilter(MouseEvent.MOUSE_MOVED) {
            mousePos = Vector2(
                x= it.x - canvas.width / 2.0,
                y= it.y - canvas.height / 2.0
            )
        }

        scene.setOnKeyPressed {
            if (it.text.lowercase() == "d") {
                debugMode = !debugMode
                updateDebugLabel()
            }
        }

        stage.scene = scene
        stage.show()

        startLoop()
    }

    private fun startLoop() {
        val g = canvas.graphicsContext2D

        var prevNanos = System.nanoTime()
        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                // clamp big spikes (e.g., window dragging)
                val secs = min((now - prevNanos) / 1000000000.0, 0.1)
                draw(g)
                update(secs)
                prevNanos = now
            }
        }
        timer.start()
    }

    private fun update(secs: Double) {
        val width = canvas.width
        val height = canvas.height
        cars.forEach {
            it.update(
                World(
                    secs = secs,
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
        val w = canvas.width
        val h = canvas.height

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

        cars.forEach { it.draw(g, debugMode) }
        g.restore()
    }

    private fun updateDebugLabel() {
        debugLabel.text = "Press D to turn debug arrows " + if (debugMode) "off" else "on"
    }

    companion object {
        private const val INIT_WIDTH = 1024.0
        private const val INIT_HEIGHT = 768.0
    }
}
