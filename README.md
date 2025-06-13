# Android Custom Metrics App

## Sentry Performance Demo

This minimal Android app demonstrates Sentry's performance monitoring for navigation and screen load timing, using both auto-instrumentation and a custom navigation timing span (NTS).

### What This App Captures

- **Navigation Timing Span (NTS):**
  - Measures the time from a button tap in `HomeActivity` to the screen being shown in `AutoTTIDTTFDWithNTSMeasurementActivity`.
  - Captured as a custom span (`ui.screen_time_to_interactive`) and attached as a measurement (`nts_ms`).

- **Time To Full Display (TTFD):**
  - Automatically captured by Sentry.
  - Ends when all content is loaded and `Sentry.reportFullyDisplayed()` is called in the destination activity.

- **Auto-instrumented Spans:**
  - Activity lifecycle (screen load)
  - OkHttp network requests
  - File I/O

### How It Works

1. **SplashActivity**: Shows a splash screen, then navigates to Home.
2. **HomeActivity**: Has a single button. When tapped, records the tap time and launches the demo screen.
3. **AutoTTIDTTFDWithNTSMeasurementActivity**: 
   - Receives the tap time, starts a custom span for NTS, and attaches the NTS duration as a measurement.
   - Loads network and file data, simulates content loading, and calls `Sentry.reportFullyDisplayed()` when done.

### Sentry Setup
- Sentry is auto-instrumented via the Android SDK and enabled in the manifest.
- The DSN is injected securely via `local.properties` and a manifest placeholder.

---

**This project is a minimal, production-quality example of Sentry Android performance instrumentation for navigation and screen load timing.** 