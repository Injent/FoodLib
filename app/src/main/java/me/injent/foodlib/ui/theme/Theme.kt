/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.injent.foodlib.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = FoodLibColorScheme(
    primary = PastelRed,
    secondary = DarkGray,
    surface = Color.White,
    background = Background,
    container = Container,
    border = LightGray,
    textPrimary = Black,
    textSecondary = LightGray,
    textLink = Blue,
    textInteractive = Blue,
    iconPrimary = Blue,
    iconSecondary = DarkGray,
    error = Color.Red,
    success = Green
)

@Composable
fun FoodLibTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val sysUiController = rememberSystemUiController()
    SideEffect {
        sysUiController.setSystemBarsColor(
            color = colorScheme.background.copy(alpha = .95f)
        )
    }

    ProvideFoodLibColorScheme(colorScheme) {
        MaterialTheme(
            colorScheme = debugColors(),
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object FoodLibTheme {
    val colorScheme: FoodLibColorScheme
        @Composable
        get() = LocalFoodLibColorScheme.current
}

@Stable
class FoodLibColorScheme(
    primary: Color,
    secondary: Color,
    surface: Color,
    background: Color,
    container: Color,
    border: Color,
    textPrimary: Color,
    textSecondary: Color,
    textLink: Color,
    textInteractive: Color,
    iconPrimary: Color,
    iconSecondary: Color,
    error: Color,
    success: Color,
    isDark: Boolean = false
) {
    var primary by mutableStateOf(primary)
        private set
    var secondary by mutableStateOf(secondary)
        private set
    var surface by mutableStateOf(surface)
        private set
    var background by mutableStateOf(background)
        private set
    var container by mutableStateOf(container)
        private set
    var border by mutableStateOf(border)
        private set
    var textPrimary by mutableStateOf(textPrimary)
        private set
    var textSecondary by mutableStateOf(textSecondary)
        private set
    var textLink by mutableStateOf(textLink)
        private set
    var textInteractive by mutableStateOf(textInteractive)
        private set
    var iconPrimary by mutableStateOf(iconPrimary)
        private set
    var iconSecondary by mutableStateOf(iconSecondary)
        private set
    var error by mutableStateOf(error)
        private set
    var success by mutableStateOf(success)
        private set
    var isDark by mutableStateOf(isDark)
        private set

    fun update(other: FoodLibColorScheme) {
        primary = other.primary
        secondary = other.secondary
        surface = other.surface
        background = other.background
        container = other.container
        border = other.border
        textPrimary = other.textPrimary
        textSecondary = other.textSecondary
        textLink = other.textLink
        textInteractive = other.textInteractive
        iconPrimary = other.iconPrimary
        iconSecondary = other.iconSecondary
        error = other.error
        success = other.success
        isDark = other.isDark
    }

    fun copy() = FoodLibColorScheme(
        primary,
        secondary,
        surface,
        background,
        container,
        border,
        textPrimary,
        textSecondary,
        textLink,
        textInteractive,
        iconPrimary,
        iconSecondary,
        error,
        success,
        isDark
    )
}

@Composable
fun ProvideFoodLibColorScheme(
    colorScheme: FoodLibColorScheme,
    content: @Composable () -> Unit
) {
    val colorPalette = remember {
        colorScheme.copy()
    }
    colorPalette.update(colorScheme)
    CompositionLocalProvider(LocalFoodLibColorScheme provides colorPalette, content = content)
}

private val LocalFoodLibColorScheme = staticCompositionLocalOf<FoodLibColorScheme> {
    error("No FoodLibColorPalette provided")
}

fun debugColors(
    debugColor: Color = Color.Magenta
) = ColorScheme(
    primary = debugColor,
    secondary = debugColor,
    background = debugColor,
    surface = debugColor,
    error = debugColor,
    onPrimary = debugColor,
    onSecondary = debugColor,
    onBackground = debugColor,
    onSurface = debugColor,
    onError = debugColor,
    errorContainer = debugColor,
    primaryContainer = debugColor,
    onPrimaryContainer = debugColor,
    inversePrimary = debugColor,
    secondaryContainer = debugColor,
    onSecondaryContainer = debugColor,
    tertiary = debugColor,
    onTertiary = debugColor,
    tertiaryContainer = debugColor,
    onTertiaryContainer = debugColor,
    surfaceTint = debugColor,
    scrim = debugColor,
    surfaceVariant = debugColor,
    onSurfaceVariant = debugColor,
    inverseOnSurface = debugColor,
    inverseSurface = debugColor,
    onErrorContainer = debugColor,
    outline = debugColor,
    outlineVariant = debugColor
)
