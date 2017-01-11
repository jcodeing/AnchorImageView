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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.util.TreeMap;

public class TestUtils {

    public static TreeMap<Integer, Anchor> getAnchors() {
        TreeMap<Integer, Anchor> anchors = new TreeMap<>();
        anchors.put(1, new Anchor(1721, 223, 615, 59, 156, 1, "Unit Two"));
        anchors.put(2, new Anchor(2721, 102, 445, 235, 318, 2, "John, I have a new schoolbag."));
        anchors.put(3, new Anchor(3721, 220, 420, 351, 423, 3, "May I see it?"));
        anchors.put(4, new Anchor(4721, 186, 435, 729, 801, 4, "I lost my notebook."));
        anchors.put(5, new Anchor(5721, 381, 617, 812, 883, 5, "What colour is it?"));
        anchors.put(6, new Anchor(6721, 308, 537, 910, 1009, 6, "It's green. I like it very much."));
        return anchors;
    }

    public static Bitmap getAnchorImage(Context context) {
        try {
            InputStream anchorIS = context.getAssets().open("anchor_test.png");
            return BitmapFactory.decodeStream(anchorIS);
        } catch (Exception e) {//IO
            return null;
        }
    }
}
