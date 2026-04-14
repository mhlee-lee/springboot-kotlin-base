package com.example.skeleton.domain.sample.model

import com.example.skeleton.common.enums.DisplayEnum

enum class Gender(override val label: String, override val priority: Int, override val displayable: Boolean) :
    DisplayEnum {
    MALE("enum.Gender.MALE", 1, true),
    FEMALE("enum.Gender.FEMALE", 2, true),
}
