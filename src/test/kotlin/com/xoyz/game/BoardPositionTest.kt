package com.xoyz.game.core

import org.junit.Assert.*
import org.junit.Test

class BoardPositionTest {

    @Test fun `flat index calculated correctly`() {
        assertEquals(0,  BoardPosition(0, 0, 0).flatIndex)
        assertEquals(4,  BoardPosition(0, 1, 1).flatIndex)   // 0*9 + 1*3 + 1
        assertEquals(13, BoardPosition(1, 1, 1).flatIndex)   // 1*9 + 1*3 + 1 (centre)
        assertEquals(26, BoardPosition(2, 2, 2).flatIndex)
    }

    @Test fun `fromFlatIndex round-trips`() {
        for (i in 0..26) {
            val pos = BoardPosition.fromFlatIndex(i)
            assertEquals(i, pos.flatIndex)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative layer throws`() { BoardPosition(-1, 0, 0) }

    @Test(expected = IllegalArgumentException::class)
    fun `layer out of bounds throws`() { BoardPosition(3, 0, 0) }

    @Test(expected = IllegalArgumentException::class)
    fun `flat index 27 throws`() { BoardPosition.fromFlatIndex(27) }

    @Test fun `data class equality works`() {
        assertEquals(BoardPosition(1, 2, 0), BoardPosition(1, 2, 0))
        assertNotEquals(BoardPosition(0, 0, 0), BoardPosition(0, 0, 1))
    }
}