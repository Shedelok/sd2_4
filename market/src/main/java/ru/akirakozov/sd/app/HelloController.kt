package ru.akirakozov.sd.app

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@RestController
class HelloController {
    @RequestMapping("/hello")
    fun hello(name: String): String {
        return "Hello, $name!"
    }
}