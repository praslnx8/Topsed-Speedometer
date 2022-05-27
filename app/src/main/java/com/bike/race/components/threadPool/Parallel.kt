package com.bike.race.components.threadPool

import com.bike.race.utils.ConsoleLog
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

object Parallel {
    private var cpuCount = Runtime.getRuntime().availableProcessors()

    fun <T> forEach(
        params: Iterable<T>,
        action: (T) -> Unit
    ) {
        val executorService =
            Executors.newFixedThreadPool(cpuCount)
        val futures =
            ArrayList<Future<*>>()
        for (param in params) {
            val future =
                executorService.submit { action(param) }
            futures.add(future)
        }
        for (future in futures) {
            try {
                future.get()
            } catch (ignore: InterruptedException) {
            } catch (ignore: ExecutionException) {
            }
        }
        executorService.shutdown()
        try {
            executorService.awaitTermination(1, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            ConsoleLog.e(e = e)
        }
    }
}