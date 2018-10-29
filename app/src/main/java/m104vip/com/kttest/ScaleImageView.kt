package m104vip.com.kttest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.view.MotionEvent
import android.graphics.PointF
import android.util.Log
import android.view.ViewTreeObserver


class ScaleImageView @JvmOverloads constructor(context: Context,attrs: AttributeSet? = null,defStyleAttr: Int = 0) : ImageView(context,attrs,defStyleAttr),View.OnTouchListener {
    private val support_touch = true//是否啟動觸碰事件

    private var mode = 0// 初始值
    internal var total_scale = MIN_SCALE
    internal var current_scale: Float = 0.toFloat()//total_scale縮放範圍2-1，當小於1回彈到1,當大於2回彈到2

    private val matrixNow = Matrix()
    val beforeImageMatrix = Matrix()
    private val mInitializationMatrix = Matrix()//初始縮放值

    private val actionDownPoint = PointF()//點擊位置
    private val dragPoint = PointF()//移動位置
    private val startPoint = PointF()//滑動位置
    private val mInitializationScalePoint = PointF()//初始縮放位置
    private val mCurrentScalePoint = PointF(0f,0f)//目前縮放位置
    private var startDis: Float = 0.toFloat()//滑動開始距離
    /** 雙指距離  */
    private var midPoint = PointF(0f,0f)
    val initializationBitmapHeight: Float
        get() = height * total_scale
    val initializationBitmapWidth: Float
        get() = width * total_scale

    init {
        initData()
    }

