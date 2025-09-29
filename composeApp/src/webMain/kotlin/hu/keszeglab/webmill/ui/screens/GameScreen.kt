package hu.keszeglab.webmill.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GameScreen(
    onBackToStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp)
    ) {
        Button(
            onClick = onBackToStart,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text("Back")
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "WebMill",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            MillBoard(
                modifier = Modifier.padding(top = 32.dp)
            )
        }
    }
}

@Composable
fun MillBoard(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints {
            val maxBoardSize = 400.dp
            val availableSize = if (maxWidth < maxHeight) maxWidth else maxHeight
            val actualBoardSize = if (availableSize < maxBoardSize) availableSize else maxBoardSize
            val nodeSize = (actualBoardSize.value * 0.06f).dp.coerceAtLeast(16.dp)
            
            Box(
                modifier = Modifier
                    .size(actualBoardSize)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                val innerBoardSize = actualBoardSize - 32.dp
                
                Box(
                    modifier = Modifier.size(innerBoardSize)
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawMillBoardLines(this)
                    }
                    
                    val positions = remember(innerBoardSize) { 
                        getMillNodePositions(with(density) { innerBoardSize.toPx() })
                    }
                    
                    positions.forEachIndexed { index, position ->
                        Box(
                            modifier = Modifier
                                .size(nodeSize)
                                .absoluteOffset(
                                    x = with(density) { position.x.toDp() - nodeSize.div(2) },
                                    y = with(density) { position.y.toDp() - nodeSize.div(2) }
                                )
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable {
                                    println("Node $index clicked")
                                }
                        )
                    }
                }
            }
        }
    }
}

private fun drawMillBoardLines(drawScope: DrawScope) {
    val size = drawScope.size
    val strokeWidth = 3.0f
    val lineColor = Color.Black
    
    val outerMargin = size.width * 0.05f
    val middleMargin = size.width * 0.2f
    val innerMargin = size.width * 0.35f
    
    drawScope.drawRect(
        color = lineColor,
        topLeft = Offset(outerMargin, outerMargin),
        size = androidx.compose.ui.geometry.Size(
            size.width - 2 * outerMargin,
            size.height - 2 * outerMargin
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
    )
    
    drawScope.drawRect(
        color = lineColor,
        topLeft = Offset(middleMargin, middleMargin),
        size = androidx.compose.ui.geometry.Size(
            size.width - 2 * middleMargin,
            size.height - 2 * middleMargin
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
    )
    
    drawScope.drawRect(
        color = lineColor,
        topLeft = Offset(innerMargin, innerMargin),
        size = androidx.compose.ui.geometry.Size(
            size.width - 2 * innerMargin,
            size.height - 2 * innerMargin
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
    )
    
    drawScope.drawLine(
        color = lineColor,
        start = Offset(size.width / 2, outerMargin),
        end = Offset(size.width / 2, innerMargin),
        strokeWidth = strokeWidth
    )
    
    drawScope.drawLine(
        color = lineColor,
        start = Offset(size.width / 2, size.height - outerMargin),
        end = Offset(size.width / 2, size.height - innerMargin),
        strokeWidth = strokeWidth
    )
    
    drawScope.drawLine(
        color = lineColor,
        start = Offset(outerMargin, size.height / 2),
        end = Offset(innerMargin, size.height / 2),
        strokeWidth = strokeWidth
    )
    
    drawScope.drawLine(
        color = lineColor,
        start = Offset(size.width - outerMargin, size.height / 2),
        end = Offset(size.width - innerMargin, size.height / 2),
        strokeWidth = strokeWidth
    )
}

private fun getMillNodePositions(boardSize: Float): List<Offset> {
    val positions = mutableListOf<Offset>()
    val center = boardSize / 2
    
    val outerMargin = boardSize * 0.05f
    val middleMargin = boardSize * 0.2f
    val innerMargin = boardSize * 0.35f
    
    positions.add(Offset(outerMargin, outerMargin))
    positions.add(Offset(center, outerMargin))
    positions.add(Offset(boardSize - outerMargin, outerMargin))
    positions.add(Offset(boardSize - outerMargin, center))
    positions.add(Offset(boardSize - outerMargin, boardSize - outerMargin))
    positions.add(Offset(center, boardSize - outerMargin))
    positions.add(Offset(outerMargin, boardSize - outerMargin))
    positions.add(Offset(outerMargin, center))
    positions.add(Offset(middleMargin, middleMargin))
    positions.add(Offset(center, middleMargin))
    positions.add(Offset(boardSize - middleMargin, middleMargin))
    positions.add(Offset(boardSize - middleMargin, center))
    positions.add(Offset(boardSize - middleMargin, boardSize - middleMargin))
    positions.add(Offset(center, boardSize - middleMargin))
    positions.add(Offset(middleMargin, boardSize - middleMargin))
    positions.add(Offset(middleMargin, center))
    positions.add(Offset(innerMargin, innerMargin))
    positions.add(Offset(center, innerMargin))
    positions.add(Offset(boardSize - innerMargin, innerMargin))
    positions.add(Offset(boardSize - innerMargin, center))
    positions.add(Offset(boardSize - innerMargin, boardSize - innerMargin))
    positions.add(Offset(center, boardSize - innerMargin))
    positions.add(Offset(innerMargin, boardSize - innerMargin))
    positions.add(Offset(innerMargin, center))

    return positions
}
