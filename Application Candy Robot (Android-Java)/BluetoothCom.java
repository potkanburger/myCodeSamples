package com.potkanburger.candyrobotdeveloppment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by PotkanBurger on 18/06/2015.
 */
public class BluetoothCom implements Runnable {
    @Override
    public void run() {
        ViewJoieBaton.activatedBluetooth = true;
        UUID robotUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        String macAdress = "00:06:66:64:64:59";
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAdress);

        try {
            bluetoothAdapter.startDiscovery();
            BluetoothSocket maChaussette = null;

            maChaussette = bluetoothDevice.createRfcommSocketToServiceRecord(robotUUID);
            bluetoothAdapter.cancelDiscovery();
            maChaussette.connect();
            InputStream reception = maChaussette.getInputStream();
            OutputStream envoi = maChaussette.getOutputStream();


            while (ViewJoieBaton.bluetoothStatus() && !ViewJoieBaton.isPaused) {
                Coordonnee tmp = new Coordonnee();
                tmp.setCoords(ViewJoieBaton.toSend);
                tmp.setCoords(Coordonnee.centeredToRobotCoords(tmp, 15));

                int[] values = {(int)tmp.getCoordX(), (int)tmp.getCoordY()};

                byte lecture = (byte)0x1F;

                for(int i=0;i<2;i++){
                    int coord = values[i];
                    coord+=15;
                    byte val = (byte)Math.abs(coord);
                    byte mask;
                    switch(i){
                        case 0:
                            mask= (byte)0x00;
                            break;
                        case 1:
                            mask= (byte)0x20;
                            break;

                        default:
                            mask= (byte)0x00;
                            break;
                    }
                    byte bRes = (byte)(val|mask);
                    byte[] byteEnvoi = {bRes};
                    envoi.write(byteEnvoi);
                }

                for(int i=0;i<ViewJoieBaton.brasAlphas.length;i++){

                    byte angle = (byte)((int)(Math.round((ViewJoieBaton.brasAlphas[i] * 30)/Math.PI)));
                    byte mask;
                    switch(i){
                        case 0:
                            mask = (byte)0x40;
                            break;

                        case 1:
                            mask = (byte)0x60;
                            break;

                        case 2:
                            mask = (byte)0x80;
                            break;

                        case 3:
                            mask = (byte)0xA0;
                            break;

                        default:
                            mask = (byte)0x00;
                            break;
                    }

                    byte res = (byte)(angle|mask);
                    byte[] byteEnvoi = {res};
                    envoi.write(byteEnvoi);
                }

                byte angle = (byte)((int)(Math.abs(ViewJoieBaton.rotation)/3));
                byte mask = (byte)0xC0;
                byte res = (byte)(angle|mask);
                byte[] byteEnvoi = {res};
                envoi.write(byteEnvoi);

                byte ouverturePince = (byte)((int)(ViewJoieBaton.ouverture*3/10));
                byte maskP = (byte)0xE0;
                byte resP = (byte)(ouverturePince|maskP);
                byte[] byteEnvoiP = {resP};
                envoi.write(byteEnvoiP);


                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            envoi.flush();
            envoi.close();
            reception.close();
            envoi = null;
            reception = null;
            maChaussette.close();
            maChaussette = null;
            ViewJoieBaton.activatedBluetooth = false;

        } catch (IOException e) {
            e.printStackTrace();
            ViewJoieBaton.activatedBluetooth = false;
        }
    }
}
