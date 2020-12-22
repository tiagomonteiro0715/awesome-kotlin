package link.kotlin.scripts.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

fun ObjectMapper(): ObjectMapper {
    return ObjectMapper().also {
        it.registerModule(KotlinModule())
        it.registerModule(JavaTimeModule())
    }
}
