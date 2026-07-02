package my.naqibrafiqi.solat.ui.feature.prayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import my.naqibrafiqi.solat.ui.component.EmptyContent
import my.naqibrafiqi.solat.ui.component.ErrorContent
import my.naqibrafiqi.solat.ui.component.LoadingContent
import my.naqibrafiqi.solat.util.SolatDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToZoneLocation: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PrayerTimeViewModel = hiltViewModel()
) {
    val zone = viewModel.zoneEntity.collectAsStateWithLifecycle().value
    val waktuSolat = viewModel.waktuSolat.collectAsStateWithLifecycle().value
    val upcomingPrayer = viewModel.upcomingPrayer.collectAsStateWithLifecycle().value
    val countdown = viewModel.countdown.collectAsStateWithLifecycle().value
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(upcomingPrayer) {
        if (waktuSolat.isNotEmpty()) {
            viewModel.calculateTimeLeft()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { /* NOTE: Leave this empty... */ },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is PrayerTimeUiState.Loading -> {
                LoadingContent()
            }

            is PrayerTimeUiState.Error -> {
                ErrorContent(message = uiState.message)
            }

            is PrayerTimeUiState.Success -> {
                if (waktuSolat.isEmpty()) {
                    EmptyContent(
                        message = "No prayer time to display.",
                        onRefresh = { viewModel.refresh() }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = innerPadding.calculateTopPadding())
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(32.dp)
                            ) {
                                // Date and Location buttons
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    DateButton()
                                    ZoneLocationButton(
                                        stateZone = zone?.let {
                                            "${it.state} (${it.code})"
                                        } ?: "Unknown zone",
                                        district = zone?.district ?: "Unknown districts",
                                        onClick = onNavigateToZoneLocation,
                                        modifier = Modifier.fillMaxWidth(3 / 4f)
                                    )
                                }
                                // Next prayer
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    NextPrayer(prayer = upcomingPrayer)
                                }
                                // Prayer countdown
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    NextPrayerCountdown(timeLeft = countdown)
                                }
                            }
                            // Bottom content
                            Column(modifier = Modifier.fillMaxWidth()) {
                                PrayerTimeCard(
                                    times = waktuSolat,
                                    currentPrayer = upcomingPrayer?.time ?: 0L,
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 56.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateButton(
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = { /* NOTE: Do nothing for now... */ },
        modifier = modifier,
        enabled = false,
        colors = ButtonDefaults.textButtonColors().copy(
            disabledContentColor = ButtonDefaults.textButtonColors().contentColor
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = SolatDateTime.formatDate(
                    date = SolatDateTime.nowEpochMilli(),
                    format = "EEEE, d MMMM uuuu"
                ),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = SolatDateTime.formatHijrahDate(
                    date = SolatDateTime.nowEpochMilli(),
                    format = "d MMMM uuuu"
                ),
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ZoneLocationButton(
    stateZone: String,
    district: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Location on",
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stateZone,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Text(
                text = district,
                fontWeight = FontWeight.Normal,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun NextPrayer(
    prayer: WaktuSolat?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = prayer?.name ?: "Unknown",
            fontSize = 80.sp,
            fontWeight = FontWeight.Light,
        )
        Text(
            text = if (prayer != null) {
                SolatDateTime.formatDate(
                    date = prayer.time,
                    format = "h:mm a"
                ).lowercase()
            } else {
                "Unknown"
            },
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraLight,
        )
    }
}

@Composable
private fun NextPrayerCountdown(
    timeLeft: Long,
    modifier: Modifier = Modifier
) {
    val tempData = mapOf(
        "Hours" to (timeLeft / (1000 * 60 * 60)).toInt().toString(),
        "Minutes" to (timeLeft / (1000 * 60) % 60).toInt().toString(),
        "Seconds" to (timeLeft / 1000 % 60).toInt().toString()
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tempData.entries.forEachIndexed { index, entry ->
            Surface(
                modifier = Modifier.widthIn(max = 80.dp),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = entry.value.padStart(2, '0'),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text(
                        text = entry.key,
                        fontWeight = FontWeight.Light,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            if (index < tempData.size - 1) {
                Text(
                    text = ":",
                    modifier = Modifier.padding(top = 16.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
    }
}

@Composable
private fun PrayerTimeCard(
    times: List<WaktuSolat>,
    currentPrayer: Long,
    modifier: Modifier = Modifier
) {
    val isTodayIshaPassed = System.currentTimeMillis() > times[4].time

    val currDate =
        if (isTodayIshaPassed) SolatDateTime.tomorrowEpochMilli() else SolatDateTime.nowEpochMilli()

    val splitDate = SolatDateTime.formatDate(
        date = currDate,
        format = "uuuu-MM-dd"
    ).split("-").map { it.toInt() }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Row {
                    Text(
                        text = "Prayer times",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = SolatDateTime.formatDate(
                        date = currDate,
                        format = "EEEE, dd/MM/uuuu"
                    ),
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                times.filter { it.day == splitDate[2] }
                    .forEach { (_, _, _, name, time) ->
                        Column(
                            modifier = Modifier.padding(
                                vertical = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = name,
                                    fontWeight = FontWeight.Normal,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = SolatDateTime.formatDate(
                                        date = time,
                                        format = "h:mm a"
                                    ).lowercase(),
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                            if (time == currentPrayer) {
                                Box(
                                    modifier = Modifier
                                        .width(32.dp)
                                        .height(6.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(50)
                                        )
                                )
                            } else {
                                Box(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
            }
        }
    }
}
