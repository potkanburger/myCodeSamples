package com.potkanburger.sudoku_durif_genoux_hu;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SquareEditText extends EditText {
    public SquareEditText(Context context) {
        super(context);
    }

    public SquareEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SquareEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthPixels = EditText.MeasureSpec.getSize(widthMeasureSpec);
        int heightPixels = EditText.MeasureSpec.getSize(heightMeasureSpec);
        int taille = Math.max(widthPixels, heightPixels);
        int myWidthMeasureSpec = EditText.MeasureSpec.makeMeasureSpec(taille, MeasureSpec.EXACTLY);
        int myHeightMeasureSpec = EditText.MeasureSpec.makeMeasureSpec(taille, MeasureSpec.EXACTLY);
        super.onMeasure(myWidthMeasureSpec, myHeightMeasureSpec);
    }
}
