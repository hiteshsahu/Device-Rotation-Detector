package com.hiteshsahu.phoneflip

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import java.math.RoundingMode
import java.text.DecimalFormat

class SensorViewModel : BaseObservable() {

    //Sensor Logic
    private var sensorManager: SensorManager? = null
    private var accelerometerSensor: Sensor? = null
    private var accelerometerPresent = false

    //Internal States
    private var currentState: DeviceStates = DeviceStates.UNKNOWN
    private var currentSensorValue = -1000f;
    private lateinit var nextState: DeviceStates

    // UI
    private var orientationValue: String = "--"
    private var sensorValue: String = currentState.toString()

    enum class DeviceStates {
        FACE_UP,
        FACE_DOWN,
        UNKNOWN;

        override fun toString(): String {
            return when (this) {
                FACE_DOWN -> "Face Down"
                FACE_UP -> "Face Up"
                else -> "UNKNOWN"
            }
        }
    }

    @Bindable
    fun getSensorValue(): String {
        return sensorValue
    }

    @Bindable
    fun getOrientationValue(): String {
        return orientationValue
    }

    fun updateSensorData(zValue: String) {
        sensorValue = zValue
        notifyPropertyChanged(BR.sensorValue)
    }


    fun roundOffDecimal(number: Float): Float {
        val df = DecimalFormat("#.####")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble().toFloat()
    }

    private fun updateOrientationValue(message: String) {
        orientationValue = message
        notifyPropertyChanged(BR.orientationValue)
    }

    fun initSensor(context: Context) {
        sensorManager =
            (context.getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager?)?.apply {
                val sensorList: List<Sensor> = getSensorList(Sensor.TYPE_ACCELEROMETER)
                if (sensorList.isNotEmpty()) {
                    accelerometerPresent = true
                    accelerometerSensor = sensorList[0]
                } else {
                    accelerometerPresent = false
                    updateSensorData("No Accelerometer Present in the Device!")
                }
            }
    }

    fun subscribeSensorData() {
        if (accelerometerPresent) {
            sensorManager?.registerListener(
                accelerometerListener,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun unsubscribeSensorData() {
        if (accelerometerPresent) {
            sensorManager?.unregisterListener(accelerometerListener)
        }
    }

    /** <ul>
     * <li> values[0]: Acceleration minus Gx on the x-axis </li>
     * <li> values[1]: Acceleration minus Gy on the y-axis </li>
     * <li> values[2]: Acceleration minus Gz on the z-axis </li>
     * </ul>
     */
    private val accelerometerListener: SensorEventListener = object : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {
            val zValue: Float = roundOffDecimal(event.values[2])

            if (currentSensorValue != zValue) {
                updateSensorData("ZIndex: $zValue")

                //Update State
                if (zValue > TILT_THRESHOLD) {
                    nextState = DeviceStates.FACE_UP
                } else if (zValue < -TILT_THRESHOLD) {
                    nextState = DeviceStates.FACE_DOWN
                }

                if (nextState != currentState) {
                    currentState = nextState
                    updateOrientationValue(currentState.toString())
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    companion object {
        private const val TILT_THRESHOLD = 5
    }
}