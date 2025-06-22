package com.tandon_ksb.backend_kotlin_springboot.database.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "refresh_tokens")
data class RefreshToken(
    val userId: ObjectId,
    @Indexed(expireAfter = "0s")
    // mongodb will automatically delete the record after expiry
    val expiresAt: Instant,
    val hashedToken : String,
    val createdAt: Instant = Instant.now()
)
