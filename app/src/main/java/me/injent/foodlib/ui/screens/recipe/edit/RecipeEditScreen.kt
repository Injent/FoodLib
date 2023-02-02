package me.injent.foodlib.ui.screens.recipe.edit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.injent.foodlib.R
import me.injent.foodlib.data.local.database.Ingredient
import me.injent.foodlib.ui.components.*
import me.injent.foodlib.ui.screens.recipe.RecipeViewModel
import me.injent.foodlib.ui.theme.FoodLibTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeEditScreen(
    onBack: () -> Unit,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.saveRecipe()
    }

    var dialogOpen by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = dialogOpen && !uiState.isLoading) {
        IngredientSelecterDialog(
            ingredients = uiState.totalIngredients,
            addedIngredients = uiState.ingredients.toImmutableList(),
            onAdd = { viewModel.addIngredient(it) },
            onRemove = { viewModel.removeIngredient(it) },
            onClose = { dialogOpen = false }
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LocalTopAppBar(
                recipeName = uiState.name,
                onClose = { viewModel.saveRecipe(); onBack() },
                onNameChanged = {
                    viewModel.updateName(it)
                },
                onIngredientsMenuClick = {
                    dialogOpen = true
                }
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        AddEditRecipeContent(
            modifier = Modifier
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            isLoading = uiState.isLoading,
            ingredients = uiState.ingredients,
            onIngredientChange = { viewModel.updateIngredient(it) }
        )
    }

    uiState.userMessage?.let { userMessage ->
        val snackbarText = stringResource(userMessage)
        LaunchedEffect(snackbarHostState, viewModel, userMessage, snackbarText) {
            snackbarHostState.showSnackbar(snackbarText)
            viewModel.snackbarMessageShown()
        }
    }
}

@Composable
private fun AddEditRecipeContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    ingredients: List<Ingredient>,
    onIngredientChange: (Ingredient) -> Unit
) {
    if (isLoading) {
        CircularProgressIndicator()
    } else{
        Column(
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .clip(RoundedCornerShape(12.dp))
        ) {
            IngredientsList(
                ingredients = ingredients,
                onIngredientChange = onIngredientChange
            )
        }
    }
}

@Composable
private fun IngredientsList(
    ingredients: List<Ingredient>,
    onIngredientChange: (Ingredient) -> Unit
) {
    var editableIngredient by remember { mutableStateOf<Ingredient?>(null) }
    var inputDialogOpen by remember { mutableStateOf(false) }

    for (ingredient in ingredients) {
        EditableIngredient(
            ingredient = ingredient,
            onClick = {
                editableIngredient = ingredient
                inputDialogOpen = true
            }
        )
    }

    if (inputDialogOpen && editableIngredient != null) {
        val scope = rememberCoroutineScope()

        InputDialog(
            title = stringResource(id = R.string.set_amount),
            initialValue = editableIngredient!!.amount,
            onDismissRequest = { inputDialogOpen = false },
            onDone = {
                inputDialogOpen = false
                onIngredientChange(editableIngredient!!.copy(amount = it))
                scope.launch {
                    delay(1000)
                    editableIngredient = null
                }
            }
        )
    }
}

@Composable
private fun EditableIngredient(
    ingredient: Ingredient,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(FoodLibTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {
            Text(
                text = ingredient.name,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = FoodLibTheme.colorScheme.textPrimary
            )
            Text(
                text = "${ingredient.amount} ${ingredient.metric}",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = FoodLibTheme.colorScheme.textSecondary
            )
        }
        Icon(
            modifier = Modifier.align(Alignment.CenterEnd),
            imageVector = Icons.Rounded.Edit,
            tint = FoodLibTheme.colorScheme.textSecondary,
            contentDescription = null
        )
        FoodLibDivider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 16.dp),
            color = FoodLibTheme.colorScheme.border
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocalTopAppBar(
    recipeName: String,
    onClose: () -> Unit,
    onNameChanged: (String) -> Unit,
    onIngredientsMenuClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = FoodLibTheme.colorScheme.textPrimary,
            actionIconContentColor = FoodLibTheme.colorScheme.textPrimary
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = (-16).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        modifier = Modifier.requiredSize(32.dp),
                        imageVector = Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = null
                    )
                }
                TransparentTextField(
                    modifier = Modifier,
                    value = recipeName,
                    onValueChange = onNameChanged,
                    placeholder = stringResource(id = R.string.name_placeholder)
                )
            }
        },
        actions = {
            IconButton(onClick = onIngredientsMenuClick) {
                Icon(imageVector = Icons.Rounded.ShoppingCart, contentDescription = null)
            }
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun IngredientSelecterDialog(
    ingredients: ImmutableList<Ingredient>,
    addedIngredients: ImmutableList<Ingredient>,
    onAdd: (Ingredient) -> Unit,
    onRemove: (Ingredient) -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
        ) {
            var filterText by remember { mutableStateOf("") }

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = filterText,
                onValueChange = { filterText = it },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = FoodLibTheme.colorScheme.textPrimary,
                    containerColor = FoodLibTheme.colorScheme.container,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    placeholderColor = FoodLibTheme.colorScheme.textSecondary,
                    focusedLeadingIconColor = FoodLibTheme.colorScheme.textSecondary,
                    unfocusedLeadingIconColor = FoodLibTheme.colorScheme.textSecondary,
                    cursorColor = FoodLibTheme.colorScheme.textPrimary
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_hint)
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = FoodLibTheme.colorScheme.textSecondary
                        )
                    }
                },
                singleLine = true
            )

            val items by produceState(initialValue = emptyList(), key1 = filterText) {
                value = if (filterText.isEmpty()) {
                    ingredients
                } else {
                    ingredients.filter { it.name.lowercase().contains(filterText) }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FoodLibTheme.colorScheme.surface)
            ) {
                items(
                    items = items,
                    key = { it.name }
                ) { ingredient ->
                    var added by remember { mutableStateOf(false) }
                    LaunchedEffect(key1 = addedIngredients) {
                        added = addedIngredients.any { it.name == ingredient.name }
                    }

                    IngredientInDialog(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .animateItemPlacement(
                                animationSpec = tween(200)
                            ),
                        ingredientName = ingredient.name,
                        added = added,
                        onAdd = {
                            onAdd(ingredient)
                        },
                        onRemove = {
                            onRemove(ingredient)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun IngredientInDialog(
    modifier: Modifier = Modifier,
    ingredientName: String,
    added: Boolean,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingredientName,
            color = FoodLibTheme.colorScheme.textPrimary
        )

        val buttonColor by animateColorAsState(
            targetValue = if (added) FoodLibTheme.colorScheme.error else FoodLibTheme.colorScheme.success
        )
        val text = if (added) stringResource(id = R.string.remove) else stringResource(id = R.string.add)

        SmallTextButton(
            text = text,
            onClick = if (added) onRemove else onAdd,
            textColor = buttonColor,
            modifier = Modifier
                .width(128.dp)
                .background(
                    buttonColor.copy(.1f),
                    RoundedCornerShape(10.dp)
                )
        )
    }
}