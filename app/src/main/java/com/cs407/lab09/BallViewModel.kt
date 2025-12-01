package com.cs407.lab09

import android.hardware.Sensor
import android.hardware.SensorEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BallViewModel : ViewModel() {

    // Scaling factor to adjust physics motion to screen size (pixels/m^2).
    // This value is crucial for realistic visual speed.
    private val SCALING_FACTOR = 500f // Adjusted for visual speed

    private var ball: Ball? = null
    private var lastTimestamp: Long = 0L

    // Expose the ball's position as a StateFlow
    private val _ballPosition = MutableStateFlow(Offset.Zero)
    val ballPosition: StateFlow<Offset> = _ballPosition.asStateFlow()

    /**
     * Called by the UI when the game field's size is known.
     * CRITICAL FIX: This function now re-initializes the ball whenever it's called.
     * This ensures that the ball's boundaries are updated with the final, correct layout size,
     * even if onSizeChanged is called multiple times.
     */
    fun initBall(fieldWidth: Float, fieldHeight: Float, ballSizePx: Float) {
        // The if-statement that prevented re-initialization has been removed.
        ball = Ball(
            backgroundWidth = fieldWidth,
            backgroundHeight = fieldHeight,
            ballSize = ballSizePx
        )
        _ballPosition.value = Offset(ball!!.posX, ball!!.posY)
    }

    /**
     * Called by the SensorEventListener in the UI.
     */
    fun onSensorDataChanged(event: SensorEvent) {
        // Ensure ball is initialized
        val currentBall = ball ?: return

        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            val currentTimestamp = event.timestamp

            if (lastTimestamp != 0L) {
                // Calculate the time difference (dT) in seconds
                val NS2S = 1.0f / 1000000000.0f
                val dT = (currentTimestamp - lastTimestamp) * NS2S

                // Sensor X-axis (event.values[0])
                val xAcc = -event.values[0] * SCALING_FACTOR

                // Sensor Y-axis (event.values[1])
                val yAcc = event.values[1] * SCALING_FACTOR

                // Update the ball's position and velocity
                currentBall.updatePositionAndVelocity(xAcc = xAcc, yAcc = yAcc, dT = dT)

                _ballPosition.update { Offset(currentBall.posX, currentBall.posY) }
            }

            lastTimestamp = currentTimestamp
        }
    }

    fun reset() {
        ball?.reset()

        ball?.let {
            _ballPosition.value = Offset(it.posX, it.posY)
        }

        lastTimestamp = 0L
    }
}
