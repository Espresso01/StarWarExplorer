package ru.fefu.starwarsexplorer.data.model

data class Person(
    val name: String,
    val height: String,
    val mass: String,
    val hair_color: String,
    val skin_color: String,
    val eye_color: String,
    val birth_year: String,
    val gender: String,
    val url: String
) {
    val id: Int
        get() = url.split("/").filter { it.isNotBlank() }.last().toIntOrNull() ?: 0
}