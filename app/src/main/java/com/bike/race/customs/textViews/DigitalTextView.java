package com.bike.race.customs.textViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;


/**
 * Created by prasi on 6/10/16.
 */

public class DigitalTextView extends AppCompatTextView {

    public DigitalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        String fontName = "fonts/digital.ttf";
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
        setTypeface(typeface);
    }
}
