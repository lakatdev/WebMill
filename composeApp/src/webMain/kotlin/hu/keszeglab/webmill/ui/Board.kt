package hu.keszeglab.webmill.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hu.keszeglab.webmill.model.Position
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

@Composable
fun Board(modifier: Modifier = Modifier, size: Dp = 400.dp, onClick: (Position) -> Unit = {}) {
    
    val density = LocalDensity.current
    val px = with(density) { size.toPx() }

    Box(modifier = modifier.size(size)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    // empty area
                }
        ) {
            drawBoard(px)
        }
        
        EdgeButton(
            size = size,
            onClick = onClick
        )
    }
}

@Composable
private fun BoxScope.EdgeButton(
    size: Dp,
    onClick: (Position) -> Unit
) {
    val positions = Position.all
    positions.forEach { position ->
        val (x, y) = getPositionCoordinates(position, size)
        
        Box(
            modifier = Modifier
                .offset(x - 20.dp, y - 20.dp)
                .size(40.dp)
                .clip(CircleShape)
                .clickable { onClick(position) }
        )
    }
}

private fun getPositionCoordinates(position: Position, boardSize: Dp): Pair<Dp, Dp> {
    val sizeValue = boardSize.value
    val center = sizeValue / 2
    
    val circleRadii = when (position.circle) {
        0 -> sizeValue * 0.45f
        1 -> sizeValue * 0.3f
        2 -> sizeValue * 0.15f
        else -> 0f
    }
    
    val angle = position.index * 45f
    val radians = angle * PI / 180.0
    
    val x = center + circleRadii * cos(radians).toFloat()
    val y = center + circleRadii * sin(radians).toFloat()
    
    return Pair(x.dp, y.dp)
}

private fun DrawScope.drawBoard(px: Float) {
    val strokeWidth = 4.dp.toPx()
    val boardColor = Color.Black
    
    val outerSize = px * 0.9f
    val middleSize = px * 0.6f
    val innerSize = px * 0.3f

    val centerX = size.width / 2
    val centerY = size.height / 2

    drawRect(
        color = boardColor,
        topLeft = Offset(centerX - outerSize/2, centerY - outerSize/2),
        size = androidx.compose.ui.geometry.Size(outerSize, outerSize),
        style = Stroke(width = strokeWidth)
    )
    
    drawRect(
        color = boardColor,
        topLeft = Offset(centerX - middleSize/2, centerY - middleSize/2),
        size = androidx.compose.ui.geometry.Size(middleSize, middleSize),
        style = Stroke(width = strokeWidth)
    )
    
    drawRect(
        color = boardColor,
        topLeft = Offset(centerX - innerSize/2, centerY - innerSize/2),
        size = androidx.compose.ui.geometry.Size(innerSize, innerSize),
        style = Stroke(width = strokeWidth)
    )
    
    drawLine(
        color = boardColor,
        start = Offset(centerX, centerY - outerSize/2),
        end = Offset(centerX, centerY - innerSize/2),
        strokeWidth = strokeWidth
    )
    
    drawLine(
        color = boardColor,
        start = Offset(centerX + outerSize/2, centerY),
        end = Offset(centerX + innerSize/2, centerY),
        strokeWidth = strokeWidth
    )
    
    drawLine(
        color = boardColor,
        start = Offset(centerX, centerY + outerSize/2),
        end = Offset(centerX, centerY + innerSize/2),
        strokeWidth = strokeWidth
    )
    
    drawLine(
        color = boardColor,
        start = Offset(centerX - outerSize/2, centerY),
        end = Offset(centerX - innerSize/2, centerY),
        strokeWidth = strokeWidth
    )
}
