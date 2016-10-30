package com.codesparts.toastappclient.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.thecodesparts.toastappclient.R;

public class CustomFontUtils {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public static void applyCustomFont(TextView customFontTextView, Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);

        String fontName = attributeArray.getString(R.styleable.CustomFontTextView_font);

        int textStyle = attributeArray.getInt(R.styleable.CustomFontTextView_textStyle, 0);

        // if nothing extra was used, fall back to regular android:textStyle parameter
        if (textStyle == 0) {
            textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        }

        Typeface customFont = selectTypeface(context, fontName, textStyle);
        customFontTextView.setTypeface(customFont);

        attributeArray.recycle();
    }

    private static Typeface selectTypeface(Context context, String fontName, int textStyle) {
        if (fontName.contentEquals(context.getString(R.string.font_name_fontawesome))) {
            return FontCache.getTypeface("fontawesome.ttf", context);
        }
        else if (fontName.contentEquals(context.getString(R.string.font_name_source_sans_pro))) {
            switch (textStyle) {
                case Typeface.BOLD: // bold
                    return FontCache.getTypeface("AvenirLTStd-Heavy.otf", context);

                case Typeface.ITALIC: // italic
                    return FontCache.getTypeface("AvenirLTStd-Oblique.otf", context);

                case Typeface.BOLD_ITALIC: // bold italic
                    return FontCache.getTypeface("AvenirLTStd-HeavyOblique.otf", context);

                case 10: // extra light, equals @integer/font_style_extra_light
                    return FontCache.getTypeface("AvenirLTStd-Light.otf", context);

                case Typeface.NORMAL: // regular
                default:
                    return FontCache.getTypeface("AvenirLTStd-Medium.otf", context);
            }
        }
        else {
            return null;
        }
    }
}