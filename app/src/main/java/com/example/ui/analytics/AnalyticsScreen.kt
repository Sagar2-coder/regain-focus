package com.example.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyAnalytics
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.viewmodel.FocusViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(viewModel: FocusViewModel) {
    val dailyData by viewModel.dailyAnalytics.collectAsState()
    val recentLogs by viewModel.recentLogs.collectAsState()
    val scrollState = rememberScrollState()

    var selectedDayIndex by remember { mutableIntStateOf(6) } // Default to today (Sunday)

    val totalMinutesThisWeek = dailyData.sumOf { it.focusMinutes }
    val totalIntercepts = dailyData.sumOf { it.reelsIntercepted }
    val totalSavedHours = dailyData.sumOf { it.minutesSaved } / 60.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Title
        Text(
            text = "Deep Analytics",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Reclaimed dopamine • Time saved from short-form feeds",
            color = Color(0xFF9CA3AF),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 4 Key Metric Cards in 2x2 Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "Time Saved",
                value = String.format(Locale.getDefault(), "%.1fh", totalSavedHours),
                subtitle = "Reclaimed this week",
                icon = Icons.Default.HourglassBottom,
                tint = EmeraldSecondary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Shorts Blocked",
                value = "$totalIntercepts",
                subtitle = "Mindful interventions",
                icon = Icons.Default.Shield,
                tint = CyanPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "Focus Time",
                value = "${totalMinutesThisWeek / 60}h ${totalMinutesThisWeek % 60}m",
                subtitle = "Deep work logged",
                icon = Icons.Default.AvTimer,
                tint = NeonViolet,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Focus Streak",
                value = "14 Days",
                subtitle = "Current consistency",
                icon = Icons.Default.Bolt,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User-Friendly Reclaimed Life Equivalence Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(EmeraldSecondary.copy(alpha = 0.4f), CyanPrimary.copy(alpha = 0.4f)))
            )
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "📚", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Life Reclaimed Equivalent",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your saved ${String.format(Locale.getDefault(), "%.1f", totalSavedHours)} hours = ~14 podcast episodes or 1 deep reading sprint!",
                        color = EmeraldSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weekly Focus Hours Canvas Bar Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(CardBorderDark, CardBorderDark))
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Weekly Focus Minutes",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap any day to see details",
                            color = Color(0xFF9CA3AF),
                            fontSize = 11.sp
                        )
                    }

                    val selectedDay = dailyData.getOrNull(selectedDayIndex)
                    if (selectedDay != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyanPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${selectedDay.focusMinutes}m (${selectedDay.reelsIntercepted} blocked)",
                                color = CyanPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                FocusBarChart(
                    data = dailyData,
                    selectedIndex = selectedDayIndex,
                    onSelect = { selectedDayIndex = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Days Labels Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    dailyData.forEachIndexed { idx, item ->
                        val shortLabel = item.dayLabel.take(3)
                        val isSelected = idx == selectedDayIndex
                        Text(
                            text = shortLabel,
                            color = if (isSelected) CyanPrimary else Color(0xFF9CA3AF),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.clickable { selectedDayIndex = idx }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Platform Interception Breakdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(CardBorderDark, CardBorderDark))
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Distraction Source Breakdown",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Which feeds trigger reflexive opening habits most often",
                    color = Color(0xFF9CA3AF),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                PlatformBreakdownRow(
                    platform = "Instagram Reels",
                    percentage = 0.52f,
                    blockedCount = "59 intercepts",
                    color = Color(0xFFE1306C)
                )
                Spacer(modifier = Modifier.height(10.dp))

                PlatformBreakdownRow(
                    platform = "YouTube Shorts",
                    percentage = 0.31f,
                    blockedCount = "35 intercepts",
                    color = Color(0xFFFF0000)
                )
                Spacer(modifier = Modifier.height(10.dp))

                PlatformBreakdownRow(
                    platform = "TikTok For You",
                    percentage = 0.17f,
                    blockedCount = "20 intercepts",
                    color = CyanPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Peak Distraction Hours Guide
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(CardBorderDark, CardBorderDark))
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⏰", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Peak Urge Windows",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "When doomscrolling attempts occur most frequently:",
                    color = Color(0xFF9CA3AF),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceVariantDark)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(text = "🌙 11 PM - 12:30 AM", color = Color(0xFFF59E0B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "48% of intercepts (Bedtime reflex)", color = Color(0xFF9CA3AF), fontSize = 10.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceVariantDark)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(text = "☕ 2:30 PM - 3:30 PM", color = CyanPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "32% of intercepts (Afternoon slump)", color = Color(0xFF9CA3AF), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Interception Audit Log
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(CardBorderDark, CardBorderDark))
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Recent Interception Audit Log",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Real-time log of surgical interventions and actions taken",
                    color = Color(0xFF9CA3AF),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                recentLogs.take(5).forEach { log ->
                    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                    val timeStr = sdf.format(Date(log.timestamp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSecondary)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${log.appName} • ${log.surface}",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${log.actionTaken} • Reclaimed ~${log.secondsSaved / 60}m",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = timeStr,
                            color = CyanPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(CardBorderDark, CardBorderDark))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color(0xFF9CA3AF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = tint,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FocusBarChart(
    data: List<DailyAnalytics>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val maxMinutes = (data.maxOfOrNull { it.focusMinutes } ?: 180).coerceAtLeast(60).toFloat()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { /* Tap handled */ }
    ) {
        val barCount = data.size
        val barWidth = 26.dp.toPx()
        val spacing = (size.width - (barCount * barWidth)) / (barCount + 1)

        data.forEachIndexed { index, item ->
            val barHeight = (item.focusMinutes / maxMinutes) * (size.height - 20.dp.toPx())
            val x = spacing + index * (barWidth + spacing)
            val y = size.height - barHeight
            val isSelected = index == selectedIndex

            drawRoundRect(
                brush = Brush.verticalGradient(
                    if (isSelected) listOf(CyanPrimary, EmeraldSecondary)
                    else listOf(SurfaceVariantDark, CardBorderDark)
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
        }
    }
}

@Composable
fun PlatformBreakdownRow(
    platform: String,
    percentage: Float,
    blockedCount: String,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = platform,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = blockedCount,
                color = Color(0xFF9CA3AF),
                fontSize = 11.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = SurfaceVariantDark
        )
    }
}
