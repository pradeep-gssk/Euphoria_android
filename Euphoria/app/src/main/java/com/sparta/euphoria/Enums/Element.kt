package com.sparta.euphoria.Enums

enum class Element(val element: String) {

    Earth("Earth"),
    Water("Water"),
    Wood("Wood"),
    Fire("Fire"),
    Metal("Metal");

    companion object {
        private val map = Element.values().associateBy(Element::element)
        fun getElement(type: String) = map[type]
    }
}