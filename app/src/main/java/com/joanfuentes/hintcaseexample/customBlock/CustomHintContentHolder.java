package com.joanfuentes.hintcaseexample.customBlock;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanfuentes.hintcase.HintCase;
import com.joanfuentes.hintcaseassets.hintcontentholders.HintContentHolder;
import com.joanfuentes.hintcaseexample.R;

import java.util.ArrayList;

public class CustomHintContentHolder extends HintContentHolder {
    public static final int NO_IMAGE = -1;
    public static final int  BACKGROUND_COLOR_TRANSPARENT = 0x00000000;
    private static final int  DEFAULT_ARROW_SIZE_IN_PX = 50;

    private int borderColor;
    private int backgroundColor;
    private int borderSize;
    private ImageView imageView;
    private int imageResourceId;
    private CharSequence contentTitle;
    private CharSequence contentText;
    private int titleStyleId;
    private int textStyleId;
    private int marginLeft;
    private int marginTop;
    private int marginRight;
    private int marginBottom;
    private ArrayList<Integer> alignBlockRules;
    private ArrayList<Integer> alignArrowRules;
    private int contentTopMargin;
    private int contentBottomMargin;
    private int contentRightMargin;
    private int contentLeftMargin;
    private int gravity;
    private float xTranslationImage;
    private float yTranslationImage;
    private TriangleShapeView arrow;
    private HintCase hintCase;
    private ViewGroup parent;
    private LinearLayout contentLinearLayout;
    private int arrowWidth;
    private int arrowHeight;
    private int shadowSize;
    private boolean useBorder;
    private TriangleShapeView.Direction arrowDirection;

    @Override
    public View getView(Context context, final HintCase hintCase, ViewGroup parent) {
        this.hintCase = hintCase;
        this.parent = parent;

        calculateDataToPutTheArroW(hintCase);

        FrameLayout.LayoutParams frameLayoutParamsBlock = getParentLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                gravity, marginLeft, marginTop, marginRight, marginBottom);

        RelativeLayout fullBlockLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams relativeLayoutParamsArrow =
                new RelativeLayout.LayoutParams(arrowWidth, arrowHeight);
        fullBlockLayout.setLayoutParams(frameLayoutParamsBlock);
        for (int rule : alignArrowRules) {
            relativeLayoutParamsArrow.addRule(rule);
        }
        arrow = new TriangleShapeView(context);
        arrow.setBackgroundColor(backgroundColor);
        if (useBorder) {
            arrow.setBorder(borderSize, borderColor);
        }
        arrow.setDirection(arrowDirection);
        arrow.setShadowSize(shadowSize);
        arrow.setLayoutParams(relativeLayoutParamsArrow);


        RelativeLayout.LayoutParams relativeLayoutParamsLinear =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int rule : alignBlockRules) {
            relativeLayoutParamsLinear.addRule(rule);
        }
        relativeLayoutParamsLinear.topMargin = contentTopMargin;
        relativeLayoutParamsLinear.bottomMargin = contentBottomMargin;
        relativeLayoutParamsLinear.rightMargin = contentRightMargin;
        relativeLayoutParamsLinear.leftMargin = contentLeftMargin;
        contentLinearLayout = new LinearLayout(context);
        contentLinearLayout.setBackgroundResource(R.drawable.bubble_border_background);
