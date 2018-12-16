package lt.boldadmin.sektor.backend.test.unit.config

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.nexus.api.service.worklog.CollaboratorUpdateListener
import lt.boldadmin.sektor.backend.config.NexusBeanFactory
import org.junit.Test
import org.springframework.context.support.GenericApplicationContext
import kotlin.test.assertEquals

class NexusBeanFactoryTest {

    @Test
    fun `Provides Collaborator update listeners map`() {
        val contextStub: GenericApplicationContext = mock()
        val startTimeUpdateDummy: CollaboratorUpdateListener = mock()
        val endTimeUpdateDummy: CollaboratorUpdateListener = mock()
        val expectedMap = mutableMapOf(
            "workTime.startOfDayInMinutes" to startTimeUpdateDummy,
            "workTime.endOfDayInMinutes" to endTimeUpdateDummy
        )
        doReturn(startTimeUpdateDummy, endTimeUpdateDummy).`when`(contextStub)
            .getBean(any<String>(), eq(CollaboratorUpdateListener::class.java))

        val actualMap = NexusBeanFactory(contextStub).createCollaboratorUpdateListenerProvider().invoke()

        assertEquals(expectedMap, actualMap)
    }

}