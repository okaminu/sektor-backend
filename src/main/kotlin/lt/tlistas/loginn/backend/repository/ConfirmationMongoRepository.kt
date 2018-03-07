package lt.tlistas.loginn.backend.repository

import lt.tlistas.crowbar.repository.ConfirmationRepository
import lt.tlistas.crowbar.type.entity.Confirmation
import org.springframework.data.mongodb.repository.MongoRepository

interface ConfirmationMongoRepository : ConfirmationRepository, MongoRepository<Confirmation, String> {

    override fun save(authentication: Confirmation)

    override fun existsByToken(token: String): Boolean

    override fun findByToken(token: String): Confirmation
}