package org.test.kotlin_base.presentation.enums

import org.test.kotlin_base.common.enums.DisplayEnum


enum class Gender(
    override val label: String,
    override val priority: Int,
    override val displayable: Boolean = true,
) : DisplayEnum {
    MALE("enum.Gender.MALE", 1),
    FEMALE("enum.Gender.FEMALE", 2),
    ;
}
