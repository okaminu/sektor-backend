package lt.boldadmin.sektor.backend.test.unit.exception.handler

import com.nhaarman.mockitokotlin2.*
import lt.boldadmin.sektor.backend.exception.IncorrectConfirmationCodeException
import lt.boldadmin.sektor.backend.exception.handler.IncorrectConfirmationCodeExceptionHandler
import lt.boldadmin.sektor.backend.exception.handler.TemplateExceptionHandler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class IncorrectConfirmationCodeExceptionHandlerTest {

    private lateinit var handler: IncorrectConfirmationCodeExceptionHandler

    @BeforeEach
    fun `Set up`() {
        handler = IncorrectConfirmationCodeExceptionHandler
    }

    @Test
    fun `Handles exception`() {
        val exchangeStub: ServerWebExchange = mock()
        doReturn(mock<ServerHttpResponse>()).`when`(exchangeStub).response

        val response = handler.handle(exchangeStub, IncorrectConfirmationCodeException)

        assertEquals(Mono.empty<Void>(), response)
    }

    @Test
    fun `Returns error when exception could not be handled`() {
        doReturn(false).`when`(mock<TemplateExceptionHandler>()).canHandle(any())

        assertThrows(Exception::class.java) {
            val response = handler.handle(mock(), mock<Exception>())
            response.block()
        }
    }

    @Test
    fun `Logs an exception`() {
        val exceptionSpy = ExceptionSpy()

        handler.handle(mock(), exceptionSpy)

        assertTrue { exceptionSpy.wasPrintStackTraceCalled }
    }

    @Test
    fun `Sets http response status for IncorrectConfirmationCodeException`() {
        val exchangeStub: ServerWebExchange = mock()
        val httpResponseSpy: ServerHttpResponse = mock()
        doReturn(httpResponseSpy).`when`(exchangeStub).response

        handler.handleException(exchangeStub, IncorrectConfirmationCodeException)

        verify(httpResponseSpy).statusCode = eq(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `Checks if exception type is IncorrectConfirmationCodeException`() {
        assertTrue(handler.canHandle(IncorrectConfirmationCodeException))
    }

    class ExceptionSpy: Exception() {
        var wasPrintStackTraceCalled: Boolean = false
            private set

        override fun printStackTrace() {
            wasPrintStackTraceCalled = true
        }
    }
}