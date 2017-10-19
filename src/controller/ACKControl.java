/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import view.TelaReceiver;
import view.TelaSender;

/**
 *
 * @author 2224715
 */
public class ACKControl extends Thread {

    private final DatagramSocket ackSocket;

    private volatile int nextSend;
    private volatile int waitingFor;
    private volatile boolean flagEnvio;
    private int numPackets;

    public ACKControl(DatagramSocket ackSocket, int numPackets) {
        this.ackSocket = ackSocket;
        this.nextSend = 0;
        this.waitingFor = 0;
        this.numPackets = numPackets;
    }

    public ACKControl() {
        this.ackSocket = null;
        this.nextSend = 0;
        this.waitingFor = 0;
    }

    @Override
    public void run() {
        int ackRecebido;
        int lastAck=0;
        while (waitingFor < numPackets ) {
            try {
                ackRecebido = ackReceive();
                if (waitingFor > ackRecebido) {
                    waitingFor = lastAck + 1;
                    nextSend = waitingFor;    
                } else if (waitingFor == ackRecebido) {
                    waitingFor++;
                    //flagEnvio=true;
                    lastAck=ackRecebido;
                } else if (waitingFor < ackRecebido) {
                    waitingFor = ackRecebido + 1;
                    lastAck=ackRecebido;
                    
                }
            } catch (SocketTimeoutException e) {
                nextSend=waitingFor;
                //flagEnvio=true;
                System.out.println("\n\n\n\n\nCAIU NO SOCKET EXCEPTION     "+waitingFor+"\n\n\n\n\n");
                
            } catch (IOException ex) {
                Logger.getLogger(ACKControl.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
    }

    public int ackReceive() throws SocketException, IOException {
        int packt = 91;
        int ackN = 0;
        int tmp;
        final int ACK_LENGTH = 4;
        byte[] receiveData = new byte[packt];
        DatagramPacket receiveAck = new DatagramPacket(receiveData, receiveData.length);
        ackSocket.receive(receiveAck);
               
        byte[] ackByte = receiveAck.getData();
        //imprimir número do ACK recebido
        for (int i = ACK_LENGTH - 1; i >= 0; i--) {
            int p2 = ((ACK_LENGTH - i - 1) * 8);
            tmp = ackByte[i] << p2;
            ackN += tmp < 0 ? tmp + (2 << (p2 + 7)) : tmp;
        }
        TelaSender.tprincipalSender.getJanela().escreverAcks("Ack Recebido: " + ackN);
        return ackN;
    }

    public void ackSend(int sequence, InetAddress ip, int porta) throws SocketException, IOException {
        int ackN = 0;
        int tmp;
        final int ACK_LENGTH = 4;

        byte[] ackByte = new byte[]{(byte) ((sequence & 4278190080L) >> 24), (byte) ((sequence & 16711680) >> 16), (byte) ((sequence & 65280) >> 8), (byte) (sequence & 255)};//65280
        DatagramPacket sendPacket = new DatagramPacket(ackByte, ackByte.length, ip, porta);

        //imprimir número do ACK enviado
        
        for (int i = ACK_LENGTH - 1; i >= 0; i--) {
            int p2 = ((ACK_LENGTH - i - 1) * 8);
            tmp = ackByte[i] << p2;
            ackN += tmp < 0 ? tmp + (2 << (p2 + 7)) : tmp;
        }
        //System.out.println("Ack Enviado: "+ackByte[0]+"-"+ackByte[1]+"-"+ackByte[2]+"-"+ackByte[3]);
        
        ackSocket.send(sendPacket);
       TelaReceiver.tprincipalReceiver.getJanela().escreverAckEnv("Ack Enviado: " + ackN);
    }

    public int getNextSend() {
        return nextSend;
    }

    public void setNextSend(int nextSend) {
        this.nextSend = nextSend;
    }

    public synchronized int getWaitingFor() {
        return waitingFor;
    }

    public synchronized void setWaitingFor(int waitingForAck) {
        this.waitingFor = waitingForAck;
    }
    
    public int getNumPackets() {
        return numPackets;
    }

    public boolean getFlagEnvio() {
        return flagEnvio;
    }

    public synchronized void setFlagEnvio(boolean deuTimeOut) {
        this.flagEnvio = deuTimeOut;
    }
    
    
}
