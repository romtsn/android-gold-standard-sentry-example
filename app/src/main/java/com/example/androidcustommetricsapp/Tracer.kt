package com.example.androidcustommetricsapp

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import io.sentry.ISpan
import io.sentry.Instrumenter.SENTRY
import io.sentry.Sentry
import io.sentry.SentryDate
import io.sentry.SentryLongDate
import io.sentry.TransactionOptions
import io.sentry.android.core.performance.AppStartMetrics
import java.util.concurrent.TimeUnit.MINUTES

object Tracer : ActivityLifecycleCallbacks {
    private const val TTFD_OP = "ui.load.full_display"
    private const val UI_LOAD_OP = "ui.load"
    private const val APP_START_OP = "app.start_flow"
    private const val APP_START_COLD = "app.start.cold"
    private const val APP_START_WARM = "app.start.warm"
    val spans = LinkedHashMap<String, ISpan>()

    @JvmStatic
    fun startSpan(opName: String, description: String? = null, startDate: SentryDate? = null): ISpan {
        val activeSpan = Sentry.getSpan()
        val span = if (activeSpan != null) {
            activeSpan.startChild(opName, description, startDate, SENTRY)
        } else {
            // Start a root transaction if none exists
            val root = Sentry.startTransaction(opName, opName, TransactionOptions().apply { startTimestamp = startDate; isBindToScope = true })
            root
        }
        return span
    }

    @JvmStatic
    fun stopSpan(span: ISpan) {
        span.finish()
        spans.entries.removeAll { it.value == span }
    }

    // TODO: replace this with Sentry.reportFullyDrawn after we support attaching TTID/TTFD spans to custom txs
    fun reportFullyDrawn(activity: Activity) {
        val key = "${TTFD_OP}_${activity.hashCode()}"
        stopSpan(spans[key] ?: return)

        if (activity::class.java.name.contains("home", ignoreCase = true)) {
            stopSpan(spans[APP_START_COLD] ?: return)
        }
    }

    fun startAppFlow() {
        val appStart = AppStartMetrics.getInstance().appStartTimeSpan
        val appStartDate = if (appStart.hasStarted() && appStart.durationMs <= MINUTES.toMillis(1)) {
            appStart.startTimestamp
        } else {
            SentryLongDate(System.nanoTime())
        }
        val appStartSpan = startSpan(APP_START_OP, "App start flow", appStartDate)
        spans[APP_START_OP] = appStartSpan
        val appSTISpan = appStartSpan.startChild(APP_START_COLD, "app.start_to_interactive", appStartDate, SENTRY)
        spans[APP_START_COLD] = appSTISpan
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val activitySpan = startSpan(UI_LOAD_OP, activity::class.java.simpleName)
        spans["${UI_LOAD_OP}_${activity.hashCode()}"] = activitySpan

        val ttfdSpan = activitySpan.startChild(TTFD_OP, "${activity::class.java.simpleName} full displayed")
        spans["${TTFD_OP}_${activity.hashCode()}"] = ttfdSpan
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        val key = "${UI_LOAD_OP}_${activity.hashCode()}"
        stopSpan(spans[key] ?: return)

        if (activity::class.java.name.contains("home", ignoreCase = true)) {
            // Special case for HomeActivity, we want to stop the app launch span
            stopSpan(spans[APP_START_OP] ?: return)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
} 