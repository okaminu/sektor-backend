package lt.boldadmin.sektor.backend.test.unit.exception.handler

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
        val templateExceptionHandler: TemplateExceptionHandler = spy()
        doReturn(true).`when`(templateExceptionHandler).canHandle(any())

        val response = templateExceptionHandler.handle(mock(), mock())

        assertEquals(Mono.empty(), response)
        verify(templateExceptionHandler).canHandle(any())
        verify(templateExceptionHandler).handleException(any(), any())
    }

    @Test
    fun `Returns error when exception could not be handled`() {
        expectedException.expect(Exception::class.java)
        val errorDummy = Exception()
        val templateExceptionHandler: TemplateExceptionHandler = spy()
        doReturn(false).`when`(templateExceptionHandler).canHandle(any())

        val response = templateExceptionHandler.handle(mock(), errorDummy)
        response.block()
    }

    @Test
    fun `Logs an exception`() {
        val errorSpy: Exception = mock()
        val templateExceptionHandler = spy<TemplateExceptionHandler>()

        templateExceptionHandler.handle(mock(), errorSpy)

        verify(errorSpy).printStackTrace()
    }

}