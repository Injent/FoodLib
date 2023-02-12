package me.injent.foodlib.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import me.injent.foodlib.ui.theme.FoodLibTheme
import me.injent.foodlib.util.clearFocusOnKeyboardDismiss
import me.injent.foodlib.util.conditional

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TransparentTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(
        color = FoodLibTheme.colorScheme.textPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    placeholder: String? = null,
    onDone: (() -> Unit)? = null,
    onFocusChange: ((FocusState) -> Unit)? = null,
    onDissmissKeyboard: (() -> Unit)? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        modifier = modifier
            .clearFocusOnKeyboardDismiss(onDismissKeyboard = { onDissmissKeyboard?.invoke() })
            .conditional(onFocusChange != null) {
                onFocusChanged(onFocusChange!!)
            },
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            textColor = FoodLibTheme.colorScheme.textPrimary,
            cursorColor = FoodLibTheme.colorScheme.textPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            selectionColors = TextSelectionColors(
                handleColor = FoodLibTheme.colorScheme.primary,
                backgroundColor = FoodLibTheme.colorScheme.primary.copy(.1f)
            )
        ),
        maxLines = maxLines,
        textStyle = textStyle,
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus(true)
                onDone?.invoke()
            }
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        placeholder = if (placeholder != null) {
            { Text(text = placeholder, style = textStyle, color = FoodLibTheme.colorScheme.textSecondary) }
        } else null
    )
}