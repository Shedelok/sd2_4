package task4.client

import java.net.HttpURLConnection
import java.net.URL

class StockClient {
    private fun sendRequest(url: String): String {
        with(URL(url).openConnection() as HttpURLConnection) {
            inputStream.bufferedReader().use {
                return it.readText()
            }
        }
    }

    fun getStockPrice(name: String): Double? {
        val pricePrefix = "price: "

        val text = sendRequest("http://localhost:8080/get?name=$name")
        val indexOfPrice = text.indexOf(pricePrefix)
        if (indexOfPrice == -1) {
            return null
        }
        return text.drop(pricePrefix.length + indexOfPrice).toDoubleOrNull()
    }

    fun buyStocks(name: String, quantity: Int, expectedPrice: Double): Boolean {
        return sendRequest("http://localhost:8080/buy?name=$name&quantity=$quantity&expectedPrice=$expectedPrice") == "OK"
    }

    fun sellStocks(name: String, quantity: Int, expectedPrice: Double): Boolean {
        return sendRequest("http://localhost:8080/sell?name=$name&quantity=$quantity&expectedPrice=$expectedPrice") == "OK"
    }
}