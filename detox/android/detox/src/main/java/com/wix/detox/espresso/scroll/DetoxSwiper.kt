package com.wix.detox.espresso.scroll

class DetoxSwiper(
        private val startX: Float,
        private val startY: Float,
        private val swipeDuration: Int,
        private val motionCount: Int,
        private val swipeOrchestratorProvider: (perMotionTime: Int) -> SwipeExecutor) {

    fun perform(endX: Float, endY: Float) {
        with(swipeOrchestratorProvider(swipeDuration / motionCount)) {
            startAt(startX, startY)

            try {
                for (step in 1..motionCount) {
                    moveTo(endX, endY)
                }
            } finally {
                finishAt(endX, endY)
            }
        }
    }
}
