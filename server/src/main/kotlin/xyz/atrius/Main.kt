package xyz.atrius

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class PopChat

fun main(args: Array<String>) {
    runApplication<PopChat>(*args)
}

@RestController
object RestController {

    @GetMapping("/")
    fun hello(): ResponseEntity<String> =
        "Hello, World!".asEntity()
}

fun <T> T.asEntity(): ResponseEntity<T> =
    ResponseEntity(this, HttpStatus.OK)