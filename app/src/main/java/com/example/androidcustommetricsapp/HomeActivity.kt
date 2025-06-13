package com.example.androidcustommetricsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * HomeActivity
 *
 * Main entry screen. Demonstrates navigation instrumentation for Sentry.
 * Each button click starts a navigation span/transaction for Sentry performance tracing.
 * See Tracer and BaseInstrumentedActivity for details on how navigation and screen performance are instrumented.
 */
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.btnAutoNTS).apply {
            text = "Auto TTID/TTFD + NTS (span, tap-to-open)"
            setOnClickListener {
                val tapTime = System.currentTimeMillis()
                val intent = Intent(this@HomeActivity, AutoTTIDTTFDWithNTSMeasurementActivity::class.java)
                intent.putExtra("tap_time", tapTime)
                startActivity(intent)
            }
        }
    }
}
