package me.injent.foodlib.util

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import me.injent.foodlib.domain.model.Recipe
import java.time.format.DateTimeFormatter

fun Instant.format(patter: String): String {
    val formatter = DateTimeFormatter.ofPattern(patter)
    val date = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return formatter.format(date.toJavaLocalDateTime())
}

fun startShare(context: Context, recipe: Recipe) {
    val sb = StringBuilder(recipe.name + "\n" + "Ингредиенты: \n")
    for ((name, amount, metric) in recipe.ingredients) {
        sb.append("$name - $amount $metric\n")
    }
    sb.append("Приготовление:\n${recipe.content}")

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(context, shareIntent, null)
}