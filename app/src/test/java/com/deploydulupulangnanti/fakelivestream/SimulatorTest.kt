package com.deploydulupulangnanti.fakelivestream

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class SimulatorTest {
    @Test fun `long sessions keep viewers valid and bound chat memory`() {
        val simulator = Simulator(Random(7))
        val config = StudioConfig()
        var state = simulator.initial(config)
        repeat(100_000) {
            state = simulator.step(state, config)
            assertTrue(state.viewers in 1..999_999)
            assertTrue(state.peak >= state.viewers)
            assertTrue(state.chat.size <= 5)
        }
        assertEquals(100_000, state.seconds)
        assertEquals(100_000 / 9, state.gifts)
    }
    @Test fun `turning random gifts off prevents automatic gifts`() {
        val simulator = Simulator(Random(1))
        val config = StudioConfig(randomGifts = false)
        var state = simulator.initial(config)
        repeat(100) { state = simulator.step(state, config) }
        assertEquals(0, state.gifts)
        assertNull(state.gift)
        state = simulator.sendGift(state, giftCatalog.first())
        assertEquals(1, state.gifts)
        assertEquals("spark", state.gift?.gift?.id)
    }
    @Test fun `comments ignore whitespace trim and enforce size`() {
        val simulator = Simulator(Random(1))
        val initial = simulator.initial(StudioConfig())
        assertEquals(initial, simulator.comment(initial, "   "))
        val next = simulator.comment(initial, " x".repeat(200))
        assertEquals(160, next.chat.last().message.length)
        assertEquals("You", next.chat.last().name)
        assertEquals(1, next.comments)
    }
    @Test fun `crowd intensity changes the chat rate`() {
        fun comments(crowd: Crowd): Int {
            val simulator = Simulator(Random(3))
            val config = StudioConfig(crowd = crowd)
            var state = simulator.initial(config)
            repeat(30) { state = simulator.step(state, config) }
            return state.comments
        }
        assertEquals(6, comments(Crowd.COZY))
        assertEquals(15, comments(Crowd.SPOTLIGHT))
    }
}
