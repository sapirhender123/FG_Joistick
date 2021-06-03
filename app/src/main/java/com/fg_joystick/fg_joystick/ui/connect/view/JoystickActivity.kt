package com.fg_joystick.fg_joystick.ui.connect.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fg_joystick.fg_joystick.R
import kotlinx.android.synthetic.main.activity_joystick.*


class JoystickActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listener = View.OnTouchListener(function = { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                view.y = motionEvent.rawY - view.height
                view.x = motionEvent.rawX - view.width/2
            }

            true
        })

        setContentView(R.layout.activity_joystick)
        imageView.setOnTouchListener(listener)
    }
}