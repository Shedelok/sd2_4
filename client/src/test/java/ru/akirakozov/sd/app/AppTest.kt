package ru.akirakozov.sd.app

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.containers.GenericContainer
import task4.client.Application
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.assertEquals

class AppTest {
    private lateinit var clientServer: ConfigurableApplicationContext

    companion object {
        @ClassRule
        @JvmField
        val simpleWebServer: GenericContainer<*> = FixedHostPortGenericContainer("market:1.0-SNAPSHOT")
            .withFixedExposedPort(8080, 8080)
            .withExposedPorts(8080)

        private fun sendRequest(url: String): String {
            with(URL(url).openConnection() as HttpURLConnection) {
                inputStream.bufferedReader().use {
                    return it.readText()
                }
            }
        }

        private fun sendRequestToMarket(method: String, params: String = "") = sendRequest("http://localhost:8080/$method?$params")

        private fun sendRequestToClient(method: String, params: String = "") = sendRequest("http://localhost:8081/$method?$params")

        @BeforeClass
        @JvmStatic
        fun setUpMarket() {
            sendRequestToMarket("add", "name=YNDX&quantity=1000&price=3")
            sendRequestToMarket("add", "name=GOOGL&quantity=1000&price=10")
        }
    }

    @Before
    fun setUp() {
        clientServer = Application.main(emptyArray())

        sendRequestToMarket("set_price", "name=YNDX&price=3")
        sendRequestToMarket("set_price", "name=GOOGL&price=10")
    }

    @After
    fun tearDown() {
        clientServer.close()
    }

    @Test
    fun createUser() {
        assertEquals("OK 0", sendRequestToClient("add", "name=first"))
        assertEquals("", sendRequestToClient("get_stocks", "id=0"))
        assertEquals("0.0", sendRequestToClient("get_sum", "id=0"))

        assertEquals("OK 1", sendRequestToClient("add", "name=second"))
    }

    @Test
    fun addMoney() {
        assertEquals("OK 0", sendRequestToClient("add", "name=first"))
        assertEquals("OK", sendRequestToClient("add_money", "id=0&amount=123"))
        assertEquals("123.0", sendRequestToClient("get_sum", "id=0"))
    }

    @Test
    fun commonMarketInteractions() {
        assertEquals("OK 0", sendRequestToClient("add", "name=first"))
        assertEquals("OK", sendRequestToClient("add_money", "id=0&amount=1000"))
        assertEquals("OK", sendRequestToClient("buy", "userId=0&stockName=YNDX&quantity=2&expectedPrice=3"))
        assertEquals("OK", sendRequestToClient("buy", "userId=0&stockName=GOOGL&quantity=5&expectedPrice=10"))
        assertEquals(
            "name: YNDX, quantity: 2, price: 3.0</br>name: GOOGL, quantity: 5, price: 10.0",
            sendRequestToClient("get_stocks", "id=0")
        )
        assertEquals("1000.0", sendRequestToClient("get_sum", "id=0"))

        assertEquals("OK", sendRequestToMarket("set_price", "name=YNDX&price=1"))
        assertEquals("OK", sendRequestToMarket("set_price", "name=GOOGL&price=1"))

        assertEquals(
            "name: YNDX, quantity: 2, price: 1.0</br>name: GOOGL, quantity: 5, price: 1.0",
            sendRequestToClient("get_stocks", "id=0")
        )
        assertEquals("951.0", sendRequestToClient("get_sum", "id=0"))
        assertEquals("Not enough stocks", sendRequestToClient("sell", "userId=0&stockName=YNDX&quantity=100&expectedPrice=1"))
        assertEquals("OK", sendRequestToClient("sell", "userId=0&stockName=YNDX&quantity=1&expectedPrice=1"))
        assertEquals("OK", sendRequestToClient("sell", "userId=0&stockName=GOOGL&quantity=5&expectedPrice=1"))
        assertEquals("name: YNDX, quantity: 1, price: 1.0", sendRequestToClient("get_stocks", "id=0"))
        assertEquals("951.0", sendRequestToClient("get_sum", "id=0"))

        assertEquals("User doesn't own any GOOGL stocks", sendRequestToClient("sell", "userId=0&stockName=GOOGL&quantity=1&expectedPrice=1"))
    }

    @Test
    fun incorrectExpectedPrice() {
        assertEquals("OK 0", sendRequestToClient("add", "name=first"))
        assertEquals("OK", sendRequestToClient("add_money", "id=0&amount=1000"))
        assertEquals("Price has changed", sendRequestToClient("buy", "userId=0&stockName=YNDX&quantity=1&expectedPrice=228"))
        assertEquals("", sendRequestToClient("get_stocks", "id=0"))
        assertEquals("1000.0", sendRequestToClient("get_sum", "id=0"))
    }
}