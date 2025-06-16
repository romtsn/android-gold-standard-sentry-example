package com.example.androidcustommetricsapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import io.sentry.Sentry
import io.sentry.ISpan
import io.sentry.SpanStatus

class SplashActivity : AppCompatActivity() {
    private var appStartToFirstScreenSpan: ISpan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Get the span from the transaction
        val transaction = Sentry.getSpan()
        if (transaction != null) {
            appStartToFirstScreenSpan = transaction.startChild(
                "app.start_to_first_screen",
                "App Start to First Screen Interactive"
            )
        }

        // Simulate app initialization
        Handler(Looper.getMainLooper()).postDelayed({
            // Mark splash screen as fully displayed
            Sentry.reportFullyDisplayed()
            val splashScreenTTFDTime = System.currentTimeMillis()
            
            // Add timing information for app start to first screen
            val appStartTime = MyApp.appStartTime
            val splashScreenStartTime = System.currentTimeMillis() - 1000 // 1 second ago
            val splashScreenDuration = splashScreenStartTime - appStartTime
            val splashScreenTTFDDuration = splashScreenTTFDTime - splashScreenStartTime
            
            appStartToFirstScreenSpan?.setData("app_start_to_splash_ms", splashScreenDuration)
            appStartToFirstScreenSpan?.setData("splash_ttfd_ms", splashScreenTTFDDuration)
            appStartToFirstScreenSpan?.setData("total_duration_ms", splashScreenTTFDTime - appStartTime)
            appStartToFirstScreenSpan?.finish(SpanStatus.OK)
            
            startHomeActivity()
        }, 1000) // Simulate 1 second initialization
    }

    private fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            // Pass timing data to HomeActivity
            putExtra("app_start_time", appStartTime)
            putExtra("splash_screen_start_time", splashScreenStartTime)
            putExtra("splash_screen_ttfd_time", splashScreenTTFDTime)
        }
        startActivity(intent)
        finish()
    }
}
