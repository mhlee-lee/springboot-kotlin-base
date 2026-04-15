package com.example.skeleton.adapter.input.web.sample

import com.example.skeleton.adapter.input.web.sample.protocol.CreateSampleRequest
import com.example.skeleton.adapter.input.web.sample.protocol.SampleResponse
import com.example.skeleton.adapter.input.web.sample.protocol.SampleSearchRequest
import com.example.skeleton.adapter.input.web.sample.protocol.UpdateSampleRequest
import com.example.skeleton.application.port.input.sample.SampleUseCase
import com.example.skeleton.common.exception.InvalidPathParameterException
import com.example.skeleton.common.exception.RequiredRequestBodyException
import com.example.skeleton.common.extensions.awaitBodyValidated
import com.example.skeleton.common.extensions.bindQueryParams
import com.example.skeleton.common.extensions.headerOrThrow
import com.example.skeleton.common.extensions.validateOrThrow
import jakarta.validation.Validator
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class SampleHandler(private val sampleUseCase: SampleUseCase, private val validator: Validator) {

    // ── GET /samples?name=...&minAge=...&maxAge=... ──────────────────────────
    // QueryParam → model 바인딩 (bindQueryParams) + validation 예시
    suspend fun searchSamples(request: ServerRequest): ServerResponse {
        val searchRequest = request.bindQueryParams<SampleSearchRequest>()
        validator.validateOrThrow(searchRequest)

        val result = sampleUseCase.searchSamples(searchRequest.toQuery())
        return ServerResponse.ok().bodyValueAndAwait(result.map(SampleResponse::from))
    }

    // ── GET /samples/{id} ────────────────────────────────────────────────────
    // PathVariable 예시 + 존재하지 않으면 404 에러 응답
    suspend fun getSample(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLongOrNull()
            ?: throw InvalidPathParameterException("id")

        val result = sampleUseCase.getSample(id)
        return ServerResponse.ok().bodyValueAndAwait(SampleResponse.from(result))
    }

    // ── POST /samples ────────────────────────────────────────────────────────
    // Body → model + validation 예시 (awaitBodyValidated)
    suspend fun createSample(request: ServerRequest): ServerResponse {
        val body = request.awaitBodyValidated<CreateSampleRequest>(validator)

        val result = sampleUseCase.createSample(body.toCommand())
        return ServerResponse.ok().bodyValueAndAwait(SampleResponse.from(result))
    }

    // ── PUT /samples/{id} ────────────────────────────────────────────────────
    // PathVariable + Header + Body validation 종합 예시
    suspend fun updateSample(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLongOrNull()
            ?: throw InvalidPathParameterException("id")
        val modifiedBy = request.headerOrThrow("X-Modified-By")
        val body = request.awaitBodyOrNull<UpdateSampleRequest>()
            ?.let(validator::validateOrThrow)
            ?: throw RequiredRequestBodyException()

        val command = body.toCommand(id, modifiedBy)
        val result = sampleUseCase.updateSample(command)
        return ServerResponse.ok().bodyValueAndAwait(SampleResponse.from(result))
    }

    // ── DELETE /samples/{id} ─────────────────────────────────────────────────
    // PathVariable + 존재하지 않으면 404 에러 응답
    suspend fun deleteSample(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLongOrNull()
            ?: throw InvalidPathParameterException("id")

        sampleUseCase.deleteSample(id)
        return ServerResponse.noContent().buildAndAwait()
    }
}
