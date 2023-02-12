package me.injent.foodlib.domain.model

import androidx.annotation.StringRes
import kotlinx.serialization.Serializable
import me.injent.foodlib.R

@Serializable
enum class Category(@StringRes val displayName: Int) {
    ALL(R.string.category_all),
    CAKES(R.string.cakes),
    COOKIES(R.string.cookies),
    FOOD(R.string.category_food);
}