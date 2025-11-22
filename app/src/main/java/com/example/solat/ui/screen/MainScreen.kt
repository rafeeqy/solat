package com.example.solat.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.LocationOn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.solat.ui.theme.SolatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { /* TODO: ??? */ },
                actions = {
                    IconButton(onClick = { /* TODO: ??? */ }) {
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
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DateButton()
                        ZoneLocationButton(modifier = Modifier.fillMaxWidth(.5f))
                    }
                    // Next prayer
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NextPrayer()
                    }
                    // Prayer countdown
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NextPrayerCountdown()
                    }
                }
                // Bottom content
                Column(modifier = Modifier.fillMaxWidth()) {
                    PrayerTimeCard(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 32.dp
                        )
                    )
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
        onClick = { /* TODO: ??? */ },
        modifier = modifier,
        enabled = false,
        colors = ButtonDefaults.textButtonColors().copy(
            disabledContentColor = ButtonDefaults.textButtonColors().contentColor
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.DateRange,
                contentDescription = "Date range"
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Saturday, 22 November 2025",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "1 Jamadil Awal 1447",
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun ZoneLocationButton(
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { /* TODO: ??? */ },
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = "Location on"
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Sabah, Zon 9",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "Beaufort, Kuala Penyu, Sipitang, Tenom, Long Pasia, Membakut, Weston, Bahagian Pendalaman (Bawah)",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun NextPrayer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Asar",
            fontSize = 80.sp,
            fontWeight = FontWeight.Light,
        )
        Text(
            text = "4:28 pm",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraLight,
        )
    }
}

@Composable
private fun NextPrayerCountdown(
    modifier: Modifier = Modifier
) {
    val tempData = mapOf(
        "Hours" to "60",
        "Minutes" to "60",
        "Seconds" to "60"
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
                        text = entry.value,
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
    modifier: Modifier = Modifier
) {
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
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(text = "Prayer times")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) {
                    Column(
                        modifier = Modifier.padding(
                            vertical = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Maghrib",
                                fontWeight = FontWeight.Normal,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = "5:50 am",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(6.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(50)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    SolatTheme {
        MainScreen()
    }
}
