package com.immortalidiot.api.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response with pagination")
data class PagedResponse<T>(
    @Schema(description = "Page content") val content: List<T>,
    @Schema(description = "Page number (start with 0)") val pageNumber: Int,
    @Schema(description = "Page size") val pageSize: Int,
    @Schema(description = "Total elements") val totalElements: Long,
    @Schema(description = "Total pages") val totalPages: Int,
    @Schema(description = "Is page last") val last: Boolean,
)
