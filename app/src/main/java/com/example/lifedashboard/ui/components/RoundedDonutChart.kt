package com.example.lifedashboard.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.*

/**
 * Data representation for each slice in the rounded spaced donut chart.
 */
data class DonutSlice(
    val label: String,
    val value: Float,
    val color: Color,
    val percentage: Float = 0f
)

/**
 * Color palette matching the modern rounded spaced donut chart.
 */
val DonutChartPalette = listOf(
    Color(0xFF5362F6), // Vibrant Indigo-Blue (Comedy in reference)
    Color(0xFF0EA5E9), // Vivid Sky Cyan (Action in reference)
    Color(0xFF22C55E), // Emerald Green (SciFi in reference)
    Color(0xFFF59E0B), // Warm Amber (Drama in reference)
    Color(0xFFEF4444), // Coral Red (Horror in reference)
    Color(0xFF8B5CF6), // Royal Purple
    Color(0xFFEC4899), // Pink
    Color(0xFF06B6D4), // Teal Cyan
    Color(0xFF14B8A6), // Mint
    Color(0xFFF97316)  // Bright Orange
)

/**
 * Modern Donut Chart with:
 * - Rounded, spaced donut slices (matching reference design)
 * - Percentage labels on slices
 * - Clean responsive layout that never gets clipped
 * - Horizontal flowing legend at bottom
 * - Smooth entrance animation
 * - Interactive slice selection
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoundedDonutChart(
    slices: List<DonutSlice>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 160.dp,
    gapDegrees: Float = 4.5f,
    cornerRadiusDp: Dp = 6.dp,
    innerCutoutRatio: Float = 0.60f, // ~60% inner hole matching reference
    showSlicePercentages: Boolean = true,
    showLegend: Boolean = true,
    legendSpacing: Dp = 8.dp,
    currencySymbol: String = "$",
    onSliceSelected: ((DonutSlice?) -> Unit)? = null
) {
    if (slices.isEmpty()) {
        EmptyDonutChartPlaceholder(chartSize = chartSize)
        return
    }

    val totalValue = remember(slices) { slices.sumOf { it.value.toDouble() }.toFloat() }
    val normalizedSlices = remember(slices, totalValue) {
        if (totalValue <= 0f) emptyList()
        else {
            slices.map { slice ->
                slice.copy(percentage = (slice.value / totalValue) * 100f)
            }
        }
    }

    if (normalizedSlices.isEmpty()) {
        EmptyDonutChartPlaceholder(chartSize = chartSize)
        return
    }

    var selectedSliceIndex by remember { mutableStateOf<Int?>(null) }
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    // Entrance animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(normalizedSlices) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
        )
    }

    val cornerRadiusPx = with(density) { cornerRadiusDp.toPx() }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Donut Canvas
        Box(
            modifier = Modifier
                .size(chartSize)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(normalizedSlices, totalValue) {
                        detectTapGestures { tapOffset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dx = tapOffset.x - center.x
                            val dy = tapOffset.y - center.y
                            val distance = sqrt(dx * dx + dy * dy)

                            val canvasMin = min(size.width, size.height)
                            val outerR = canvasMin / 2f - 6f
                            val innerR = outerR * innerCutoutRatio

                            if (distance in (innerR - 15f)..(outerR + 15f)) {
                                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                if (angle < -90f) angle += 360f // Align starting from -90° (top)

                                var currentAngle = -90f
                                var tappedIndex: Int? = null

                                for ((index, slice) in normalizedSlices.withIndex()) {
                                    val sweep = (slice.value / totalValue) * 360f
                                    val start = currentAngle
                                    val end = currentAngle + sweep
                                    if (angle in start..end) {
                                        tappedIndex = index
                                        break
                                    }
                                    currentAngle += sweep
                                }

                                selectedSliceIndex = if (selectedSliceIndex == tappedIndex) null else tappedIndex
                                onSliceSelected?.invoke(selectedSliceIndex?.let { normalizedSlices[it] })
                            } else {
                                selectedSliceIndex = null
                                onSliceSelected?.invoke(null)
                            }
                        }
                    }
            ) {
                val canvasMin = min(size.width, size.height)
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseOuterRadius = (canvasMin / 2f) - 6f
                val baseInnerRadius = baseOuterRadius * innerCutoutRatio
                val thickness = baseOuterRadius - baseInnerRadius

                var startAngle = -90f // Start from 12 o'clock

                val isSingleSlice = normalizedSlices.size == 1

                normalizedSlices.forEachIndexed { index, slice ->
                    val fullSweep = (slice.value / totalValue) * 360f
                    val animatedFullSweep = fullSweep * animProgress.value

                    val isSelected = selectedSliceIndex == index
                    val outerRadius = if (isSelected) baseOuterRadius + 4f else baseOuterRadius
                    val innerRadius = if (isSelected) baseInnerRadius - 2f else baseInnerRadius

                    if (isSingleSlice) {
                        // Full donut ring for single category
                        drawCircle(
                            color = slice.color,
                            radius = (outerRadius + innerRadius) / 2f,
                            center = center,
                            style = Stroke(width = thickness)
                        )
                    } else if (animatedFullSweep > 0.5f) {
                        // Spacing gap calculation
                        val actualGap = minOf(gapDegrees, animatedFullSweep * 0.35f)
                        val sliceStart = startAngle + (actualGap / 2f)
                        val sliceSweep = animatedFullSweep - actualGap
                        val sliceEnd = sliceStart + sliceSweep

                        val path = createRoundedDonutSlicePath(
                            cx = center.x,
                            cy = center.y,
                            innerRadius = innerRadius,
                            outerRadius = outerRadius,
                            startAngleDeg = sliceStart,
                            endAngleDeg = sliceEnd,
                            cornerRad = cornerRadiusPx
                        )

                        drawPath(path = path, color = slice.color)

                        // Percentage Label on Slice
                        if (showSlicePercentages && animProgress.value > 0.6f && sliceSweep >= 14f) {
                            val midAngleDeg = sliceStart + (sliceSweep / 2f)
                            val midRadius = (innerRadius + outerRadius) / 2f
                            val rad = Math.toRadians(midAngleDeg.toDouble())
                            val textCenterX = (center.x + midRadius * cos(rad)).toFloat()
                            val textCenterY = (center.y + midRadius * sin(rad)).toFloat()

                            val percentText = String.format(Locale.US, "%.1f%%", slice.percentage)
                            val fontSize = if (sliceSweep < 24f) 10.sp else 11.5.sp

                            val textLayout = textMeasurer.measure(
                                text = percentText,
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = fontSize,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            )

                            // Only draw if text fits within slice bounds comfortably
                            val arcLength = midRadius * Math.toRadians(sliceSweep.toDouble()).toFloat()
                            if (arcLength > textLayout.size.width * 0.85f) {
                                drawText(
                                    textLayoutResult = textLayout,
                                    topLeft = Offset(
                                        x = textCenterX - (textLayout.size.width / 2f),
                                        y = textCenterY - (textLayout.size.height / 2f)
                                    ),
                                    alpha = ((animProgress.value - 0.6f) / 0.4f).coerceIn(0f, 1f)
                                )
                            }
                        }
                    }

                    startAngle += fullSweep
                }
            }

            // Optional center details when a slice is selected
            selectedSliceIndex?.let { index ->
                val selected = normalizedSlices.getOrNull(index)
                if (selected != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = selected.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = selected.color
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f%%", selected.percentage),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$currencySymbol${String.format(Locale.US, "%.2f", selected.value)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Horizontal Legend below the chart matching the reference image
        if (showLegend) {
            Spacer(modifier = Modifier.height(legendSpacing))
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                normalizedSlices.forEachIndexed { index, slice ->
                    val isSelected = selectedSliceIndex == index
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                selectedSliceIndex = if (selectedSliceIndex == index) null else index
                                onSliceSelected?.invoke(selectedSliceIndex?.let { normalizedSlices[it] })
                            }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        // Color dot/bullet
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(slice.color)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = slice.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) slice.color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Constructs an exact Path for an annular sector with rounded corners and spacing.
 */
