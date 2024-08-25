package com.mss.assignment.exception

import org.springframework.http.HttpStatus

data class NotFoundException(
    override val errorCode: ErrorCode,
    override val status: HttpStatus = HttpStatus.NOT_FOUND,
) : GlobalHttpException(status, errorCode)
