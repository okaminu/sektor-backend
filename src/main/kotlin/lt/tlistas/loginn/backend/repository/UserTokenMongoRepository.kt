package lt.tlistas.loginn.backend.repository

import lt.tlistas.crowbar.repository.UserTokenRepository
import lt.tlistas.crowbar.type.entity.UserToken
import org.springframework.data.mongodb.repository.MongoRepository

interface UserTokenMongoRepository : UserTokenRepository, MongoRepository<UserToken, String> {

    override fun save(userToken: UserToken)

    override fun existsByToken(token: String): Boolean

    override fun findByToken(token: String): UserToken
}