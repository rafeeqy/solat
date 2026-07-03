package my.naqibrafiqi.solat.util.states

sealed interface UiEvent<out T> {

    data class Loading(
        val progress: Double? = null
    ) : UiEvent<Nothing>

    data class Error(
        val errorMessage: String
    ) : UiEvent<Nothing>

    data class Success<T>(
        val data: T,
        val message: String
    ) : UiEvent<T>
}
