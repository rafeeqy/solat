package my.naqibrafiqi.solat.ui.feature.prayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import my.naqibrafiqi.solat.R
import my.naqibrafiqi.solat.domain.model.DailyPrayer
import my.naqibrafiqi.solat.domain.model.PrayerTime
import my.naqibrafiqi.solat.domain.model.enums.PrayerName
import my.naqibrafiqi.solat.ui.component.EmptyContent
import my.naqibrafiqi.solat.util.SolatDateTime
import my.naqibrafiqi.solat.util.snackbar.SnackbarManager
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyPrayerScreen(
    snackbarManager: SnackbarManager,
    onNavigateToSettings: () -> Unit,
    onNavigateToZoneLocation: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DailyPrayerViewModel = hiltViewModel()
) {
    val zone = viewModel.zone.collectAsStateWithLifecycle().value
    val todayAndTomorrowPrayers =
        viewModel.todayAndTomorrowPrayers.collectAsStateWithLifecycle().value

    val countdownText = viewModel.countdownText.collectAsStateWithLifecycle().value
    val nextPrayer = viewModel.nextPrayer.collectAsStateWithLifecycle().value

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { /* NOTE: Leave this empty... */ },
                actions = {
                    IconButton(onClick = { /* TODO: Refresh... */ }) {
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
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarManager.snackbarState)
        }
    ) { innerPadding ->
        if (todayAndTomorrowPrayers.isEmpty() || todayAndTomorrowPrayers.size <= 1) {
            EmptyContent(
                message = stringResource(id = R.string.common_results_no_prayers),
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val todayPrayers = todayAndTomorrowPrayers[0]
            val tomorrowPrayers = todayAndTomorrowPrayers[1]

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
                                } ?: stringResource(id = R.string.common_label_unknown_zone),
                                district = zone?.district
                                    ?: stringResource(id = R.string.common_label_unknown_district),
                                onClick = onNavigateToZoneLocation,
                                modifier = Modifier.fillMaxWidth(3 / 4f)
                            )
                        }

                        // Next prayer
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NextPrayer(prayer = nextPrayer)
                        }

                        // Prayer countdown
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NextPrayerCountdown(countdown = countdownText)
                        }
                    }

                    // Bottom content
                    Column(modifier = Modifier.fillMaxWidth()) {
                        PrayerTimeCard(
                            todayPrayer = todayPrayers,
                            tomorrowPrayer = tomorrowPrayers,
                            currentPrayer = nextPrayer
                        )
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
            disabledContentColor = MaterialTheme.colorScheme.tertiary
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
    prayer: PrayerTime?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (prayer != null) {
                stringResource(id = prayer.prayer.resId)
            } else {
                stringResource(id = R.string.common_label_unknown_prayer)
            },
            fontSize = 80.sp,
            fontWeight = FontWeight.Medium,
        )

        Text(
            text = if (prayer != null) {
                SolatDateTime.formatLocalTime(time = prayer.time).lowercase()
            } else {
                stringResource(id = R.string.common_label_unknown_prayer_time)
            },
            fontSize = 40.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
private fun NextPrayerCountdown(
    countdown: String,
    modifier: Modifier = Modifier
) {
    val splitCountdown = countdown.split(":")

    val tempData = mapOf(
        stringResource(id = R.string.prayer_label_hours) to splitCountdown[0],
        stringResource(id = R.string.prayer_label_minutes) to splitCountdown[1],
        stringResource(id = R.string.prayer_label_seconds) to splitCountdown[2]
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
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.displaySmall
                    )

                    Text(
                        text = entry.key,
                        fontWeight = FontWeight.Normal,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrayerTimeCard(
    todayPrayer: DailyPrayer,
    tomorrowPrayer: DailyPrayer,
    currentPrayer: PrayerTime?,
    modifier: Modifier = Modifier
) {
    val currentTime = LocalTime.now()
    val todayIsha = todayPrayer.prayerTimes.find { it.prayer == PrayerName.ISYAK }

    val isPastTodayIsha = currentTime.isAfter(todayIsha?.time)

    val prayerDate =
        if (isPastTodayIsha) tomorrowPrayer.date else todayPrayer.date
    val currentPrayers =
        if (isPastTodayIsha) tomorrowPrayer.prayerTimes else todayPrayer.prayerTimes

    val initialItem =
        currentPrayers.indexOfFirst { it.prayer == currentPrayer?.prayer }
    val carouselState =
        rememberCarouselState(initialItem = if (initialItem < 0) 0 else initialItem) {
            currentPrayers.count()
        }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 56.dp
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Row {
                Text(
                    text = stringResource(id = R.string.prayer_label_today_prayer),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = SolatDateTime.formatLocalDate(
                    date = prayerDate,
                    format = "EEEE, dd/MM/uuuu"
                ),
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.labelLarge
            )
        }

        HorizontalUncontainedCarousel(
            state = carouselState,
            itemWidth = 104.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 16.dp),
            itemSpacing = 8.dp
        ) { index ->
            val prayerTime = currentPrayers[index]

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .maskClip(RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = if (index == initialItem) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.surface
                },
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(id = prayerTime.prayer.resId),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text =
                                SolatDateTime.formatLocalTime(time = prayerTime.time).lowercase(),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
