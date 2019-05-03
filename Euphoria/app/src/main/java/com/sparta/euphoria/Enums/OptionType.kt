package com.sparta.euphoria.Enums

import com.sparta.euphoria.R

enum class OptionType(val option: Int,
                      val resId: Int) {
    never(
        0,
        R.string.other
    ),

    toggle(
        1,
        R.string.specify
    ),

    always(
        2,
        R.string.other
    );

    companion object {
        private val map = OptionType.values().associateBy(OptionType::option)
        fun getOptionType(type: Int) = map[type]
    }
}