package com.example.fg_joystick.ui.connect

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import com.example.fg_joystick.R
import com.example.fg_joystick.ui.connect.view_model.ConnectionViewModel
import com.example.fg_joystick.ui.connect.view_model.ConnectionViewModelFactory

class ConnectionActivity : AppCompatActivity() {

    private lateinit var connectionViewModel: ConnectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_connect)

        val ip = findViewById<EditText>(R.id.IP)
        val port = findViewById<EditText>(R.id.Port)
        val connect = findViewById<Button>(R.id.connect)
        val loading = findViewById<ProgressBar>(R.id.loading)

        connectionViewModel = ViewModelProviders.of(this,
            ConnectionViewModelFactory()
        )
                .get(ConnectionViewModel::class.java)

        connectionViewModel.connectionFormState.observe(this@ConnectionActivity, Observer {
            val connectionState = it ?: return@Observer

            // disable connect button unless both username / password is valid
            connect.isEnabled = connectionState.isDataValid

            if (connectionState.ipError != null) {
                ip.error = getString(connectionState.ipError)
            }
            if (connectionState.portError != null) {
                port.error = getString(connectionState.portError)
            }
        })

        connectionViewModel.connectionResult.observe(this@ConnectionActivity, Observer {
            val connectionResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (connectionResult.error != null) {
                showConnectionFailed(connectionResult.error)
            }
            if (connectionResult.success != null) {
                updateUiWithUser(connectionResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy connect activity once successful
            finish()
        })

        ip.afterTextChanged {
            connectionViewModel.connectionDataChanged(
                    ip.text.toString(),
                    port.text.toString()
            )
        }

        port.apply {
            afterTextChanged {
                connectionViewModel.connectionDataChanged(
                        ip.text.toString(),
                        port.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        connectionViewModel.connect(
                                ip.text.toString(),
                                port.text.toString()
                        )
                }
                false
            }

            connect.setOnClickListener {
                loading.visibility = View.VISIBLE
                connectionViewModel.connect(ip.text.toString(), port.text.toString())
            }
        }
    }

    private fun updateUiWithUser(model: ConnectedUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
                applicationContext,
                "$welcome $displayName",
                Toast.LENGTH_LONG
        ).show()
    }

    private fun showConnectionFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}