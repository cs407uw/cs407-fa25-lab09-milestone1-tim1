package com.cs407.lab09

class Ball(val backgroundWidth: Float, val backgroundHeight: Float, val ballSize: Float) {

    // Initial position in the center of the screen
    private val initialPosX = (backgroundWidth - ballSize) / 2f
    private val initialPosY = (backgroundHeight - ballSize) / 2f

    var posX: Float = initialPosX
    var posY: Float = initialPosY

    // Initial velocity
    private var velX: Float = 0f
    private var velY: Float = 0f

    /**
     * Updates the ball's position and velocity based on acceleration and time delta.
     * It also handles collisions with the screen boundaries.
     */
    fun updatePositionAndVelocity(xAcc: Float, yAcc: Float, dT: Float) {

        // Update velocity based on acceleration and time.
        velX += xAcc * dT
        velY += yAcc * dT

        // Update position based on velocity and time.
        posX += velX * dT
        posY += velY * dT

        // --- Boundary Collision Detection ---

        // Left boundary
        if (posX < 0) {
            posX = 0f
            velX = -velX * 0.8f // Reflect and dampen
        }

        // Right boundary
        if (posX + ballSize > backgroundWidth) {
            posX = backgroundWidth - ballSize
            velX = -velX * 0.8f // Reflect and dampen
        }

        // Top boundary
        if (posY < 0) {
            posY = 0f
            velY = -velY * 0.8f // Reflect and dampen
        }

        // CRITICAL FIX: The bottom boundary should be the full height of the background,
        // not half of it.
        if (posY + ballSize > backgroundHeight) {
            posY = backgroundHeight - ballSize
            velY = -velY * 0.8f // Reflect and dampen
        }
    }

    /**
     * Resets the ball to its initial state.
     */
    fun reset() {
        posX = initialPosX
        posY = initialPosY
        velX = 0f
        velY = 0f
    }
}
