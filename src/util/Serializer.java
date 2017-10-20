/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import controller.PcktReceiver;
import controller.PcktSender;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 2224715
 */
public class Serializer {
    
    public static byte[] serializeObj(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream ous = new ObjectOutputStream(baos);
            ous.writeObject(obj);
            return baos.toByteArray();

        } catch (IOException ex) {
            Logger.getLogger(PcktSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static Object recoverObj(byte[] bytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException ex) {
            Logger.getLogger(PcktReceiver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PcktReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
