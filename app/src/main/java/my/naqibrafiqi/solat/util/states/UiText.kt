package my.naqibrafiqi.solat.util.states

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import my.naqibrafiqi.solat.R

/**
 * Text that ViewModels hand to the UI. A [Resource] is looked up when it's shown, in the app's
 * current language, so ViewModels never build user-facing English strings.
 */
sealed interface UiText {

    /** Text that isn't translated, such as a zone code or an exception's message. */
    data class Plain(val value: String) : UiText

    /** A string resource. Arguments may be [UiText] too; they're resolved first. */
    data class Resource(@param:StringRes val id: Int, val args: List<Any> = emptyList()) : UiText {
        constructor(@StringRes id: Int, vararg args: Any) : this(id, args.toList())
    }

    fun asString(context: Context): String =
        when (this) {
            is Plain -> value
            is Resource -> context.getString(
                id,
                *args.map { if (it is UiText) it.asString(context) else it }.toTypedArray()
            )
        }
}

@Composable
fun UiText.asString(): String = asString(LocalContext.current)

/** "Unexpected error: …" with the exception's own message (which isn't translated). */
fun unexpectedError(e: Throwable): UiText =
    UiText.Resource(
        R.string.common_error_unexpected,
        UiText.Plain(e.message ?: e.javaClass.simpleName)
    )
