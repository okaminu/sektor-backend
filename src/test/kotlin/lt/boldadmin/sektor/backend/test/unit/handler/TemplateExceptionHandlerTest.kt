package lt.boldadmin.sektor.backend.test.unit.handler

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.sektor.backend.exception.handler.TemplateExceptionHandler
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import reactor.core.publisher.Mono
import kotlin.test.assertEquals

class TemplateExceptionHandlerTest {

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Test
    fun `Handles generic exception`() {
        val templateExceptionHandler = spy<TemplateExceptionHandler>()
        doReturn(true).`when`(templateExceptionHandler).canHandle(any())

        val response = templateExceptionHandler.handle(mock(), mock())

        assertEquals(Mono.empty(), response)
        verify(templateExceptionHandler).canHandle(any())
        verify(templateExceptionHandler).handleException(any(), any())
    }

    @Test
    fun `Returns error when exception could not be handled`() {
        expectedException.expect(Exception::class.java)
        val errorMock = Exception()
        val templateExceptionHandler = spy<TemplateExceptionHandler>()
        doReturn(false).`when`(templateExceptionHandler).canHandle(any())

        val response = templateExceptionHandler.handle(mock(), errorMock)
        response.block()
    }

}