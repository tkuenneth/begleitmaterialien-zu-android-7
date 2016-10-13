package com.thomaskuenneth.multiwindowdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Locale;

public final class AnimatedNumberView extends FrameLayout {

    private static final int NUM = 20;
    private static final float STARTSIZE = 36;
    private static final float ENDSIZE = 128;
    private static final int DURATION = 3000;

    private final TextView[] textviews;

    public AnimatedNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textviews = new TextView[NUM];
        for (int i = 0; i < NUM; i++) {
            // TextView
            TextView tv = new TextView(context, attrs);
            tv.setText(String.format(Locale.US,
                    "%d", i));
            addView(tv);
            textviews[i] = tv;
            // Animation
            ValueAnimator anim = ValueAnimator.ofFloat(STARTSIZE, ENDSIZE);
            anim.setDuration(DURATION +
                    getRandomDuration());
            anim.setTarget(tv);
            anim.setRepeatMode(ValueAnimator.RESTART);
            anim.setRepeatCount(ValueAnimator.INFINITE);
            anim.addListener(new Animator.AnimatorListener() {

                private final TextView _tv = tv;

                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    setRandomLocation(_tv,
                            getWidth(),
                            getHeight());
                }
            });
            anim.addUpdateListener(animator -> {
                float size = (float) animator.getAnimatedValue();
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
                        size);
            });
            tv.setTag(anim);
        }
        setEnabled(isEnabled());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        for (TextView tv : textviews) {
            setRandomLocation(tv, w, h);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (TextView tv : textviews) {
            Animator anim = (Animator) tv.getTag();
            if (enabled) {
                if (anim.isPaused()) {
                    anim.resume();
                } else {
                    anim.start();
                }
            } else {
                anim.pause();
            }
            tv.setEnabled(enabled);
        }
    }

    private void setRandomLocation(TextView tv,
                                   int w, int h) {
        tv.setX((int) ((double) w * Math.random()));
        tv.setY((int) ((double) h * Math.random()));
    }

    private long getRandomDuration() {
        return (long) (((double) DURATION) * Math.random());
    }
}
