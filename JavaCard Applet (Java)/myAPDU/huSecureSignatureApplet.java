package myAPDU;

import applets.SimpleApplet;
import java.util.Scanner;
import java.util.regex.Pattern;
import javacard.framework.Util;
import javax.smartcardio.ResponseAPDU;

/**
 * @author original: xsvenda, modifications: hu.sebastien
 */
public class huSecureSignatureApplet {
    static CardMngr cardManager = new CardMngr();

    private static byte APPLET_AID[] = {(byte) 0x4C, (byte) 0x61, (byte) 0x62, (byte) 0x61, (byte) 0x6B,
        (byte) 0x41, (byte) 0x70, (byte) 0x70, (byte) 0x6C, (byte) 0x65, (byte) 0x74};
    
    private static byte SELECT_SIMPLEAPPLET[] = {(byte) 0x00, (byte) 0xa4, (byte) 0x04, (byte) 0x00, (byte) 0x0b, 
        (byte) 0x4C, (byte) 0x61, (byte) 0x62, (byte) 0x61, (byte) 0x6B,
        (byte) 0x41, (byte) 0x70, (byte) 0x70, (byte) 0x6C, (byte) 0x65, (byte) 0x74};

    private final byte selectCM[] = {
        (byte) 0x00, (byte) 0xa4, (byte) 0x04, (byte) 0x00, (byte) 0x07, (byte) 0xa0, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x18, (byte) 0x43, (byte) 0x4d};
    
    private static byte ERROR[] = {(byte) 0x69, (byte) 0x00};
    private static byte SUCCESS[] = {(byte) 0x90, (byte) 0x00};
    
    private static int NB_SIGNATURE = 3;
    
