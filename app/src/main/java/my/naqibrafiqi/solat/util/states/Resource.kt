package my.naqibrafiqi.solat.util.states

sealed interface Resource<out T> {

    object Loading : Resource<Nothing>

    data class Error(
        val exception: Exception
    ) : Resource<Nothing>

    data class Success<T>(
        val data: T
    ) : Resource<T>
}