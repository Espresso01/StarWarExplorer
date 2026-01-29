package ru.fefu.starwarsexplorer.ui.list

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleListScreen(
    viewModel: PeopleListViewModel,
    favoritesViewModel: FavoritesViewModel,
    navController: NavController
) {
    var search by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadPeople()
    }

    LaunchedEffect(search) {
        delay(400)
        viewModel.loadPeople(search.takeIf { it.isNotBlank() })
    }

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
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = Color.Red
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            TextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Поиск") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = viewModel.uiState) {
                is PeopleUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator(color = Color.Red)
                    }
                }

                is PeopleUiState.Error -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
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
                                onClick = { viewModel.loadPeople(search.takeIf { it.isNotBlank() }) },
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

                is PeopleUiState.Success -> {
                    if (state.people.isEmpty()) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text("Персонажи не найдены")
                        }
                    } else {
                        LazyColumn {
                            items(state.people) { person ->
                                val isFavorite = favoritesViewModel.isFavorite(person.id)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            navController.navigate("detail/${person.id}")
                                        }
                                        .border(2.dp, Color.Red, RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = person.name,
                                            modifier = Modifier.weight(1f)
                                        )

                                        IconButton(
                                            onClick = {
                                                favoritesViewModel.toggleFavorite(person.id)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isFavorite)
                                                    Icons.Default.Favorite
                                                else
                                                    Icons.Default.FavoriteBorder,
                                                contentDescription = null,
                                                tint = if (isFavorite) Color.Red else Color.Gray
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