package my.naqibrafiqi.solat.util.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SnackbarManager(
    val snackbarState: SnackbarHostState,
    val coroutineScope: CoroutineScope
) {

    fun showSnackbar(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Indefinite
    ) {
        coroutineScope.launch {
            snackbarState.currentSnackbarData?.dismiss()

            val snackbarJob = launch {
                snackbarState.showSnackbar(
                    message = message,
                    withDismissAction = true,
                    duration = duration
                )
            }

            delay(1500.milliseconds)

            snackbarJob.cancel()
        }
    }

    fun showActionSnackbar(
        message: String,
        actionLabel: String,
        onClick: () -> Unit,
        duration: SnackbarDuration = SnackbarDuration.Indefinite
    ) {
        coroutineScope.launch {
            val result = snackbarState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                withDismissAction = true,
                duration = duration
            )

            when (result) {
                SnackbarResult.Dismissed -> {
                    // NOTE: Do nothing...
                }

                SnackbarResult.ActionPerformed -> {
                    onClick()
                }
            }
        }
    }
}