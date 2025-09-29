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
            Position(0, 0) to listOf(),
            Position(0, 1) to listOf(),
            Position(0, 2) to listOf(),
            Position(0, 3) to listOf(),
            Position(0, 4) to listOf(),
            Position(0, 5) to listOf(),
            Position(0, 6) to listOf(),
            Position(0, 7) to listOf(),

            Position(1, 0) to listOf(),
            Position(1, 1) to listOf(),
            Position(1, 2) to listOf(),
            Position(1, 3) to listOf(),
            Position(1, 4) to listOf(),
            Position(1, 5) to listOf(),
            Position(1, 6) to listOf(),
            Position(1, 7) to listOf()

            Position(2, 0) to listOf(),
            Position(2, 1) to listOf(),
            Position(2, 2) to listOf(),
            Position(2, 3) to listOf(),
            Position(2, 4) to listOf(),
            Position(2, 5) to listOf(),
            Position(2, 6) to listOf(),
            Position(2, 7) to listOf()
        )
    }
}
