package hu.keszeglab.webmill.model

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

    fun isValidMove(state: GameState, from: Position?, to: Position): Boolean {
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

    fun makeMove(gameState: GameState, from: Position?, to: Position): GameState {
        if (!isValidMove(gameState, from, to)) {
            return gameState
        }

        val newPieces = gameState.pieces.toMutableList()
        val newPiecesToPlace = gameState.piecesToPlace.toMutableMap()

        when (gameState.status) {
            GameStatus.PLACE -> {
                newPieces.add(Piece(gameState.currentPlayer, to))
                newPiecesToPlace[gameState.currentPlayer] = newPiecesToPlace[gameState.currentPlayer]!! - 1
            }
            GameStatus.MOVE -> {
                if (from != null) {
                    val pieceIndex = newPieces.indexOfFirst { it.position == from }
                    if (pieceIndex >= 0) {
                        val piece = newPieces[pieceIndex]
                        newPieces.removeAt(pieceIndex)
                        newPieces.add(Piece(piece.player, to))
                    }
                }
            }
            GameStatus.REMOVE -> {
                val pieceIndex = newPieces.indexOfFirst { it.position == from }
                if (pieceIndex >= 0) {
                    newPieces.removeAt(pieceIndex)
                }
            }
            GameStatus.FINISHED -> {
                return gameState
            }
        }

        val newGameState = gameState.copy(
            pieces = newPieces,
            piecesToPlace = newPiecesToPlace,
            selectedPosition = null,
            validMoves = emptySet()
        )

        return updateGameStatus(newGameState)
    }

    private fun getMillsForPlayer(gameState: GameState, player: Player): List<List<Position>> {
        return possibleMills.filter { mill ->
            mill.all { pos -> gameState.pieceAt(pos)!!.player == player }
        }
    }

    private fun updateGameStatus(gameState: GameState): GameState {
        val millsFormed = getMillsForPlayer(gameState, gameState.currentPlayer)
        val newMills = millsFormed.filter { mill ->
            !gameState.mills.any() { existingMill ->
                existingMill.toSet() == mill.toSet()
            }
        }

        return when {
            isGameOver(gameState) -> {
                gameState.copy(
                    status = GameStatus.FINISHED,
                    winner = getWinner(gameState)
                )
            }
            newMills.isNotEmpty() && gameState.status != GameStatus.REMOVE -> {
                gameState.copy(
                    status = GameStatus.REMOVE,
                    mills = gameState.mills + newMills.map { it }
                )
            }
            gameState.status == GameStatus.REMOVE -> {
                val allPlaced = gameState.piecesToPlace.values.all { it == 0 }
                gameState.copy(
                    status = if (allPlaced) GameStatus.MOVE else GameStatus.PLACE,
                    currentPlayer = gameState.currentPlayer.opponent
                )
            }
            else -> {
                val allPlaced = gameState.piecesToPlace.values.all { it == 0 }
                gameState.copy(
                    status = if (allPlaced) GameStatus.MOVE else GameStatus.PLACE,
                    currentPlayer = gameState.currentPlayer.opponent
                )
            }
        }
    }

    fun getValidMoves(gameState: GameState, selectedPosition: Position?): Set<Position> {
        return when (gameState.status) {
            GameStatus.PLACE -> {
                Position.all.filter { !gameState.isOccupied(it) }.toSet()
            }
            GameStatus.MOVE -> {
                if (selectedPosition == null) return emptySet()
                val playerPieceCount = gameState.numPieces(gameState.currentPlayer)

                if (playerPieceCount == 3) {
                    Position.all.filter { !gameState.isOccupied(it) }.toSet()
                }
                else {
                    possibleMoves[selectedPosition]?.filter {
                        !gameState.isOccupied(it)
                    }?.toSet() ?: emptySet()
                }
            }
            GameStatus.REMOVE -> {
                gameState.pieces(gameState.currentPlayer.opponent)
                    .filter { isValidRemoval(gameState, it.position) }
                    .map { it.position }
                    .toSet()
            }
            GameStatus.FINISHED -> emptySet()
        }
    }

    private fun isGameOver(gameState: GameState): Boolean {
        val darkPieces = gameState.numPieces(Player.DARK)
        val lightPieces = gameState.numPieces(Player.LIGHT)

        if ((gameState.piecesToPlace[Player.DARK]!! <= 0 && darkPieces <= 2) ||
            (gameState.piecesToPlace[Player.LIGHT]!! <= 0 && lightPieces <= 2)) {
            return true
        }

        if (gameState.status == GameStatus.MOVE) {
            val playerPosition = gameState.pieces(gameState.currentPlayer)
            val canMove = playerPosition.any { piece ->
                getValidMoves(gameState.copy(selectedPosition = piece.position), piece.position).isNotEmpty()
            }
            if (!canMove) {
                return true
            }
        }
        return false
    }

    private fun getWinner(gameState: GameState): Player? {
        val lightPieces = gameState.numPieces(Player.LIGHT)
        val darkPieces = gameState.numPieces(Player.DARK)

        return when {
            lightPieces < 3 && gameState.piecesToPlace[Player.LIGHT]!! <= 0 -> Player.DARK
            darkPieces < 3 && gameState.piecesToPlace[Player.DARK]!! <= 0 -> Player.LIGHT
            gameState.status == GameStatus.MOVE -> gameState.currentPlayer.opponent
            else -> null
        }
    }
}
