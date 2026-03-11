package com.seleniumcraft.wait;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * RetryEngine - Lambda-based retry logic for flexible wait conditions.
 * Provides a clean alternative to verbose WebDriverWait/ExpectedConditions.
 */
public class RetryEngine {

    private static final long DEFAULT_POLL_INTERVAL = 200; // milliseconds

    /**
     * Retries a supplier until the predicate returns true or timeout is reached.
     *
     * @param supplier  The function that provides the value to check
     * @param condition The predicate that must return true for success
     * @param timeoutMs Maximum time to wait in milliseconds
     * @param <T>       The type of value being checked
     * @return The value when condition is met
     * @throws RuntimeException if timeout is reached
     */
    public static <T> T retryUntil(Supplier<T> supplier, Predicate<T> condition, long timeoutMs) {
        return retryUntil(supplier, condition, timeoutMs, DEFAULT_POLL_INTERVAL);
    }

    /**
     * Retries a supplier until the predicate returns true or timeout is reached.
     *
     * @param supplier     The function that provides the value to check
     * @param condition    The predicate that must return true for success
     * @param timeoutMs    Maximum time to wait in milliseconds
     * @param pollInterval Time between retry attempts in milliseconds
     * @param <T>          The type of value being checked
     * @return The value when condition is met
     * @throws RuntimeException if timeout is reached
     */
    public static <T> T retryUntil(Supplier<T> supplier, Predicate<T> condition,
            long timeoutMs, long pollInterval) {
        long startTime = System.currentTimeMillis();
        T result = null;
        Exception lastException = null;

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                result = supplier.get();
                if (condition.test(result)) {
                    return result;
                }
            } catch (Exception e) {
                lastException = e;
                // Continue retrying
            }

            try {
                Thread.sleep(pollInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Retry interrupted", e);
            }
        }

        String message = String.format(
                "RetryEngine timeout after %dms. Last result: %s",
                timeoutMs, result);

        if (lastException != null) {
            throw new RuntimeException(message, lastException);
        }
        throw new RuntimeException(message);
    }

    /**
     * Retries an action until it completes without throwing an exception.
     *
     * @param action    The action to perform
     * @param timeoutMs Maximum time to wait in milliseconds
     */
    public static void retryAction(Runnable action, long timeoutMs) {
        retryUntil(() -> {
            action.run();
            return true;
        }, result -> result, timeoutMs);
    }
}
