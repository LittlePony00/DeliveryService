package com.nikita.main.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.*

//@Component
@Order(1)
class LoggingAndTracingFilter : Filter {
    override fun doFilter(
        servletRequest: ServletRequest,
        servletResponse: ServletResponse,
        filterChain: FilterChain
    ) {
        val request = servletRequest as HttpServletRequest
        val response = servletResponse as HttpServletResponse

        var correlationId = request.getHeader(CORRELATION_ID_HEADER)
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString()
        }

        MDC.put(CORRELATION_ID_MDC_KEY, correlationId)

        response.setHeader(CORRELATION_ID_HEADER, correlationId)

        val startTime = System.currentTimeMillis()
        val needLog = request.requestURI.startsWith("/api", true)

        try {
            if (needLog) { LOG.info("Request started: ${request.method} ${request.requestURI}") }

            filterChain.doFilter(request, response)
        } finally {
            val duration = System.currentTimeMillis() - startTime

            if (needLog) LOG.info("Request finished: ${request.method} ${request.requestURI} with status ${response.status} in $duration ms")
            MDC.remove(CORRELATION_ID_MDC_KEY)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(LoggingAndTracingFilter::class.java)

        private const val CORRELATION_ID_HEADER = "X-Request-ID"
        private const val CORRELATION_ID_MDC_KEY = "correlationId"
    }
}
