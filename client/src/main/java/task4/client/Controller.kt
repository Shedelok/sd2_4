package task4.client

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {
    private val clientManager = ClientInMemoryManager()

    @RequestMapping("/add")
    fun addUser(@RequestParam name: String): String {
        return "OK ${clientManager.addUser(name)}"
    }

    @RequestMapping("/add_money")
    fun addMoneyToUser(@RequestParam id: Int, @RequestParam amount: Double): String {
        return try {
            clientManager.addMoneyToUser(id, amount)
            "OK"
        } catch (e: IllegalArgumentException) {
            e.message!!
        }
    }

    @RequestMapping("/get_stocks")
    fun getUserStocks(@RequestParam id: Int): String {
        return try {
            return clientManager.getUserStocks(id).joinToString("</br>") {
                "name: ${it.name}, quantity: ${it.quantity}, price: ${it.price}"
            }
        } catch (e: IllegalArgumentException) {
            e.message!!
        }
    }

    @RequestMapping("/get_sum")
    fun getUserSum(@RequestParam id: Int): String {
        return try {
            return clientManager.getUserSum(id).toString()
        } catch (e: IllegalArgumentException) {
            e.message!!
        }
    }

    @RequestMapping("/buy")
    fun buyStocks(@RequestParam userId: Int, @RequestParam stockName: String, @RequestParam quantity: Int, @RequestParam expectedPrice: Double): String {
        return try {
            return if (clientManager.buyStocks(userId, stockName, quantity, expectedPrice)) "OK" else "Price has changed"
        } catch (e: IllegalArgumentException) {
            e.message!!
        }
    }

    @RequestMapping("/sell")
    fun sellStocks(@RequestParam userId: Int, @RequestParam stockName: String, @RequestParam quantity: Int, @RequestParam expectedPrice: Double): String {
        return try {
            return if (clientManager.sellStocks(userId, stockName, quantity, expectedPrice)) "OK" else "Price has changed"
        } catch (e: IllegalArgumentException) {
            e.message!!
        }
    }
}