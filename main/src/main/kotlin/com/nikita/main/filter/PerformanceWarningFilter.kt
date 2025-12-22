package com.immortalidiot.main.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(2)
class PerformanceWarningFilter : Filter {
    override fun doFilter(
        servletRequest: ServletRequest,
        servletResponse: ServletResponse,
        filterChain: FilterChain
    ) {
        val request = servletRequest as HttpServletRequest
        val response = servletResponse as HttpServletResponse
        val startTime = System.currentTimeMillis()

        try {
            filterChain.doFilter(request, response)
        } finally {
            val duration = System.currentTimeMillis() - startTime

            if (duration >= TIME_MILLIS_WARNING) { LOG.warn("Slow request detected: ${request.method} ${request.requestURI} with status ${response.status} in $duration ms") }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PerformanceWarningFilter::class.java)
        private const val TIME_MILLIS_WARNING = 150
    }
}
