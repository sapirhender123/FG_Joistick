package com.fg_joystick.fg_joystick.ui.connect.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.fg_joystick.fg_joystick.R
import com.fg_joystick.fg_joystick.ui.connect.view_model.JoystickViewModel
import com.fg_joystick.fg_joystick.ui.connect.view_model.JoystickViewModelFactory
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

    private var joystick : Joystick = Joystick()
    private lateinit var joystickViewModel : JoystickViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joystick)

        // https://stackoverflow.com/questions/48284994/lambda-implementation-of-interface-in-kotlin
        // Anonymous object instead of Java SAM
        joystick.onChange = object : OnJoystickChange {
            override fun onChange(a: Float, e: Float) {
                joystickViewModel.setAileron(a)
                joystickViewModel.setElevator(e)
            }
        }

        joystickViewModel = ViewModelProviders.of(this,
            JoystickViewModelFactory()
        ).get(JoystickViewModel::class.java)

        // Get screen metrics
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = display
            display?.getRealMetrics(metrics)
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(metrics)
        }

        // Calculate margins
        bottomMargin = (metrics.heightPixels - margin * 1.6f) / 2
        rightMargin = (metrics.widthPixels - margin / 4) / 2

        // Assign touch listener
        val touchListener = View.OnTouchListener(function = { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                view.y = topMargin.coerceAtLeast(motionEvent.rawY - view.height)
                    .coerceAtMost(bottomMargin)

                view.x = leftMargin.coerceAtLeast(motionEvent.rawX - view.width / 2)
                    .coerceAtMost(rightMargin)

                // Update the viewModel
                // Clamp values to range [-1, 1]
                (joystick.onChange as OnJoystickChange).onChange(
                    (view.x - joystickX) / ((rightMargin - leftMargin) / 2.0f),
                    (joystickY - view.y) / ((bottomMargin - topMargin) / 2.0f)
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
            }
        })

        seekBarThrottle.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                // [0, 1]
                Throttle.text = "Throttle: " + progress / 100.0f
                joystickViewModel.setThrottle(progress / 100.0f)
            }
        })

        seekBarRudder.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                // [-1, 1]
                // 0 -> progress=100
                Rudder.text = "Rudder: " + ((progress - 100) / 100.0f)
                joystickViewModel.setRudder((progress - 100) / 100.0f)
            }
        })

        Disconnect.setOnClickListener {
            joystickViewModel.disconnect()
            val switchActivityIntent = Intent(this, ConnectionActivity::class.java)
            startActivity(switchActivityIntent)
        }
    }
}