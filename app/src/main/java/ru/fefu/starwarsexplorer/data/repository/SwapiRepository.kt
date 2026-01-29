package ru.fefu.starwarsexplorer.data.repository

import ru.fefu.starwarsexplorer.data.model.Person
import ru.fefu.starwarsexplorer.data.remote.SwapiApi

class SwapiRepository(private val api: SwapiApi) {
    suspend fun getPeople(search: String? = null): List<Person> {
        return api.getPeople(search).results
    }

    suspend fun getPerson(id: Int): Person {
        return api.getPerson(id)
    }
}