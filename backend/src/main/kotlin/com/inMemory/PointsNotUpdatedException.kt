package com.inMemory

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class PointsNotUpdatedException(message: String) : Exception(message)