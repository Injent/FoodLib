package me.injent.foodlib.util

interface Searchable {
    fun doesMatchQuery(query: String): Boolean
}