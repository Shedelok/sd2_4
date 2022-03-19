package task4.client.entity

data class User(val name: String, var money: Double, val stocks: MutableList<OwnedStock>) {
    constructor(name: String) : this(name, 0.0, mutableListOf())
}