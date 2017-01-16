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

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.SparseArray;

import java.util.Timer;
import java.util.TimerTask;

/**
 * All logic is only for testing
 * Actual use of self dispose
 */
class AudioLoader implements MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private MediaState mediaState;
    private MediaPlayer mediaPlayer;
    private SparseArray<AudioParagraph> audioParagraphs;
    private AudioParagraph curAudioParagraph;

    AudioLoader(AssetFileDescriptor aFD, SparseArray<AudioParagraph> audioParagraphs) {
        try {
            initMedia();
            prepare(aFD);
            this.audioParagraphs = audioParagraphs;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMedia() {
        if (mediaPlayer != null)
            return;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
        mediaPlayer.setVolume(10, 10);

        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaState = MediaState.INVALID;
    }

    private void prepare(AssetFileDescriptor aFD) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(aFD.getFileDescriptor(), aFD.getStartOffset(), aFD.getLength());
            mediaPlayer.prepareAsync();
            mediaState = MediaState.PREPARING;
        } catch (Exception e) {//IO
            e.printStackTrace();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        start();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaState = MediaState.PREPARED;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaState = MediaState.ERROR;
        return false;
    }

    void playByParagraph(int para) {
        try {
            if (isInPlaybackState()) {
                curAudioParagraph = audioParagraphs.get(para);
                this.para = para - 1;//previous para
                isItInPara = false;
                seekTo(curAudioParagraph.start);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() {
        if (mediaPlayer != null && mediaState != MediaState.PLAYING && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mediaState = MediaState.PLAYING;
            startTimer();
        }
    }

    private void pause() {
        if (mediaPlayer != null && mediaState != MediaState.PAUSE) {
            stopTimer();
            mediaPlayer.pause();
            mediaState = MediaState.PAUSE;
        }
    }

    private void seekTo(int milliseconds) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                pause();
            mediaPlayer.seekTo(milliseconds);
            mediaState = MediaState.SEEK_TO_ING;
        }
    }


    private int position() {
        if (mediaPlayer != null)
            return mediaPlayer.getCurrentPosition();
        return 0;
    }


    boolean stopAndRelease() {
        try {
            pause();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {//IllegalStateException
            return false;
        }
        return true;
    }

    private boolean isInPlaybackState() {
        return (mediaState != null &&
                mediaState != MediaState.INVALID &&
                mediaState != MediaState.ERROR &&
                mediaState != MediaState.PREPARING);
    }


    // ------------------------------K------------------------------@Timer
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean mTimerStart = false;

    private void startTimer() {
        if (mTimerStart) {
            return;
        }
        if (mTimer == null)
            mTimer = new Timer();
        mTimerTask = new MyTimerTask();
        mTimer.schedule(mTimerTask, 100, 100);
        mTimerStart = true;
    }

    private void stopTimer() {
        if (!mTimerStart) {
            return;
        }
        mTimerStart = false;
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
            mTimer = null;
        }
    }

    /**
     * current para
     */
    private int para = 0;
    /**
     * ...[start-end]...
     */
    private boolean isItInPara;

    private class MyTimerTask extends TimerTask {
        @Override
        public synchronized void run() {

            if (audioParagraphs != null && audioParagraphs.size() > 0) {
                // ============================@Paragraphs@============================
                int position = position();
                AudioParagraph audioParagraph;

                if (isItInPara && para <= audioParagraphs.size() && (audioParagraph = audioParagraphs.get(para)) != null) {
                    // =========@Paragraphs End@=========
                    if (audioParagraph.end <= position) {
                        // =========@update current data
                        isItInPara = false;//exit para[start-end]
                        Log.d("K", "Para(" + para + ") End ...");

                        // =========@Doing...@=========
                        if (audioParagraph.equals(curAudioParagraph)) {
                            pause();
                            curAudioParagraph = null;
                        }

                        return;
                    }
                }

                int paraNext = para + 1;
                if (paraNext <= audioParagraphs.size() && (audioParagraph = audioParagraphs.get(paraNext)) != null) {
                    // =========@Paragraphs Start@=========
                    if (audioParagraph.start <= position) {
                        // =========@update current data
                        para = paraNext;//current para
                        isItInPara = true;//enter para[start-end]
                        Log.d("K", "Para(" + para + ") Start ...");

                        // =========@Doing...@=========
                    }
                }
            }
        }
    }
}
