package ru.fefu.starwarsexplorer.ui.list

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.foundation.shape.CircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleListScreen(
    viewModel: PeopleListViewModel,
    favoritesViewModel: FavoritesViewModel,
    navController: NavController
) {
    var search by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadPeople() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Star Wars Explorer") },
                actions = {
                    IconButton(
                        onClick = { navController.navigate("favorites") },
                        modifier = Modifier
                            .size(48.dp)
                            .border(2.dp, Color.Red, CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = search,
                onValueChange = {
                    search = it
                    viewModel.loadPeople(search)
                },
                label = { Text("Поиск") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = Color.Red,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = Color.Red,
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = viewModel.uiState) {
                is PeopleUiState.Loading -> {
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
                is PeopleUiState.Error -> Box(
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
                            onClick = { viewModel.loadPeople(search) },
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
                is PeopleUiState.Success -> {
                    if (state.people.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Персонажи не найдены",
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn {
                            items(state.people) { person ->
                                val isFavorite by remember {
                                    derivedStateOf {
                                        favoritesViewModel.isFavorite(
                                            person.url.split("/").filter { it.isNotEmpty() }.last().toInt()
                                        )
                                    }
                                }
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {val id = person.url.split("/")
                                            .filter { it.isNotEmpty() }.last()
                                            navController.navigate("detail/$id")}
                                        .border(2.dp, Color.Red, RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = person.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.weight(1f)
                                        )

                                        IconButton(
                                            onClick = {
                                                val id = person.url.split("/")
                                                    .filter { it.isNotEmpty() }.last().toInt()
                                                if (isFavorite) {
                                                    favoritesViewModel.removeFavorite(id)
                                                } else {
                                                    favoritesViewModel.addFavorite(id)
                                                }
                                            },
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isFavorite) Icons.Default.Favorite
                                                else Icons.Default.FavoriteBorder,
                                                contentDescription = if (isFavorite)
                                                    "Remove from favorites"
                                                else "Add to favorites",
                                                tint = if (isFavorite) Color.Red
                                                else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}