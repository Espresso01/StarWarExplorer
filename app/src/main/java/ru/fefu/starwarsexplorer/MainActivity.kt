package ru.fefu.starwarsexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.fefu.starwarsexplorer.data.remote.RetrofitInstance
import ru.fefu.starwarsexplorer.data.repository.SwapiRepository
import ru.fefu.starwarsexplorer.ui.detail.PersonDetailScreen
import ru.fefu.starwarsexplorer.ui.detail.PersonDetailViewModel
import ru.fefu.starwarsexplorer.ui.favorites.FavoritesScreen
import ru.fefu.starwarsexplorer.ui.favorites.FavoritesViewModel
import ru.fefu.starwarsexplorer.ui.list.PeopleListScreen
import ru.fefu.starwarsexplorer.ui.list.PeopleListViewModel
import ru.fefu.starwarsexplorer.ui.theme.StarWarsExplorerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = SwapiRepository(RetrofitInstance.api)

        setContent {
            StarWarsExplorerTheme {
                AppContent(repository)
            }
        }
    }
}

@Composable
fun AppContent(repository: SwapiRepository) {
    val navController = rememberNavController()
    val peopleListVM: PeopleListViewModel = viewModel(
        factory = PeopleListVMFactory(repository)
    )
    val personDetailVM: PersonDetailViewModel = viewModel(
        factory = PersonDetailVMFactory(repository)
    )
    val favoritesVM: FavoritesViewModel = viewModel()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            PeopleListScreen(
                viewModel = peopleListVM,
                favoritesViewModel = favoritesVM,
                navController = navController
            )
        }
        composable("detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 1
            PersonDetailScreen(
                viewModel = personDetailVM,
                favoritesViewModel = favoritesVM,
                personId = id,
                navController = navController
            )
        }
        composable("favorites") {
            FavoritesScreen(
                viewModel = favoritesVM,
                navController = navController
            )
        }
    }
}

class PeopleListVMFactory(private val repo: SwapiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PeopleListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PeopleListViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class PersonDetailVMFactory(private val repo: SwapiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonDetailViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}