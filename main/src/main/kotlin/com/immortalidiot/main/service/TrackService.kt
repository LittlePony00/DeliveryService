package com.immortalidiot.main.service

import com.immortalidiot.api.dto.PagedResponse
import com.immortalidiot.api.dto.TrackRequest
import com.immortalidiot.api.dto.TrackResponse
import com.immortalidiot.api.dto.TrackStatus
import com.immortalidiot.api.exception.ResourceNotFoundException
import com.immortalidiot.events.TrackEvent
import com.immortalidiot.main.config.RabbitMQConfig
import com.immortalidiot.main.storage.InMemoryRepository
import com.immortalidiot.main.websocket.TrackWebSocketPublisher
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import kotlin.math.ceil
import kotlin.math.min

interface TrackService {
    fun createTrack(trackRequest: TrackRequest): TrackResponse
    fun getTrackById(id: Long): TrackResponse
    fun getAllTracks(page: Int, size: Int): PagedResponse<TrackResponse>
    fun archiveTrack(id: Long): TrackResponse
    fun deleteTrack(id: Long): TrackResponse
}

@Service
internal class TrackServiceImpl(
    private val rabbitMqTemplate: RabbitTemplate,
    private val repository: InMemoryRepository,
    private val publisher: TrackWebSocketPublisher
) : TrackService {
    override fun createTrack(trackRequest: TrackRequest): TrackResponse {
        val newTrack = repository.createTrack(trackRequest)

        createTrackCreatedEvent(
            id = newTrack.id,
            title = newTrack.title,
            author = newTrack.author
        )

        return newTrack
    }

    private fun createTrackCreatedEvent(id: Long, title: String, author: String) {
        val event = TrackEvent.TrackCreatedEvent(
            trackId = id,
            title = title,
            author = author
        )

        rabbitMqTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_TRACK_CREATED, event)

        publisher.sendEvent(event)
    }

    override fun getTrackById(id: Long): TrackResponse {
        val track = repository.getTrackById(id) ?: throw ResourceNotFoundException("Track", id)
        if (track.status != TrackStatus.ACTIVE) throw ResourceNotFoundException("Track", id)

        return track
    }

    override fun getAllTracks(page: Int, size: Int): PagedResponse<TrackResponse> {
        val allTracks = repository.getAllTracks()
            .sortedBy(TrackResponse::id)

        val totalElements = allTracks.size
        val totalPages = ceil(totalElements / size.toDouble()).toInt()
        val fromIndex = page * size
        val toIndex = min(fromIndex + size, totalElements)

        val content = if (fromIndex >= totalElements) emptyList()
        else allTracks.subList(fromIndex, toIndex).filter { track -> track.status == TrackStatus.ACTIVE }

        return PagedResponse(
            content = content,
            pageNumber = page,
            pageSize = size,
            totalElements = totalElements.toLong(),
            totalPages = totalPages,
            last = page >= totalPages - 1
        )
    }

    override fun archiveTrack(id: Long): TrackResponse =
        repository.archiveTrack(id) ?: throw NoSuchElementException("Track with id $id not found")

    override fun deleteTrack(id: Long): TrackResponse {
        val track = repository.deleteTrack(id) ?: throw NoSuchElementException("Track with id $id not found")

        createTrackDeletedEvent(track.id)

        return track
    }

    private fun createTrackDeletedEvent(id: Long) {
        val event = TrackEvent.TrackDeletedEvent(
            trackId = id,
        )

        rabbitMqTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_TRACK_DELETED, event)

        publisher.sendEvent(event)
    }
}
