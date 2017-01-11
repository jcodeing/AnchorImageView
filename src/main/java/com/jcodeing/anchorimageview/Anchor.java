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

public class Anchor {

    Anchor(int id, int left, int right, int top, int bottom, int sequence, String subtitle) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.sequence = sequence;
        this.subtitle = subtitle;
    }

    public int id;
    public int left;
    public int right;
    public int top;
    public int bottom;
    int sequence;
    String subtitle;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Anchor) {
            Anchor anchor = (Anchor) obj;
            return id == anchor.id && sequence == anchor.sequence;
        }
        return false;
    }
}
