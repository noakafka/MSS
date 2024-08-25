package com.mss.assignment.exception

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messageSource: ResourceBundleMessageSource
) {
    @ExceptionHandler(GlobalHttpException::class)
    fun handleGlobalHttpException(ex: GlobalHttpException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = ex.status.value(),
            message = ex.errorCode.getMessage(messageSource)
        )
        return ResponseEntity(errorResponse, ex.status)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = ErrorCode.INTERNAL_SERVER_ERROR.getMessage(messageSource)
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}