package ru.fefu.starwarsexplorer.data.remote

import ru.fefu.starwarsexplorer.data.model.Person
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class PeopleResponse(
    val count: Int,
    val results: List<Person>
)

interface SwapiApi {
    @GET("people/")
    suspend fun getPeople(@Query("search") search: String? = null): PeopleResponse

    @GET("people/{id}/")
    suspend fun getPerson(@Path("id") id: Int): Person
}