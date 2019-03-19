package com.example.euphoria.Enums

import android.media.Image

enum class DietType(val diet: String,
                    val title: String,
                    val character: String,
                    val characterImage: Image?,
                    val accessoryImage: Image?) {
    fruits(
        "Fruits",
        "Fruits",
        "F",
        null,
        null
    ),

    vegetables(
        "Vegetables",
        "Vegetables",
        "V",
        null,
        null
    ),

    nuts(
        "Nuts-Seeds-Beans",
        "Nuts, Seeds & Beans",
        "N",
        null,
        null
    ),

    meat(
        "Meat-Fish",
        "Meat & Fish",
        "M",
        null,
        null
    ),

    herbs(
        "Herbs-Spices",
        "Herbs & Spices",
        "H",
        null,
        null
    ),

    grains(
        "Grains",
        "Grains",
        "G",
        null,
        null
    ),

    dairy(
        "Dairy-Other",
        "Dairy & Other",
        "D",
        null,
        null
    );

    companion object {
        private val map = DietType.values().associateBy(DietType::diet)
        fun getDietType(type: String) = map[type]
    }
}