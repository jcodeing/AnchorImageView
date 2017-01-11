/*
 * MIT License
 *
 * Copyright (c) 2017 K Sun <jcodeing@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.jcodeing.anchorimageview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jcodeing.anchorimageview.Anchor;

import java.util.TreeMap;

public class AnchorImageView extends ImageView implements View.OnTouchListener {

    public int parentWidth;
    public int parentHeight;
    public float widthRatio;
    public float heightRatio;

    private Paint clickableStroke;
    private Paint clickFill;
    private Paint clickStroke;
    private Paint warnFill;
    private Paint warnStroke;

    private RectF rectAnchor;
    private RectF rectClickAnchor;
    public int drawRoundRectRadius;

    private Anchor anchor;
    private Anchor anchorPrevious;
    private TreeMap<Integer, Anchor> anchors;

    public boolean isShowClickableAnchor;
    public boolean isAnchorCutProcess;
    private boolean isWarnAnchor;

    private GestureDetector gestureDetector;


    public AnchorImageView(Context context) {
        this(context, null);
    }

    public AnchorImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnchorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawRoundRectRadius = dpToPx(5);
        setScaleType(ImageView.ScaleType.FIT_XY);

        clickableStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickableStroke.setStrokeWidth(1f);
        clickableStroke.setStyle(Paint.Style.STROKE);
        clickableStroke.setStrokeCap(Paint.Cap.ROUND);
        clickableStroke.setColor(Color.BLUE);

        clickFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickFill.setStyle(Paint.Style.FILL_AND_STROKE);
        clickFill.setStrokeCap(Paint.Cap.ROUND);
        clickFill.setColor(Color.BLUE);
        clickFill.setAlpha(70);

        clickStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        clickStroke.setStyle(Paint.Style.STROKE);
        clickStroke.setColor(Color.BLUE);
        clickStroke.setStrokeWidth(2f);
        clickStroke.setStrokeCap(Paint.Cap.ROUND);

        warnFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        warnFill.setStyle(Paint.Style.FILL_AND_STROKE);
        warnFill.setStrokeCap(Paint.Cap.ROUND);
        warnFill.setColor(Color.YELLOW);
        warnFill.setAlpha(70);

        warnStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        warnStroke.setStyle(Paint.Style.STROKE);
        warnStroke.setColor(Color.YELLOW);
        warnStroke.setStrokeWidth(2f);
        warnStroke.setStrokeCap(Paint.Cap.ROUND);

        setOnTouchListener(this);
        gestureDetector = new GestureDetector(getContext(), new OnGestureListenerAnchor(this));
    }

    @Override
    protected void onDetachedFromWindow() {
        this.resetData();
        super.onDetachedFromWindow();
    }

    public void resetData() {
        isShowClickableAnchor = false;
        anchors = null;
        anchor = null;
        anchorPrevious = null;
        if (rectClickAnchor != null)
            rectClickAnchor.setEmpty();

        if (rectAnchor != null)
            rectAnchor.setEmpty();

        onDrawAnchorListener = null;

        postInvalidate();
        isAnchorCutProcess = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        if (isShowClickableAnchor || onDrawAnchorListener != null) {
            if (rectAnchor == null)
                rectAnchor = new RectF();
            if (anchors != null) {
                for (int key : anchors.keySet()) {
                    Anchor anchor = anchors.get(key);
                    rectAnchor.set((float) anchor.left * widthRatio, (float) anchor.top * heightRatio, (float) anchor
                            .right * widthRatio, (float) anchor.bottom * heightRatio);
                    if (isShowClickableAnchor)
                        if (rectClickAnchor == null || !rectClickAnchor.contains(rectAnchor)) {
                            if (isWarnAnchor) {
                                canvas.drawRoundRect(rectAnchor, (float) drawRoundRectRadius, (float) drawRoundRectRadius, warnFill);
                                canvas.drawRoundRect(rectAnchor, (float) drawRoundRectRadius, (float) drawRoundRectRadius, warnStroke);
                            } else {
                                canvas.drawRoundRect(rectAnchor, (float) drawRoundRectRadius, (float) drawRoundRectRadius, clickableStroke);
                            }
                        }
                    if (onDrawAnchorListener != null)
                        onDrawAnchorListener.onDrawAnchor(anchor, rectAnchor, canvas);
                }
            }
        }

        if (rectClickAnchor != null && !rectClickAnchor.isEmpty()) {
            if (rectClickAnchor != null) {
                canvas.drawRoundRect(rectClickAnchor, ((float) drawRoundRectRadius), ((float) drawRoundRectRadius), this.clickFill);
                canvas.drawRoundRect(rectClickAnchor, ((float) drawRoundRectRadius), ((float) drawRoundRectRadius), this.clickStroke);
            }
        }

        canvas.restore();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    protected void disposeEvent(MotionEvent e) {
        if (anchors == null || anchors.size() == 0) {
            Toast.makeText(getContext(), "No Anchor", Toast.LENGTH_LONG).show();
        } else {
            float x = e.getX();
            float y = e.getY();
            if (anchors != null) {
                for (Integer key : anchors.keySet()) {
                    Anchor anchor = anchors.get(key);
                    if (new RectF(widthRatio * (float) anchor.left, heightRatio * (float) anchor.top,
                            widthRatio * (float) anchor.right, heightRatio * (float) anchor.bottom).contains(x, y)) {
                        // =========@Bingo@=========
                        this.anchor = anchor;

                        //clean WarnAnchor
                        if (isWarnAnchor) {
                            isWarnAnchor = false;
                            postInvalidate();
                        }
                        //on listener
                        if (onAnchorClickListener != null)
                            onAnchorClickListener.onAnchorClick(anchor, anchor.id, widthRatio, heightRatio);

                        return;
                    }
                }
                // =========@Cry@=========

                //current click show -> return
                /*if (rectClickAnchor != null && !rectClickAnchor.isEmpty())
                    return;*/

                //mark WarnAnchor
                if (isWarnAnchor)
                    return;
                isWarnAnchor = true;

                postInvalidate();
            }
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        setWidthHeightRatio((float) parentWidth * 1f / (float) bm.getWidth(), (float) parentHeight * 1f / (float) bm.getHeight());
        super.setImageBitmap(bm);
    }

    // ============================@Set/Get@============================
    public void setParentWidthHeight(int width, int height) {
        parentWidth = width;
        parentHeight = height;
    }

    public void setWidthHeightRatio(float widthRatio, float heightRatio) {
        this.widthRatio = widthRatio;
        this.heightRatio = heightRatio;
    }

    public void setCurrentClickAnchor(Anchor anchor) {
        if (anchor != null) {
            this.anchor = anchor;
            runAnchorCutProcess();
        }
    }

    public void setDefaultImageResId(int defaultImageResId) {
        setScaleType(ImageView.ScaleType.CENTER);
        setImageResource(defaultImageResId);
    }

    public void setIShowClickableAnchor(boolean iShowClickableAnchor) {
        this.isShowClickableAnchor = iShowClickableAnchor;
        postInvalidate();
    }

    public void setAnchors(TreeMap<Integer, Anchor> anchors) {
        this.anchors = anchors;
    }

    public int getAnchorSize() {
        return anchors == null ? 0 : anchors.size();
    }

    public TreeMap<Integer, Anchor> getAnchors() {
        return anchors;
    }

    public void cleanCurrentClickAnchor() {
        if (rectClickAnchor != null)
            rectClickAnchor.setEmpty();

        isWarnAnchor = false;
        postInvalidate();
    }


    // ============================@OnAnchorClickListener@============================
    public interface OnDrawAnchorListener {
        void onDrawAnchor(Anchor anchor, RectF rectAnchor, Canvas canvas);
    }

    private OnDrawAnchorListener onDrawAnchorListener;

    public void setOnDrawAnchorListener(OnDrawAnchorListener onDrawAnchorListener) {
        this.onDrawAnchorListener = onDrawAnchorListener;
    }

    // ============================@OnAnchorClickListener@============================
    public interface OnAnchorClickListener {
        void onAnchorClick(Anchor anchor, int id, float widthRatio, float heightRatio);
    }

    private OnAnchorClickListener onAnchorClickListener;

    public void setOnAnchorClickListener(OnAnchorClickListener onAnchorClickListener) {
        this.onAnchorClickListener = onAnchorClickListener;
    }


    // ============================@AnchorCutProcess@============================
    private AnchorCutProcess anchorCutProcess;

    private void stopAnchorCutProcess() {
        isAnchorCutProcess = false;
        if (rectClickAnchor != null)
            rectClickAnchor.setEmpty();

        postInvalidate();
    }

    private void runAnchorCutProcess() {
        if (anchorCutProcess != null) {
            anchorCutProcess.interrupt();
        }

        anchorCutProcess = new AnchorCutProcess(this);
        anchorCutProcess.start();
    }

    class AnchorCutProcess extends Thread {
        /**
         * difference
         */
        private final float leftD;
        private final float rightD;
        private final float topD;
        private final float bottomD;
        /**
         * difference move element
         */
        private final float leftDMovElement;
        private final float rightDMovElement;
        private final float topDMovElement;
        private final float bottomDMovElement;
        /**
         * Previous
         */
        private float leftP;
        private float topP;
        private float rightP;
        private float bottomP;
        /**
         * diffMovElementTotal:200
         * diffMovElementMultiple:10
         * diffMovElementMultipleIncrement:+=10 | 10~200 | RunTotal:20
         */
        private int diffMovElementMultipleIncrement;

        private AnchorImageView anchorImageView;

        AnchorCutProcess(AnchorImageView anchorImageView) {
            super();
            float diffMovElementTotal = 200f;
            this.anchorImageView = anchorImageView;

            isAnchorCutProcess = true;
            // =========@AnchorCutProcess Start@=========

            if (rectClickAnchor == null)
                rectClickAnchor = new RectF();

            if (anchorPrevious != null) {
                leftP = (float) anchorPrevious.left;
                rightP = (float) anchorPrevious.right;
                topP = (float) anchorPrevious.top;
                bottomP = (float) anchorPrevious.bottom;
            }

            leftD = (float) anchor.left - leftP;
            rightD = (float) anchor.right - rightP;
            topD = (float) anchor.top - topP;
            bottomD = (float) anchor.bottom - bottomP;

            rectClickAnchor.set(leftP * widthRatio, topP * heightRatio, rightP * widthRatio, bottomP * heightRatio);

            leftDMovElement = leftD * 1f / diffMovElementTotal;
            rightDMovElement = rightD * 1f / diffMovElementTotal;
            topDMovElement = topD * 1f / diffMovElementTotal;
            bottomDMovElement = bottomD * 1f / diffMovElementTotal;
        }

        public void run() {
            while (isAnchorCutProcess) {
                if (anchor == null)
                    return;

                diffMovElementMultipleIncrement += 10;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    anchorImageView.stopAnchorCutProcess();
                    return;
                }

                float leftR = leftP + leftDMovElement * (float) diffMovElementMultipleIncrement;
                float rightR = rightP + rightDMovElement * (float) diffMovElementMultipleIncrement;
                float topR = topP + topDMovElement * (float) diffMovElementMultipleIncrement;
                float bottomR = bottomP + bottomDMovElement * (float) diffMovElementMultipleIncrement;
                try {
                    if (Math.abs(leftR - (float) anchor.left) <= 2f)
                        leftR = (float) anchor.left;

                    if (Math.abs(rightR - (float) anchor.right) <= 2f)
                        rightR = (float) anchor.right;

                    if (Math.abs(topR - (float) anchor.top) <= 2f)
                        topR = (float) anchor.top;

                    if (Math.abs(bottomR - (float) anchor.bottom) <= 2f)
                        bottomR = (float) anchor.bottom;

                    rectClickAnchor.set(widthRatio * leftR, heightRatio * topR, widthRatio * rightR, heightRatio * bottomR);
                    anchorImageView.postInvalidate();

                    if (anchor == null) {
                        continue;
                    }

                    if (leftR != (float) anchor.left || topR != (float) anchor.top || rightR != (float) anchor.right || bottomR != (float) anchor.bottom)
                        continue;

                    isAnchorCutProcess = false;
                    // =========@AnchorCutProcess End@=========
                    anchorPrevious = anchor;
                    anchor = null;
                    return;
                } catch (Exception e) {
                    anchorImageView.stopAnchorCutProcess();
                }
            }
        }
    }


    // ------------------------------K------------------------------@Assist
    public int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}

