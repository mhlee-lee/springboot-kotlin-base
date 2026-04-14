package org.test.kotlin_base.adapter.out.persistence.jpa.sample.entity

import jakarta.persistence.*
import org.test.kotlin_base.domain.sample.model.Sample

@Entity
@Table(name = "my_table1")
class SampleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,
    @Column(name = "name")
    var name: String? = null,
    @Column(name = "age")
    var age: Int? = null,
)

fun SampleEntity.toDomain(): Sample {
    return Sample(
        id = id,
        name = name,
        age = age,
    )
}
