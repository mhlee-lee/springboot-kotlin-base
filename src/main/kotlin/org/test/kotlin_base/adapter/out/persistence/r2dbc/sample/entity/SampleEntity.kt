package org.test.kotlin_base.adapter.out.persistence.r2dbc.sample.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.test.kotlin_base.domain.sample.model.Sample

@Table("my_table1")
data class SampleEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("name")
    val name: String? = null,
    @Column("age")
    val age: Int? = null,
)

fun SampleEntity.toDomain(): Sample {
    return Sample(
        id = id,
        name = name,
        age = age,
    )
}
