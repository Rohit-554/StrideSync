package io.jadu.strideSync

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testHealth() = testApplication {
        application {
            module()
        }
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("{\"status\":\"ok\"}", response.bodyAsText())
    }
}