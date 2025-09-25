package food_delivery.service

import food_delivery.exception.*
import food_delivery.model.*
import food_delivery.util.Logger
import java.time.Duration
import java.time.LocalDateTime

/**
 * Service class for managing food delivery orders.
 * Handles order creation, status updates, and business logic.
 */
class OrderService(
    private val logger: Logger = Logger()
) {
    private val orders = mutableMapOf<String, Order>()
    
    /**
     * Creates a new order and adds it to the system.
     * @throws InvalidOrderException if order validation fails
     */
    fun createOrder(
        customerId: String,
        restaurantId: String,
        items: Map<MenuItem, Int>,
        deliveryAddress: Address,
        specialInstructions: String = ""
    ): Order {
        try {
            val order = Order(
                customerId = customerId,
                restaurantId = restaurantId,
                items = items.toMap(), // Create a defensive copy
                deliveryAddress = deliveryAddress,
                specialInstructions = specialInstructions
            )
            
            orders[order.id] = order
            logger.info("Created new order: ${order.id}")
            
            return order
        } catch (ex: Exception) {
            logger.error("Failed to create order", ex)
            throw OrderProcessingException("Failed to create order: ${ex.message}", ex)
        }
    }
    
    /**
     * Updates the status of an existing order.
     * @return true if status was updated, false otherwise
     */
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Boolean {
        val order = getOrderById(orderId)
        
        if (order.status.isFinal()) {
            logger.warn("Attempted to update status of finalized order: $orderId")
            return false
        }
        
        if (order.updateStatus(newStatus)) {
            order.updatedAt = LocalDateTime.now()
            logger.info("Updated order $orderId status to ${newStatus.displayName()}")
            return true
        }
        
        return false
    }
    
    /**
     * Cancels an order with the given reason.
     */
    fun cancelOrder(orderId: String, reason: String = "Customer request") {
        val order = getOrderById(orderId)
        
        if (order.status.isFinal()) {
            throw OrderFinalizedException(orderId)
        }
        
        order.updateStatus(OrderStatus.Cancelled(reason))
        logger.info("Cancelled order $orderId. Reason: $reason")
    }
    
    /**
     * Gets an order by ID.
     * @throws OrderNotFoundException if order is not found
     */
    fun getOrderById(orderId: String): Order {
        return orders[orderId] ?: throw OrderNotFoundException(orderId)
    }
    
    /**
     * Gets all orders for a specific customer.
     */
    fun getCustomerOrders(customerId: String): List<Order> {
        return orders.values.filter { it.customerId == customerId }
    }
    
    /**
     * Gets all orders for a specific restaurant.
     */
    fun getRestaurantOrders(restaurantId: String): List<Order> {
        return orders.values.filter { it.restaurantId == restaurantId }
    }
    
    /**
     * Gets orders that are currently in progress (not in final state).
     */
    fun getInProgressOrders(): List<Order> {
        return orders.values.filterNot { it.status.isFinal() }
    }
    
    /**
     * Gets the estimated delivery time for an order.
     * This is a simplified estimation based on order status.
     */
    fun getEstimatedDeliveryTime(orderId: String): String {
        val order = getOrderById(orderId)
        
        return when (order.status) {
            is OrderStatus.Delivered -> "Order has been delivered"
            is OrderStatus.Cancelled -> "Order was cancelled"
            is OrderStatus.Pending -> "Preparing your order..."
            is OrderStatus.Preparing -> "Estimated delivery in 30-45 minutes"
            is OrderStatus.ReadyForDelivery -> "Your order is ready and will be delivered soon"
            is OrderStatus.OutForDelivery -> "Your order is on its way!"
        }
    }
    
    /**
     * Gets the order history with timing information.
     */
    fun getOrderHistory(orderId: String): Map<String, String> {
        val order = getOrderById(orderId)
        
        val history = mutableMapOf(
            "Ordered" to order.createdAt.toString()
        )
        
        // In a real app, we'd have a proper audit log
        if (order.status is OrderStatus.Delivered) {
            history["Delivered"] = order.updatedAt.toString()
        } else if (order.status is OrderStatus.Cancelled) {
            history["Cancelled"] = order.updatedAt.toString()
        }
        
        return history
    }
}
