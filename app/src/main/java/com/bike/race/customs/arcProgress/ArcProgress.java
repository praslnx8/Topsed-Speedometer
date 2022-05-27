package com.bike.race.customs.arcProgress;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.bike.race.R;


/**
 * Created by bruce on 11/6/14.
 */
public class ArcProgress extends View {
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_STROKE_WIDTH = "stroke_width";
    private static final String INSTANCE_SUFFIX_TEXT_SIZE = "suffix_text_size";
    private static final String INSTANCE_SUFFIX_TEXT_PADDING = "suffix_text_padding";
    private static final String INSTANCE_BOTTOM_TEXT_SIZE = "bottom_text_size";
    private static final String INSTANCE_BOTTOM_TEXT = "bottom_text";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_ARC_ANGLE = "arc_angle";
    private static final String INSTANCE_SUFFIX = "suffix";
    private final int default_unfinished_color = Color.rgb(204, 204, 204);
    private final int default_text_color = Color.rgb(66, 145, 241);
    private final float default_suffix_padding;
    private final float default_stroke_width;
    private final String default_suffix_text;
    private final int min_size;
    protected Paint textPaint;
    private Paint finishedPaint;
    private Paint unfinishedPaint;
    private final RectF finishedOuterRect = new RectF();
    private final RectF unfinishedOuterRect = new RectF();
    private String middleText;
    private float finishedStrokeWidth;
    private float unfinishedStrokeWidth;
    private float strokeWidth;
    private float suffixTextSize;
    private float bottomTextSize;
    private String bottomText;
    private float textSize;
    private int textColor;
    private int progress = 0;
    private int max;
    private int finishedStrokeColor;
    private int unfinishedStrokeColor;
    private float arcAngle;
    private String suffixText = "";
    private float suffixTextPadding;
    private float arcBottomHeight;
    private Typeface typeface;

