package com.tandon_ksb.backend_kotlin_springboot.controller

import com.tandon_ksb.backend_kotlin_springboot.database.model.Note
import com.tandon_ksb.backend_kotlin_springboot.database.repository.NoteRepository
import com.tandon_ksb.backend_kotlin_springboot.security.SecurityConfig
import jakarta.validation.Valid
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import org.springframework.web.bind.annotation.*

// multiple endpoints for notes

// GET http://localhost:8080/notes?ownerID=56356
// POST http://localhost:8080/notes
// DELETE http://localhost:8080/notes/56356

@RestController
@RequestMapping("/notes")

class NoteController(
    private val repository: NoteRepository,
    private val noteRepository: NoteRepository
) {
    data class NoteRequest(
        val id : String?,
        @field:jakarta.validation.constraints.NotBlank (message = "Title cannot be blank")
        val title: String,
        val content: String,
        val color: Long
    )

    data class NoteResponse(
        val id: String,
        val title: String,
        val content: String,
        val color: Long,
        val createdAt: Instant
    )

    // serialization and deserialization via Spring Boot

    @PostMapping
    // aim is to update and create notes
    fun save(@Valid @RequestBody body:NoteRequest) : NoteResponse {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        val note = repository.save(
            Note(
                id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                title = body.title,
                content = body.content,
                color = body.color,
                createdAt = Instant.now(),
                ownerId = ObjectId(ownerId)
            )
        )

        return note.toResponse()
    }

    // return all notes of a user
    @GetMapping
    fun findByOwnerId() : List<NoteResponse> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String

        return repository.findByOwnerId(ObjectId(ownerId)).map{
            it.toResponse()
        }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
        val note = noteRepository.findById(ObjectId(id)).orElseThrow {
            IllegalArgumentException("Note not found")
        }
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        if(note.ownerId.toHexString() == ownerId) {
            repository.deleteById(ObjectId(id))
        }
    }
}

// returns response for a note
private fun Note.toResponse(): NoteController.NoteResponse {
    return NoteController.NoteResponse(
        id = id.toHexString(),
        title = title,
        content = content,
        color = color,
        createdAt = createdAt
    )
}

/*
GET -> retrieve data
PUT -> update data
POST -> create data
DELETE -> delete data
 */