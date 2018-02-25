package lt.tlistas.loginn.backend.repository

import lt.tlistas.mobile.number.confirmation.repository.ConfirmationRepository
import lt.tlistas.mobile.number.confirmation.type.entity.Confirmation
import org.springframework.data.mongodb.repository.MongoRepository

interface ConfirmationMongoRepository : ConfirmationRepository, MongoRepository<Confirmation, String> {

    override fun save(confirmation: Confirmation)

    override fun delete(confirmation: Confirmation)

    override fun existsByCode(confirmationCode: String): Boolean

    override fun findByCode(confirmationCode: String): Confirmation
}