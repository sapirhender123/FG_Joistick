package com.fg_joystick.fg_joystick.ui.connect.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.fg_joystick.fg_joystick.R
import kotlinx.android.synthetic.main.activity_joystick.*


class JoystickActivity : AppCompatActivity() {

    private var joystickX: Float = 0.0f
    private var joystickY: Float = 0.0f
    private var metrics: DisplayMetrics = DisplayMetrics()

    private val margin: Float = 200.0f
    private val topMargin: Float = (margin / 2.0f)
    private var bottomMargin: Float = 0.0f
    private val leftMargin: Float = margin
    private var rightMargin: Float = 0.0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joystick)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = display
            display?.getRealMetrics(metrics)
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(metrics)
        }

        bottomMargin = (metrics.heightPixels - margin * 1.6f) / 2
        rightMargin = (metrics.widthPixels - margin / 4) / 2

        val touchListener = View.OnTouchListener(function = { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                view.y = topMargin.coerceAtLeast(motionEvent.rawY - view.height)
                    .coerceAtMost(bottomMargin)

                view.x = leftMargin.coerceAtLeast(motionEvent.rawX - view.width / 2)
                    .coerceAtMost(rightMargin)

                // Update the viewModel
                // Clamp values to range [-1, 1]
                onChanged(
                    (joystickY - view.y) / ((bottomMargin - topMargin) / 2.0f),
                    (view.x - joystickX) / ((rightMargin - leftMargin) / 2.0f)
                )
            }

            true
        })
        joystickView.setOnTouchListener(touchListener)

        // Create tree observer in order to know the initial position of the joystick
        val vto: ViewTreeObserver = joystickView.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                joystickView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                joystickY = joystickView.y
                joystickX = joystickView.x
                Log.d("X and Y Point", "%s %s".format(joystickX.toString(), joystickY.toString()))
            }
        })

        seekBarThrottle.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                // [0, 1]
                Log.d("seekBarThrottle", (progress / 100.0f).toString())
                // viewModel.setThrottle(progress / 100.0f)
            }
        })

        seekBarRadder.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                // [-1, 1]
                // 0 -> progress=100
                Log.d("seekBarRadder", ((progress - 100) / 100.0f).toString())
                // viewModel.setRadder((progress - 100) / 100.0f))
            }
        })
    }

    private fun onChanged(a: Float, e: Float) {
        // viewModel.setAileron(a)
        // viewModel.setElevator(e)
        Log.d("joystickView", "Aileron: %s, Elevator: %s".format(a.toString(), e.toString()))
    }
}