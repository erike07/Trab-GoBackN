/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.UDPFile;
import view.TelaReceiver;

/**
 *
 * @author 2224715
 */
public class ACKSender extends Thread {

    private Set<UDPFile> packts = Collections.synchronizedSet(new HashSet<>());

    private int waitingFor;
    private int port;
    private InetAddress ip;
    private ByteArrayOutputStream ops;
    private int numPackets;
    private boolean endFlag;

    public ACKSender(InetAddress ip, int port, ByteArrayOutputStream ops, int numPackets) {

        this.waitingFor = 0;
        this.numPackets = numPackets;
        this.ip = ip;
        this.ops = ops;
        this.port = port;
        endFlag = false;

    }

    @Override
    public void run() {
        UDPFile uf;
        while (!endFlag) {
            uf = moduloAtraso();
            if (uf != null) {
                processaPacote(uf);
            }
        }
    }

    private synchronized UDPFile moduloAtraso() {
        
        for (UDPFile packt : packts) {
            if (packt.getTn() <= System.currentTimeMillis()) {
                packts.remove(packt);
                return packt;
            }
        }
        return null;
    }

    private void processaPacote(UDPFile uf) {
        try {
            System.out.println(
                    "\ngetSequence:" + uf.getSequence() + "\nwaitingFor:"
                    + waitingFor
                    + "\nnumPackets:" + numPackets + "\n");
            if (uf.getSequence() != waitingFor && waitingFor != 0 && uf.getSequence() != -1) {
                ackSend(waitingFor - 1, ip, port + 1);
                return;
            } else if (waitingFor == uf.getSequence() && uf.getSequence() != -1) {

                TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Pacote: " + uf.getSequence() + " "
                        + (uf.getSequence() * 100 / (numPackets - 1)) + "%");
                //if (uf.getSequence() != udpControl.getAckControl().getNumPackets()-1)
                waitingFor++;
                ackSend(waitingFor - 1, ip, port + 1);

            } else if (uf.getSequence() == -1) {
                UDPReceiver.closeSsocket();
                return;
            }
            ops.write(uf.getContent()); //escreve pacote recebido no array
        } catch (IOException ex) {
            Logger.getLogger(ACKSender.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        DatagramSocket ackSocket = new DatagramSocket();
        ackSocket.send(sendPacket);
        TelaReceiver.tprincipalReceiver.getJanela().escreverAckEnv("Ack Enviado: " + ackN);

    }

    public synchronized void addPacketToSet(UDPFile uf) {
        packts.add(uf);
    }

    public int getWaitingFor() {
        return waitingFor;
    }

    public void setWaitingFor(int waitingFor) {
        this.waitingFor = waitingFor;
    }

    public int getNumPackets() {
        return numPackets;
    }

    public boolean isEndFlag() {
        return endFlag;
    }

    public void setEndFlag(boolean endFlag) {
        this.endFlag = endFlag;
    }

}
