package lt.boldadmin.sektor.backend.repository

import lt.boldadmin.crowbar.repository.UserConfirmationCodeRepository
import lt.boldadmin.crowbar.type.entity.UserConfirmationCode
import org.springframework.data.mongodb.repository.MongoRepository

interface UserConfirmationCodeMongoRepository
    : UserConfirmationCodeRepository, MongoRepository<UserConfirmationCode, String> {

    override fun save(code: UserConfirmationCode)

    override fun deleteByCode(id: String)

    override fun existsByCode(code: String): Boolean

    override fun findByCode(code: String): UserConfirmationCode
}