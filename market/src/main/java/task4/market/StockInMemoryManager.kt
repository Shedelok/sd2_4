package task4.market

class StockInMemoryManager {
    private val stocks = mutableListOf<Stock>()

    fun getStock(name: String) = stocks.firstOrNull { it.name == name }

    fun addStock(stock: Stock) {
        require(getStock(stock.name) == null) { "Stock ${stock.name} already exists" }

        stocks.add(stock)
    }

    fun buyStocks(name: String, quantity: Int, expectedPrice: Double) {
        val stock = getStock(name)
        require(stock != null) { "Stock $name doesn't exist" }

        require(stock.price == expectedPrice) { "Price has changed" }

        val newQuantity = stock.quantity - quantity
        require(newQuantity >= 0) { "Not enough stocks" }

        stocks.remove(stock)
        stocks.add(stock.copy(quantity = newQuantity))
    }

    fun sellStocks(name: String, quantity: Int, expectedPrice: Double) {
        val stock = getStock(name)
        require(stock != null) { "Stock $name doesn't exist" }

        require(stock.price == expectedPrice) { "Price has changed" }

        stocks.remove(stock)
        stocks.add(stock.copy(quantity = stock.quantity + quantity))
    }

    fun setStockPrice(name: String, price: Double) {
        val stock = getStock(name)
        require(stock != null) { "Stock $name doesn't exist" }

        stocks.remove(stock)
        stocks.add(stock.copy(price = price))
    }
}