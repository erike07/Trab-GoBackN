/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import model.UDPFile;
import controller.UDPControl;
import controller.UDPReceiver;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import view.TelaReceiver;

/**
 *
 * @author 2224715
 */
public class TimerX extends Thread {

    private final int MAX_TIME = 1000;
    private int rtt;
    private final double X = Math.random() * 100;
    private static int nextId = 0;
    private int threadId;
    private static long variavelX = 60;
    private long time;
    private final UDPControl udpControl;
    private final InetAddress address;
    private final byte[] data;
    private boolean died;

    public TimerX(UDPControl udpc, InetAddress address, byte[] data, int rtt) {
        this.udpControl = udpc;
        this.time = (int) (Math.random() * MAX_TIME);
        this.address = address;
        this.data = data;
        this.died = true;
        this.threadId = nextId;
        this.rtt=rtt;
        nextId++;
    }

    public long getNextTime() {
        //this.time = (int) (Math.random() * MAX_TIME);
        time = time + 100;
        return time;
    }

    @Override
    public synchronized void run() {

        UDPFile uf = (UDPFile) Serializer.recoverObj(data);//is.readObject();
        died = false;
        if (perdaEatraso(uf.getSequence())) return; 
        
        try {
            System.out.println("Timer " + threadId
                    + "\ngetSequence:" + uf.getSequence() + "\nwaitingFor:"
                    + udpControl.getAckControl().getWaitingFor()
                    + "\nnumPackets:" + udpControl.getAckControl().getNumPackets() + "\n"
            //+ "Delay: " + printDelay + "\n"
            );

            if (uf.getSequence() != udpControl.getAckControl().getWaitingFor() && udpControl.getAckControl().getWaitingFor() != 0 && uf.getSequence() != -1) {
                udpControl.getAckControl().ackSend(udpControl.getAckControl().getWaitingFor() - 1, address, udpControl.getPort() + 1);
                return;
            } else if (udpControl.getAckControl().getWaitingFor() == uf.getSequence() && uf.getSequence() != -1) {
                
                TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Pacote: " + uf.getSequence() + " "
                        + (uf.getSequence() * 100 / (udpControl.getAckControl().getNumPackets() - 1)) + "%");
                //if (uf.getSequence() != udpControl.getAckControl().getNumPackets()-1)
                    udpControl.getAckControl().setWaitingFor(udpControl.getAckControl().getWaitingFor() + 1);
                udpControl.getAckControl().ackSend(udpControl.getAckControl().getWaitingFor()-1, address, udpControl.getPort() + 1);

            } else if (uf.getSequence() == -1) {
                udpControl.getBaos().flush();
                UDPReceiver.closeSsocket();
                return;
            }
            udpControl.getBaos().write(uf.getContent()); //escreve pacote recebido no array
            
        } catch (IOException ex) {
            Logger.getLogger(TimerX.class.getName()).log(Level.SEVERE, null, ex);

        }
        try {
            this.interrupt();
        } catch (Throwable ex) {
            Logger.getLogger(TimerX.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("\nfinalizou thread: "+threadId);
    }

    public boolean isDied() {
        return died;
    }

    private boolean probability() {
        double p = Math.random() * 100;
        return (p < 1 - p) ? true : false;
    }

    private boolean perdaEatraso(int pctPerdido) {
        
        if (probability()) {
            TelaReceiver.tprincipalReceiver.getJanela().escreverPckperdido("Pacote Perdido nº: "+pctPerdido);
            return true;
        }

        long tempoTotal = (long) (Math.random() * 100);
        rtt += 1;
        long tempoStart = 0;
        long text=0;
        text = (long) x1();
            //tempoStart = System.currentTimeMillis() + tempoTotal;
           // while (tempoTotal > 0) {
                try {
                    wait(text);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TimerX.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("\nTempo Espera: "+text);
                //tempoTotal = tempoStart - System.currentTimeMillis();
            //}
            //wait(10L);
        
            
        return false;
        }
        
//        try {
//            wait(text);
//            System.out.println("\nEsperou: "+text);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(TimerX.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        
    

    private double x1() {
        double tAtual = 1, rt = 200, lambda = rt,  x=0;
       
            x = Math.random() * 50;
            tAtual += x+ (rt / 2) + (lambda * ((Math.pow(Math.E, -lambda * x))));
            
           
        return tAtual;
    }

    
}
