package com.theathletic.performance

import com.google.firebase.perf.FirebasePerformance

/**
 * Performance Singleton that is effectively a Factory for Trace instances.
 */
object Performance {

    /**
     * Returns a new instance of a Trace for the given String name.
     */
    fun newTrace(traceName: String): Trace = FirebaseTraceAdapter(FirebasePerformance.getInstance().newTrace(traceName))

    /**
     * This class wraps FirebaseTrace objects and acts as a boundary between our code and theirs.
     */
    private class FirebaseTraceAdapter(private val trace: com.google.firebase.perf.metrics.Trace) : Trace {
        override fun start(): Trace {
            trace.start()
            return this
        }

        override fun stop(): Trace {
            trace.stop()
            return this
        }
    }
}

/**
 * Lightweight interface that allows measuring performance times of processes.
 */
interface Trace {
    /**
     * Invoke when timing should start. Returns self to allow more fluent API.
     */
    fun start(): Trace

    /**
     * Invoke when timing should stop. Returns self to allow more fluent API.
     */
    fun stop(): Trace
}