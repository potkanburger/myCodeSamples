package com.potkanburger.candyrobotdeveloppment;

import android.util.Log;
import android.widget.ImageView;

/**
 * Created by PotkanBurger on 20/05/2015.
 */
public class Coordonnee {

    private float coordX;
    private float coordY;

    public Coordonnee(){
        this.coordX = 0.0f;
        this.coordY = 0.0f;
    }

    public Coordonnee(float a, float b){
        this.coordX = a;
        this.coordY = b;
    }

    protected void setCoords(Coordonnee c){
        this.coordX = c.getCoordX();;
        this.coordY = c.getCoordY();
    }

    protected void setCoords(float a, float b){
        this.coordX = a;
        this.coordY = b;
    }

    protected void setCoordX(float a){
        this.coordX = a;
    }

    protected void setCoordY(float a){
        this.coordY = a;
    }

    protected float getCoordX() {
        return coordX;
    }

    protected float getCoordY() {
        return coordY;
    }

    public static Coordonnee absoluteToCenteredCoords(Coordonnee coordonnee, ImageView imgv){
        Coordonnee tmpC = new Coordonnee();
        tmpC.setCoords(-(imgv.getWidth() / 2) + coordonnee.getCoordX(), imgv.getHeight() / 2 - coordonnee.getCoordY());
        return tmpC;
    }


    public static Coordonnee centeredToAbsoluteCoords(Coordonnee coordonnee, ImageView imgv){
        Coordonnee tmpC = new Coordonnee();
        tmpC.setCoords((imgv.getWidth() / 2) + coordonnee.getCoordX(), imgv.getHeight() / 2 - coordonnee.getCoordY());
        return tmpC;
    }


    public static Coordonnee centeredToRobotCoords(Coordonnee coordonnee, int valueRayon){
        float cx = coordonnee.getCoordX();
        float cy = coordonnee.getCoordY();
        Coordonnee res = new Coordonnee();
        res.setCoordX((cx*valueRayon)/1000);
        res.setCoordY((cy*valueRayon)/1000);
        return res;
    }

    public void setCoordsFromPoint(Coordonnee pointA, float rayon, double alphaAngle){
        float x = pointA.getCoordX();
        float y = pointA.getCoordY();
        float Cx = (float)Math.cos(alphaAngle)*rayon;
        float Cy = (float)Math.sin(alphaAngle)*rayon;
        Coordonnee C = new Coordonnee();
        C.setCoords(x+Cx,y-Cy);

        this.setCoords(C);
    }

    public double getAlpha(float rayon, Coordonnee posEvent){
        float p_Cx = this.getCoordX();
        float p_Cy = this.getCoordY();
        float event_Cx = posEvent.getCoordX();
        float event_Cy = posEvent.getCoordY();
        float Cx = 0.0f;
        float Cy = 0.0f;

        if(event_Cx<p_Cx){
            Cx = -(p_Cx-event_Cx);
        }else {
            Cx= event_Cx-p_Cx;
        }

        if(event_Cy<p_Cy){
            Cy = -(event_Cy-p_Cy);
        }else {
            Cy = p_Cy-event_Cy;
        }

        double alpha = Math.acos(Cx / Math.sqrt(Cy * Cy + Cx * Cx));
        if(Math.abs(alpha)<Math.PI/4){
            alpha = Math.PI/4;
        }
        else if(Math.abs(alpha)>3*Math.PI/4){
            alpha = 3*Math.PI/4;
        }
        return alpha;
    }


    public double[] getUnlimitedAlphas(float rayon, Coordonnee posEvent){
        float p_Cx = this.getCoordX();
        float p_Cy = this.getCoordY();
        float event_Cx = posEvent.getCoordX();
        float event_Cy = posEvent.getCoordY();
        float Cx = 0.0f;
        float Cy = 0.0f;

        if(event_Cx<p_Cx){
            Cx = -(p_Cx-event_Cx);
        }else {
            Cx= event_Cx-p_Cx;
        }

        if(event_Cy<p_Cy){
            Cy = -(event_Cy-p_Cy);
        }else {
            Cy = p_Cy-event_Cy;
        }

        double alpha_cos = Math.acos(Cx / Math.sqrt(Cy * Cy + Cx * Cx));
        double alpha_sin = Math.asin(Cy / Math.sqrt(Cy * Cy + Cx * Cx));
        double alphas[] = {alpha_cos, alpha_sin};
        return alphas;
    }

}
