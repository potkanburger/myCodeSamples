package com.potkanburger.sudoku_durif_genoux_hu;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.GridView;

public class SquareGridView extends GridView{


    public SquareGridView(Context context) {
        super(context);
    }

    public SquareGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SquareGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    int convertDpToPx(int value){
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int res = Math.round(value*(displayMetrics.densityDpi /160.0f));
        return res;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthPixels = GridView.MeasureSpec.getSize(widthMeasureSpec);
        int heightPixels = GridView.MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = GridView.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = GridView.MeasureSpec.getMode(heightMeasureSpec);
        int taille = Math.min(widthPixels, heightPixels);
        int taille_voulue = taille-convertDpToPx(20);
        taille_voulue = taille_voulue%9!=0 ? taille_voulue : taille_voulue-(taille_voulue%9);
        taille_voulue = taille_voulue + convertDpToPx(20);
        int myWidthMeasureSpec = GridView.MeasureSpec.makeMeasureSpec(taille_voulue, widthMode);
        int myHeightMeasureSpec = GridView.MeasureSpec.makeMeasureSpec(taille_voulue, heightMode);
        super.onMeasure(myWidthMeasureSpec, myHeightMeasureSpec);
    }
}
