package com.aar.app.wsp

import com.aar.app.wsp.commons.generator.StringListGridGenerator
import com.aar.app.wsp.model.Word
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Words are filtered with LENGTH(string) <= gridSize. A word exactly as long as
 * the grid must still be placeable, otherwise the inclusive filter would offer
 * words the generator drops.
 */
class GridFitTest {

    @Test
    fun wordAsLongAsGridIsPlaced() {
        for (dim in 4..12) {
            repeat(20) {
                val word = Word(1, 1, "A".repeat(dim))
                val grid = Array(dim) { CharArray(dim) }
                val used = StringListGridGenerator().setGrid(listOf(word), grid)
                assertEquals("dim=$dim", 1, used.size)
            }
        }
    }
}
