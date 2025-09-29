package hu.keszeglab.webmill.model

import hu.keszeglab.webmill.model.*

class Logic {
    companion object {
        private val possibleMills = listOf(
            listOf(Position(0, 0), Position(0, 1), Position(0, 2)),
            listOf(Position(0, 2), Position(0, 3), Position(0, 4)),
            listOf(Position(0, 4), Position(0, 5), Position(0, 6)),
            listOf(Position(0, 6), Position(0, 7), Position(0, 0)),

            listOf(Position(1, 0), Position(1, 1), Position(1, 2)),
            listOf(Position(1, 2), Position(1, 3), Position(1, 4)),
            listOf(Position(1, 4), Position(1, 5), Position(1, 6)),
            listOf(Position(1, 6), Position(1, 7), Position(1, 0)),

            listOf(Position(2, 0), Position(2, 1), Position(2, 2)),
            listOf(Position(2, 2), Position(2, 3), Position(2, 4)),
            listOf(Position(2, 4), Position(2, 5), Position(2, 6)),
            listOf(Position(2, 6), Position(2, 7), Position(2, 0)),

            listOf(Position(0, 1), Position(1, 1), Position(2, 1)),
            listOf(Position(0, 3), Position(1, 3), Position(2, 3)),
            listOf(Position(0, 5), Position(1, 5), Position(2, 5)),
            listOf(Position(0, 7), Position(1, 7), Position(2, 7))
        )

        private val possibleMoves = mapOf(
            Position(0, 0) to listOf(Position(0, 1), Position(0, 7)),
            Position(0, 1) to listOf(Position(0, 0), Position(0, 2), Position(1, 1)),
            Position(0, 2) to listOf(Position(0, 1), Position(0, 3)),
            Position(0, 3) to listOf(Position(0, 2), Position(0, 4), Position(1, 3)),
            Position(0, 4) to listOf(Position(0, 3), Position(0, 5)),
            Position(0, 5) to listOf(Position(0, 4), Position(0, 6), Position(1, 5)),
            Position(0, 6) to listOf(Position(0, 5), Position(0, 7)),
            Position(0, 7) to listOf(Position(0, 6), Position(0, 0), Position(1, 7)),

            Position(1, 0) to listOf(Position(1, 1), Position(1, 7)),
            Position(1, 1) to listOf(Position(1, 0), Position(1, 2), Position(0, 1), Position(2, 1)),
            Position(1, 2) to listOf(Position(1, 1), Position(1, 3)),
            Position(1, 3) to listOf(Position(1, 2), Position(1, 4), Position(0, 3), Position(2, 3)),
            Position(1, 4) to listOf(Position(1, 3), Position(1, 5)),
            Position(1, 5) to listOf(Position(1, 4), Position(1, 6), Position(0, 5), Position(2, 5)),
            Position(1, 6) to listOf(Position(1, 5), Position(1, 7)),
            Position(1, 7) to listOf(Position(1, 6), Position(1, 0), Position(0, 7), Position(2, 7)),

            Position(2, 0) to listOf(Position(2, 1), Position(2, 7)),
            Position(2, 1) to listOf(Position(2, 0), Position(2, 2), Position(1, 1)),
            Position(2, 2) to listOf(Position(2, 1), Position(2, 3)),
            Position(2, 3) to listOf(Position(2, 2), Position(2, 4), Position(1, 3)),
            Position(2, 4) to listOf(Position(2, 3), Position(2, 5)),
            Position(2, 5) to listOf(Position(2, 4), Position(2, 6), Position(1, 5)),
            Position(2, 6) to listOf(Position(2, 5), Position(2, 7)),
            Position(2, 7) to listOf(Position(2, 6), Position(2, 0), Position(1, 7))
        )
    }

    fun isValidMove(state: GameState, from: Position, to: Position): Boolean {
        return when (state.status) {
            GameStatus.PLACE -> isValidPlacement(state, to)
            GameStatus.MOVE -> isValidMovement(state, from, to)
            GameStatus.REMOVE -> isValidRemoval(state, to)
            GameStatus.FINISHED -> false
        }
    }

    private fun isValidPlacement(gameState: GameState, to: Position): Boolean {
        return !gameState.isOccupied(to) && 
               gameState.piecesToPlace[gameState.currentPlayer]!! > 0
    }
    
    private fun isValidMovement(gameState: GameState, from: Position?, to: Position): Boolean {
        if (from == null || gameState.isOccupied(to)) return false
        
        val playerAtFrom = gameState.pieceAt(from)?.player
        if (playerAtFrom != gameState.currentPlayer) return false
        
        val playerPieceCount = gameState.numPieces(gameState.currentPlayer)
        if (playerPieceCount == 3) {
            return true
        }
        
        return possibleMoves[from]?.contains(to) == true
    }
    
    private fun isValidRemoval(gameState: GameState, to: Position): Boolean {
        val playerAt = gameState.pieceAt(to)?.player
        if (playerAt != gameState.currentPlayer.opponent) return false
        
        if (isPartOfMill(gameState, to, playerAt)) {
            val opponentPositions = gameState.pieces(gameState.currentPlayer.opponent)
            return opponentPositions.all {
                isPartOfMill(gameState, it.position, gameState.currentPlayer.opponent)
            }
        }
        return true
    }

    private fun isPartOfMill(gameState: GameState, position: Position, player: Player): Boolean {
        return possibleMills.any { mill ->
            mill.contains(position) && mill.all { pos ->
                gameState.pieceAt(pos)?.player == player
            }
        }
    }
}
