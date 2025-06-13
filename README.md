# Android Custom Metrics App

## Performance Instrumentation with Sentry

This app uses a scalable, base-activity pattern for Sentry performance tracing. All screens extending `BaseInstrumentedActivity` are automatically instrumented for:

- **Navigation to screen (NTS):** Time from navigation start to screen open
- **Time to first layout (TTFL):** Time until the first frame is drawn
- **Screen time to interactive (TTI):** Time until the screen is fully interactive (call `markTTI()`)
- **Rich content load time (RCLT):** Time until rich content (e.g., images) is loaded (call `markRCLT()`)

### How to Instrument a New Screen

1. Extend `BaseInstrumentedActivity` in your Activity.
2. Call `markTTI()` when your screen is fully interactive (e.g., after data is loaded and UI is ready).
3. Call `markRCLT()` when rich content (e.g., images) is loaded.

### Navigation Instrumentation

Each navigation event from the Home screen starts a Sentry span/transaction, so you can see tap-to-screen navigation performance in Sentry.

### Limitations

- **Cross-activity traces are not natively linked** in Sentry (as of current SDK). The `Tracer` singleton is designed to be future-proof for upcoming Sentry SDK features that may allow linking transactions across activities.
- **To correlate traces across activities,** we use navigation span/transaction names and (optionally) tags. When Sentry adds public APIs for span linking, this pattern can be extended easily.

### Extending Instrumentation

- For fragments or Jetpack Compose, you can adapt the base pattern or use similar lifecycle hooks.
- For business logic, network, or database spans, use `Tracer.startSpan()` and `Tracer.stopSpan()` directly.

---

**See comments in `BaseInstrumentedActivity.kt`, `Tracer.kt`, and example screens for more details.** 