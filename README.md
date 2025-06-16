# Android Custom Metrics App

## Sentry Performance Demo

This minimal Android app demonstrates Sentry's performance monitoring for navigation, cold start, and screen load timing, using both auto-instrumentation and custom spans/attributes.

### What This App Captures

- **App Start to Interactive (Cold Start):**
  - Measures the time from app cold start (icon tap) through splash screen to the Home screen being fully interactive.
  - Captured as a custom span (`app.start_to_interactive`) on the Home screen transaction.
  - The duration is attached as a data attribute and visible as a span in Sentry.

- **Navigation Timing Span (NTS) and Time To Interactive (TTI):**
  - In `AutoTTIDTTFDWithNTSMeasurementActivity`, a custom span (`ui.screen_time_to_interactive`) is created.
  - **NTS (`nts_ms`)**: Time from button tap (navigation event) to when the screen is opened (Activity `onCreate`).
  - **TTI (`tti_ms`)**: Time from screen open to when the screen is fully rendered and interactive.
  - Both are attached as data attributes (not measurements) on the custom span and are visible in Sentry under the span's Data section.

- **Time To Full Display (TTFD):**
  - Automatically captured by Sentry.
  - Ends when all content is loaded and `Sentry.reportFullyDisplayed()` is called in the destination activity.

- **Auto-instrumented Spans:**
  - Activity lifecycle (screen load)
  - OkHttp network requests
  - File I/O

### How It Works

1. **SplashActivity**: Shows a splash screen, then navigates to Home.
2. **HomeActivity**: Receives the app start time, starts a custom span for App Start to Interactive, and marks the screen as fully displayed when ready.
3. **AutoTTIDTTFDWithNTSMeasurementActivity**: 
   - Receives the tap time, starts a custom span for NTS/TTI, and attaches both as data attributes.
   - Loads network and file data, simulates content loading, and calls `Sentry.reportFullyDisplayed()` when done.

### Sentry Setup
- Sentry is auto-instrumented via the Android SDK and enabled in the manifest.
- The DSN is injected securely via `local.properties` and a manifest placeholder.

---

**This project is a minimal, production-quality example of Sentry Android performance instrumentation for navigation, cold start, and screen load timing.** 