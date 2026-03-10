package com.xoyz.game.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * Verifies the structural correctness of [WinningLinesTable].
 *
 * These tests prove the lookup table is:
 *  - Exactly 49 lines long
 *  - Every line has exactly 3 distinct indices in range 0..26
 *  - No duplicate lines exist
 *  - Each category contributes the expected count
 */
class WinningLinesTableTest {

    // ─── Count ────────────────────────────────────────────────────────────────

    @Test fun `table contains exactly 49 lines`() {
        assertEquals(49, WinningLinesTable.ALL_LINES.size)
    }

    @Test fun `bitmask array matches line array size`() {
        assertEquals(WinningLinesTable.ALL_LINES.size, WinningLinesTable.ALL_MASKS.size)
    }

    // ─── Individual line integrity ────────────────────────────────────────────

    @Test fun `every line has exactly 3 cells`() {
        WinningLinesTable.ALL_LINES.forEachIndexed { i, line ->
            assertEquals("Line $i should have 3 cells", 3, line.size)
        }
    }

    @Test fun `every cell index is within 0 to 26`() {
        WinningLinesTable.ALL_LINES.forEach { line ->
            line.forEach { idx ->
                assertTrue("Index $idx out of range", idx in 0..26)
            }
        }
    }

    @Test fun `every line has 3 distinct indices`() {
        WinningLinesTable.ALL_LINES.forEachIndexed { i, line ->
            val distinct = line.toSet()
            assertEquals("Line $i has duplicate indices: ${line.toList()}", 3, distinct.size)
        }
    }

    // ─── No duplicates ────────────────────────────────────────────────────────

    @Test fun `no two lines are identical`() {
        val normalised = WinningLinesTable.ALL_LINES.map { it.sorted() }
        val unique = normalised.toSet()
        assertEquals(
            "Found ${normalised.size - unique.size} duplicate line(s)",
            normalised.size,
            unique.size
        )
    }

    // ─── Category counts ──────────────────────────────────────────────────────

    /**
     * Validates category counts by reconstructing each category and comparing
     * with what the table actually contains (as bitmasks).
     */
    @Test fun `layer rows contribute 9 lines`() {
        val rowLines = mutableListOf<Set<Int>>()
        for (layer in 0..2) for (row in 0..2) {
            rowLines += setOf(layer*9+row*3+0, layer*9+row*3+1, layer*9+row*3+2)
        }
        assertEquals(9, rowLines.size)
        assertAllPresentInTable(rowLines)
    }

    @Test fun `layer columns contribute 9 lines`() {
        val colLines = mutableListOf<Set<Int>>()
        for (layer in 0..2) for (col in 0..2) {
            colLines += setOf(layer*9+0*3+col, layer*9+1*3+col, layer*9+2*3+col)
        }
        assertEquals(9, colLines.size)
        assertAllPresentInTable(colLines)
    }

    @Test fun `layer diagonals contribute 6 lines`() {
        val diagLines = mutableListOf<Set<Int>>()
        for (layer in 0..2) {
            diagLines += setOf(layer*9+0, layer*9+4, layer*9+8)  // top-left → bottom-right
            diagLines += setOf(layer*9+2, layer*9+4, layer*9+6)  // top-right → bottom-left
        }
        assertEquals(6, diagLines.size)
        assertAllPresentInTable(diagLines)
    }

    @Test fun `vertical columns contribute 9 lines`() {
        val vertLines = mutableListOf<Set<Int>>()
        for (row in 0..2) for (col in 0..2) {
            vertLines += setOf(0*9+row*3+col, 1*9+row*3+col, 2*9+row*3+col)
        }
        assertEquals(9, vertLines.size)
        assertAllPresentInTable(vertLines)
    }

    @Test fun `space diagonals contribute 4 lines`() {
        val spaceDiags = listOf(
            setOf(idx(0,0,0), idx(1,1,1), idx(2,2,2)),
            setOf(idx(0,0,2), idx(1,1,1), idx(2,2,0)),
            setOf(idx(0,2,0), idx(1,1,1), idx(2,0,2)),
            setOf(idx(0,2,2), idx(1,1,1), idx(2,0,0))
        )
        assertEquals(4, spaceDiags.size)
        assertAllPresentInTable(spaceDiags)
    }

    // ─── Bitmask integrity ────────────────────────────────────────────────────

    @Test fun `each bitmask has exactly 3 bits set`() {
        WinningLinesTable.ALL_MASKS.forEachIndexed { i, mask ->
            assertEquals("Mask $i should have 3 bits, got ${Integer.bitCount(mask)}",
                3, Integer.bitCount(mask))
        }
    }

    @Test fun `bitmask matches its corresponding line`() {
        WinningLinesTable.ALL_LINES.forEachIndexed { i, line ->
            val expected = line.fold(0) { acc, idx -> acc or (1 shl idx) }
            assertEquals("Mask $i mismatch", expected, WinningLinesTable.ALL_MASKS[i])
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun idx(layer: Int, row: Int, col: Int) = layer * 9 + row * 3 + col

    private fun assertAllPresentInTable(expected: List<Set<Int>>) {
        val tableAsSets = WinningLinesTable.ALL_LINES.map { it.toSet() }.toSet()
        expected.forEach { line ->
            assertTrue("Line $line not found in table", line in tableAsSets)
        }
    }
}