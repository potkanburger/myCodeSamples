package com.potkanburger.candyrobotdeveloppment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by PotkanBurger on 09/06/2015.
 */
public class BaseBras extends View {

    private float rayon = 0.0f;
    private double alpha_cos = 0.0;
    private double alpha_sin = 0.0;
    private Coordonnee center = null;

    public BaseBras(Context context){
        super(context);
        this.rayon = 0.0f;
        this.alpha_cos = Math.PI/2;
        this.alpha_sin = Math.PI/2;
        this.center = new Coordonnee();
    }

    public BaseBras(Context context, float rayon){
        super(context);
        this.rayon = rayon;
        this.alpha_cos = Math.PI/2;
        this.alpha_sin = Math.PI/2;
        this.center = new Coordonnee();
    }

    public BaseBras(Context context, float rayon, Coordonnee center){
        super(context);
        this.rayon = rayon;
        this.alpha_cos = Math.PI/2;
        this.alpha_sin = Math.PI/2;
        this.center = center;
    }

    public BaseBras(Context context, float rayon, double acos, double asin){
        super(context);
        this.rayon = rayon;
        this.alpha_cos = acos;
        this.alpha_sin = asin;
        this.center = new Coordonnee();
    }

    public BaseBras(Context context, float rayon, double acos, double asin, Coordonnee center){
        super(context);
        this.rayon = rayon;
        this.alpha_cos = acos;
        this.alpha_sin = asin;
        this.center = center;
    }

    protected double getAlpha_cos() {
        return this.alpha_cos;
    }

    protected double getAlpha_sin(){
        return this.alpha_sin;
    }

    protected double getRayon(){
        return this.rayon;
    }

    protected Coordonnee getCenter(){
        return this.center;
    }

    protected void setRayon(float rayon) {
        this.rayon = rayon;
    }

    protected void setAlphas(double alpha_cos, double alpha_sin) {
        this.alpha_cos = alpha_cos;
        this.alpha_sin = alpha_sin;
    }

    protected void setCenter(Coordonnee coordonnee){
        this.center = coordonnee;
    }


    protected boolean isInBase(Coordonnee coordonnee){
        float Cx = coordonnee.getCoordX()-this.center.getCoordX();
        float Cy = this.center.getCoordY()-coordonnee.getCoordY();
        if(Cx*Cx+Cy*Cy<=this.getRayon()*this.getRayon()){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(3.0f);
        p.setStyle(Paint.Style.STROKE);
        Paint p_rayon = new Paint();
        p_rayon.setColor(Color.BLUE);
        p_rayon.setStrokeWidth(4.0f);

        Coordonnee coordPos = new Coordonnee();
        coordPos.setCoordX((float) Math.cos(this.alpha_cos) * this.rayon + this.center.getCoordX());
        coordPos.setCoordY(this.center.getCoordY() - (float) Math.sin(this.alpha_sin) * this.rayon);


        //limite 180 degrés
        if(coordPos.getCoordY()>this.center.getCoordY()){
            coordPos.setCoordY(this.center.getCoordY());
            if(coordPos.getCoordX()<this.center.getCoordX()){
                coordPos.setCoordX(this.center.getCoordX()-this.rayon);
            }else {
                coordPos.setCoordX(this.center.getCoordX()+this.rayon);
            }
        }

        /*double deltAlpha = Math.PI/12;
        Coordonnee flecheP1 = new Coordonnee();
        flecheP1.setCoordX((float)Math.cos(this.alpha_cos+deltAlpha)*(this.rayon)+this.center.getCoordX());
        flecheP1.setCoordY(this.center.getCoordY() - (float) Math.sin(this.alpha_sin+deltAlpha) * (this.rayon));

        Coordonnee flecheP2 = new Coordonnee();
        flecheP2.setCoordX((float)Math.cos(this.alpha_cos-deltAlpha)*(this.rayon)+this.center.getCoordX());
        flecheP2.setCoordY(this.center.getCoordY() - (float) Math.sin(this.alpha_sin-deltAlpha) * (this.rayon));*/

        canvas.drawCircle(this.center.getCoordX(), this.center.getCoordY(), this.rayon, p);
        canvas.drawLine(this.center.getCoordX(), this.center.getCoordY(), coordPos.getCoordX(), coordPos.getCoordY(), p_rayon);
        /*canvas.drawLine(coordPos.getCoordX(), coordPos.getCoordY(), flecheP1.getCoordX(), flecheP1.getCoordY(), p_rayon);
        canvas.drawLine(coordPos.getCoordX(), coordPos.getCoordY(),flecheP2.getCoordX(), flecheP2.getCoordY(), p_rayon);*/
    }
}
