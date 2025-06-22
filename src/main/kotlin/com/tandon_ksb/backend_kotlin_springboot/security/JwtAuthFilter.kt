package com.tandon_ksb.backend_kotlin_springboot.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter (private val jwtService : JwtService)
    : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ){
         val authHeader = request.getHeader("Authorization")
         if(authHeader != null && authHeader.startsWith("Bearer ")){

              if(jwtService.validateAccessToken(authHeader)){
                  val userId = jwtService.getUserIdFromToken(authHeader)

                  val auth = UsernamePasswordAuthenticationToken(userId, null, emptyList())
                      SecurityContextHolder.getContext().authentication = auth
              }
        }

        filterChain.doFilter(request, response)
    }
}



//This code defines a JWT authentication filter for a Spring Boot application using Kotlin. The `JwtAuthFilter` class extends `OncePerRequestFilter`, which allows it to intercept HTTP requests and perform authentication checks.

// The `doFilterInternal` method checks for the presence of an "Authorization" header in the request. If the header exists and starts with "Bearer ", it validates the JWT token using the `JwtService`.

// If the token is valid, it extracts the user ID from the token and creates a `UsernamePasswordAuthenticationToken`, which is then set in the security context.

// This allows subsequent parts of the application to access the authenticated user's information.

//Finally, it calls `filterChain.doFilter(request, response)` to continue processing the request. This filter is typically used to secure endpoints by ensuring that only requests with valid JWT tokens can access protected resources.