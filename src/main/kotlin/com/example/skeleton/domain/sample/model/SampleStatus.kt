package com.example.skeleton.domain.sample.model

import com.example.skeleton.common.enums.DisplayEnum
import com.example.skeleton.common.enums.GenericEnum

enum class SampleStatus(
    override val value: String,
    override val label: String,
    override val priority: Int,
    override val displayable: Boolean = true,
) : GenericEnum, DisplayEnum {
    ACTIVE("active", "enum.SampleStatus.ACTIVE", 1),
    INACTIVE("inactive", "enum.SampleStatus.INACTIVE", 2),
    SUSPENDED("suspended", "enum.SampleStatus.SUSPENDED", 3),
    // companion object 불필요 — 공통 유틸 사용:
    // requireByValue<SampleStatus>("active")  → SampleStatus.ACTIVE
    // byValue<SampleStatus>("active")         → SampleStatus.ACTIVE? (nullable)
}
