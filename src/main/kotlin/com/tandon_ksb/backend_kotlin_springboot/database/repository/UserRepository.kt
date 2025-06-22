package com.tandon_ksb.backend_kotlin_springboot.database.repository

import com.tandon_ksb.backend_kotlin_springboot.database.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, ObjectId> {
    fun findByEmail(email: String): User?
}