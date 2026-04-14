package org.test.kotlin_base.adapter.out.persistence.jooq.sample

import org.jooq.DSLContext
import org.springframework.stereotype.Component
import org.test.kotlin_base.adapter.out.persistence.jooq.generated.tables.references.MY_TABLE1
import org.test.kotlin_base.application.port.out.sample.LoadSamplePort
import org.test.kotlin_base.domain.sample.model.Sample

@Component
class SamplePersistenceAdapter(
    private val dslContext: DSLContext,
) : LoadSamplePort {
    override fun findAll(): List<Sample> {
        return dslContext.select(
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
}
