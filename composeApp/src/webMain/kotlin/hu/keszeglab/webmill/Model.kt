package hu.keszeglab.webmill

enum class Player {
    DARK, LIGHT;
    val opponent: Player get() = if (this == DARK) LIGHT else DARK
}

enum class GameStatus {
    PLACE, MOVE, REMOVE, FINISHED;
}

data class Position(val circle: Int, val index: Int) {
    companion object {
        val all: List<Position> = List(24) { i ->
            Position(i / 8, i % 8)
        }
    }
}

data class Piece(val player: Player, val position: Position)

data class GameState(
    val pieces: List<Piece> = emptyList(),
    val currentPlayer: Player = Player.DARK,
    val status: GameStatus = GameStatus.PLACE,
    val selectedPosition: Position? = null,
    val piecesToPlace: Map<Player, Int> = mapOf(Player.DARK to TOTAL_PIECES_PER_PLAYER, Player.LIGHT to TOTAL_PIECES_PER_PLAYER),
    val winner: Player? = null,
    val mills: Set<List<Position>> = emptySet(),
    val validMoves: Set<Position> = emptySet()
) {
    companion object {
        const val TOTAL_PIECES_PER_PLAYER = 9
    }

    fun pieceAt(position: Position): Piece? {
        return pieces.find { it.position == position }
    }

    fun isOccupied(position: Position): Boolean {
        return pieceAt(position) != null
    }

    fun numPieces(player: Player): Int {
        return pieces.count { it.player == player }
    }

    fun pieces(player: Player): List<Piece> {
        return pieces.filter { it.player == player }
    }
}
