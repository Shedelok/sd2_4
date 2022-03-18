package task4.market

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {
    private val stockManager = StockInMemoryManager()

    @RequestMapping("/add")
    fun addStock(@RequestParam name: String, @RequestParam quantity: Int, @RequestParam price: Double): String {
        return try {
            stockManager.addStock(Stock(name, quantity, price))
            "OK"
        } catch (e: IllegalArgumentException) {
            e.message!!
        }
    }

    @RequestMapping("/get")
    fun getStock(@RequestParam name: String): String {
        val stock = stockManager.getStock(name) ?: return "Stock $name doesn't exist"
        return "name: ${stock.name}</br>quantity: ${stock.quantity}</br>price: ${stock.price}"
    }

    @RequestMapping("/buy")
    fun buyStocks(@RequestParam name: String, @RequestParam quantity: Int, @RequestParam expectedPrice: Double): String {
        return try {
            stockManager.buyStocks(name, quantity, expectedPrice)
            "OK"
        } catch (e: IllegalArgumentException) {
            e.message!!
        }
    }

    @RequestMapping("/sell")
    fun sellStocks(@RequestParam name: String, @RequestParam quantity: Int, @RequestParam expectedPrice: Double): String {
        return try {
            stockManager.sellStocks(name, quantity, expectedPrice)
            "OK"
        } catch (e: IllegalArgumentException) {
            e.message!!
        }
    }

    @RequestMapping("/set_price")
    fun setStockPrice(@RequestParam name: String, @RequestParam price: Double): String {
        return try {
            stockManager.setStockPrice(name, price)
            "OK"
        } catch (e: IllegalArgumentException) {
            e.message!!
        }
    }
}