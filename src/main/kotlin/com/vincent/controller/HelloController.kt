package com.vincent.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/echo")
class HelloController {

    @GetMapping
    fun echo(@RequestParam message: String) = "Echo: $message"

}