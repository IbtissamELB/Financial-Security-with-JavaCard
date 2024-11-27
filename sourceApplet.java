/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pack;

import javacard.framework.*;

/**
 *
 * @author INPT
 */
public class Compteur24 extends Applet {

    public final static byte CLA = (byte) 0x80;
    public final static byte INS_INC = (byte) 0x00;
    public final static byte INS_DEC = (byte) 0x01;
    public final static byte INS_INI = (byte) 0x02;
    public final static byte INS_LIR = (byte) 0x03;
    private byte compteur;
    /**
     * Installs this applet.
     * 
     * @param bArray
     *            the array containing installation parameters
     * @param bOffset
     *            the starting offset in bArray
     * @param bLength
     *            the length in bytes of the parameter data in bArray
     */
    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new Compteur24();
    }

    /**
     * Only this class's install method should create the applet object.
     */
    protected Compteur24() {
        register();
        compteur = 25;
    }

    /**
     * Processes an incoming APDU.
     * 
     * @see APDU
     * @param apdu
     *            the incoming APDU
     */
    public void process(APDU apdu) {
        byte[] a = apdu.getBuffer();
        if (selectingApplet()){
            return;
        }
        if (a[ISO7816.OFFSET_CLA]!=CLA){
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
        switch (a[ISO7816.OFFSET_INS]){
            case INS_INC:{
                compteur++;
                return;
            }
            case INS_DEC:{
                compteur--;
                return;
            }
            case INS_INI:{
                compteur=0;
                return;
            }
            case INS_LIR:{
                a[0] = compteur;
                apdu.setOutgoingAndSend((short) 0, (short)1);
                return;
            }
            default:{
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
            }

        }
    }
}