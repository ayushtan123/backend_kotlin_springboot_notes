package com.tandon_ksb.backend_kotlin_springboot.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.Base64
import java.util.Date

@Service
class JwtService (
    @Value("\${jwt.secret}") private val jwtSecret: String ){
    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
     // It decodes the base64 encoded JWT secret and creates a key for signing JWTs.

    private val accessTokenValidityMs = 15L * 60L * 1000L           // 15 minutes (access token)
    val refreshTokenValidityMs = 15L * 24L * 60L * 60L * 1000L      // 15 days (refresh token)

    private fun generateToken(
        userId : String,
        type : String,
        expiry : Long
    ) : String {
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(userId)
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(userId: String): String {
        return generateToken(userId, "access", accessTokenValidityMs)
    }

    fun generateRefreshToken(userId: String): String {
        return generateToken(userId, "refresh", refreshTokenValidityMs)
    }

    fun validateAccessToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?:  return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == "access"
    }

    fun validateRefreshToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?:  return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == "refresh"
    }

    // The `getUserIdFromToken` function extracts the user ID from a JWT token.
    fun getUserIdFromToken(token: String): String? {
        val claims = parseAllClaims(token) ?: throw ResponseStatusException(
             HttpStatusCode.valueOf(401),
            "Invalid refresh token"
        )
        return claims.subject
    }

    // The `parseAllClaims` function attempts to parse the JWT token and extract its claims.
    // It uses the JWT library to create a parser that verifies the token with the provided secret key.
    // If the token is valid, it returns the claims contained in the token.
    // If the token is invalid or parsing fails, it catches the exception and returns null.

    private fun parseAllClaims(token: String): Claims? {
        val rawToken = if(token.startsWith("Bearer ")) {
            token.removePrefix("Bearer ")
        } else {
            token
        }
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (e: Exception) {
            null // Return null if parsing fails
        }
    }
}