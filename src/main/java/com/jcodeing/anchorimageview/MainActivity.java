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

package com.jcodeing.anchorimageview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.jcodeing.anchorimageview.widget.AnchorImageView;

import java.util.TreeMap;


public class MainActivity extends AppCompatActivity implements AnchorImageView.OnAnchorClickListener, AnchorImageView.OnDrawAnchorListener {

    // =========@View@=========
    private View activityMain;
    private AnchorImageView anchorIv;

    // =========@Data@=========
    TreeMap<Integer, Anchor> markAnchors;
    AnchorIvOperationMode anchorIvOM = AnchorIvOperationMode.CLICK;
    private AudioLoader audioLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anchorIv = (AnchorImageView) findViewById(R.id.anchor_iv);
        anchorIv.setAnchors(TestUtils.getAnchors());
        anchorIv.setOnAnchorClickListener(this);
        anchorIv.setIShowClickableAnchor(true);
        anchorIv.setOnDrawAnchorListener(this);

        activityMain = findViewById(R.id.activity_main);
        activityMain.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                anchorIv.setParentWidthHeight(activityMain.getWidth(), activityMain.getHeight());
                anchorIv.setImageBitmap(TestUtils.getAnchorImage(MainActivity.this));
                activityMain.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        audioLoader = new AudioLoader(TestUtils.getAnchorAudio(this), TestUtils.getAudioParagraphs());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioLoader != null)
            audioLoader.stopAndRelease();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_mark_anchor:
                if (markAnchors == null)
                    markAnchors = new TreeMap<>();
                anchorIvOM = AnchorIvOperationMode.MARK;
                return true;
            case R.id.action_click_anchor:
                anchorIvOM = AnchorIvOperationMode.CLICK;
                return true;
            case R.id.action_clean_mark_anchor:
                if (markAnchors != null) {
                    markAnchors.clear();
                    anchorIv.postInvalidate();
                }
                return true;
            case R.id.action_s_h_clickable_anchor:
                anchorIv.setIShowClickableAnchor(!anchorIv.isShowClickableAnchor);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAnchorClick(Anchor anchor, int id, float widthRatio, float heightRatio) {
        if (anchorIvOM.equals(AnchorIvOperationMode.CLICK)) {
            anchorIv.setCurrentClickAnchor(anchor);
            showToast(anchor.subtitle);
            if (audioLoader != null)
                audioLoader.playByParagraph(anchor.sequence);
        } else if (anchorIvOM.equals(AnchorIvOperationMode.MARK)) {
            if (markAnchors.containsKey(anchor.sequence)) {
                markAnchors.remove(anchor.sequence);
                showToast("Clean Mark " + anchor.sequence);
            } else {
                markAnchors.put(anchor.sequence, anchor);
                showToast("Mark " + anchor.sequence);
            }
            anchorIv.postInvalidate();

        }
    }

    @Override
    public void onDrawAnchor(Anchor anchor, RectF rectAnchor, Canvas canvas) {
        if (markAnchors != null && markAnchors.size() > 0) {
            initDrawTagAnchor();
            for (int key : markAnchors.keySet()) {
                Anchor markAnchor = markAnchors.get(key);
                if (markAnchor.equals(anchor)) {
                    canvas.drawRoundRect(rectAnchor, (float) anchorIv.drawRoundRectRadius, (float) anchorIv.drawRoundRectRadius, paintMark);
                    canvas.drawCircle(rectAnchor.centerX(), rectAnchor.centerY() - rectAnchor.height() / 2 - paintMarkTextBackRadius, paintMarkTextBackRadius, paintMarkTextBack);
                    canvas.drawText(String.valueOf(anchor.sequence), rectAnchor.centerX(), rectAnchor.centerY() - rectAnchor.height() / 2 - paintMarkTextBackRadius - toCenterDistance, paintMarkText);
                }
            }
        }
    }

    private Paint paintMarkText;
    private Paint paintMarkTextBack;
    private Paint paintMark;
    private float toCenterDistance;
    private int paintMarkTextBackRadius;

    public void initDrawTagAnchor() {
        if (paintMarkTextBackRadius <= 0)
            paintMarkTextBackRadius = anchorIv.dpToPx(7);

        if (paintMarkTextBack == null) {
            paintMarkTextBack = new Paint();
            paintMarkTextBack.setColor(Color.RED);
            paintMarkTextBack.setStyle(Paint.Style.FILL);
            paintMarkTextBack.setAntiAlias(true);
        }

        if (paintMarkText == null) {
            paintMarkText = new Paint();
            paintMarkText.setColor(Color.WHITE);
            paintMarkText.setTextSize(anchorIv.dpToPx(11));
            paintMarkText.setStyle(Paint.Style.FILL);
            paintMarkText.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fontMetrics = paintMarkText.getFontMetrics();
            float top = fontMetrics.top;
            float bottom = fontMetrics.bottom;
            toCenterDistance = top / 2 + bottom / 2;
        }
        if (paintMark == null) {
            paintMark = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintMark.setStrokeWidth(3f);
            paintMark.setStyle(Paint.Style.STROKE);
            paintMark.setStrokeCap(Paint.Cap.ROUND);
            paintMark.setColor(Color.RED);
        }
    }

    // ------------------------------K------------------------------@Assist
    private Toast toast;

    private void showToast(String str) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.show();
    }
}