    private fun initData() {
        if (support_touch) {
            setOnTouchListener(this)
        }
        this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                this@ScaleImageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val viewLocation = IntArray(2)
                this@ScaleImageView.getLocationInWindow(viewLocation)
                val viewX = viewLocation[0] // x座標
                val viewY = viewLocation[1] // y座標
                mInitializationScalePoint.set((this@ScaleImageView.width / 2).toFloat(),(viewY + this@ScaleImageView.height / 2).toFloat())//初始縮放位置
                Log.i("yangxun","寬：" + mInitializationScalePoint.x + "高：" + mInitializationScalePoint.y)
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    protected override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)//清空
        if (matrixNow!=null) {
            canvas.concat(matrixNow)
        }
        super.onDraw(canvas)
    }

    override fun setImageMatrix(matrix: Matrix) {
        matrixNow.set(matrix)
        invalidate()
    }


    fun resetImageMatrix() {
        this.matrixNow.set(mInitializationMatrix)
        invalidate()
    }

    //最小重置數據
    private fun resetToMinStatus() {
        mCurrentScalePoint.set(0f,0f)
        total_scale = MIN_SCALE
    }

    //最大重置數據
    private fun resetToMaxStatus() {
        total_scale = MAX_SCALE
    }

    override fun onTouch(v: View,event: MotionEvent): Boolean {
        if (total_scale!=1f) {
            parent.requestDisallowInterceptTouchEvent(true)//觸碰事件
        }
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN//單指觸碰
            -> {
                mode = MODE_DRAG
                beforeImageMatrix.set(imageMatrix)
                matrixNow.set(imageMatrix)
                dragPoint.set(event.x,event.y)
                actionDownPoint.set(event.x,event.y)
            }

            MotionEvent.ACTION_POINTER_DOWN//雙指觸碰
            -> {
                parent.requestDisallowInterceptTouchEvent(true)//觸碰監聽
                mode = MODE_ZOOM
                startPoint.set(event.x,event.y)
                startDis = distance(event)
                /** 計算雙指距離  */
                if (startDis > 10f) {
                    //記錄縮放倍數
                    beforeImageMatrix.set(imageMatrix)
                    matrixNow.set(imageMatrix)
                }
            }

            MotionEvent.ACTION_MOVE -> if (mode==MODE_DRAG && total_scale > 1) {
                val dx = event.x - dragPoint.x
                val dy = event.y - dragPoint.y
                dragPoint.set(event.x,event.y)
                imgTransport(dx,dy)
            } else if (mode==MODE_ZOOM) {//縮放
                val endDis = distance(event)
                midPoint = mid(event)
                if (endDis > 10f) {
                    current_scale = endDis / startDis//縮放倍數
                    total_scale *= current_scale
                    matrixNow.postScale(current_scale,current_scale,midPoint.x,midPoint.y)
                    invalidate()
                }
                startDis = endDis
            }

            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)//取消觸碰事件監聽
                mode = 0
                if (mode==MODE_DRAG)
                    checkClick(event.x,event.y,actionDownPoint.x,actionDownPoint.y)
            }

            MotionEvent.ACTION_POINTER_UP -> {
                checkZoomValid()
                mode = 0
            }
        }
        return true
    }

    /**
     * 移動圖片
     * @param x
     * @param y
     */
    fun imgTransport(x: Float,y: Float) {
        var x = x
        var y = y
        mCurrentScalePoint.set(mCurrentScalePoint.x + x,mCurrentScalePoint.y + y)
        if (mCurrentScalePoint.x >= (total_scale - 1) * width / 2) {
            mCurrentScalePoint.x = (total_scale - 1) * width / 2
            x = 0f
        } else {
            if (mCurrentScalePoint.x <= -((total_scale - 1) * width) / 2) {
                mCurrentScalePoint.x = -((total_scale - 1) * width) / 2
                x = 0f
            }
        }
        if (mCurrentScalePoint.y >= (total_scale - 1) * height / 2) {
            mCurrentScalePoint.y = (total_scale - 1) * height / 2
            y = 0f
        } else {
            if (mCurrentScalePoint.y <= -((total_scale - 1) * height) / 2) {
                mCurrentScalePoint.y = -((total_scale - 1) * height) / 2
                y = 0f
            }
        }
        Log.i(TAG,"mCurrentScalePoint.x:" + mCurrentScalePoint.x + "   x:" + x)
        matrixNow.postTranslate(x,y)
        invalidate()
    }

    private fun checkZoomValid(): Boolean {
        if (mode==MODE_ZOOM) {
            if (total_scale > MAX_SCALE) {
                resetToMaxStatus()
                matrixNow.set(mInitializationMatrix)
                matrixNow.postScale(MAX_SCALE,MAX_SCALE,mInitializationScalePoint.x,mInitializationScalePoint.y)
                matrixNow.postTranslate(mCurrentScalePoint.x,mCurrentScalePoint.y)
                invalidate()
                return false
            } else if (total_scale < MIN_SCALE) {
                resetToMinStatus()
                matrixNow.set(mInitializationMatrix)
                invalidate()
                return false
            }
            invalidate()
        }
        return true
    }

    private fun distance(event: MotionEvent): Float {
        val dx = event.getX(1) - event.getX(0)
        val dy = event.getY(1) - event.getY(0)
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    private fun mid(event: MotionEvent): PointF {
        val midX = (event.getX(1) + event.getX(0)) / 2
        val midY = (event.getY(1) + event.getY(0)) / 2
        return PointF(midX,midY)
    }

    internal fun checkClick(last_x: Float,last_y: Float,now_x: Float,now_y: Float): Boolean {
        val x_d = Math.abs(last_x - now_x)
        val y_d = Math.abs(last_y - now_y)
        if (x_d < 10 && y_d < 10) {//點擊事件
            //點擊事件處理
        }
        if (total_scale==1f) {
            matrixNow.set(mInitializationMatrix)
            invalidate()
        }
        return false
    }

    override fun getImageMatrix(): Matrix {
        return matrixNow
    }

    companion object {
        private val TAG = ScaleImageView::class.java.simpleName
        private val MODE_DRAG = 1//平移
        private val MODE_ZOOM = 2//縮放

        private val MAX_SCALE = 4f//最大放大倍數
        private val MIN_SCALE = 1f//最小放大倍數
    }
}
