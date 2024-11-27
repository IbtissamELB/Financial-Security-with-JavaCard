/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app_compteur24;

import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

/**
 *
 * @author INPT
 */
public class App_Compteur24 {

    private static byte[] SELECT_APPLET = {0x00,(byte) 0xA4, 0x04, 0x00, 0X06, (byte)0XAA, 0X01, 0X23,
        0X77, 0X5D,(byte) 0XD0};
    public final static byte CLA = (byte) 0x80;
    public final static byte INS_INC = (byte) 0x00;
    public final static byte INS_DEC = (byte) 0x01;
    public final static byte INS_INI = (byte) 0x02;
    public final static byte INS_LIR = (byte) 0x03;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws CardException {
       TerminalFactory tf = TerminalFactory.getDefault();
        CardTerminals readers = tf.terminals();
        List<CardTerminal> readersList = readers.list();
        readersList.forEach((ct) -> {
            System.out.println(ct.getName());
        }); 
        CardTerminal myReader = readers.getTerminal ("Alcorlink USB Smart Card Reader 0");
        Card ma_carte = null;
        if (myReader.isCardPresent()){
            ma_carte = myReader.connect("*");
            if (ma_carte !=null) {
                System.out.println("ATR = "+conv_hex(ma_carte.getATR().getBytes()));
                CardChannel ch = ma_carte.getBasicChannel();
                CommandAPDU APDU = new CommandAPDU(SELECT_APPLET);
                ResponseAPDU rep = ch.transmit(APDU);
                if (rep.getSW()==0x9000){
                    System.out.println("Ok : APPLET selected");
                }else{
                    System.out.println("Nok : APPLET not found"+Integer.toHexString(rep.getSW()));
                }
                byte[] apdu =new byte[5];
                apdu[0]=CLA;
                apdu[1]=INS_INC;
                apdu[2]=0x00;
                apdu[3]=0x00;
                apdu[4]=0x00;
                APDU = new CommandAPDU(apdu);
                rep = ch.transmit(APDU);
                if (rep.getSW()==0x9000){
                    System.out.println("Ok : compteur updated");
                }else{
                    System.out.println("Nok : "+Integer.toHexString(rep.getSW()));
                }
                apdu =new byte[5];
                apdu[0]=CLA;
                apdu[1]=INS_LIR;
                apdu[2]=0x00;
                apdu[3]=0x00;
                apdu[4]=0x00;
                APDU = new CommandAPDU(apdu);
                rep = ch.transmit(APDU);
                if (rep.getSW()==0x9000){
                    System.out.println("Ok : compteur = "+conv_hex(rep.getData()));
                }else{
                    System.out.println("Nok : "+Integer.toHexString(rep.getSW()));
                }
                apdu[1]=INS_INI;
                apdu[2]=0x00;
                apdu[3]=0x00;
                apdu[4]=0x00;
                APDU = new CommandAPDU(apdu);
                rep = ch.transmit(APDU);
                if (rep.getSW()==0x9000){
                    System.out.println("Ok : compteur initialized");
                }else{
                    System.out.println("Nok : "+Integer.toHexString(rep.getSW()));
                }
                apdu[1]=INS_LIR;
                apdu[2]=0x00;
                apdu[3]=0x00;
                apdu[4]=0x00;
                APDU = new CommandAPDU(apdu);
                rep = ch.transmit(APDU);
                if (rep.getSW()==0x9000){
                    System.out.println("Ok : compteur = "+conv_hex(rep.getData()));
                }else{
                    System.out.println("Nok : "+Integer.toHexString(rep.getSW()));
                }                
            }
        }

    }
        private static String conv_hex(byte[] v){
        StringBuilder s = new StringBuilder();
        for (byte b:v)
           s.append(String.format("%02X ", b));
        return s.toString();
    }
    
}