//        contentLinearLayout.setBackgroundDrawable(getLayerDrawable());
//        GradientDrawable gradientDrawable = (GradientDrawable) contentLinearLayout.getBackground().getCurrent();
        LayerDrawable layerDrawable = (LayerDrawable) contentLinearLayout.getBackground().getCurrent();
        GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable.getDrawable(layerDrawable.getNumberOfLayers() - 1);
        gradientDrawable.setColor(backgroundColor);
        if (useBorder) {
            gradientDrawable.setStroke(borderSize, borderColor);
        }

        contentLinearLayout.setLayoutParams(relativeLayoutParamsLinear);
        contentLinearLayout.setGravity(Gravity.CENTER);
        int padding = borderSize + shadowSize;
        contentLinearLayout.setPadding(padding, padding, padding, padding);
        contentLinearLayout.setOrientation(LinearLayout.VERTICAL);

        if (contentTitle != null) {
            contentLinearLayout.addView(getTextViewTitle(context));
        }
        if (existImage()) {
            contentLinearLayout.addView(getImage(context, imageView, imageResourceId));
        }
        if (contentText != null) {
            contentLinearLayout.addView(getTextViewDescription(context));
        }
        fullBlockLayout.setAlpha(0);
        fullBlockLayout.addView(contentLinearLayout);
        fullBlockLayout.addView(arrow);
        return fullBlockLayout;
    }

    /*
    private LayerDrawable getLayerDrawable(){
        Drawable layer1 = getShadowDrawable();
        Drawable layerX = getBorderAndContentDrawable();

        return new LayerDrawable(new Drawable[]{layer1, layerX});
    }

    private Drawable getShadowDrawable() {
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, null);
        gradientDrawable.setCornerRadius(context.getResources().getDimensionPixelSize(R.dimen.shadow));
        gradientDrawable.setColor(Color.parseColor("#05000000"));
        return gradientDrawable;
    }

    private Drawable getBorderAndContentDrawable() {
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, null);
        gradientDrawable.setShapeAnimators(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(context.getResources().getDimensionPixelSize(R.dimen.corner_radius));
        gradientDrawable.setColor(backgroundColor);
        if (useBorder) {
            gradientDrawable.setStroke(borderSize, borderColor);
        }
        return gradientDrawable;
    }
*/

    @Override
    public void onLayout() {
        calculateArrowTranslation();
        arrow.setTranslationX(xTranslationImage);
        arrow.setTranslationY(yTranslationImage);
        if (hintCase.getBlockInfoPosition() == HintCase.HINT_BLOCK_POSITION_RIGHT
                || hintCase.getBlockInfoPosition() == HintCase.HINT_BLOCK_POSITION_LEFT) {
            if (arrow.getTop() >= contentLinearLayout.getBottom()) {
                float translationY = arrow.getY() + (arrow.getHeight()/2) - contentLinearLayout.getY() - (contentLinearLayout.getHeight()/2);
                contentLinearLayout.setTranslationY(translationY);

            }
        }
    }

    private void calculateArrowTranslation() {
        int textPosition = hintCase.getBlockInfoPosition();
        switch (textPosition) {
            case HintCase.HINT_BLOCK_POSITION_TOP:
                xTranslationImage = hintCase.getShape().getCenterX() - parent.getWidth()/2;
                yTranslationImage = 0;
                break;
            case HintCase.HINT_BLOCK_POSITION_BOTTOM:
                xTranslationImage = hintCase.getShape().getCenterX() - parent.getWidth()/2;
                yTranslationImage = 0;
                break;
            case HintCase.HINT_BLOCK_POSITION_RIGHT:
                xTranslationImage = 0;
                yTranslationImage = hintCase.getShape().getCenterY() - parent.getHeight()/2;
                break;
            case HintCase.HINT_BLOCK_POSITION_LEFT:
                xTranslationImage = 0;
                yTranslationImage = hintCase.getShape().getCenterY() - (parent.getHeight()/2);
                break;
            default:
                xTranslationImage = 0;
                yTranslationImage = 0;
        }
    }

    private void calculateDataToPutTheArroW(HintCase hintCase) {
        int textPosition = hintCase.getBlockInfoPosition();
        alignArrowRules = new ArrayList<>();
        alignBlockRules = new ArrayList<>();

        switch (textPosition) {
            case HintCase.HINT_BLOCK_POSITION_TOP:
                alignBlockRules.add(RelativeLayout.ALIGN_PARENT_BOTTOM);
                alignArrowRules.add(RelativeLayout.CENTER_HORIZONTAL);
                alignArrowRules.add(RelativeLayout.ALIGN_PARENT_BOTTOM);
                gravity = Gravity.BOTTOM;
                contentRightMargin = 0;
                contentLeftMargin = 0;
                contentTopMargin = 0;
                contentBottomMargin = arrowHeight - borderSize - shadowSize;
                arrowDirection = TriangleShapeView.Direction.DOWN;
                marginBottom = 0;
                break;
            case HintCase.HINT_BLOCK_POSITION_BOTTOM:
                alignBlockRules.add(RelativeLayout.ALIGN_PARENT_TOP);
                alignArrowRules.add(RelativeLayout.CENTER_HORIZONTAL);
                alignArrowRules.add(RelativeLayout.ALIGN_PARENT_TOP);
                gravity = Gravity.TOP;
                contentRightMargin = 0;
                contentLeftMargin = 0;
                contentTopMargin = arrowHeight - borderSize - shadowSize;
                contentBottomMargin = 0;
                arrowDirection = TriangleShapeView.Direction.UP;
                marginTop = 0;
                break;
            case HintCase.HINT_BLOCK_POSITION_RIGHT:
                alignBlockRules.add(RelativeLayout.ALIGN_PARENT_LEFT);
                alignArrowRules.add(RelativeLayout.CENTER_VERTICAL);
                alignArrowRules.add(RelativeLayout.ALIGN_PARENT_LEFT);
                gravity = Gravity.LEFT;
                contentRightMargin = 0;
                contentLeftMargin = arrowHeight;
                contentTopMargin = 0;
                contentBottomMargin = 0;
                arrowDirection = TriangleShapeView.Direction.LEFT;
                marginLeft = 0;
                break;
            case HintCase.HINT_BLOCK_POSITION_LEFT:
                alignBlockRules.add(RelativeLayout.ALIGN_PARENT_RIGHT);
                alignArrowRules.add(RelativeLayout.ALIGN_PARENT_RIGHT);
                alignArrowRules.add(RelativeLayout.CENTER_VERTICAL);
                gravity = Gravity.RIGHT;
                contentRightMargin = arrowHeight;
                contentLeftMargin = 0;
                contentTopMargin = 0;
                contentBottomMargin = 0;
                arrowDirection = TriangleShapeView.Direction.RIGHT;
                marginRight = 0;
                break;
            default:
                alignBlockRules.add(RelativeLayout.CENTER_HORIZONTAL);
                alignArrowRules.add(RelativeLayout.CENTER_IN_PARENT);
                gravity = Gravity.CENTER;
                contentRightMargin = 0;
                contentLeftMargin = 0;
                contentTopMargin = 0;
                contentBottomMargin = 0;
                xTranslationImage = 0;
                yTranslationImage = 0;
        }
    }

    private ImageView getImage(Context context, ImageView image, int imageResourceId) {
        LinearLayout.LayoutParams linearLayoutParamsImage =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1.0f);
        linearLayoutParamsImage.setMargins(0, 20, 0, 50);
        if (image == null) {
            image = new ImageView(context);
        }
        if (imageResourceId != CustomHintContentHolder.NO_IMAGE) {
            image.setImageResource(imageResourceId);
        }
        image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        image.setAdjustViewBounds(true);
        image.setLayoutParams(linearLayoutParamsImage);
        return image;
    }

    private View getTextViewTitle(Context context) {
        LinearLayout.LayoutParams linearLayoutParamsTitle =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutParamsTitle.setMargins(0, 0, 0, 20);
        TextView textViewTitle = new TextView(context);
        textViewTitle.setLayoutParams(linearLayoutParamsTitle);
        textViewTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        SpannableString spannableStringTitle= new SpannableString(contentTitle);
        TextAppearanceSpan titleTextAppearanceSpan = new TextAppearanceSpan(context, titleStyleId);
        spannableStringTitle.setSpan(titleTextAppearanceSpan, 0, spannableStringTitle.length(), 0);
        textViewTitle.setText(spannableStringTitle);
        return textViewTitle;
    }

    private View getTextViewDescription(Context context) {
        LinearLayout.LayoutParams linearLayoutParamsText =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textViewDescription = new TextView(context);
        textViewDescription.setLayoutParams(linearLayoutParamsText);
        textViewDescription.setGravity(Gravity.CENTER_HORIZONTAL);
        SpannableString spannableStringText= new SpannableString(contentText);
        TextAppearanceSpan textTextAppearanceSpan = new TextAppearanceSpan(context, textStyleId);
        spannableStringText.setSpan(textTextAppearanceSpan, 0, spannableStringText.length(), 0);
        textViewDescription.setText(spannableStringText);
        return textViewDescription;
    }

    private boolean existImage() {
        return imageView != null ||
                imageResourceId != CustomHintContentHolder.NO_IMAGE;
    }

    public static class Builder {
        private CustomHintContentHolder blockInfo;
        private Context context;

        public Builder(Context context) {
            this.context = context;
            this.blockInfo = new CustomHintContentHolder();
            this.blockInfo.imageResourceId = NO_IMAGE;
            this.blockInfo.arrowWidth = DEFAULT_ARROW_SIZE_IN_PX;
            this.blockInfo.arrowHeight = DEFAULT_ARROW_SIZE_IN_PX;
            this.blockInfo.useBorder = false;
            this.blockInfo.shadowSize = context.getResources().getDimensionPixelSize(R.dimen.shadow);
        }

        public Builder setBorder(int resourceId, int resId) {
            blockInfo.useBorder = true;
            blockInfo.borderSize = context.getResources().getDimensionPixelSize(resourceId);
            blockInfo.borderColor = context.getResources().getColor(resId);
            return this;
        }

        public Builder setBackgroundColorFromResource(int resId) {
            blockInfo.backgroundColor = context.getResources().getColor(resId);
            return this;
        }

        public Builder setBackgroundColor(int color) {
            blockInfo.backgroundColor = color;
            return this;
        }

        public Builder setImageDrawableId(int resourceId) {
            blockInfo.imageResourceId = resourceId;
            return this;
        }

        public Builder setImageView(ImageView imageView) {
            blockInfo.imageView = imageView;
            return this;
        }

        public Builder setContentTitle(CharSequence title) {
            blockInfo.contentTitle = title;
            return this;
        }

        public Builder setContentTitle(int resId) {
            blockInfo.contentTitle = context.getString(resId);
            return this;
        }

        public Builder setTitleStyle(int resId) {
            blockInfo.titleStyleId = resId;
            return this;
        }

        public Builder setContentText(CharSequence text) {
            blockInfo.contentText = text;
            return this;
        }

        public Builder setContentText(int resId) {
            blockInfo.contentText = context.getString(resId);
            return this;
        }

        public Builder setContentStyle(int resId) {
            blockInfo.textStyleId = resId;
            return this;
        }

        public Builder setMargin(int left, int top, int right, int bottom) {
            blockInfo.marginLeft = left;
            blockInfo.marginTop = top;
            blockInfo.marginRight = right;
            blockInfo.marginBottom = bottom;
            return this;
        }

        public Builder setArrowSize(int widthResId, int heightResId) {
            blockInfo.arrowWidth = context.getResources().getDimensionPixelSize(widthResId);
            blockInfo.arrowHeight = context.getResources().getDimensionPixelSize(heightResId);
            return this;
        }

        public CustomHintContentHolder build() {
            return blockInfo;
        }
    }
}
