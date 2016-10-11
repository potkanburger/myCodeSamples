package com.potkanburger.candyrobotdeveloppment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by PotkanBurger on 13/06/2015.
 */
public class PinceBras extends View{

    private float taille = 0.0f;
    private float ouverture = 100.0f;
    private Coordonnee basePince = null;

    public PinceBras(Context context){
        super(context);
        this.taille = 0.0f;
        this.ouverture = 100.0f;
        this.basePince = new Coordonnee();
    }

    public PinceBras(Context context, float taillePince, float percentOpening){
        super(context);
        this.taille = taillePince;
        if(percentOpening>=0.0f && percentOpening<=100.0f){
            this.ouverture = percentOpening;
        }
        else{
            this.ouverture = 100.0f;
        }
        this.basePince = new Coordonnee();
    }


    protected float getOuverture(){
        return this.ouverture;
    }

    protected float getTaille(){
        return this.taille;
    }

    protected Coordonnee getBasePince(){
        return this.basePince;
    }

    protected void setBasePince(Coordonnee basePince){
        this.basePince = basePince;
    }

    protected void setTaille(float t){
        this.taille = t;
    }

    protected void setOuverture(float ouverture){
        this.ouverture = ouverture;
    }

    protected double getAlphaByOuverture(){
        //Ouverture maxi = Pi/4 des deux côtés
        return (this.ouverture * Math.PI)/400;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(4.0f);

        double angleOuverture = this.getAlphaByOuverture();
        Coordonnee pinceGauche = new Coordonnee();
        pinceGauche.setCoordX((float) Math.cos((Math.PI / 2) + angleOuverture) * this.taille + basePince.getCoordX());
        pinceGauche.setCoordY(basePince.getCoordY() - (float) Math.sin((Math.PI / 2) + angleOuverture) * this.taille);

        Coordonnee pinceDroite = new Coordonnee();
        pinceDroite.setCoordX((float) Math.cos((Math.PI/2) - angleOuverture)*this.taille+basePince.getCoordX());
        pinceDroite.setCoordY(basePince.getCoordY() - (float) Math.sin((Math.PI/2) - angleOuverture) * this.taille);

        canvas.drawLine(basePince.getCoordX(), basePince.getCoordY(), pinceGauche.getCoordX(), pinceGauche.getCoordY(), p);
        canvas.drawLine(basePince.getCoordX(), basePince.getCoordY(), pinceDroite.getCoordX(), pinceDroite.getCoordY(),p);
    }
}
