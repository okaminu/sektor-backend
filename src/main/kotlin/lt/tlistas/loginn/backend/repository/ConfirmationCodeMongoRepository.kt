package lt.tlistas.loginn.backend.repository

import lt.tlistas.crowbar.repository.ConfirmationCodeRepository
import lt.tlistas.crowbar.type.entity.ConfirmationCode
import org.springframework.data.mongodb.repository.MongoRepository

interface ConfirmationCodeMongoRepository : ConfirmationCodeRepository, MongoRepository<ConfirmationCode, String> {

    override fun save(confirmationCode: ConfirmationCode)

    override fun delete(confirmationCode: ConfirmationCode)

    override fun existsByCode(confirmationCode: String): Boolean

    override fun findByCode(confirmationCode: String): ConfirmationCode
}