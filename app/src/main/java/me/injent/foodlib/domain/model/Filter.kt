package me.injent.foodlib.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import kotlinx.collections.immutable.persistentListOf
import me.injent.foodlib.R

@Stable
class Filter(
    @StringRes val name: Int,
    enabled: Boolean = false,
    @DrawableRes val icon: Int? = null
) {
    val enabled = mutableStateOf(enabled)
}

val foodTypeFilters = persistentListOf(
    Filter(name = R.string.every_day),
    Filter(name = R.string.cakes),
    Filter(name = R.string.desserts),
    Filter(name = R.string.pies)
)