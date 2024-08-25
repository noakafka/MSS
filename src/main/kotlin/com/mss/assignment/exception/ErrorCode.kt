package com.mss.assignment.exception

import org.springframework.context.MessageSource
import java.util.Locale

enum class ErrorCode(private val key: String) {
    BAD_REQUEST("error.bad_request"),
    PRODUCT_NOT_FOUND("error.not_found.product"),
    BRAND_NOT_FOUND("error.not_found.brand"),
    CATEGORY_NOT_FOUND("error.not_found.category"),
    INTERNAL_SERVER_ERROR("error.internal_server"),
    ;

    fun getMessage(
        messageSource: MessageSource,
        vararg args: Any,
        locale: Locale = Locale.KOREA,
    ): String {
        return messageSource.getMessage(this.key, args, locale)
    }
}
