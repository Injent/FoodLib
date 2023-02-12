package me.injent.foodlib.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import me.injent.foodlib.R
import me.injent.foodlib.ui.theme.FoodLibTheme
import me.injent.foodlib.util.clearFocusOnKeyboardDismiss

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun FoodLibSearchBar(
    modifier: Modifier = Modifier,
    color: Color,
    shape: Shape = RoundedCornerShape(12.dp),
    contentColor: Color,
    placeholder: String,
    hintColor: Color,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    onValueChanged: (value: String) -> Unit,
    focusedOnInit: Boolean = false,
    onDismiss: (() -> Unit)? = null
) {
    var focused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var text by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        if (focusedOnInit)
            focusRequester.requestFocus()
    }

    BasicTextField(
        modifier = modifier
            .onFocusChanged { focusState -> focused = (focusState.isFocused) }
            .focusRequester(focusRequester)
            .clip(shape)
            .background(color)
            .clearFocusOnKeyboardDismiss(onDismissKeyboard = { onDismiss?.invoke() }),
        value = text,
        onValueChange = {
            text = it
            onValueChanged(it)
        },
        cursorBrush = SolidColor(contentColor),
        textStyle = textStyle,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            focusManager.clearFocus(true)
        })
    ) { innerTextField ->
        TextFieldDefaults.TextFieldDecorationBox(
            value = text,
            placeholder = {
                if (!focused) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            tint = hintColor,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        LargeBodyText(
                            text = placeholder,
                            color = hintColor
                        )
                    }
                } else {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = stringResource(id = R.string.start_typing),
                        color = hintColor
                    )
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = contentColor,
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = contentColor,
                selectionColors = TextSelectionColors(
                    handleColor = FoodLibTheme.colorScheme.primary,
                    backgroundColor = FoodLibTheme.colorScheme.primary.copy(.25f)
                ),
                focusedTrailingIconColor = hintColor,
                unfocusedTrailingIconColor = hintColor
            ),
            innerTextField = {
                Row(
                    modifier = modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) { innerTextField() }
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = text.isNotEmpty() && focused,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        modifier = Modifier.sizeIn(24.dp),
                        onClick = {
                            onValueChanged("")
                            text = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null
                        )
                    }
                }
            },
            enabled = true,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            contentPadding = PaddingValues(0.dp)
        )
    }
}