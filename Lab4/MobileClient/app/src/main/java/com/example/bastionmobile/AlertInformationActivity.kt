package com.example.bastionmobile

import android.os.Bundle

import android.text.Html
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class AlertInformationActivity : BaseAfterLoginActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_information)

        val alertTextView: TextView = findViewById(R.id.messageAlertTextView)
        val messageTextView: TextView = findViewById(R.id.messageTextView)

        val message = intent.getStringExtra("message")
        val isAlert : Boolean = message?.contains("Відбій") == false

        message?.let {
            messageTextView.text = Html.fromHtml("<b>$it</b>", Html.FROM_HTML_MODE_COMPACT)
        }

        if (isAlert) {
            alertTextView.text = Html.fromHtml(getString(R.string.alert_information_alert_active), Html.FROM_HTML_MODE_COMPACT)
        } else {
            alertTextView.text = Html.fromHtml(getString(R.string.alert_information_alert_inactive), Html.FROM_HTML_MODE_COMPACT)
        }

        var bottomNav : BottomNavigationView = findViewById(R.id.bottom_navigation)
        setupBottomNavigationView(bottomNav)
        bottomNav.selectedItemId = R.id.navigation_alarms

    }
}
