package com.tandon_ksb.backend_kotlin_springboot.security

import com.tandon_ksb.backend_kotlin_springboot.database.model.RefreshToken
import com.tandon_ksb.backend_kotlin_springboot.database.model.User
import com.tandon_ksb.backend_kotlin_springboot.database.repository.RefreshTokenRepository
import com.tandon_ksb.backend_kotlin_springboot.database.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.Base64
import java.util.*


@Service
class AuthService (
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    data class TokenPair(
        val accessToken:  String,
        val refreshToken: String
    )

    fun register(email: String, password: String): User {
        val user = userRepository.findByEmail(email.trim())
        if(user != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists with this email")
        }
        val pswd = hashEncoder.encode(password)
        return userRepository.save(
            User(email = email, hashedPassword = pswd))
    }

    fun login(email: String, password: String): TokenPair {
        // find user by email
        val user = userRepository.findByEmail(email)
            ?: throw BadCredentialsException ("User not found")

        // check if the password matches the hashed password stored in the database

        if (!hashEncoder.matches(password, user.hashedPassword)) {
            throw BadCredentialsException ("Invalid password")
        }

        // user entered valid credentials, generate tokens

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(newAccessToken, newRefreshToken)
    }

    // Apply only if all operations are successful, otherwise dont commit the transaction
    @Transactional
    fun refresh(refreshToken: String): TokenPair  {
        if(!jwtService.validateRefreshToken(refreshToken)){
            throw ResponseStatusException (HttpStatusCode.valueOf(401),"Invalid refresh token")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow{
            ResponseStatusException (HttpStatusCode.valueOf(401),"Invalid refresh token")
        }

        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw ResponseStatusException (HttpStatusCode.valueOf(401), "Refresh token not found")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())
        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)
        return TokenPair(newAccessToken, newRefreshToken)
    }

    // If access token expires, we need to get it from refresh token. In order to validate the refresh token, we need to save it.

    private fun storeRefreshToken(userId: ObjectId, refreshToken: String) {

        // It hashes the refresh token and saves it in the database with the user ID and expiry time.

        val hashed = hashToken(refreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hashedBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashedBytes)
    }
}

// why toHexString is used here?
// because the id is of type ObjectId, which is not a string, so we need to convert it to a string

// what does toHexString do?
// it converts the ObjectId to a hexadecimal string representation

// toString() would return the ObjectId in a format that is not suitable for JWT generation