package me.injent.foodlib.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import me.injent.foodlib.R
import me.injent.foodlib.ui.theme.FoodLibIcons
import me.injent.foodlib.ui.theme.FoodLibTheme

@Composable
fun InputDialog(
    title: String,
    metrics: List<String>,
    initialValue: Int = 0,
    initialMetric: String,
    onDismissRequest: () -> Unit,
    onDone: (value: Int, metric: String) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            var value by remember { mutableStateOf(if (initialValue == 0) "" else initialValue.toString()) }

            InputField(
                onDone = onDone,
                title = title,
                value = value,
                metric = initialMetric,
                metrics = metrics,
            )
            LazyVerticalGrid(
                modifier = Modifier.wrapContentSize(),
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                numPad(
                    modifier = Modifier
                        .layout { measurable, constraints ->
                            val tileSize = constraints.maxWidth

                            val placeable = measurable.measure(
                                constraints.copy(
                                    minWidth = tileSize,
                                    maxWidth = tileSize,
                                    minHeight = tileSize,
                                    maxHeight = tileSize,
                                )
                            )

                            layout(placeable.width, placeable.width) {
                                placeable.place(x = 0, y = 0, zIndex = 0f)
                            }
                        },
                    onClick = {
                        value += it
                    },
                    onClear = {
                        value = value.dropLast(1)
                    },
                    onCancel = onDismissRequest
                )
            }
        }
    }
}

@Composable
private fun InputField(
    onDone: (value: Int, metric: String) -> Unit,
    title: String,
    value: String,
    metric: String,
    metrics: List<String>
) {
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

            var selectedMetric by remember {
                val defaultMetric = metric.ifEmpty { metrics[0] }
                mutableStateOf(defaultMetric)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .background(
                            color = FoodLibTheme.colorScheme.container,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .weight(1f),
                    text = value.ifEmpty { "0" },
                    style = TextStyle(
                        color = FoodLibTheme.colorScheme.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
                MetricSelecter(
                    modifier = Modifier.wrapContentSize(),
                    metrics = metrics,
                    onMetricSelect = { selectedMetric = it },
                    selectedMetric = selectedMetric
                )
            }
            FoodLibButton(
                modifier = Modifier,
                onClick = { onDone(value.ifEmpty { "0" }.toInt(), selectedMetric) },
                text = "OK",
                color = Color.Transparent
            )
        }
    }
}

@Composable
fun MetricSelecter(
    modifier: Modifier = Modifier,
    metrics: List<String>,
    selectedMetric: String,
    onMetricSelect: (metric: String) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        repeat(metrics.size) { index ->
            val selected = metrics[index] == selectedMetric
            val textColor by animateColorAsState(
                targetValue = if (selected) FoodLibTheme.colorScheme.surface else FoodLibTheme.colorScheme.primary
            )
            val containerColor by animateColorAsState(
                targetValue = if (selected) FoodLibTheme.colorScheme.primary else Color.Transparent
            )

            SmallHeaderText(
                modifier = Modifier
                    .selectable(
                        selected = selected,
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = { onMetricSelect(metrics[index]) }
                    )
                    .background(containerColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = metrics[index],
                color = textColor
            )
        }
    }
}

fun LazyGridScope.numPad(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onCancel: () -> Unit,
    onClear: () -> Unit
) {
    items(
        count = 9,
        key = { it }
    ) { index ->
        val num: String = remember { (index + 1).toString() }
        Num(
            modifier = modifier,
            onClick = onClick,
            value = num
        )
    }
    item {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .clickable(onClick = onCancel)
                .background(color = FoodLibTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .sizeIn(minWidth = 32.dp, minHeight = 32.dp),
                imageVector = FoodLibIcons.Cancel,
                tint = FoodLibTheme.colorScheme.primary,
                contentDescription = null
            )
        }
    }
    item {
        Num(
            modifier = modifier,
            onClick = onClick,
            value = "0"
        )
    }
    item {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .clickable(onClick = onClear)
                .background(color = FoodLibTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .sizeIn(minWidth = 32.dp, minHeight = 32.dp),
                painter = painterResource(id = FoodLibIcons.BackSpace),
                tint = FoodLibTheme.colorScheme.primary,
                contentDescription = null
            )
        }
    }
}

@Composable
fun Num(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    value: String
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color = FoodLibTheme.colorScheme.surface)
            .clickable { onClick(value) }
            .padding(16.dp)
    ) {
        HeaderText(
            modifier = Modifier.align(Alignment.Center),
            text = value,
            color = FoodLibTheme.colorScheme.primary
        )
    }
}

@Composable
fun ConfirmDialog(
    alert: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = FoodLibTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            MediumBodyText(
                modifier = Modifier.fillMaxWidth(),
                text = alert,
                maxLines = 10,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClickableText(onClick = onCancel) {
                    MediumBodyText(text = stringResource(id = R.string.cancel))
                }
                FoodLibButton(
                    color = FoodLibTheme.colorScheme.primary,
                    contentColor = FoodLibTheme.colorScheme.surface,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    onClick = onConfirm,
                    text = "OK"
                )
            }
        }
    }
}