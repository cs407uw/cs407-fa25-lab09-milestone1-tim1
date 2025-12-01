package com.cs407.lab09

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.lab09.ui.theme.Lab09Theme
import kotlin.math.roundToInt

// Main Activity
class MainActivity : ComponentActivity() {

    private val viewModel: BallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun GameScreen(viewModel: BallViewModel) {
    val ballPosition by viewModel.ballPosition.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val ballSize = 50.dp

    // FIX: Capture density at composition level, not inside callbacks
    val density = LocalDensity.current
    val ballSizePx = with(density) { ballSize.toPx() }

    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    viewModel.onSensorDataChanged(it)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        sensorManager.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.field),
                contentScale = ContentScale.FillBounds
            )
            .onSizeChanged { size ->
                // FIX: Use the pre-calculated ballSizePx instead of calling LocalDensity here
                viewModel.initBall(
                    fieldWidth = size.width.toFloat(),
                    fieldHeight = size.height.toFloat(),
                    ballSizePx = ballSizePx
                )
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.soccer),
            contentDescription = "Soccer Ball",
            modifier = Modifier
                .offset {
                    IntOffset(
                        ballPosition.x.roundToInt(),
                        ballPosition.y.roundToInt()
                    )
                }
                .size(ballSize)
        )

        Column(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(16.dp)) {
            Button(onClick = { viewModel.reset() }) {
                Text("Reset")
            }
        }
    }
}