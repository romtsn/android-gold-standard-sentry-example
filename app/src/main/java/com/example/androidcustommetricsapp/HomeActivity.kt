package com.example.androidcustommetricsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.sentry.Sentry
import io.sentry.ITransaction
import io.sentry.SentryInstantDate
import java.time.Instant

/**
 * HomeActivity
 *
 * Main entry screen. Demonstrates navigation instrumentation for Sentry.
 * Each button click starts a navigation span/transaction for Sentry performance tracing.
 * See Tracer and BaseInstrumentedActivity for details on how navigation and screen performance are instrumented.
 */
class HomeActivity : AppCompatActivity() {
    private var appStartToInteractiveSpan: io.sentry.ISpan? = null
    private var appStartTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Get app start time from intent or fallback to static field
        appStartTime = intent.getLongExtra("app_start_time", MyApp.appStartTime)
        val transaction = Sentry.getSpan() as? ITransaction
        if (transaction != null && appStartTime > 0) {
            val startTimestamp = SentryInstantDate(Instant.ofEpochMilli(appStartTime))
            appStartToInteractiveSpan = transaction.startChild(
                "app.start_to_interactive",
                "App Start to Interactive",
                startTimestamp
            )
        }

        findViewById<Button>(R.id.btnAutoNTS).apply {
            text = "Auto TTID/TTFD + NTS (span, tap-to-open)"
            setOnClickListener {
                val tapTime = System.currentTimeMillis()
                val intent = Intent(this@HomeActivity, AutoTTIDTTFDWithNTSMeasurementActivity::class.java)
                intent.putExtra("tap_time", tapTime)
                intent.putExtra("screen_origin", "HomeActivity")
                startActivity(intent)
            }
        }
        // Mark Home screen as fully displayed for Sentry TTFD/cold start
        Sentry.reportFullyDisplayed()
        // Finish the custom span and set measurement
        val duration = System.currentTimeMillis() - appStartTime
        appStartToInteractiveSpan?.setMeasurement("app_start_to_interactive_ms", duration.toDouble())
        appStartToInteractiveSpan?.finish()
    }
}
