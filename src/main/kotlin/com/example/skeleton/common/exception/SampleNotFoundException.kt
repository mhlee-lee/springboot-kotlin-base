package com.example.skeleton.common.exception

import com.example.skeleton.common.errors.SampleErrorCode
import org.springframework.http.HttpStatus

class SampleNotFoundException(id: Long) :
    DefaultException(HttpStatus.NOT_FOUND, SampleErrorCode.SAMPLE_NOT_FOUND, arrayOf(id))
