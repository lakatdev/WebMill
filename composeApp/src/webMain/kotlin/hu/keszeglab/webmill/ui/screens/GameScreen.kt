package hu.keszeglab.webmill.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hu.keszeglab.webmill.model.GameState
import hu.keszeglab.webmill.model.Logic
import hu.keszeglab.webmill.model.Player
import hu.keszeglab.webmill.model.Position

@Composable
fun GameScreen(
    onBackToStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    var gameState by remember { mutableStateOf(GameState()) }
    val logic = remember { Logic() }

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
            Spacer(modifier = Modifier.height(16.dp))
            GameStatusIndicators(gameState = gameState)
            MillBoard(
                gameState = gameState,
                onNodeClick = { position ->
                    try {
                        gameState = logic.makeMove(gameState, gameState.selectedPosition, position)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                modifier = Modifier.padding(top = 32.dp)
            )
        }
    }
}

@Composable
fun GameStatusIndicators(gameState: GameState) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Now: ",
                    style = MaterialTheme.typography.bodyLarge
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            if (gameState.currentPlayer == Player.DARK) Color.Black else Color.White
                        )
                        .border(
                            1.dp, 
                            if (gameState.currentPlayer == Player.DARK) Color.Gray else Color.Black,
                            CircleShape
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dark")
                    }
                    Text(
                        text = "${gameState.piecesToPlace[Player.DARK] ?: 0} left",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color.Black, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Light")
                    }
                    Text(
                        text = "${gameState.piecesToPlace[Player.LIGHT] ?: 0} left",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            if (gameState.winner != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Winner: ${if (gameState.winner == Player.DARK) "Dark" else "Light"}!",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (gameState.status) {
                        hu.keszeglab.webmill.model.GameStatus.PLACE -> "Place"
                        hu.keszeglab.webmill.model.GameStatus.MOVE -> "Move"
                        hu.keszeglab.webmill.model.GameStatus.REMOVE -> "Remove"
                        hu.keszeglab.webmill.model.GameStatus.FINISHED -> "Game over"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
}

@Composable
fun MillBoard(
    gameState: GameState,
    onNodeClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
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
                        val boardPosition = Position(index / 8, index % 8)
                        val piece = gameState.pieceAt(boardPosition)
                        val isSelected = gameState.selectedPosition == boardPosition
                        
                        Box(
                            modifier = Modifier
                                .size(nodeSize)
                                .absoluteOffset(
                                    x = with(density) { position.x.toDp() - nodeSize.div(2) },
                                    y = with(density) { position.y.toDp() - nodeSize.div(2) }
                                )
                                .clip(CircleShape)
                                .background(
                                    when {
                                        piece?.player == Player.DARK -> Color.Black
                                        piece?.player == Player.LIGHT -> Color.White
                                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    }
                                )
                                .then(
                                    if (piece?.player == Player.LIGHT || piece == null) {
                                        Modifier.border(1.dp, Color.Black, CircleShape)
                                    } else Modifier
                                )
                                .then(
                                    if (isSelected) {
                                        Modifier.border(3.dp, Color.Red, CircleShape)
                                    } else Modifier
                                )
                                .clickable {
                                    onNodeClick(boardPosition)
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
