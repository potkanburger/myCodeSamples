/*
 * PACKAGEID: 4C 61 62 61 6B
 * APPLETID: 4C 61 62 61 6B 41 70 70 6C 65 74
 */
package applets;

/*
 * Imported packages
 */
// specific import for Javacard API access
import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;

public class SimpleApplet extends javacard.framework.Applet
{
    // MAIN INSTRUCTION CLASS
    final static byte CLA_SIMPLEAPPLET                = (byte) 0xB0;

    // INSTRUCTIONS
    final static byte INS_ENCRYPT                    = (byte) 0x50;
    final static byte INS_DECRYPT                    = (byte) 0x51;
    final static byte INS_SETKEY                     = (byte) 0x52;
    final static byte INS_HASH                       = (byte) 0x53;
    final static byte INS_RANDOM                     = (byte) 0x54;
    final static byte INS_VERIFYPIN                  = (byte) 0x55;
    final static byte INS_SETPIN                     = (byte) 0x56;
    final static byte INS_RETURNDATA                 = (byte) 0x57;
    final static byte INS_SIGNDATA                   = (byte) 0x58;
    final static byte INS_GETAPDUBUFF                = (byte) 0x59;
    final static byte INS_GENRANDAES                 = (byte) 0x60;

    final static short ARRAY_LENGTH                   = (short) 0xff;
    final static byte  AES_BLOCK_LENGTH               = (short) 0x16;

    final static short SW_BAD_TEST_DATA_LEN          = (short) 0x6680;
    final static short SW_KEY_LENGTH_BAD             = (short) 0x6715;
    final static short SW_CIPHER_DATA_LENGTH_BAD     = (short) 0x6710;
    final static short SW_OBJECT_NOT_AVAILABLE       = (short) 0x6711;
    final static short SW_BAD_PIN                    = (short) 0x6900;

    private   AESKey         m_aesKey = null;
    private   Cipher         m_encryptCipher = null;
    private   Cipher         m_decryptCipher = null;
    private   RandomData     m_secureRandom = null;
    private   MessageDigest  m_hash = null;
    private   OwnerPIN       m_pin = null;
    private   Signature      m_sign = null;
    private   KeyPair        m_keyPair = null;
    private   Key            m_privateKey = null;
    private   Key            m_publicKey = null;

    private   short          m_apduLogOffset = (short) 0;
    // TEMPORARRY ARRAY IN RAM
    private   byte           m_ramArray[] = null;
    // PERSISTENT ARRAY IN EEPROM
    private   byte           m_dataArray[] = null;
    
    private   byte           customPin[] = null;
    
    private   byte           tmpArray[] = null;

