package xyz.atrius

import org.slf4j.LoggerFactory
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
object ChatController {
    var counter = 1

    private val logger = LoggerFactory.getLogger(ChatController::class.java)

    @GetMapping("/")
    fun hello(): ResponseEntity<String> {
        logger.info("Called '/' $counter time(s)")
        return "Hello, World! ${counter++}".asEntity()
    }
}


fun <T> T.asEntity(): ResponseEntity<T> =
    ResponseEntity(this, HttpStatus.OK)