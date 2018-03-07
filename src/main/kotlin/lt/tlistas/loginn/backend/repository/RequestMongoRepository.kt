package lt.tlistas.loginn.backend.repository

import lt.tlistas.crowbar.repository.RequestRepository
import lt.tlistas.crowbar.type.entity.Request
import org.springframework.data.mongodb.repository.MongoRepository

interface RequestMongoRepository : RequestRepository, MongoRepository<Request, String> {

    override fun save(confirmation: Request)

    override fun delete(confirmation: Request)

    override fun existsByCode(confirmationCode: String): Boolean

    override fun findByCode(confirmationCode: String): Request
}