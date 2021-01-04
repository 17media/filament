package com.google.android.filament.utils

import androidx.annotation.Size
import com.google.android.filament.Camera
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class CameraController() {
    var cameraCurrentPosition = CameraPosition(5.0, 0.0, 0.0)
    private val cameraHomePosition = CameraPosition(5.0, 0.0, 0.0)
    private val cameraHeadPosition = CameraPosition(5.0, 0.0, 0.0)

    private val target: Float3 = Float3(0.0f, 0.0f, -4.0f)
    private val upVec: Float3 = Float3(0.0f, 1.0f, 0.0f)

    private var modelX: Float = 0.0f
    private var modelY: Float = 0.0f
    private var modelZ: Float = 0.0f

    fun setModelBox(x: Float, y: Float, z: Float) {
        modelX = x
        modelY = y
        modelZ = z
    }

    fun setCameraHomePosition(aspect: Double, kFovDegrees: Double, direction: Camera.Fov, modelRatio: Float = 1.0f, headRatio: Float = 1.0f) {
        val verticalDegrees = when (direction) {
            Camera.Fov.VERTICAL -> {
                radians((kFovDegrees / 2.0).toFloat())
            }
            Camera.Fov.HORIZONTAL -> {
                atan(tan(radians((kFovDegrees / 2.0).toFloat())) / aspect.toFloat())
            }
        }

        val surfaceDepth = modelY / (2 * modelRatio * tan(verticalDegrees))
        val shiftHeight = surfaceDepth * tan(verticalDegrees) - modelY / 2.0

        val maxHalfModelThick = kotlin.math.max(modelX, modelZ) / 2.0
        cameraHomePosition.radius = (surfaceDepth + maxHalfModelThick)
        cameraHomePosition.degree = 0.0
        cameraHomePosition.height = shiftHeight

        val headSurfaceDepth = (modelY / 2) / (2 * headRatio * tan(verticalDegrees))
        val headShiftHeight = modelY / 4

        cameraHeadPosition.radius = headSurfaceDepth + maxHalfModelThick
        cameraHeadPosition.degree = 0.0
        cameraHeadPosition.height = headShiftHeight.toDouble()

        cameraCurrentPosition = cameraHomePosition.copy()
    }

    fun lookAtHead() {
        cameraCurrentPosition = cameraHeadPosition.copy()
    }

    fun lookAtHome() {
        cameraCurrentPosition = cameraHomePosition.copy()
    }

    fun getLookAt(@Size(min = 3) eyePosition: DoubleArray,
                  @Size(min = 3) targetPosition: DoubleArray,
                  @Size(min = 3) upward: DoubleArray) {
        eyePosition[0] = this.target[0] + cameraCurrentPosition.radius * sin(radians(cameraCurrentPosition.degree.toFloat()))
        eyePosition[1] = this.target[1] + cameraCurrentPosition.height
        eyePosition[2] = this.target[2] + cameraCurrentPosition.radius * cos(radians(cameraCurrentPosition.degree.toFloat()))
        targetPosition[0] = this.target[0].toDouble()
        targetPosition[1] = this.target[1].toDouble() + cameraCurrentPosition.height
        targetPosition[2] = this.target[2].toDouble()
        upward[0] = upVec[0].toDouble()
        upward[1] = upVec[1].toDouble()
        upward[2] = upVec[2].toDouble()
    }
}