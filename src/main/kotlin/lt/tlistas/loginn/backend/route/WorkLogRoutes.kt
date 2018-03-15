package lt.tlistas.loginn.backend.route

import lt.tlistas.loginn.backend.handler.identityconfirmed.WorkLogHandler
import org.aspectj.apache.bcel.classfile.AttributeUtils.accept

class WorkLogRoutes(private val workLogHandler: WorkLogHandler) {

    fun router() = router {
        "/worklog".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                POST("/log-by-location", workLogHandler::logByLocation)
            }
        }
        GET("/status", { ok().body(fromObject("OK")) })
    }
}