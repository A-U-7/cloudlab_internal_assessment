package food_delivery.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Simple logging utility for the food delivery application.
 * In a real application, this would be replaced with a proper logging framework like Log4j or SLF4J.
 */

class Logger(private val className: String = "") {
    
    private val timestampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    
    /**
     * Logs an informational message.
     */
    fun info(message: String) {
        log("INFO", message)
    }
    
    /**
     * Logs a warning message.
     */
    fun warn(message: String) {
        log("WARN", message)
    }
    
    /**
     * Logs an error message with an optional exception.
     */
    fun error(message: String, exception: Throwable? = null) {
        val fullMessage = if (exception != null) {
            "$message - ${exception.javaClass.simpleName}: ${exception.message}"
        } else {
            message
        }
        log("ERROR", fullMessage)
        
        // In a real app, we'd log the stack trace at a different level
        exception?.printStackTrace()
    }
    
    /**
     * Logs a debug message (only if debug is enabled).
     */
    fun debug(message: String) {
        if (isDebugEnabled) {
            log("DEBUG", message)
        }
    }
    
    private fun log(level: String, message: String) {
        val timestamp = LocalDateTime.now().format(timestampFormat)
        val logPrefix = if (className.isNotBlank()) "[$className]" else ""
        println("$timestamp [$level]$logPrefix $message")
    }
    
    companion object {
        // In a real app, this would be configurable
        var isDebugEnabled: Boolean = false
        
        /**
         * Creates a logger for the given class.
         */
        fun forClass(clazz: Class<*>) = Logger(clazz.simpleName)
    }
}

/**
 * Extension function to get a logger for any class.
 */
inline fun <reified T> T.logger(): Logger = Logger(T::class.java.simpleName)
