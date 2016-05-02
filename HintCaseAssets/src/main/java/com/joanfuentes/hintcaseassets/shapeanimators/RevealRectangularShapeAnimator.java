package com.joanfuentes.hintcaseassets.shapeanimators;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.view.View;

import com.joanfuentes.hintcase.ShapeAnimator;
import com.joanfuentes.hintcase.RectangularShape;
import com.joanfuentes.hintcase.Shape;

public class RevealRectangularShapeAnimator extends ShapeAnimator {
    private FloatEvaluator floatEvaluator;

    public RevealRectangularShapeAnimator() {
        super();
        floatEvaluator = new FloatEvaluator();
    }

    @Override
    public ValueAnimator getAnimator(final View view, Shape shape,
                                     final OnFinishListener onFinishListener) {
        final RectangularShape rectangularShape = (RectangularShape) shape;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(rectangularShape.getMaxHeight(),
                rectangularShape.getMinHeight());
        valueAnimator.setDuration(DEFAULT_ANIMATION_DURATION_IN_MILLISECONDS)
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        rectangularShape.setCurrentHeight((Float) valueAnimator.getAnimatedValue());
                        float fraction = valueAnimator.getAnimatedFraction();
                        rectangularShape.setCurrentWidth(
                                floatEvaluator.evaluate(fraction, rectangularShape.getMaxWidth(),
                                        rectangularShape.getMinWidth()));
                        if (rectangularShape.getCurrentHeight() == rectangularShape.getMinHeight()) {
                            if (onFinishListener != null) {
                                onFinishListener.onFinish();
                            }
                        }
                        view.invalidate();
                    }
                });
        return valueAnimator;
    }
}
