package food_delivery.exception

/**
 * Base exception for order-related errors.
 */
open class OrderException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Thrown when order validation fails.
 */
class InvalidOrderException(message: String) : OrderException(message)

/**
 * Thrown when an invalid status transition is attempted.
 */
class InvalidStatusTransitionException(
    currentStatus: String,
    targetStatus: String
) : OrderException(
    "Cannot transition from '$currentStatus' to '$targetStatus'"
)

/**
 * Thrown when attempting to modify a finalized order.
 */
class OrderFinalizedException(orderId: String) : OrderException(
    "Order $orderId is in a final state and cannot be modified"
)

/**
 * Thrown when an order cannot be found.
 */
class OrderNotFoundException(orderId: String) : OrderException(
    "Order with ID $orderId not found"
)

/**
 * Thrown when there's an issue with order processing.
 */
class OrderProcessingException(message: String, cause: Throwable? = null) : OrderException(message, cause)

/**
 * Thrown when there's an issue with order delivery.
 */
class DeliveryException(message: String, cause: Throwable? = null) : OrderException(message, cause)

/**
 * Thrown when there's an issue with payment processing.
 */
class PaymentException(message: String, cause: Throwable? = null) : OrderException(message, cause)
