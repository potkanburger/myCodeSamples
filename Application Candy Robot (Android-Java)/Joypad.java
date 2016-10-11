package com.potkanburger.candyrobotdeveloppment;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Admin on 20/05/2015.
 */
public class Joypad extends ImageView{
    public Joypad(Context context) {
        super(context);
    }

    public Joypad(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Joypad(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Joypad(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    protected Coordonnee getCenter(){
        return new Coordonnee(this.getWidth()/2, this.getHeight()/2);
    }

    protected Coordonnee ConvertTopLeftToCenter(Coordonnee coordonnee){
        Coordonnee tmpC = new Coordonnee();
        tmpC.setCoords(coordonnee.getCoordX()-this.getWidth()/2,coordonnee.getCoordY()-this.getHeight()/2);
        return tmpC;
    }

    protected float getRayon(){
        return (this.getWidth()/2 + this.getHeight()/2)/2;
    }


    protected float getResizedRayonByJoystick(Joypad joystick){
        return this.getRayon()-joystick.getRayon();
    }

    protected boolean isInJoypad(Coordonnee coordonnee){
        float rayon = this.getRayon();
        float a = coordonnee.getCoordX();
        float b = coordonnee.getCoordY();

        if(a*a+b*b <= rayon*rayon){
            return true;
        }
        else{
            return false;
        }
    }

    protected boolean isInJoypad(Coordonnee coordonnee, Joypad joystick){
        float rayon = this.getResizedRayonByJoystick(joystick);
        float a = coordonnee.getCoordX();
        float b = coordonnee.getCoordY();

        if(a*a+b*b <= rayon*rayon){
            return true;
        }
        else{
            return false;
        }
    }

    protected Coordonnee projeteSurJoypad(Joypad joystick, Coordonnee coordonnee){
        float aX = coordonnee.getCoordX();
        float aY = coordonnee.getCoordY();
        double alphaAngle = Math.asin(aY/Math.sqrt(aX*aX + aY*aY));
        float resizedRayon = getResizedRayonByJoystick(joystick);
        float bX = (float) Math.abs(Math.cos(alphaAngle)*resizedRayon);
        float bY = (float) Math.abs(Math.sin(alphaAngle)*resizedRayon);
        if(aX<0){
            bX = -bX;
        }
        if(aY<0){
            bY = -bY;
        }

        return new Coordonnee(bX, bY);
    }


    protected Coordonnee coordAsRatio(Coordonnee coordonnee, Joypad joystick){
        float rayon = this.getResizedRayonByJoystick(joystick);
        float aX = coordonnee.getCoordX()*1000;
        float aY = coordonnee.getCoordY()*1000;

        return new Coordonnee(aX/rayon, aY/rayon);
    }

    protected  Coordonnee ratioToLocalCoord(Coordonnee coordonnee, Joypad joystick){
        float rayon = this.getResizedRayonByJoystick(joystick);
        float aX = coordonnee.getCoordX()/1000;
        float aY = coordonnee.getCoordY()/1000;

        return new Coordonnee(rayon*aX, rayon*aY);
    }

}