    public ArcProgress(Context context) {
        this(context, null);
    }

    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        min_size = (int) dp2px(getResources(), 100);
        default_suffix_padding = dp2px(getResources(), 4);
        default_suffix_text = "";
        default_stroke_width = dp2px(getResources(), 4);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    protected void initByAttributes(TypedArray attributes) {
        TextView mainTextView = new TextView(getContext());
        mainTextView.setTextAppearance(R.style.TextAppearance_AppCompat_Display1);
        TextView secondaryTextView = new TextView(getContext());
        secondaryTextView.setTextAppearance(R.style.TextAppearance_AppCompat_Caption);
        TextView tertiaryTextView = new TextView(getContext());
        tertiaryTextView.setTextAppearance(R.style.TextAppearance_AppCompat_Caption);

        finishedStrokeWidth = attributes.getDimension(R.styleable.ArcProgress_arc_finished_stroke_width, default_stroke_width);
        unfinishedStrokeWidth = attributes.getDimension(R.styleable.ArcProgress_arc_unfinished_stroke_width, default_stroke_width);
        int default_finished_color = Color.BLUE;
        finishedStrokeColor = attributes.getColor(R.styleable.ArcProgress_arc_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(R.styleable.ArcProgress_arc_unfinished_color, default_unfinished_color);
        textColor = attributes.getColor(R.styleable.ArcProgress_arc_text_color, default_text_color);
        textSize = mainTextView.getTextSize();
        float default_arc_angle = 360 * 0.7f;
        arcAngle = attributes.getFloat(R.styleable.ArcProgress_arc_angle, default_arc_angle);
        int default_max = 100;
        setMax(attributes.getInt(R.styleable.ArcProgress_arc_max, default_max));
        setProgress(attributes.getInt(R.styleable.ArcProgress_arc_progress, 0));
        strokeWidth = attributes.getDimension(R.styleable.ArcProgress_arc_stroke_width, default_stroke_width);
        suffixTextSize = secondaryTextView.getTextSize();
        suffixText = TextUtils.isEmpty(attributes.getString(R.styleable.ArcProgress_arc_suffix_text)) ? default_suffix_text : attributes.getString(R.styleable.ArcProgress_arc_suffix_text);
        suffixTextPadding = attributes.getDimension(R.styleable.ArcProgress_arc_suffix_text_padding, default_suffix_padding);
        bottomTextSize = secondaryTextView.getTextSize();
        bottomText = attributes.getString(R.styleable.ArcProgress_arc_bottom_text);
    }

    protected void initPainters() {
        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/digital.ttf");
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);

        finishedPaint = new Paint();
        if (max > 0) {
            int percentage = (progress / max) * 100;

            int color = getColorBasedOnPercentage((double) percentage);
            finishedPaint.setColor(color);
        } else {
            finishedPaint.setColor(finishedStrokeColor);
        }

        finishedPaint.setAntiAlias(true);
        finishedPaint.setStrokeWidth(finishedStrokeWidth);
        finishedPaint.setStyle(Paint.Style.STROKE);
        finishedPaint.setStrokeCap(Paint.Cap.ROUND);

        unfinishedPaint = new Paint();
        unfinishedPaint.setColor(unfinishedStrokeColor);
        unfinishedPaint.setStyle(Paint.Style.STROKE);
        unfinishedPaint.setAntiAlias(true);
        unfinishedPaint.setStrokeWidth(unfinishedStrokeWidth);
        unfinishedPaint.setStyle(Paint.Style.STROKE);
        unfinishedPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public float getSuffixTextSize() {
        return suffixTextSize;
    }

    public void setMiddleText(String middleText) {
        this.middleText = middleText;
        invalidate();
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
        this.invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        int max = getMax();
        if (this.progress > max && max > 0) {
            this.progress %= max;
        }
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = Math.max(max, 0);
        invalidate();
    }

    public float getBottomTextSize() {
        return bottomTextSize;
    }

    public float getTextSize() {
        return textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getFinishedStrokeColor() {
        return finishedStrokeColor;
    }

    public int getUnfinishedStrokeColor() {
        return unfinishedStrokeColor;
    }

    public float getArcAngle() {
        return arcAngle;
    }

    public String getSuffixText() {
        return suffixText;
    }

    public float getSuffixTextPadding() {
        return suffixTextPadding;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return min_size;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return min_size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        unfinishedOuterRect.set(strokeWidth / 2f, strokeWidth / 2f, width - strokeWidth / 2f, MeasureSpec.getSize(heightMeasureSpec) - strokeWidth / 2f);
        finishedOuterRect.set(strokeWidth / 2f, strokeWidth / 2f, width - strokeWidth / 2f, MeasureSpec.getSize(heightMeasureSpec) - strokeWidth / 2f);
        float radius = width / 2f;
        float angle = (360 - arcAngle) / 2f;
        arcBottomHeight = radius * (float) (1 - Math.cos(angle / 180 * Math.PI));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startAngle = 270 - arcAngle / 2f;
        float finishedSweepAngle = 1;
        if (progress > 0) {
            finishedSweepAngle = progress / (float) getMax() * arcAngle;
        }

        float delta = Math.max(finishedStrokeWidth, unfinishedStrokeWidth);
        finishedOuterRect.set(delta,
                delta,
                getWidth() - delta,
                getHeight() - delta);
        unfinishedOuterRect.set(delta,
                delta,
                getWidth() - delta,
                getHeight() - delta);
        textPaint.setTypeface(typeface);

        unfinishedPaint.setColor(unfinishedStrokeColor);
        unfinishedPaint.setTextSize(1f);
        unfinishedPaint.setStrokeWidth(unfinishedStrokeWidth);
        canvas.drawArc(unfinishedOuterRect, startAngle, arcAngle, false, unfinishedPaint);
        finishedPaint.setColor(finishedStrokeColor);
        finishedPaint.setTextSize(8f);
        finishedPaint.setStrokeWidth(finishedStrokeWidth);
        canvas.drawArc(finishedOuterRect, startAngle, finishedSweepAngle, false, finishedPaint);

        String text = String.valueOf(getProgress());
        if (!TextUtils.isEmpty(text)) {
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize * 1.5f);

            float textHeight = textPaint.descent() + textPaint.ascent();
            float textBaseline = (getHeight() - textHeight) / 2.5f;
            canvas.drawText(text, (getWidth() - textPaint.measureText(text)) / 2.0f, textBaseline, textPaint);
            textPaint.setTextSize(suffixTextSize);
            float suffixHeight = textPaint.descent() + textPaint.ascent();
            canvas.drawText(suffixText, getWidth() / 2.0f + textPaint.measureText(text) + suffixTextPadding, textBaseline + textHeight - suffixHeight, textPaint);
        }

        if (arcBottomHeight == 0) {
            float radius = getWidth() / 2f;
            float angle = (360 - arcAngle) / 2f;
            arcBottomHeight = radius * (float) (1 - Math.cos(angle / 180 * Math.PI));
        }

        if (!TextUtils.isEmpty(middleText)) {
            float angle = (360 - getArcAngle()) / 2f;
            float radius = getWidth() / 2f;
            float arcBottomHeight = radius * (float) (1 - Math.cos(angle / 180 * Math.PI));

            float bottomTextBaseline = getHeight() - arcBottomHeight - (textPaint.descent() + textPaint.ascent()) / 2;

            textPaint.setTextSize(30f);
            textPaint.setColor(ActivityCompat.getColor(getContext(), R.color.secondaryText));
            canvas.drawText(middleText, (getWidth() - textPaint.measureText(middleText)) / 2.0f, bottomTextBaseline / 1.25f, textPaint);
        }

        if (!TextUtils.isEmpty(getBottomText())) {
            textPaint.setTextSize(bottomTextSize * 1.5f);
            float bottomTextBaseline = getHeight() - arcBottomHeight - (textPaint.descent() + textPaint.ascent()) / 2;
            canvas.drawText(getBottomText(), (getWidth() - textPaint.measureText(getBottomText())) / 2.0f, bottomTextBaseline, textPaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_STROKE_WIDTH, getStrokeWidth());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_SIZE, getSuffixTextSize());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_PADDING, getSuffixTextPadding());
        bundle.putFloat(INSTANCE_BOTTOM_TEXT_SIZE, getBottomTextSize());
        bundle.putString(INSTANCE_BOTTOM_TEXT, getBottomText());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor());
        bundle.putFloat(INSTANCE_ARC_ANGLE, getArcAngle());
        bundle.putString(INSTANCE_SUFFIX, getSuffixText());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            strokeWidth = bundle.getFloat(INSTANCE_STROKE_WIDTH);
            suffixTextSize = bundle.getFloat(INSTANCE_SUFFIX_TEXT_SIZE);
            suffixTextPadding = bundle.getFloat(INSTANCE_SUFFIX_TEXT_PADDING);
            bottomTextSize = bundle.getFloat(INSTANCE_BOTTOM_TEXT_SIZE);
            bottomText = bundle.getString(INSTANCE_BOTTOM_TEXT);
            textSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            finishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            unfinishedStrokeColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            suffixText = bundle.getString(INSTANCE_SUFFIX);
            initPainters();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    private int getColorBasedOnPercentage(Double percentage) {
        return (int) new ArgbEvaluator().evaluate(percentage.floatValue(), 0x006400, 0x8B0000);
    }

    private static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
}