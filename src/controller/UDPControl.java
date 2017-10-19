/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.ByteArrayOutputStream;

/**
 *
 * @author 2224715
 */
public interface UDPControl {
    

    public ACKControl getAckControl();

    public void setAckControl(ACKControl ackSender);

    public ByteArrayOutputStream getBaos();
    
    public int getPort();
    
}