    private   boolean        isPinValidated = false;   
    /**
     * LabakApplet default constructor
     * Only this class's install method should create the applet object.
     */
    protected SimpleApplet(byte[] buffer, short offset, byte length)
    {
        // data offset is used for application specific parameter.
        // initialization with default offset (AID offset).
        short dataOffset = offset;
        boolean isOP2 = false;
        customPin = new byte[4];
        Util.arrayFillNonAtomic(customPin, (short) 0, (short) 4, (byte) 0);
        customPin[0] = (byte)0x02; 
        customPin[1] = (byte)0x00; 
        customPin[2] = (byte)0x01; 
        customPin[3] = (byte)0x06;
        // default PIN is 2016
        
        if(length > 9) {
            // Install parameter detail. Compliant with OP 2.0.1.

            // | size | content
            // |------|---------------------------
            // |  1   | [AID_Length]
            // | 5-16 | [AID_Bytes]
            // |  1   | [Privilege_Length]
            // | 1-n  | [Privilege_Bytes] (normally 1Byte)
            // |  1   | [Application_Proprietary_Length]
            // | 0-m  | [Application_Proprietary_Bytes]
            // shift to privilege offset
            dataOffset += (short)( 1 + buffer[offset]);
            // finally shift to Application specific offset
            dataOffset += (short)( 1 + buffer[dataOffset]);
            // go to proprietary data
            dataOffset++;
            m_dataArray = new byte[ARRAY_LENGTH];
            Util.arrayFillNonAtomic(m_dataArray, (short) 0, ARRAY_LENGTH, (byte) 0);
            
            m_ramArray = JCSystem.makeTransientByteArray((short) 260, JCSystem.CLEAR_ON_DESELECT);
            
            m_pin = new OwnerPIN((byte) 5, (byte) 4);
            m_pin.update(customPin, (byte) 0, (byte) 4);
            Util.arrayFillNonAtomic(customPin, (short) 0, (short) 4, (byte) 0);
            // CREATE RSA KEYS AND PAIR
            m_keyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1024); 
            m_keyPair.genKeyPair();
            
            // CREATE AES KEY OBJECT
            m_aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
            // CREATE OBJECTS FOR CBC CIPHERING
            m_encryptCipher = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);
            m_decryptCipher = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);
            
            m_aesKey.setKey(m_dataArray, (short) 0);
            
            m_secureRandom = RandomData.getInstance(RandomData.ALG_SECURE_RANDOM);

            // INIT CIPHERS WITH NEW KEY
            m_encryptCipher.init(m_aesKey, Cipher.MODE_ENCRYPT);
            m_decryptCipher.init(m_aesKey, Cipher.MODE_DECRYPT);
            
            // update flag
            isOP2 = true;

        }
        register();
    }

    /**
     * Method installing the applet.
     * @param bArray the array constaining installation parameters
     * @param bOffset the starting offset in bArray
     * @param bLength the length in bytes of the data parameter in bArray
     */
    public static void install(byte[] bArray, short bOffset, byte bLength) throws ISOException
    {
        new SimpleApplet (bArray, bOffset, bLength);
    }

   
    /**
     * Method processing an incoming APDU.
     * @see APDU
     * @param apdu the incoming APDU
     * @exception ISOException with the response bytes defined by ISO 7816-4
     */
    public void process(APDU apdu) throws ISOException
    {
        // get the APDU buffer
        byte[] apduBuffer = apdu.getBuffer();
        

        // ignore the applet select command dispached to the process
        if (selectingApplet()){
            isPinValidated = false;
            return;
        }

        // APDU instruction parser
        if (apduBuffer[ISO7816.OFFSET_CLA] == CLA_SIMPLEAPPLET) {
            switch ( apduBuffer[ISO7816.OFFSET_INS] )
            {
                case INS_VERIFYPIN: VerifyPIN(apdu); break;
                case INS_SETPIN: SetPIN(apdu); break;
                case INS_SIGNDATA: Sign(apdu); break;
                case INS_GENRANDAES: GenerateRandomAES(apdu); break;
                case INS_ENCRYPT: Encrypt(apdu); break;
                case INS_DECRYPT: Decrypt(apdu); break;
                default :
                    // The INS code is not supported by the dispatcher
                    ISOException.throwIt( ISO7816.SW_INS_NOT_SUPPORTED ) ;
                break ;

            }
        }
        else ISOException.throwIt( ISO7816.SW_CLA_NOT_SUPPORTED);
    }



    // VERIFY PIN
     void VerifyPIN(APDU apdu) {
      byte[]    apdubuf = apdu.getBuffer();
      short     dataLen = apdu.setIncomingAndReceive();

      // VERIFY PIN
      
      if (m_pin.check(apdubuf, ISO7816.OFFSET_CDATA, (byte) dataLen) == false){
          ISOException.throwIt(SW_BAD_PIN);
      }else{
          isPinValidated = true;
      }
      
    }
     
     // SET PIN
     void SetPIN(APDU apdu) {
      byte[]    apdubuf = apdu.getBuffer();
      short     dataLen = apdu.setIncomingAndReceive();
      // SET NEW PIN
      m_pin.update(apdubuf, ISO7816.OFFSET_CDATA, (byte) dataLen);
      
    }
     

    void Sign(APDU apdu) {
     byte[]    apdubuf = apdu.getBuffer();
     short     dataLen = apdu.setIncomingAndReceive();
     short     signLen = 0;

     if(!m_pin.isValidated() || isPinValidated)
         ISOException.throwIt(SW_BAD_PIN);
        // STARTS KEY GENERATION PROCESS

        // OBTAIN KEY REFERENCES
        m_publicKey = m_keyPair.getPublic();
        m_privateKey = m_keyPair.getPrivate();

        // CREATE SIGNATURE OBJECT
        Signature m_sign = Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1, false);

        // INIT WITH PRIVATE KEY
        m_sign.init(m_privateKey, Signature.MODE_SIGN);

        // SIGN INCOMING BUFFER
        signLen = m_sign.sign(apdubuf, ISO7816.OFFSET_CDATA, (byte) dataLen, m_ramArray, (byte) 0);

        // COPY ENCRYPTED DATA INTO OUTGOING BUFFER
        Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, signLen);

        // SEND OUTGOING BUFFER
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, signLen);
   }
    
    
    // SET ENCRYPTION & DECRYPTION KEY
    void GenerateRandomAES(APDU apdu) {
        
      tmpArray = new byte[KeyBuilder.LENGTH_AES_256];
      m_secureRandom.generateData(tmpArray, (short) 0, KeyBuilder.LENGTH_AES_256);
      // SET KEY VALUE
      m_aesKey.setKey(tmpArray, (short) 0);
      Util.arrayFillNonAtomic(tmpArray, (short) 0, KeyBuilder.LENGTH_AES_256, (byte) 0);   
      
      
      // INIT CIPHERS WITH NEW KEY
      m_encryptCipher.init(m_aesKey, Cipher.MODE_ENCRYPT);
      m_decryptCipher.init(m_aesKey, Cipher.MODE_DECRYPT);
    }
   
    // ENCRYPT INCOMING BUFFER
     void Encrypt(APDU apdu) {
      byte[]    apdubuf = apdu.getBuffer();
      short     dataLen = apdu.setIncomingAndReceive();
      short     i;

      // CHECK EXPECTED LENGTH (MULTIPLY OF 64 bites)
      if ((dataLen % 8) != 0){
          ISOException.throwIt(SW_CIPHER_DATA_LENGTH_BAD);
      }

      // ENCRYPT INCOMING BUFFER
      m_encryptCipher.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);

      // COPY ENCRYPTED DATA INTO OUTGOING BUFFER
      Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, dataLen);

      // SEND OUTGOING BUFFER
      apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, dataLen);
    }

    // DECRYPT INCOMING BUFFER
    void Decrypt(APDU apdu) {
      byte[]    apdubuf = apdu.getBuffer();
      short     dataLen = apdu.setIncomingAndReceive();
      short     i;

      // CHECK EXPECTED LENGTH (MULTIPLY OF 64 bites)
      if ((dataLen % 8) != 0) ISOException.throwIt(SW_CIPHER_DATA_LENGTH_BAD);

      // ENCRYPT INCOMING BUFFER
      m_decryptCipher.doFinal(apdubuf, ISO7816.OFFSET_CDATA, dataLen, m_ramArray, (short) 0);

      // COPY ENCRYPTED DATA INTO OUTGOING BUFFER
      Util.arrayCopyNonAtomic(m_ramArray, (short) 0, apdubuf, ISO7816.OFFSET_CDATA, dataLen);

      // SEND OUTGOING BUFFER
      apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, dataLen);
    }
}

