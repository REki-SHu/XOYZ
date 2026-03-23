//package com.xoyz.game.core
//
//import com.xoyz.game.utils.BoardConstants
//
///**
// * Immutable snapshot of the XOYZ 3×3×3 board state.
// *
// * The board is stored internally as a 27-element flat array for
// * cache-friendly access and bitboard-style win checking.
// *
// * All mutating operations return a *new* [Board] instance, keeping
// * each snapshot safe to cache, compare, or pass to tests.
// */
//class Board private constructor(
//    private val cells: Array<CellState>
//) {
//
//    init {
//        require(cells.size == BoardConstants.TOTAL_CELLS) {
//            "Board requires exactly ${BoardConstants.TOTAL_CELLS} cells, got ${cells.size}"
//        }
//    }
//
//    // ─── Accessors ────────────────────────────────────────────────────────────
//
//    /** Returns the [CellState] at the given [position]. */
//    fun getCell(position: BoardPosition): CellState = cells[position.flatIndex]
//
//    /** Returns the [CellState] at the given flat [index] (0..26). */
//    fun getCell(index: Int): CellState {
//        require(index in 0 until BoardConstants.TOTAL_CELLS) {
//            "Cell index must be 0..26, was $index"
//        }
//        return cells[index]
//    }
//
//    /** Returns true if the cell at [position] is empty. */
//    fun isEmpty(position: BoardPosition): Boolean =
//        getCell(position) == CellState.EMPTY
//
//    /** Returns true if every cell is occupied. */
//    val isFull: Boolean get() = cells.none { it == CellState.EMPTY }
//
//    /** Returns a list of all empty positions. */
//    fun emptyCells(): List<BoardPosition> =
//        cells.indices
//            .filter { cells[it] == CellState.EMPTY }
//            .map { BoardPosition.fromFlatIndex(it) }
//
//    // ─── Mutation (returns new Board) ─────────────────────────────────────────
//
//    /**
//     * Returns a new [Board] with the cell at [position] set to [state].
//     * @throws IllegalArgumentException if the cell is not empty.
//     */
//    fun withMove(position: BoardPosition, state: CellState): Board {
//        require(state != CellState.EMPTY) { "Cannot place EMPTY via withMove; use clear()" }
//        require(isEmpty(position))        { "Cell $position is already occupied" }
//        val newCells = cells.copyOf()
//        newCells[position.flatIndex] = state
//        return Board(newCells)
//    }
//
//    // ─── Bitboard helpers ─────────────────────────────────────────────────────
//
//    /**
//     * Builds a bitmask (Int) where bit *i* is set when [predicate] is true
//     * for cell *i*.  Used by the rules engine for O(49) bitwise win-checking.
//     */
//    fun buildMask(predicate: (CellState) -> Boolean): Int =
//        cells.foldIndexed(0) { i, acc, state ->
//            if (predicate(state)) acc or (1 shl i) else acc
//        }
//
//    // ─── Equality & display ───────────────────────────────────────────────────
//
//    override fun equals(other: Any?): Boolean =
//        other is Board && cells.contentEquals(other.cells)
//
//    override fun hashCode(): Int = cells.contentHashCode()
//
//    /**
//     * Returns a human-readable multi-line representation showing all three layers.
//     * Useful for console debugging and test failure messages.
//     */
//    override fun toString(): String = buildString {
//        for (layer in 0 until BoardConstants.LAYERS) {
//            appendLine("Layer $layer:")
//            for (row in 0 until BoardConstants.ROWS) {
//                for (col in 0 until BoardConstants.COLS) {
//                    append(cells[layer * 9 + row * 3 + col].displayChar)
//                    if (col < 2) append("|")
//                }
//                appendLine()
//            }
//            if (layer < 2) appendLine("---")
//        }
//    }
//
//    // ─── Factory ──────────────────────────────────────────────────────────────
//
//    companion object {
//        /** Creates an empty 27-cell board. */
//        fun empty(): Board = Board(Array(BoardConstants.TOTAL_CELLS) { CellState.EMPTY })
//
//        /**
//         * Creates a [Board] from a flat array of exactly 27 [CellState] values.
//         * Useful for unit tests and deserialisation.
//         */
//        fun fromArray(cells: Array<CellState>): Board = Board(cells.copyOf())
//    }
//}

package com.xoyz.game.core

class Board {
    // Flat 1D array representing 3×3×3 = 27 cells
    // Index formula: layer * 9 + row * 3 + col
    private val cells = Array(27) { CellState.EMPTY }

    fun getCell(layer: Int, row: Int, col: Int): CellState =
        cells[index(layer, row, col)]

    fun setCell(layer: Int, row: Int, col: Int, state: CellState) {
        cells[index(layer, row, col)] = state
    }

    fun isCellEmpty(layer: Int, row: Int, col: Int): Boolean =
        getCell(layer, row, col) == CellState.EMPTY

    fun isFull(): Boolean = cells.none { it == CellState.EMPTY }

    fun reset() = cells.fill(CellState.EMPTY)

    // Convert 3D coords to flat index
    private fun index(layer: Int, row: Int, col: Int): Int = layer * 9 + row * 3 + col

    // Expose flat array for bitboard / win checking
    fun getFlat(): Array<CellState> = cells.copyOf()
}