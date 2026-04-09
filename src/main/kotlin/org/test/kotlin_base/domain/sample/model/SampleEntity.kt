package org.test.kotlin_base.domain.sample.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

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
