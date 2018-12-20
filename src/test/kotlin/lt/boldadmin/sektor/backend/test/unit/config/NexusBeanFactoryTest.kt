package lt.boldadmin.sektor.backend.test.unit.config

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import lt.boldadmin.nexus.api.service.worklog.CollaboratorUpdateSubscriber
import lt.boldadmin.sektor.backend.config.NexusBeanFactory
import org.junit.Test
import org.springframework.context.support.GenericApplicationContext
import kotlin.test.assertEquals

class NexusBeanFactoryTest {

    @Test
    fun `Provides Collaborator update subscribers map`() {
        val contextStub: GenericApplicationContext = mock()
        val startTimeUpdateDummy: CollaboratorUpdateSubscriber = mock()
        val endTimeUpdateDummy: CollaboratorUpdateSubscriber = mock()
        val expectedSubscribersMap = mutableMapOf(
            "workTime.startOfDayInMinutes" to startTimeUpdateDummy,
            "workTime.endOfDayInMinutes" to endTimeUpdateDummy
        )
        doReturn(startTimeUpdateDummy, endTimeUpdateDummy).`when`(contextStub)
            .getBean(any<String>(), eq(CollaboratorUpdateSubscriber::class.java))

        val actualSubscribersMap = NexusBeanFactory(contextStub).createCollaboratorUpdateSubscribersProvider().invoke()

        assertEquals(expectedSubscribersMap, actualSubscribersMap)
    }

}