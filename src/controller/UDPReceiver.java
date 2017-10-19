package controller;

import view.JanelaReceiver;
import util.UDPFile;
import util.UDPFileInformation;
import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.TimerX;
import view.TelaReceiver;

public class UDPReceiver extends Thread implements UDPControl {

    private static int port;
    private static int rtt;
    private static final Set<TimerX> timers = Collections.synchronizedSet(new HashSet<TimerX>());
    private ByteArrayOutputStream ops;
    private volatile ACKControl ackSender;
    private DatagramPacket receivePacket;
    private static DatagramSocket serverSocket;

    public void processAndReceive() throws Exception {
        long inicioTempotransmisao;
        long fimTempotransmisao;
        long totalTempotransmisao;
        double vazao;

        port = TelaReceiver.tprincipalReceiver.getJanela().getPort();
        rtt = TelaReceiver.tprincipalReceiver.getJanela().getRTT();
        serverSocket = new DatagramSocket(port);
        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Na escuta...");
        int packt = 1600; //precisa ter 87 posições a mais que o client
        byte[] receiveData = new byte[packt];
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);
        inicioTempotransmisao = System.currentTimeMillis();
        UDPFileInformation ufi = (UDPFileInformation) UDPFile.recoverObj(receivePacket.getData());
        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Arquivo a ser recebido");
        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Nome: " + ufi.getName());
        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Tamanho(B): " + ufi.getSizeFile());
        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Pacotes: " + ufi.getNumPackets());
        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("\n");
        ops = new ByteArrayOutputStream();
        DatagramSocket ackSocket = new DatagramSocket();
        ackSender = new ACKControl(ackSocket, ufi.getNumPackets());
        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Total: " + ackSender.getNumPackets());
        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Inciando recepção de pacotes");
        ExecutorService requisitionPool = Executors.newFixedThreadPool(1);
        serverSocket.setSoTimeout(2000);

        while (ackSender.getWaitingFor() < ackSender.getNumPackets()) {

            DatagramPacket receivePkt = new DatagramPacket(receiveData, receiveData.length);

            try {
                serverSocket.receive(receivePkt);
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                break;
            }
            TimerX timerXX = new TimerX(this, receivePkt.getAddress(), receivePkt.getData().clone(), rtt);
            requisitionPool.submit(timerXX);
            //timerXX.inicioTempotransmisao();
            while (timerXX.isDied()) {
                synchronized (this) {
                    wait(10L);
                };
            }
        }

        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Preparando para gravar arquivo");
        FileOutputStream fos = new FileOutputStream("C:\\Users\\2224715\\" + ufi.getName());
        fos.write(ops.toByteArray());
        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Arquivo gravado com sucesso");
        fos.close();
        //serverSocket.close();
        //ackSocket.close();
        fimTempotransmisao = System.currentTimeMillis();
        totalTempotransmisao = (fimTempotransmisao - inicioTempotransmisao) / 1000;
        vazao = ((ufi.getSizeFile() * 8) / 1024) / totalTempotransmisao;

        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Tempo Total do Recebimento: " + totalTempotransmisao + " segundos");
        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Tamanho do Arquivo: " + ufi.getSizeFile() + " Bytes");
        TelaReceiver.tprincipalReceiver.getJanela().escreverPcktrec("Vazão: " + vazao + " kbps");
    }

    @Override
    public synchronized void run() {
        try {
            processAndReceive();
        } catch (Exception ex) {
            Logger.getLogger(JanelaReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ByteArrayOutputStream getBaos() {
        return ops;
    }

    @Override
    public ACKControl getAckControl() {
        return ackSender;
    }

    @Override
    public void setAckControl(ACKControl ackSender) {
        ackSender = ackSender;
    }

    @Override
    public int getPort() {
        return port;
    }

    public static void closeSsocket() {
        serverSocket.close();
    }

}
