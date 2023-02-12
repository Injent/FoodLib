package me.injent.foodlib.ui.screens.recipe.edit

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.injent.foodlib.R
import me.injent.foodlib.data.ResultOf
import me.injent.foodlib.domain.model.Ingredient
import me.injent.foodlib.ui.components.*
import me.injent.foodlib.ui.theme.FoodLibIcons
import me.injent.foodlib.ui.theme.FoodLibTheme
import me.injent.foodlib.util.*

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeEditScreen(
    onBack: () -> Unit,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var confirmExitDialogOpen by remember { mutableStateOf(false) }

    val onBackLogic = remember {
        {
            confirmExitDialogOpen = false
            if (viewModel.isLoadedFromDatabase) {
                val saved = viewModel.saveRecipe()
                if (saved) onBack()
            } else {
                viewModel.saveDraft()
            }
        }
    }
    BackHandler { onBackLogic() }

//    TODO("Error: Duplicate saving draft on stop lifecycle")
//    OnLifecycleEvent { _, event ->
//        when (event) {
//            Lifecycle.Event.ON_STOP -> {
//                onBackLogic()
//            }
//            else -> Unit
//        }
//    }

    if (confirmExitDialogOpen && !uiState.isRecipeSaved) {
        ConfirmDialog(
            alert = stringResource(id = R.string.dialog_confirm_exit_text),
            onConfirm = onBackLogic,
            onCancel = { confirmExitDialogOpen = false }
        )
    }

    var ingredientsDialogOpen by remember { mutableStateOf(false) }
    AnimatedVisibility(visible = ingredientsDialogOpen && !uiState.isLoading) {
        val searchText by viewModel.searchText.collectAsStateWithLifecycle()
        val ingredientsFullList by viewModel.filteredIngredients.collectAsStateWithLifecycle()

        IngredientSelecterDialog(
            searchText = searchText,
            ingredients = ingredientsFullList,
            addedIngredients = uiState.ingredients.toImmutableList(),
            onAdd = { viewModel.addIngredient(it) },
            onRemove = { viewModel.removeIngredient(it) },
            onClose = { ingredientsDialogOpen = false; viewModel.onSearchTextChange("") },
            onSearchTextChange = {
                viewModel.onSearchTextChange(it)
            }
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LocalTopAppBar(
                recipeName = uiState.name,
                onClose = onBackLogic,
                onNameChanged = { viewModel.updateName(it) },
                onDeleteRequest = {
                    onBack()
                    viewModel.deleteDraftOrRecipe()
                },
                onSave = {
                    val saved = viewModel.saveRecipe()
                    if (saved)
                        onBack()
                },
                isSaved = uiState.isRecipeSaved
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        AddEditRecipeContent(
            modifier = Modifier
                .padding(paddingValues),
            isLoading = uiState.isLoading,
            ingredients = uiState.ingredients,
            onIngredientChange = { viewModel.updateIngredient(it) },
            onContentChange = { viewModel.updateContent(it) },
            onIngredientDelete = { viewModel.removeIngredient(it.name) },
            onAddIngredient = { ingredientsDialogOpen = true },
            content = uiState.content
        )
    }

    // TODO("Create custom snackbar")
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
    onIngredientChange: (Ingredient) -> Unit,
    onIngredientDelete: (Ingredient) -> Unit,
    onContentChange: (String) -> Unit,
    onAddIngredient: () -> Unit,
    content: String
) {
    if (isLoading) {
        CircularProgressIndicator()
    } else{
        Column(
            modifier
                .fillMaxSize()
        ) {
            var textFieldFullSize by remember { mutableStateOf(false) }

            AnimatedVisibility(visible = !textFieldFullSize) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Ingredients(
                        ingredients = ingredients,
                        onIngredientChange = onIngredientChange,
                        onDelete = onIngredientDelete,
                        onAddIngredient = onAddIngredient
                    )
                }
            }

            TransparentTextField(
                modifier = Modifier
                    .weight(.8f)
                    .fillMaxSize()
                    .background(FoodLibTheme.colorScheme.container),
                value = content,
                onValueChange = { onContentChange(it) },
                textStyle = MaterialTheme.typography.bodyLarge,
                onFocusChange = { textFieldFullSize = it.isFocused }
            )
            Spacer(modifier = Modifier.conditional(!textFieldFullSize) { weight(.2f) })
        }
    }
}

