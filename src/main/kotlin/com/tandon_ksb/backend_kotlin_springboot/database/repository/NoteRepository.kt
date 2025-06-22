package com.tandon_ksb.backend_kotlin_springboot.database.repository

import com.tandon_ksb.backend_kotlin_springboot.database.model.Note
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface NoteRepository: MongoRepository<Note, ObjectId> {
    fun findByOwnerId(ownerId: ObjectId): List<Note>
}

// Repository is an interface that provides methods for CRUD operations on Note documents in MongoDB.
// inbuilt methods include by MongoDB and Spring Data MongoDB, such as save(), findById(), findAll(), deleteById(), etc.
