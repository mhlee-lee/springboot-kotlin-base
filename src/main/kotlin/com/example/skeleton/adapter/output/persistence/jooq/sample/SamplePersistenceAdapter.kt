package com.example.skeleton.adapter.output.persistence.jooq.sample

import com.example.skeleton.application.port.output.sample.LoadSamplePort
import com.example.skeleton.domain.sample.model.Sample
import com.example.skeleton.jooq.generated.tables.MyTable1.Companion.MY_TABLE1
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component
class SamplePersistenceAdapter(private val dslContext: DSLContext) : LoadSamplePort {
    override fun findAll(): List<Sample> = dslContext.select(
        MY_TABLE1.ID,
        MY_TABLE1.NAME,
        MY_TABLE1.AGE,
    )
        .from(MY_TABLE1)
        .orderBy(MY_TABLE1.ID.asc())
        .fetch { record ->
            Sample(
                id = record[MY_TABLE1.ID],
                name = record[MY_TABLE1.NAME],
                age = record[MY_TABLE1.AGE],
            )
        }
}
