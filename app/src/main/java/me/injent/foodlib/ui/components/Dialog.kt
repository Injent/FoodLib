package me.injent.foodlib.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import me.injent.foodlib.ui.theme.FoodLibTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputDialog(
    title: String,
    initialValue: Float? = null,
    onDismissRequest: () -> Unit,
    onDone: (Float) -> Unit
) {
    var value by remember { mutableStateOf(initialValue?.toString() ?: "") }

    Dialog(onDismissRequest = onDismissRequest) {
        FoodLibSurface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HeaderText(text = title)

                val focusRequester = remember { FocusRequester() }
                //LaunchedEffect(Unit) { focusRequester.requestFocus() }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { value = (value.toFloat() + 1f).toString() }
                            .weight(1f)
                    )
                    TextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .weight(2f),
                        value = value,
                        onValueChange = {
                            if (it.toFloatOrNull() != null)
                                value = it
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = FoodLibTheme.colorScheme.container,
                            textColor = FoodLibTheme.colorScheme.textPrimary,
                            cursorColor = FoodLibTheme.colorScheme.textPrimary,
                            focusedIndicatorColor = FoodLibTheme.colorScheme.primary,
                            unfocusedIndicatorColor = FoodLibTheme.colorScheme.secondary,
                            selectionColors = TextSelectionColors(
                                handleColor = FoodLibTheme.colorScheme.primary,
                                backgroundColor = FoodLibTheme.colorScheme.primary.copy(.1f)
                            )
                        ),
                        singleLine = true,
                        readOnly = true,
                        textStyle = TextStyle(
                            color = FoodLibTheme.colorScheme.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardActions = KeyboardActions(
                            onDone = { onDone(value.toFloat()) }
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Decimal
                        )
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                if ((value.toFloatOrNull() ?: 0f) != 0f)
                                    value = (value.toFloat() - 1f).toString()
                            }
                            .weight(1f)
                    )
                }
                FoodLibButton(
                    modifier = Modifier,
                    onClick = { onDone(value.toFloat()) },
                    text = "OK",
                    color = Color.Transparent
                )
            }
        }
    }
}