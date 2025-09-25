package food_delivery

import food_delivery.model.*
import food_delivery.service.OrderService
import food_delivery.util.Logger


fun main() {
    // Enable debug logging
    Logger.isDebugEnabled = true
    val logger = Logger("Main")
    
    logger.info("Starting Food Delivery Application")
    

    val orderService = OrderService()
    
    try {

        val pizzaPlace = "Pizza Hurt"
        val burgerJoint = "Burger House"
        
        val margheritaPizza = MenuItem(
            id = "pz-001",
            name = "Margherita Pizza",
            description = "Classic pizza with tomato sauce and mozzarella",
            price = 250.00,
            category = FoodCategory.MAIN_COURSE
        )
        
        val cheeseBurger = MenuItem(
            id = "bg-001",
            name = "Cheeseburger",
            description = "Juicy beef patty with cheese and fresh veggies",
            price = 300.00,
            category = FoodCategory.MAIN_COURSE
        )
        
        val fries = MenuItem(
            id = "fr-001",
            name = "French Fries",
            description = "Crispy golden fries with sea salt",
            price = 100.00,
            category = FoodCategory.SIDE_DISH
        )
        
        val cola = MenuItem(
            id = "cola-001",
            name = "Cola",
            description = "Refreshing cola drink",
            price = 80.00,
            category = FoodCategory.BEVERAGE
        )
        
        // Create delivery address
        val deliveryAddress = Address(
            street = "Janakpuri",
            city = "Foodie City",
            state = "Delhi",
            postalCode = "176005",
            country = "India"
        )
        
        // Scenario 1: Place a pizza order
        logger.info("Placing a Pizza Order")
        val pizzaOrder = orderService.createOrder(
            customerId = "customer-001",
            restaurantId = pizzaPlace,
            items = mapOf(
                margheritaPizza to 1,
                cola to 2
            ),
            deliveryAddress = deliveryAddress,
            specialInstructions = "Extra cheese, please!"
        )
        
        println("Order created successfully!")
        println(pizzaOrder.getSummary())
        
        // Update order status
        println("Updating order status")
        orderService.updateOrderStatus(pizzaOrder.id, OrderStatus.Preparing)
        orderService.updateOrderStatus(pizzaOrder.id, OrderStatus.ReadyForDelivery)
        
        // Try invalid status transition (should be ignored)
        orderService.updateOrderStatus(pizzaOrder.id, OrderStatus.Pending)
        
        // Complete the delivery
        orderService.updateOrderStatus(pizzaOrder.id, OrderStatus.OutForDelivery)
        orderService.updateOrderStatus(pizzaOrder.id, OrderStatus.Delivered)
        
        // Scenario 2: Place and cancel a burger order
        logger.info("Placing a Burger Order")
        val burgerOrder = orderService.createOrder(
            customerId = "customer-002",
            restaurantId = burgerJoint,
            items = mapOf(
                cheeseBurger to 2,
                fries to 1,
                cola to 2
            ),
            deliveryAddress = deliveryAddress
        )
        
        println("Order created successfully!")
        println(burgerOrder.getSummary())
        
        // Cancel the order
        println("Cancelling order")
        orderService.cancelOrder(burgerOrder.id, "Changed my mind")
        
        // Try to update a cancelled order (should fail)
        println("Trying to update cancelled order")
        val updated = orderService.updateOrderStatus(burgerOrder.id, OrderStatus.Preparing)
        println("Update successful: $updated (expected: false)")
        
        // Get order history
        println("Order History:")
        val history = orderService.getOrderHistory(pizzaOrder.id)
        history.forEach { (status, timestamp) ->
            println("$status at $timestamp")
        }
        
    } catch (ex: Exception) {
        logger.error("An error occurred", ex)
        println("Error: ${ex.message}")
    } finally {
        logger.info("Application finished")
    }
    
    println("Thank you for using the Food Delivery System!")
}
