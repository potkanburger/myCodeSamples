package com.potkanburger.candyrobotdeveloppment;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class ViewJoieBaton extends Activity{

    private Joypad monTouchPad = null;
    private Joypad joieStick = null;
    private Coordonnee coordPos = new Coordonnee();
    private Coordonnee centeredPos = new Coordonnee(0.0f, 0.0f);
    private boolean joieStick_triggered = false;
    private EditText adrServeur = null;


    protected static Coordonnee toSend = new Coordonnee(0.0f, 0.0f);
    private Socket socket = null;
    private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;
    //tests
    private CheckBox tailleDeLaJoie = null;
    private String adresse = null;

    private RelativeLayout brasArticuleLayout = null;
    private RelativeLayout baseBras = null;
    private RelativeLayout pinceBras = null;
    private ImageView pinceRotation = null;

    private SeekBar ouvrePince = null;
    private Coordonnee A = null;
    private Coordonnee B = null;
    private Coordonnee C = null;
    private Coordonnee D = null;
    private float rayonBras = 0.0f;
    private float maxW = 0.0f;
    private float maxH = 0.f;
    private float maxWbras = 0.0f;
    private float maxHbras = 0.f;

    private float baseArbreRayon = 0.0f;

    private float taillePince = 0.0f;
    protected static float ouverture = 100.0f;

    private BrasArticule vueBras = null;
    private BaseBras vueBaseBras = null;
    private PinceBras vuePinceBras = null;


    private ImageView statusCo = null;
    private Button boutonCo = null;
    private static int connection = 0;

    protected static double brasAlphas[] = new double[4];
    protected static float rotation = 0.0f;
    protected int isRotating = 0;
    //0 = no, 1 = goRight, 2 = goLeft

    int touchedCoord = -1;

    protected static boolean isPaused = false;

    protected static boolean bluetoothStatus(){
        return (connection==2);
    }

    protected static boolean activatedBluetooth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_joie_baton);
        //init
        monTouchPad = (Joypad) findViewById(R.id.toucheMoi);
        joieStick = (Joypad) findViewById(R.id.joiebaton);
        adrServeur = (EditText) findViewById(R.id.ipServeur);


        tailleDeLaJoie = (CheckBox) findViewById(R.id.sizeJoystick);
        statusCo = (ImageView) findViewById(R.id.etatConnection);
        boutonCo = (Button) findViewById(R.id.boutonConnect);



        adresse = adrServeur.getText().toString();



        //Dessin Bras
        brasArticuleLayout = (RelativeLayout) findViewById(R.id.brasArticule);
        baseBras = (RelativeLayout) findViewById(R.id.baseBras);
        pinceBras = (RelativeLayout) findViewById(R.id.pinceBras);
        ouvrePince = (SeekBar) findViewById(R.id.cursorPinceBras);
        pinceRotation = (ImageView) findViewById(R.id.rotationPince);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float WindowH= getResources().getDisplayMetrics().heightPixels;
        float WindowW= getResources().getDisplayMetrics().widthPixels;

        maxW = WindowW-getResources().getDimension(R.dimen.activity_horizontal_margin)-getResources().getDrawable(R.drawable.joypad_small).getIntrinsicWidth();
        maxH = getResources().getDrawable(R.drawable.joypad_small).getIntrinsicHeight();

        maxWbras = WindowW-(2*getResources().getDimension(R.dimen.activity_horizontal_margin));
        maxHbras = maxWbras/2 + 3*getResources().getDimension(R.dimen.point_arbre);

        ViewGroup.LayoutParams paramsBras = brasArticuleLayout.getLayoutParams();
        paramsBras.height = (int)maxHbras;
        paramsBras.width = (int)maxWbras;

        ViewGroup.LayoutParams paramsPinceRotation = pinceRotation.getLayoutParams();
        paramsPinceRotation.height = (int)getResources().getDrawable(R.drawable.pince).getIntrinsicWidth();

        baseArbreRayon = getResources().getDimension(R.dimen.base_arbre_rayon);
        ViewGroup.LayoutParams paramsBaseBras = baseBras.getLayoutParams();
        paramsBaseBras.height = (int)(2*baseArbreRayon+2*getResources().getDimension(R.dimen.base_arbre_marges));
        paramsBaseBras.width = (int)maxW;

        taillePince =  maxW/((float) Math.sqrt(2.0)*2);

        ViewGroup.LayoutParams paramsPinceBras = pinceBras.getLayoutParams();
        paramsPinceBras.height = (int)taillePince;
        paramsPinceBras.width = (int)maxW;

        ViewGroup.LayoutParams paramsSeekbarPince = ouvrePince.getLayoutParams();
        paramsSeekbarPince.width = (int)(maxW-2*getResources().getDimension(R.dimen.ouverture_pince_marges));

        rayonBras = maxWbras/((float) Math.sqrt(2.0)*4);



        //Séquence d'init bras
        A = new Coordonnee(maxWbras/2+2, maxHbras-20);
        B = new Coordonnee();
        B.setCoordsFromPoint(A, rayonBras, Math.PI / 4);
        C = new Coordonnee();
        C.setCoordsFromPoint(B, rayonBras, 3*Math.PI/4);
        D = new Coordonnee();
        D.setCoordsFromPoint(C, rayonBras, 3 * Math.PI / 4);

        //base bras
        brasAlphas[0] =  Math.PI/2;

        //bras articulé
        brasAlphas[1] = 0;
        brasAlphas[2] = Math.PI;
        brasAlphas[3] = Math.PI;

        final Coordonnee positionsBras[] = {A,B,C,D};
        vueBras = new BrasArticule(this, positionsBras);
        brasArticuleLayout.addView(vueBras);


        //init base
        Coordonnee centerBaseBras = new Coordonnee(maxW/2, baseArbreRayon+getResources().getDimension(R.dimen.base_arbre_marges));
        vueBaseBras = new BaseBras(this, baseArbreRayon, centerBaseBras);
        baseBras.addView(vueBaseBras);


        //init pince
        Coordonnee basePince = new Coordonnee(maxW/2, taillePince);
        vuePinceBras = new PinceBras(this, taillePince, ouverture);
        vuePinceBras.setBasePince(basePince);
        pinceBras.addView(vuePinceBras);



        //Communication

        new Thread(new Communication()) {
        }.start();


        //Listeners


        boutonCo.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                adresse = adrServeur.getText().toString();
                isPaused = false;
                new Thread(new Communication()) {
                }.start();
            }
        });

        pinceRotation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    isRotating = 0;
                }
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    if(rotation>-90 && event.getX()<pinceRotation.getDrawable().getBounds().centerX()){
                        isRotating = 2;
                        new Thread(new RotatePince()).start();
                    }
                    else if(rotation<0 && event.getX()>pinceRotation.getDrawable().getBounds().centerX()){
                        isRotating = 1;
                        new Thread(new RotatePince()).start();
                    }
                    else{
                        isRotating = 0;
                    }
                }
                return true;
            }
        });


        brasArticuleLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP) || event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                    touchedCoord = -1;
                } else {
                    if (touchedCoord == -1) {
                        for (int i = 1; i < positionsBras.length; i++) {
                            float sizePoint = getResources().getDimension(R.dimen.point_arbre);
                            if (Math.abs(positionsBras[i].getCoordX() - event.getX()) <= (2 * sizePoint) / 3 && Math.abs(positionsBras[i].getCoordY() - event.getY()) <= (2 * sizePoint) / 3) {
                                touchedCoord = i;
                                break;
                            }
                        }


                    } else {

                        Coordonnee posDoigt = new Coordonnee(event.getX(), event.getY());

                        double angleAlfois = 0.0;
                        double nextAngleAlfois = 0.0;
                        if (touchedCoord < positionsBras.length - 1) {
                            angleAlfois = positionsBras[touchedCoord].getAlpha(rayonBras, positionsBras[touchedCoord + 1]);
                        }
                        brasAlphas[touchedCoord] = (positionsBras[touchedCoord - 1].getAlpha(rayonBras, posDoigt) - Math.PI / 4) * 2;
                        positionsBras[touchedCoord].setCoordsFromPoint(positionsBras[touchedCoord - 1], rayonBras, positionsBras[touchedCoord - 1].getAlpha(rayonBras, posDoigt));

                        for (int i = touchedCoord + 1; i < positionsBras.length; i++) {

                            if (i < positionsBras.length - 1) {
                                nextAngleAlfois = positionsBras[i].getAlpha(rayonBras, positionsBras[i + 1]);
                            }
                            positionsBras[i].setCoordsFromPoint(positionsBras[i - 1], rayonBras, angleAlfois);
                            angleAlfois = nextAngleAlfois;
                        }


                    }
                }

                vueBras.setPoints(positionsBras);
                vueBras.invalidate();
                return true;
            }

        });

        baseBras.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Coordonnee coordEvent = new Coordonnee(event.getX(), event.getY());
                //Pour limiter le toucher dans le cercle
                //if (vueBaseBras.isInBase(coordEvent))

                double alphas[] = vueBaseBras.getCenter().getUnlimitedAlphas((float) vueBaseBras.getRayon(), coordEvent);
                vueBaseBras.setAlphas(alphas[0], alphas[1]);
                if (alphas[1] < 0) {
                    if (alphas[0] >= (Math.PI / 2)) {
                        brasAlphas[0] = Math.PI;
                    } else {
                        brasAlphas[0] = 0.0;
                    }
                } else {
                    brasAlphas[0] = alphas[0];
                }

                vueBaseBras.invalidate();
                return true;
            }
        });


        ouvrePince.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ouverture = (float)progress;
                vuePinceBras.setOuverture(progress);
                vuePinceBras.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        monTouchPad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP) || event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                    coordPos.setCoords(monTouchPad.getCenter());
                    joieStick.setVisibility(View.VISIBLE);
                    joieStick_triggered = false;
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(100);
                    centeredPos.setCoords(Coordonnee.absoluteToCenteredCoords(coordPos, monTouchPad));
                    toSend.setCoords(monTouchPad.coordAsRatio(centeredPos, joieStick));
                } else {
                    coordPos.setCoords(event.getX(), event.getY());
                    joieStick.setVisibility(View.INVISIBLE);
                    centeredPos.setCoords(Coordonnee.absoluteToCenteredCoords(coordPos, monTouchPad));
                    if (!joieStick_triggered && monTouchPad.isInJoypad(centeredPos)) {
                        joieStick_triggered = true;
                        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(50);
                    }
                }


                if (monTouchPad.isInJoypad(centeredPos, joieStick)) {
                    toSend.setCoords(monTouchPad.coordAsRatio(Coordonnee.absoluteToCenteredCoords(coordPos, monTouchPad), joieStick));
                    Coordonnee tmpCoordPlace = new Coordonnee();
                    tmpCoordPlace.setCoords(joieStick.ConvertTopLeftToCenter(coordPos));
                    joieStick.setX(tmpCoordPlace.getCoordX());
                    joieStick.setY(tmpCoordPlace.getCoordY());
                    joieStick.setVisibility(View.VISIBLE);
                } else {
                    Coordonnee tmpCoordPlace = new Coordonnee();
                    tmpCoordPlace.setCoords(monTouchPad.projeteSurJoypad(joieStick, centeredPos));
                    toSend.setCoords(monTouchPad.coordAsRatio(tmpCoordPlace, joieStick));
                    tmpCoordPlace.setCoords(tmpCoordPlace.centeredToAbsoluteCoords(tmpCoordPlace, monTouchPad));
                    tmpCoordPlace.setCoords(joieStick.ConvertTopLeftToCenter(tmpCoordPlace));
                    joieStick.setX(tmpCoordPlace.getCoordX());
                    joieStick.setY(tmpCoordPlace.getCoordY());
                    joieStick.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        tailleDeLaJoie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tailleDeLaJoie.isChecked()) {
                    joieStick.setImageResource(R.drawable.joystick_big);
                } else {
                    joieStick.setImageResource(R.drawable.joystick);
                }
            }
        });

        adrServeur.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adresse = adrServeur.getText().toString();
                isPaused = false;
                new Thread(new Communication()) {
                }.start();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public class RotatePince implements Runnable{
        @Override
        public void run() {
            while(isRotating>0 && !isPaused){
                if(isRotating==2 && rotation>-90){
                    rotation-=3;
                }
                else if(isRotating==1 && rotation<0){
                    rotation+=3;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pinceRotation.setRotation(rotation);
                    }
                });

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public class Communication implements Runnable{
        @Override
        public void run() {
            boolean goRun = true;
            String receivedData = "";
            while(goRun && !isPaused){

                try {
                    socket = new Socket(adresse,8888);
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream.writeUTF(String.valueOf(toSend.getCoordX()) + ";" + String.valueOf(toSend.getCoordY()));

                    receivedData = String.valueOf(dataInputStream.readInt());

                    if(receivedData.equals("0") || receivedData.equals("1") || receivedData.equals("2")){
                       int tmp = Integer.valueOf(receivedData);
                       if(tmp != connection){
                           connection = tmp;
                           if(tmp==2 && !activatedBluetooth){
                               new Thread(new BluetoothCom()).start();
                           }

                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   switch(connection){
                                       case 0:
                                           statusCo.setImageResource(R.drawable.no_connection);
                                           boutonCo.setVisibility(View.VISIBLE);
                                           break;

                                       case 1:
                                           statusCo.setImageResource(R.drawable.pause_connection);
                                           boutonCo.setVisibility(View.INVISIBLE);
                                           break;

                                       case 2:
                                           statusCo.setImageResource(R.drawable.accept_connection);
                                           boutonCo.setVisibility(View.INVISIBLE);
                                           break;

                                       default:
                                           statusCo.setImageResource(R.drawable.no_connection);
                                           boutonCo.setVisibility(View.VISIBLE);
                                           break;
                                   }

                               }
                           });

                       }
                    }

                    socket.close();
                } catch (IOException e) {
                        e.printStackTrace();
                        goRun = false;
                        connection = 0;
                        activatedBluetooth = false;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusCo.setImageResource(R.drawable.no_connection);
                                boutonCo.setVisibility(View.VISIBLE);

                            }
                        });


                }
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
