package lt.boldadmin.sektor.backend.repository

import lt.boldadmin.crowbar.repository.UserTokenRepository
import lt.boldadmin.crowbar.type.entity.UserToken
import org.springframework.data.mongodb.repository.MongoRepository

interface UserTokenMongoRepository : UserTokenRepository, MongoRepository<UserToken, String> {

    override fun existsByToken(token: String): Boolean

    override fun findByToken(token: String): UserToken

    override fun save(token: UserToken)
}