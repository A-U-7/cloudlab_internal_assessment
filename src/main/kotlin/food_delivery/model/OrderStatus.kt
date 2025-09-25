package food_delivery.model

/**
 * Defines the various states an order can be in.
 * This is a sealed class to ensure type safety when handling different statuses.
 */
sealed class OrderStatus {
    object Pending : OrderStatus()
    object Preparing : OrderStatus()
    object ReadyForDelivery : OrderStatus()
    object OutForDelivery : OrderStatus()
    object Delivered : OrderStatus()
    data class Cancelled(val reason: String) : OrderStatus()
    
    companion object {
        /**
         * Valid transitions between order statuses.
         * This map defines which status changes are allowed.
         */
        private val allowedTransitions = mapOf<
            OrderStatus,
            Set<OrderStatus>
        >(
            Pending to setOf(Preparing, Cancelled("Customer request")),
            Preparing to setOf(ReadyForDelivery, Cancelled("Restaurant cancelled")),
            ReadyForDelivery to setOf(OutForDelivery, Cancelled("Customer cancelled")),
            OutForDelivery to setOf(Delivered, Cancelled("Delivery failed"))
            // Delivered and Cancelled are final states with no transitions out
        )
        
        /**
         * Checks if transitioning from one status to another is valid.
         * @param from Current status
         * @param to Desired new status
         * @return Boolean indicating if the transition is allowed
         */
        fun isValidTransition(from: OrderStatus, to: OrderStatus): Boolean {
            // Can't transition to the same status
            if (from::class == to::class && from !is OrderStatus.Cancelled) return false
            
            // Check if 'from' status can transition to 'to' status
            return allowedTransitions[from]?.any { to::class == it::class } ?: false
        }
    }
}

/**
 * Extension function to check if status is a final state (Delivered or Cancelled).
 */
fun OrderStatus.isFinal(): Boolean = this is OrderStatus.Delivered || this is OrderStatus.Cancelled

/**
 * Extension function to get status display name.
 */
fun OrderStatus.displayName(): String = when (this) {
    is OrderStatus.Pending -> "Pending"
    is OrderStatus.Preparing -> "Preparing"
    is OrderStatus.ReadyForDelivery -> "Ready for Delivery"
    is OrderStatus.OutForDelivery -> "Out for Delivery"
    is OrderStatus.Delivered -> "Delivered"
    is OrderStatus.Cancelled -> "Cancelled: ${this.reason}"
}
