package food_delivery.model

import food_delivery.exception.InvalidOrderException
import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a food delivery order in the system.
 * @property id Unique identifier for the order
 * @property customerId ID of the customer who placed the order
 * @property restaurantId ID of the restaurant
 * @property items List of ordered items with quantities
 * @property deliveryAddress Delivery address
 * @property status Current status of the order
 * @property createdAt When the order was created
 * @property updatedAt When the order was last updated
 * @property specialInstructions Any special instructions for the order
 */
data class Order(
    val id: String = UUID.randomUUID().toString(),
    val customerId: String,
    val restaurantId: String,
    val items: Map<MenuItem, Int>,
    val deliveryAddress: Address,
    var status: OrderStatus = OrderStatus.Pending,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    val specialInstructions: String = ""
) {
    init {
        validate()
    }

    /**
     * Validates the order meets minimum requirements.
     * @throws InvalidOrderException if validation fails
     */
    private fun validate() {
        if (customerId.isBlank()) {
            throw InvalidOrderException("Customer ID cannot be blank")
        }
        if (restaurantId.isBlank()) {
            throw InvalidOrderException("Restaurant ID cannot be blank")
        }
        if (items.isEmpty()) {
            throw InvalidOrderException("Order must contain at least one item")
        }
        if (items.any { (_, quantity) -> quantity <= 0 }) {
            throw InvalidOrderException("Item quantity must be greater than zero")
        }
    }

    /**
     * Updates the order status if the transition is valid.
     * @param newStatus The new status to transition to
     * @return true if status was updated, false otherwise
     */
    fun updateStatus(newStatus: OrderStatus): Boolean {
        if (status.isFinal()) {
            return false
        }
        
        if (!OrderStatus.isValidTransition(status, newStatus)) {
            return false
        }
        
        status = newStatus
        updatedAt = LocalDateTime.now()
        return true
    }

    /**
     * Calculates the total order amount.
     */
    fun calculateTotal(): Double {
        return items.entries.sumOf { (item, quantity) -> item.price * quantity }
    }

    /**
     * Returns a brief summary of the order.
     */
    fun getSummary(): String {
        return """
            Order $id
            Status: ${status.displayName()}
            Items: ${items.entries.joinToString(", ") { "${it.key.name} x${it.value}" }}
            Total: Rs ${String.format("%.2f", calculateTotal())}
        """.trimIndent()
    }
}

/**
 * Represents a menu item in the system.
 * @property id Unique identifier
 * @property name Name of the item
 * @property description Item description
 * @property price Price in local currency
 * @property category Food category
 */
data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: FoodCategory
)

/**
 * Represents a delivery address.
 * @property street Street address
 * @property city City
 * @property state State/Province
 * @property postalCode Postal/ZIP code
 * @property country Country
 */
data class Address(
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String
)

/**
 * Food categories for menu items.
 */
enum class FoodCategory {
    APPETIZER,
    MAIN_COURSE,
    DESSERT,
    BEVERAGE,
    SIDE_DISH
}
