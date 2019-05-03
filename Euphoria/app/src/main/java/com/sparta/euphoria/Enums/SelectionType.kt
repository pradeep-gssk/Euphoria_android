package com.sparta.euphoria.Enums

enum class SelectionType(val option: Int) {
    single(0),
    multiple(1);

    companion object {
        private val map = SelectionType.values().associateBy(SelectionType::option)
        fun getSelectionType(type: Int) = map[type]
    }
}