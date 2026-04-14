package org.test.kotlin_base.adapter.output.persistence.jooq.sample

import org.jooq.DSLContext
import org.springframework.stereotype.Component
import org.test.kotlin_base.application.port.output.sample.LoadSamplePort
import org.test.kotlin_base.domain.sample.model.Sample
import org.test.kotlin_base.jooq.generated.tables.MyTable1.Companion.MY_TABLE1

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
