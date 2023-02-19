package me.injent.foodlib.ui.screens.recipe.details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.injent.foodlib.R
import me.injent.foodlib.domain.model.Ingredient
import me.injent.foodlib.domain.model.Recipe
import me.injent.foodlib.ui.components.LargeBodyText
import me.injent.foodlib.ui.components.MediumHeadLineText
import me.injent.foodlib.ui.components.SmallHeadLineText
import me.injent.foodlib.ui.navigation.NavActions
import me.injent.foodlib.ui.theme.FoodLibIcons
import me.injent.foodlib.ui.theme.FoodLibTheme
import me.injent.foodlib.util.startShare

@Composable
fun RecipeDetailsRoute(
    navActions: NavActions,
    viewModel: RecipeDetailsViewModel = hiltViewModel()
) {
    val recipeUiState by viewModel.recipeUiState.collectAsStateWithLifecycle()
    when (recipeUiState) {
        RecipeDetailsUiState.Loading -> Unit
        is RecipeDetailsUiState.Success -> {
            RecipeDetailsScreen(
                recipe = (recipeUiState as RecipeDetailsUiState.Success).recipe,
                onBack = {
                    navActions.navigateToHome()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailsScreen(
    recipe: Recipe,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    MediumHeadLineText(
                        text = recipe.name,
                        fontWeight = FontWeight.W200
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = FoodLibTheme.colorScheme.textPrimary,
                    actionIconContentColor = FoodLibTheme.colorScheme.primary,
                    navigationIconContentColor = FoodLibTheme.colorScheme.primary
                ),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            imageVector = FoodLibIcons.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    val context = LocalContext.current
                    IconButton(onClick = { startShare(context, recipe) }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = FoodLibIcons.Share,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IngredientsList(ingredients = recipe.ingredients)
            RecipeContent(text = recipe.content)
        }
    }
}

@Composable
private fun IngredientsList(ingredients: List<Ingredient>) {
    SmallHeadLineText(text = stringResource(id = R.string.ingredients))
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for ((name, amount, metric) in ingredients) {
            IngredientItem(ingredient = name, metric = "$amount $metric")
        }
    }
}

@Composable
private fun IngredientItem(
    ingredient: String,
    metric: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = FoodLibTheme.colorScheme.container,
        contentColor = FoodLibTheme.colorScheme.textPrimary
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LargeBodyText(
                modifier = Modifier
                    .padding(16.dp),
                text = ingredient
            )
            LargeBodyText(
                modifier = Modifier
                    .padding(16.dp),
                text = metric,
                color = FoodLibTheme.colorScheme.textPrimary.copy(.75f)
            )
        }
    }
}

@Composable
private fun RecipeContent(
    text: String
) {
    SmallHeadLineText(text = stringResource(id = R.string.recipe_content))
    LargeBodyText(
        text = text,
        fontWeight = FontWeight.W500
    )
}