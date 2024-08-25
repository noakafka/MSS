package com.mss.assignment.exception

import org.springframework.http.HttpStatus

open class GlobalHttpException(
    open val status: HttpStatus,
    open val errorCode: ErrorCode,
) : RuntimeException()