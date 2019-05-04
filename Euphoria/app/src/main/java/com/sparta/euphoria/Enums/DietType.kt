package com.sparta.euphoria.Enums

enum class DietType(val diet: String,
                    val title: String,
                    val character: String) {
    fruits(
        "Fruits",
        "Fruits",
        "F"
    ),

    vegetables(
        "Vegetables",
        "Vegetables",
        "V"
    ),

    nuts(
        "Nuts-Seeds-Beans",
        "Nuts, Seeds & Beans",
        "N"
    ),

    meat(
        "Meat-Fish",
        "Meat & Fish",
        "M"
    ),

    herbs(
        "Herbs-Spices",
        "Herbs & Spices",
        "H"
    ),

    grains(
        "Grains",
        "Grains",
        "G"
    ),

    dairy(
        "Dairy-Other",
        "Dairy & Other",
        "D"
    );

    companion object {
        private val map = DietType.values().associateBy(DietType::diet)
        fun getDietType(type: String) = map[type]
    }
}