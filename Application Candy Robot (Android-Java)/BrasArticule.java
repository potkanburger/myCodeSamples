package com.potkanburger.candyrobotdeveloppment;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * Created by PotkanBurger on 20/05/2015.
 */
public class BrasArticule extends View{

    Coordonnee coords[];

    public BrasArticule(Context context) {
        super(context);
    }

    public BrasArticule(Context context, Coordonnee c[]){
        super(context);
        this.coords = c;
    }


    public BrasArticule(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BrasArticule(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BrasArticule(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(3.0f);
        Paint point = new Paint();
        point.setColor(Color.BLACK);
        //point.setStyle(Paint.Style.FILL_AND_STROKE);
        Coordonnee A = coords[0];
        Coordonnee B = coords[1];
        Coordonnee C = coords[2];
        Coordonnee D = coords[3];

        canvas.drawLine(A.getCoordX(), A.getCoordY(), B.getCoordX(), B.getCoordY(), p);
        canvas.drawLine(B.getCoordX(),B.getCoordY(),C.getCoordX(),C.getCoordY(),p);
        canvas.drawLine(C.getCoordX(), C.getCoordY(), D.getCoordX(), D.getCoordY(), p);
        canvas.drawCircle(B.getCoordX(), B.getCoordY(), getResources().getDimension(R.dimen.point_arbre), point);
        canvas.drawCircle(C.getCoordX(), C.getCoordY(), getResources().getDimension(R.dimen.point_arbre), point);
        canvas.drawCircle(D.getCoordX(), D.getCoordY(), getResources().getDimension(R.dimen.point_arbre), point);
    }


    public void setPoints(Coordonnee coordonnee[]){
        if(coordonnee.length == 4){
            this.coords = coordonnee;
        }
    }
}
