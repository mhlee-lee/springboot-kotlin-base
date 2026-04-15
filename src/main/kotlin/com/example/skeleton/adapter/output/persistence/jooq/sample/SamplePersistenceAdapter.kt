package com.example.skeleton.adapter.output.persistence.jooq.sample

import com.example.skeleton.application.port.output.sample.SamplePort
import com.example.skeleton.domain.sample.model.Sample
import com.example.skeleton.jooq.generated.tables.Samples.Companion.SAMPLES
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Component

@Component
class SamplePersistenceAdapter(private val dslContext: DSLContext) : SamplePort {

    override fun findAll(): List<Sample> = dslContext
        .selectFrom(SAMPLES)
        .orderBy(SAMPLES.ID.asc())
        .fetch(::toSample)

    override fun findByFilter(name: String?, minAge: Int?, maxAge: Int?): List<Sample> {
        val conditions = DSL.noCondition()
            .let { c -> name?.let { c.and(SAMPLES.NAME.containsIgnoreCase(it)) } ?: c }
            .let { c -> minAge?.let { c.and(SAMPLES.AGE.ge(it)) } ?: c }
            .let { c -> maxAge?.let { c.and(SAMPLES.AGE.le(it)) } ?: c }

        return dslContext
            .selectFrom(SAMPLES)
            .where(conditions)
            .orderBy(SAMPLES.ID.asc())
            .fetch(::toSample)
    }

    override fun findById(id: Long): Sample? = dslContext
        .selectFrom(SAMPLES)
        .where(SAMPLES.ID.eq(id))
        .fetchOne(::toSample)

    override fun insert(name: String, age: Int): Sample = dslContext
        .insertInto(SAMPLES)
        .set(SAMPLES.NAME, name)
        .set(SAMPLES.AGE, age)
        .returning()
        .fetchSingle(::toSample)

    override fun update(id: Long, name: String, age: Int): Boolean = dslContext
        .update(SAMPLES)
        .set(SAMPLES.NAME, name)
        .set(SAMPLES.AGE, age)
        .where(SAMPLES.ID.eq(id))
        .execute() > 0

    override fun delete(id: Long): Boolean = dslContext
        .deleteFrom(SAMPLES)
        .where(SAMPLES.ID.eq(id))
        .execute() > 0

    private fun toSample(record: org.jooq.Record): Sample = Sample(
        id = record[SAMPLES.ID]!!,
        name = record[SAMPLES.NAME]!!,
        age = record[SAMPLES.AGE]!!,
    )
}
