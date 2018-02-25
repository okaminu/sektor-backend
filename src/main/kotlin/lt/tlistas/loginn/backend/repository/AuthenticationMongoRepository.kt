package lt.tlistas.loginn.backend.repository

import lt.tlistas.mobile.number.confirmation.repository.AuthenticationRepository
import lt.tlistas.mobile.number.confirmation.type.entity.Authentication
import org.springframework.data.mongodb.repository.MongoRepository

interface AuthenticationMongoRepository : AuthenticationRepository, MongoRepository<Authentication, String> {

    override fun save(authentication: Authentication)

    override fun existsByToken(token: String): Boolean

    override fun findByToken(token: String): Authentication
}