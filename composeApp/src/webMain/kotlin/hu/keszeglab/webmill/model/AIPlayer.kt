package hu.keszeglab.webmill.model

import kotlin.random.Random

object AIPlayer {

    fun calculateComputerMove(gameState: GameState, smartness: Int): Pair<Position?, Position>? {
        if (Random.nextInt(100) >= smartness) {
            return getRandomMove(gameState)
        }
        return getBestMove(gameState)
    }

    private fun getRandomMove(gameState: GameState): Pair<Position?, Position>? {
        val logic = Logic()
        val validMoves = mutableListOf<Pair<Position?, Position>>()

        when (gameState.status) {
            GameStatus.PLACE -> {
                val availablePositions = Position.all.filter { !gameState.isOccupied(it) }
                if (availablePositions.isNotEmpty()) {
                    val randomPos = availablePositions.random()
                    return null to randomPos
                }
            }
            GameStatus.MOVE -> {
                val myPieces = gameState.pieces(gameState.currentPlayer)
                for (piece in myPieces) {
                    val moves = logic.getValidMoves(gameState.copy(selectedPosition = piece.position), piece.position)
                    moves.forEach { to ->
                        validMoves.add(piece.position to to)
                    }
                }
                if (validMoves.isNotEmpty()) {
                    return validMoves.random()
                }
            }
            GameStatus.REMOVE -> {
                val removablePieces = gameState.pieces(gameState.currentPlayer.opponent)
                    .filter { logic.isValidMove(gameState, null, it.position) }
                
                val validRemovals = gameState.pieces(gameState.currentPlayer.opponent).filter { 
                     logic.isValidMove(gameState, null, it.position)
                }
                
                if (validRemovals.isNotEmpty()) {
                    return null to validRemovals.random().position
                }
            }
            GameStatus.FINISHED -> return null
        }
        return null
    }

    private fun getBestMove(gameState: GameState): Pair<Position?, Position>? {
        val logic = Logic()
        
        when (gameState.status) {
            GameStatus.PLACE -> {
                val millMove = findMoveThatFormsMill(gameState, logic)
                if (millMove != null) return null to millMove

                val blockMove = findMoveThatBlocksOpponent(gameState, logic)
                if (blockMove != null) return null to blockMove
                
                return getRandomMove(gameState)
            }
            GameStatus.MOVE -> {
                val myPieces = gameState.pieces(gameState.currentPlayer)
                for (piece in myPieces) {
                    val moves = logic.getValidMoves(gameState.copy(selectedPosition = piece.position), piece.position)
                    for (to in moves) {
                        val nextState = logic.makeMove(gameState, piece.position, to)
                        if (nextState.status == GameStatus.REMOVE || nextState.status == GameStatus.FINISHED) {
                            return piece.position to to
                        }
                    }
                }
                
                return getRandomMove(gameState)
            }
            GameStatus.REMOVE -> {
                val opponentPieces = gameState.pieces(gameState.currentPlayer.opponent)
                val validRemovals = opponentPieces.filter { logic.isValidMove(gameState, null, it.position) }
                
                if (validRemovals.isNotEmpty()) {
                    return null to validRemovals.random().position
                }
            }
            GameStatus.FINISHED -> return null
        }
        return getRandomMove(gameState)
    }

    private fun findMoveThatFormsMill(gameState: GameState, logic: Logic): Position? {
        val available = Position.all.filter { !gameState.isOccupied(it) }
        for (pos in available) {
            val tempPieces = gameState.pieces + Piece(gameState.currentPlayer, pos)
            val tempState = gameState.copy(pieces = tempPieces)
            val nextState = logic.makeMove(gameState, null, pos)
            if (nextState.status == GameStatus.REMOVE || nextState.status == GameStatus.FINISHED) {
                return pos
            }
        }
        return null
    }

    private fun findMoveThatBlocksOpponent(gameState: GameState, logic: Logic): Position? {
        val opponent = gameState.currentPlayer.opponent
        val available = Position.all.filter { !gameState.isOccupied(it) }
        
        for (pos in available) {
            val tempPieces = gameState.pieces + Piece(opponent, pos)
            val tempState = gameState.copy(pieces = tempPieces, currentPlayer = opponent)
            
            val hypotheticalState = gameState.copy(currentPlayer = opponent)
            val nextState = logic.makeMove(hypotheticalState, null, pos)
            
            if (nextState.status == GameStatus.REMOVE || nextState.status == GameStatus.FINISHED) {
                return pos
            }
        }
        return null
    }
}
