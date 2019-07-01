package com.wix.detox.espresso.scroll

import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import java.lang.Exception
import java.lang.RuntimeException

class DetoxSwiperTest {
    private val defaultStartX = 100f
    private val defaultStartY = 200f
    private val defaultEndX = 110f
    private val defaultEndY = 220f

    private lateinit var swipeExecutor: SwipeExecutor

    @Before fun setUp() {
        swipeExecutor = mock()
    }

    @Test fun `should generate swipe orchestrator`() {
        val swipeExecutorProvider: (perMotionTime: Int) -> SwipeExecutor = mock {
            onGeneric { invoke(any()) }.doReturn(swipeExecutor)
        }
        val swipeDuration = 500
        val motionCount = 10
        val expectedPerMotionTime = 50 // i.e. swipeDuration / motionsCount

        val uut = DetoxSwiper(0f, 0f, swipeDuration, motionCount, swipeExecutorProvider)
        uut.perform(1f, 1f)

        verify(swipeExecutorProvider)(expectedPerMotionTime)
    }

    @Test fun `should start at coordinates`() {
        uut().perform(defaultEndX, defaultEndY)
        verify(swipeExecutor).startAt(defaultStartX, defaultStartY)
    }

    @Test fun `should move once`() {
        uut().perform(defaultEndX, defaultEndY)
        verify(swipeExecutor).moveTo(defaultEndX, defaultEndY)
        verify(swipeExecutor, times(1)).moveTo(any(), any())
    }

    @Test fun `should finish`() {
        uut().perform(defaultEndX, defaultEndY)
        verify(swipeExecutor).finishAt(defaultEndX, defaultEndY)
    }

    @Test fun `should finish even if motion fails`() {
        whenever(swipeExecutor.moveTo(any(), any())).doThrow(RuntimeException())

        try {
            uut().perform(defaultEndX, defaultEndY)
        } catch (e: Exception) {
        }

        verify(swipeExecutor).finishAt(defaultEndX, defaultEndY)
    }

    @Test fun `should move in substeps`() {
        val motionCount = 2
        uut(motionCount).perform(defaultEndX, defaultEndY)

        verify(swipeExecutor, times(2)).moveTo(any(), any())
    }

    private fun uut(motionCount: Int = 1): DetoxSwiper = DetoxSwiper(defaultStartX, defaultStartY, 100, motionCount) { swipeExecutor }
}
