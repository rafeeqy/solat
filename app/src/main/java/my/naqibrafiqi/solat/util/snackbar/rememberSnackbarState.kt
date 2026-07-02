package my.naqibrafiqi.solat.util.snackbar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberSnackbarState(
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(snackbarState, coroutineScope) {
    SnackbarManager(
        snackbarState = snackbarState,
        coroutineScope = coroutineScope
    )
}
