package com.example.androidcustommetricsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.sentry.Sentry
import io.sentry.ITransaction
import io.sentry.SentryInstantDate
import io.sentry.SpanStatus
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
    private var homeToInteractiveSpan: io.sentry.ISpan? = null
    private var appStartTime: Long = 0
    private var splashScreenStartTime: Long = 0
    private var splashScreenTTFDTime: Long = 0
    private var homeScreenStartTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeScreenStartTime = System.currentTimeMillis()
        setContentView(R.layout.activity_home)

        // Get timing data from SplashActivity
        val appStartTime = intent.getLongExtra("app_start_time", 0)
        val splashScreenStartTime = intent.getLongExtra("splash_screen_start_time", 0)
        val splashScreenTTFDTime = intent.getLongExtra("splash_screen_ttfd_time", 0)

        // Create and finish the app.start_to_first_screen span with the correct timing
        val appStartToFirstScreenSpan = Sentry.getSpan()?.startChild(
            "app.start_to_first_screen",
            "Time from app start to first screen TTFD"
        )
        appStartToFirstScreenSpan?.setData("app_start_time", appStartTime)
        appStartToFirstScreenSpan?.setData("splash_screen_start_time", splashScreenStartTime)
        appStartToFirstScreenSpan?.setData("splash_screen_ttfd_time", splashScreenTTFDTime)
        appStartToFirstScreenSpan?.setData("splash_screen_duration", splashScreenTTFDTime - splashScreenStartTime)
        appStartToFirstScreenSpan?.setData("ttfd_duration", splashScreenTTFDTime - appStartTime)
        appStartToFirstScreenSpan?.finish(SpanStatus.OK)

        // Start the app.start_to_interactive span
        appStartToInteractiveSpan = Sentry.getSpan()?.startChild(
            "app.start_to_interactive",
            "Time from app start to home screen TTFD"
        )

        // Get app start time from intent or fallback to static field
        this.appStartTime = appStartTime
        this.splashScreenStartTime = splashScreenStartTime
        this.splashScreenTTFDTime = splashScreenTTFDTime
        
        val transaction = Sentry.getSpan() as? ITransaction
        if (transaction != null && appStartTime > 0) {
            // Main app start to interactive span
            val startTimestamp = SentryInstantDate(Instant.ofEpochMilli(appStartTime))
            appStartToInteractiveSpan = transaction.startChild(
                "app.start_to_interactive",
                "App Start to Interactive",
                startTimestamp
            )
            
            // Add detailed timing information for app start to interactive
            val splashScreenDuration = splashScreenStartTime - appStartTime
            val splashScreenTTFDDuration = splashScreenTTFDTime - splashScreenStartTime
            val homeScreenLoadDuration = homeScreenStartTime - splashScreenTTFDTime
            
            appStartToInteractiveSpan?.setData("app_start_to_splash_ms", splashScreenDuration)
            appStartToInteractiveSpan?.setData("splash_ttfd_ms", splashScreenTTFDDuration)
            appStartToInteractiveSpan?.setData("splash_to_home_ms", homeScreenLoadDuration)
            appStartToInteractiveSpan?.setData("total_duration_ms", homeScreenStartTime - appStartTime)
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
        
        // Start home to interactive span from splash screen TTFD
        if (transaction != null) {
            homeToInteractiveSpan = transaction.startChild(
                "app.home_to_interactive",
                "Home Screen to Interactive",
                SentryInstantDate(Instant.ofEpochMilli(splashScreenTTFDTime))
            )
        }
        
        // Finish the spans and set measurements
        val fullyInteractiveTime = System.currentTimeMillis()
        val totalDuration = fullyInteractiveTime - appStartTime
        val homeToInteractiveDuration = fullyInteractiveTime - splashScreenTTFDTime
        
        appStartToInteractiveSpan?.setData("fully_interactive_duration_ms", totalDuration)
        appStartToInteractiveSpan?.finish(SpanStatus.OK)
        
        homeToInteractiveSpan?.setData("duration_ms", homeToInteractiveDuration)
        homeToInteractiveSpan?.finish(SpanStatus.OK)
    }
}