private fun createRoundedDonutSlicePath(
    cx: Float,
    cy: Float,
    innerRadius: Float,
    outerRadius: Float,
    startAngleDeg: Float,
    endAngleDeg: Float,
    cornerRad: Float
): Path {
    val sweepDeg = endAngleDeg - startAngleDeg
    if (sweepDeg <= 0.1f) return Path()

    val rOut = outerRadius
    val rIn = innerRadius
    val thickness = rOut - rIn

    // Clamp corner radius to prevent self-intersection
    val sweepRad = Math.toRadians(sweepDeg.toDouble()).toFloat()
    val maxCornerBySweep = (rIn * sweepRad) / 2.15f
    val maxCornerByThickness = thickness / 2.1f
    val r = minOf(cornerRad, maxCornerByThickness, maxCornerBySweep).coerceAtLeast(0f)

    val path = Path()
    val outerRect = Rect(cx - rOut, cy - rOut, cx + rOut, cy + rOut)
    val innerRect = Rect(cx - rIn, cy - rIn, cx + rIn, cy + rIn)

    if (r < 0.5f) {
        // Fallback to crisp sharp annular sector if radius is negligible
        path.arcTo(outerRect, startAngleDeg, sweepDeg, false)
        val pInnerEnd = pointOnCircle(cx, cy, rIn, endAngleDeg)
        path.lineTo(pInnerEnd.x, pInnerEnd.y)
        path.arcTo(innerRect, endAngleDeg, -sweepDeg, false)
        path.close()
        return path
    }

    val deltaO = Math.toDegrees((r / rOut).toDouble()).toFloat()
    val deltaI = Math.toDegrees((r / rIn).toDouble()).toFloat()

    val thetaOutStart = startAngleDeg + deltaO
    val thetaOutEnd = endAngleDeg - deltaO
    val sweepOut = (thetaOutEnd - thetaOutStart).coerceAtLeast(0.01f)

    val thetaInStart = startAngleDeg + deltaI
    val thetaInEnd = endAngleDeg - deltaI
    val sweepIn = (thetaInEnd - thetaInStart).coerceAtLeast(0.01f)

    // Key points
    val p1 = pointOnCircle(cx, cy, rOut, thetaOutStart)
    val vOuterEnd = pointOnCircle(cx, cy, rOut, endAngleDeg)
    val p3 = pointOnCircle(cx, cy, rOut - r, endAngleDeg)
    val p4 = pointOnCircle(cx, cy, rIn + r, endAngleDeg)
    val vInnerEnd = pointOnCircle(cx, cy, rIn, endAngleDeg)
    val p5 = pointOnCircle(cx, cy, rIn, thetaInEnd)
    val vInnerStart = pointOnCircle(cx, cy, rIn, startAngleDeg)
    val p7 = pointOnCircle(cx, cy, rIn + r, startAngleDeg)
    val p8 = pointOnCircle(cx, cy, rOut - r, startAngleDeg)
    val vOuterStart = pointOnCircle(cx, cy, rOut, startAngleDeg)

    // Trace path with smooth quadratic Bezier rounded corners
    path.moveTo(p1.x, p1.y)
    path.arcTo(outerRect, thetaOutStart, sweepOut, false)
    path.quadraticTo(vOuterEnd.x, vOuterEnd.y, p3.x, p3.y)
    path.lineTo(p4.x, p4.y)
    path.quadraticTo(vInnerEnd.x, vInnerEnd.y, p5.x, p5.y)
    path.arcTo(innerRect, thetaInEnd, -sweepIn, false)
    path.quadraticTo(vInnerStart.x, vInnerStart.y, p7.x, p7.y)
    path.lineTo(p8.x, p8.y)
    path.quadraticTo(vOuterStart.x, vOuterStart.y, p1.x, p1.y)
    path.close()

    return path
}

private fun pointOnCircle(cx: Float, cy: Float, radius: Float, angleDeg: Float): Offset {
    val rad = Math.toRadians(angleDeg.toDouble())
    return Offset(
        x = (cx + radius * cos(rad)).toFloat(),
        y = (cy + radius * sin(rad)).toFloat()
    )
}

@Composable
private fun EmptyDonutChartPlaceholder(chartSize: Dp) {
    Box(
        modifier = Modifier
            .size(chartSize)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasMin = min(size.width, size.height)
            val outerR = canvasMin / 2f - 6f
            val thickness = outerR * 0.38f
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.25f),
                radius = outerR - thickness / 2f,
                style = Stroke(width = thickness)
            )
        }
        Text(
            text = "No expenses yet",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}
