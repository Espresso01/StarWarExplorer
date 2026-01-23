package ru.fefu.starwarsexplorer.ui.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.fefu.starwarsexplorer.ui.favorites.FavoritesViewModel
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    viewModel: PersonDetailViewModel,
    favoritesViewModel: FavoritesViewModel,
    personId: Int,
    navController: NavController
) {
    LaunchedEffect(personId) { viewModel.loadPerson(personId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали персонажа") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.Red
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    navigationIconContentColor = Color.Red
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = viewModel.uiState) {
                is PersonDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.Red,
                            strokeWidth = 4.dp
                        )
                    }
                }
                is PersonDetailUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ошибка: ${state.message}",
                                color = Color.Red,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            FloatingActionButton(
                                onClick = { viewModel.loadPerson(personId) },
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Retry"
                                )
                            }
                        }
                    }
                }
                is PersonDetailUiState.Success -> {
                    val p = state.person
                    val isFavorite by remember {
                        derivedStateOf {
                            favoritesViewModel.isFavorite(personId)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .border(3.dp, Color.Red, RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    onClick = {
                                        favoritesViewModel.toggleFavorite(personId)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Default.Favorite
                                        else Icons.Default.FavoriteBorder,
                                        contentDescription = if (isFavorite)
                                            "Remove from favorites"
                                        else "Add to favorites",
                                        tint = if (isFavorite) Color.Red
                                        else Color.Gray,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = p.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.Red,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                Divider(
                                    color = Color.Red.copy(alpha = 0.3f),
                                    thickness = 1.dp,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                )

                                DetailRow("Рост:", p.height)
                                DetailRow("Вес:", p.mass)
                                DetailRow("Цвет волос:", p.hair_color)
                                DetailRow("Цвет кожи:", p.skin_color)
                                DetailRow("Цвет глаз:", p.eye_color)
                                DetailRow("Год рождения:", p.birth_year)
                                DetailRow("Пол:", p.gender)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}