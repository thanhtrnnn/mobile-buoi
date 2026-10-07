package ptit.cnpm1.tranxuanthanh.bai08

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

/** Xử lý chạm và vuốt ngang trên ô lịch. */
fun View.setCalendarGestures(onTap: () -> Unit, onWeekOrMonthSwipe: (Int) -> Unit) {
    isClickable = true
    isFocusable = true
    setOnClickListener { onTap() }

    val minDistance = (72 * resources.displayMetrics.density).toInt()
    val detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean = true

        override fun onSingleTapUp(event: MotionEvent): Boolean {
            performClick()
            return true
        }

        override fun onFling(
            start: MotionEvent?,
            end: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (start == null) return false
            val distanceX = end.x - start.x
            val distanceY = end.y - start.y
            if (abs(distanceX) < minDistance || abs(distanceX) < abs(distanceY) * 1.25f) {
                return false
            }
            onWeekOrMonthSwipe(if (distanceX < 0) 1 else -1)
            return true
        }
    })

    setOnTouchListener { _, event -> detector.onTouchEvent(event) }
}