@Composable
private fun Ingredients(
    ingredients: List<Ingredient>,
    onIngredientChange: (Ingredient) -> Unit,
    onAddIngredient: () -> Unit,
    onDelete: (Ingredient) -> Unit
) {
    var editableIngredient by remember { mutableStateOf<Ingredient?>(null) }
    var inputDialogOpen by remember { mutableStateOf(false) }

    for (ingredient in ingredients) {
        val isLast = remember(ingredients) { ingredients.last() == ingredient }
        EditableIngredient(
            ingredient = ingredient,
            onEdit = {
                editableIngredient = ingredient
                inputDialogOpen = true
            },
            isLast = isLast,
            onDelete = { onDelete(ingredient) }
        )
    }
    FoodLibIconButton(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onAddIngredient,
        imageVector = FoodLibIcons.AddCircle,
        shape = RoundedCornerShape(12.dp),
        text = stringResource(id = R.string.add_ingredient)
    )

    if (inputDialogOpen && editableIngredient != null) {
        val scope = rememberCoroutineScope()

        InputDialog(
            title = stringResource(id = R.string.set_amount),
            initialValue = editableIngredient!!.amount,
            metrics = editableIngredient!!.avalaibleMetrics,
            onDismissRequest = { inputDialogOpen = false },
            onDone = { value, metric ->
                inputDialogOpen = false
                onIngredientChange(editableIngredient!!.copy(amount = value, metric = metric))
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
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = FoodLibIcons.Menu,
                tint = FoodLibTheme.colorScheme.primary,
                contentDescription = null
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LargeBodyText(text = ingredient.name)
            LargeBodyText(text = "${ingredient.amount} ${ingredient.metric}")
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = FoodLibIcons.Delete,
                tint = FoodLibTheme.colorScheme.primary,
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocalTopAppBar(
    recipeName: String,
    onClose: () -> Unit,
    onNameChanged: (String) -> Unit,
    onDeleteRequest: () -> Unit,
    onSave: () -> Unit,
    isSaved: Boolean
) {
    TopAppBar(
        modifier = Modifier.foodLibShadow(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FoodLibTheme.colorScheme.surface,
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
                    placeholder = stringResource(id = R.string.name_placeholder),
                    maxLines = 1
                )
            }
        },
        actions = {
            var expanded by remember { mutableStateOf(false) }
            AnimatedVisibility(
                visible = !isSaved,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = onSave) {
                    Icon(
                        painter = painterResource(id = FoodLibIcons.Save),
                        tint = FoodLibTheme.colorScheme.textPrimary,
                        contentDescription = null
                    )
                }
            }
            IconButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    tint = FoodLibTheme.colorScheme.textPrimary,
                    contentDescription = null
                )
            }
            AnimatedVisibility(
                visible = expanded,
            ) {
                DropdownMenu(
                    modifier = Modifier
                        .background(FoodLibTheme.colorScheme.surface)
                        .clip(RoundedCornerShape(12.dp)),
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        text = {
                            MediumBodyText(
                                text = stringResource(id = R.string.delete)
                            )
                        },
                        onClick = onDeleteRequest
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun IngredientSelecterDialog(
    ingredients: List<Ingredient>,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    addedIngredients: ImmutableList<Ingredient>,
    onAdd: (Ingredient) -> Unit,
    onRemove: (name: String) -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = searchText,
                onValueChange = { onSearchTextChange(it) },
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FoodLibTheme.colorScheme.surface)
            ) {
                items(
                    items = ingredients,
                    key = { it.name }
                ) { ingredient ->
                    var added by remember { mutableStateOf<ResultOf<Boolean>>(ResultOf.Loading) }

                    LaunchedEffect(addedIngredients) {
                        added = ResultOf.Success(addedIngredients.any { it.name == ingredient.name })
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
                            onRemove(ingredient.name)
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
    added: ResultOf<Boolean>,
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

        if (added is ResultOf.Success) {
            val buttonColor by animateColorAsState(
                targetValue = if (added.data) FoodLibTheme.colorScheme.error else FoodLibTheme.colorScheme.success
            )
            val text = if (added.data) stringResource(id = R.string.remove) else stringResource(id = R.string.add)

            FoodLibTextButton(
                text = text,
                onClick = if (added.data) onRemove else onAdd,
                textColor = buttonColor,
                modifier = Modifier
                    .width(128.dp)
                    .background(
                        buttonColor.copy(.1f),
                        RoundedCornerShape(10.dp)
                    )
            )
        } else if (added is ResultOf.Loading) {
            FoodLibTextButton(
                text = "",
                onClick = {},
                modifier = Modifier
                    .width(128.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shimmerEffect()
            )
        }
    }
}