package task4.client

import task4.client.entity.OwnedStock
import task4.client.entity.OwnedStockWithPrice
import task4.client.entity.User

class ClientInMemoryManager {
    private val stockClient = StockClient()

    private val users = mutableListOf<User>()

    fun addUser(name: String): Int {
        users.add(User(name))
        return users.size - 1
    }

    fun addMoneyToUser(id: Int, amount: Double) {
        require(id in users.indices) { "User $id doesn't exist" }
        users[id].money += amount
    }

    fun getUserStocks(id: Int): List<OwnedStockWithPrice> {
        require(id in users.indices) { "User $id doesn't exist" }
        return users[id].stocks.map { OwnedStockWithPrice(it.name, it.quantity, stockClient.getStockPrice(it.name)) }
    }

    fun getUserSum(id: Int): Double {
        require(id in users.indices) { "User $id doesn't exist" }
        return users[id].money + getUserStocks(id).filter { it.price != null }.sumOf { it.quantity * it.price!! }
    }

    fun buyStocks(userId: Int, stockName: String, quantity: Int, expectedPrice: Double): Boolean {
        require(userId in users.indices) { "User $userId doesn't exist" }
        val user = users[userId]

        val totalPrice = expectedPrice * quantity
        require(user.money >= totalPrice) { "Not enough money" }

        return if (stockClient.buyStocks(stockName, quantity, expectedPrice)) {
            user.money -= totalPrice
            val stock = user.stocks.firstOrNull { it.name == stockName }
            if (stock == null) {
                user.stocks.add(OwnedStock(stockName, quantity))
            } else {
                stock.quantity += quantity
            }
            true
        } else {
            false
        }
    }

    fun sellStocks(userId: Int, stockName: String, quantity: Int, expectedPrice: Double): Boolean {
        require(userId in users.indices) { "User $userId doesn't exist" }
        val user = users[userId]
        val stock = user.stocks.firstOrNull { it.name == stockName }
        require(stock != null) { "User doesn't own any $stockName stocks" }

        val newQuantity = stock.quantity - quantity
        require(newQuantity >= 0) { "Not enough stocks" }

        return if (stockClient.sellStocks(stockName, quantity, expectedPrice)) {
            user.money += expectedPrice * quantity
            stock.quantity = newQuantity
            if (stock.quantity == 0) {
                user.stocks.remove(stock)
            }
            true
        } else {
            false
        }
    }
}