    public static void main(String[] args) {
        try {            
            if (cardManager.ConnectToCard()) {
                
                // Select our application on card
                cardManager.sendAPDU(SELECT_SIMPLEAPPLET);
           
                // prepare proper APDU command
                short additionalDataLenPin = 4; //Pin code data has a length of 4 (4 digits, each one is one byte in hexa)
                short additionalDataLen = 1;

                //Building apdu request with Instruction 0x58 to ask for signature
                byte apdu[] = new byte[CardMngr.HEADER_LENGTH + additionalDataLen];
                apdu[CardMngr.OFFSET_CLA] = (byte) 0xB0;
                apdu[CardMngr.OFFSET_INS] = (byte) 0x58;
                apdu[CardMngr.OFFSET_P1] = (byte) 0x00;
                apdu[CardMngr.OFFSET_P2] = (byte) 0x00;
                apdu[CardMngr.OFFSET_LC] = (byte) additionalDataLen;
                apdu[CardMngr.OFFSET_DATA] = (byte) 0x44;

                System.out.println("Asking for card signature without insering PIN code.");
                ResponseAPDU response = cardManager.sendAPDU(apdu);

                if(testResponse(response.getBytes(), SUCCESS))
                    System.out.println("Signature was succesfull");
                else
                    System.out.println("Signature failed");

                // Scanning user input for pin code. Pin code must be and only be 4 digits.
                Scanner scan = new Scanner(System.in);
                System.out.println("Insert PIN code: [hint: 2016]");
                String s = scan.next();
                while(!Pattern.matches("[0-9]{4}", s)){
                    System.out.println("PIN code shoud be composed of only 4 digits. Re-enter PIN code: [hint: 2016]");
                    s = scan.next();
                }

                // Building apdu request with Instruction 0x55 to check the pin code entered
                byte apduTryPin[] = new byte[CardMngr.HEADER_LENGTH + additionalDataLenPin];;
                apduTryPin[CardMngr.OFFSET_CLA] = (byte) 0xB0;
                apduTryPin[CardMngr.OFFSET_INS] = (byte) 0x55;
                apduTryPin[CardMngr.OFFSET_P1] = (byte) 0x00;
                apduTryPin[CardMngr.OFFSET_P2] = (byte) 0x00;
                apduTryPin[CardMngr.OFFSET_LC] = (byte) additionalDataLenPin;

                for(int i=0;i<additionalDataLenPin;i++){
                    apduTryPin[CardMngr.OFFSET_DATA+i] = Byte.valueOf(String.valueOf(s.charAt(i)));
                }



                response = cardManager.sendAPDU(apduTryPin);

                if(testResponse(response.getBytes(), SUCCESS)){
                    System.out.println("Valid PIN !");
                }else{
                    while(!testResponse(response.getBytes(), SUCCESS)){
                        System.out.println("Wrong PIN. Re-enter PIN: [hint: 2016]");
                        s = scan.next();
                        while(!Pattern.matches("[0-9]{4}", s)){
                            System.out.println("PIN code shoud be composed of only 4 digits. Re-enter PIN code: [hint: 2016]");
                            s = scan.next();
                        }
                        for(int i=0;i<additionalDataLenPin;i++){
                            apduTryPin[CardMngr.OFFSET_DATA+i] = Byte.valueOf(String.valueOf(s.charAt(i)));
                        }
                        response = cardManager.sendAPDU(apduTryPin);
                    }
                }
                long t1 = System.nanoTime();
                response = cardManager.sendAPDU(apdu);
                long t2 = System.nanoTime();

                System.out.println("Signature time: "+String.valueOf((t2-t1)/1000000)+" ms");
                if(testResponse(response.getBytes(), SUCCESS))
                    System.out.println("Signing was succesfull");
                else
                    System.out.println("Signature failed");

                t1 = System.nanoTime();
                for(int signI=0; signI<NB_SIGNATURE; signI++){
                    apdu[CardMngr.OFFSET_DATA] = (byte) signI;
                    response = cardManager.sendAPDU(apdu);
                }
                t2 = System.nanoTime();
                System.out.println("Average signature time: "+String.valueOf((t2-t1)/(1000000*NB_SIGNATURE))+" ms");

                //Building apdu request with Instruction 0x60 to generate a random AES key on-card
                byte genAES[] = new byte[CardMngr.HEADER_LENGTH + additionalDataLen];
                genAES[CardMngr.OFFSET_CLA] = (byte) 0xB0;
                genAES[CardMngr.OFFSET_INS] = (byte) 0x60;
                genAES[CardMngr.OFFSET_P1] = (byte) 0x00;
                genAES[CardMngr.OFFSET_P2] = (byte) 0x00;
                genAES[CardMngr.OFFSET_LC] = (byte) additionalDataLen;
                genAES[CardMngr.OFFSET_DATA] = (byte) 0x00;

                response = cardManager.sendAPDU(genAES);

                int dataLength = 16;
                byte[] data = new byte[dataLength];

                // create a "testing" data
                for(int i=0; i<dataLength;i++){
                    data[i] = (byte) ((i*(i+1))%16);
                }
                System.out.println("Data to encrypt: "+ data.toString());
                byte encryptAES[] = new byte[CardMngr.HEADER_LENGTH + dataLength];
                encryptAES[CardMngr.OFFSET_CLA] = (byte) 0xB0;
                encryptAES[CardMngr.OFFSET_INS] = (byte) 0x50;
                encryptAES[CardMngr.OFFSET_P1] = (byte) 0x01;
                encryptAES[CardMngr.OFFSET_P2] = (byte) 0x00;
                encryptAES[CardMngr.OFFSET_LC] = (byte) dataLength;

                for(int i=0;i<dataLength;i++){
                    encryptAES[CardMngr.OFFSET_DATA+i] = data[i];
                }

                response = cardManager.sendAPDU(encryptAES);

                int dataLengthB = response.getBytes().length-2;

                byte decryptAES[] = new byte[CardMngr.HEADER_LENGTH + dataLengthB];
                decryptAES[CardMngr.OFFSET_CLA] = (byte) 0xB0;
                decryptAES[CardMngr.OFFSET_INS] = (byte) 0x51;
                decryptAES[CardMngr.OFFSET_P1] = (byte) 0x01;
                decryptAES[CardMngr.OFFSET_P2] = (byte) 0x00;
                decryptAES[CardMngr.OFFSET_LC] = (byte) dataLength;
                for(int i=0;i<dataLengthB;i++){
                    decryptAES[CardMngr.OFFSET_DATA+i] = response.getData()[i];
                }


                response = cardManager.sendAPDU(decryptAES);
                
                
                // compares the data before encryption and the data that went through the encryotion+decryption process
                boolean testEncryption = true;
                if(dataLength != (response.getBytes().length-2)){
                    testEncryption = false;
                }
                for(int i=0;i<dataLength;i++){
                    if(data[i] != response.getData()[i]){
                        testEncryption = false;
                        break;
                    }
                }
                if(testEncryption){
                    System.out.println("Encryption/Decryption was successful");
                }else{
                    System.out.println("Encryption/Decryption failed");
                }
                
                // Measurement of encryption duration
                
                byte eAES[];
                int testAES[] = {16,32,64,128};
                for(int signI=0; signI<4; signI++){
                    data = new byte[testAES[signI]];
                    for(int i=0; i<testAES[signI];i++){
                        data[i] = (byte) ((i*(i+1))%16);
                    }
                    eAES = new byte[CardMngr.HEADER_LENGTH + testAES[signI]];
                    eAES[CardMngr.OFFSET_CLA] = (byte) 0xB0;
                    eAES[CardMngr.OFFSET_INS] = (byte) 0x50;
                    eAES[CardMngr.OFFSET_P1] = (byte) 0x01;
                    eAES[CardMngr.OFFSET_P2] = (byte) 0x00;
                    eAES[CardMngr.OFFSET_LC] = (byte) testAES[signI];
                    for(int i=0;i<testAES[signI];i++){
                        eAES[CardMngr.OFFSET_DATA+i] = data[i];
                    }
                    t1 = System.nanoTime();
                    response = cardManager.sendAPDU(eAES);
                    t2 = System.nanoTime();
                    System.out.println("Encryption duration time (data of "+String.valueOf(testAES[signI])+" bytes): "+String.valueOf((t2-t1)/(1000000))+" ms");
                }
                
                
                cardManager.DisconnectFromCard();
            
            } else {
                System.out.println("Failed to connect to card");
            }
            
        } catch (Exception ex) {
            System.out.println("Exception : " + ex);
        }
     
    }
    
    /**
     * Method testing if the response matches a specified code
     * @param response from the cardManager
     * @param expect is the code the response is compared too
     * @return true if the response code matches the expected one
     */
    private static boolean testResponse(byte[] response, byte[] expect){
        int lengthR = response.length;
        for(int i= 0;i<2;i++){
            if(response[i+lengthR-2] != expect[i])
                return false;
        }
        return true;
    }
    
}
