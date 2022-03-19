package task4.client.entity

data class MarketStock(val name: String, val quantity: Int, val price: Double)

data class OwnedStock(val name: String, var quantity: Int)

data class OwnedStockWithPrice(val name: String, val quantity: Int, val price: Double?)
