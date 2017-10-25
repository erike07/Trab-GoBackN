package controller;

import model.UDPFile;
import model.UDPFileInformation;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import util.Serializer;
import view.TelaSender;

public class PcktSender extends Thread implements PcktControl {

    private ArrayList<UDPFile> buffer;
    private InetAddress ip;
    private int port;
    private String fileName, dir;
    private int timeOut; //tempo em ms
    private int windowSize; //tamanho da janela de envio
    private int mss;
    private volatile ACKReceiver ackControl;

    @Override
    public void run() {
        try {
            processAndSend();
        } catch (Exception ex) {
            Logger.getLogger(PcktSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PcktSender() {

    }

    public PcktSender(String fileName, String dir, InetAddress ip, int port, int windowSize, int timeOut, int mss) {
        this.fileName = fileName;
        this.dir = dir;
        this.ip = ip;
        this.port = port;
        this.windowSize = windowSize;
        this.timeOut = timeOut;
        this.mss = mss;
    }

    public void processAndSend() throws Exception {

        InputStream is = (InputStream) (new FileInputStream(dir));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = new byte[1460];
        int nRead;

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            baos.write(data, 0, nRead);
        }
        baos.flush();
        byte[] sendData2 = baos.toByteArray();
        int cont = 0;
        int seqPckts = 0;
        int pckt = mss;
        int totalPackts = (sendData2.length % mss > 0) ? sendData2.length / mss + 1 : sendData2.length / mss;
        UDPFileInformation ufi = new UDPFileInformation(fileName, sendData2.length, totalPackts);
        DatagramSocket clientSocket;
        clientSocket = new DatagramSocket();
        byte[] fileByteInfo = Serializer.serializeObj(ufi);

        DatagramPacket sendPacketInfo = new DatagramPacket(fileByteInfo, fileByteInfo.length, ip, port);
        clientSocket.send(sendPacketInfo);
        clientSocket.close();
        TelaSender.tprincipalSender.getJanela().escreverPckt("File Informations");
        TelaSender.tprincipalSender.getJanela().escreverPckt("Name: " + fileName);
        TelaSender.tprincipalSender.getJanela().escreverPckt("Size: " + sendData2.length);
        TelaSender.tprincipalSender.getJanela().escreverPckt("Packages: " + totalPackts);
        buffer = new ArrayList<>();
        TelaSender.tprincipalSender.getJanela().escreverPckt("Envio");
        while (cont < sendData2.length) {
            byte[] sendData = new byte[mss];
            for (int i = 0; i < pckt && cont < sendData2.length; i++) {
                sendData[i] = sendData2[cont++];
            }
            UDPFile uf = new UDPFile();
            uf.setContent(sendData);
            uf.setSequence(seqPckts++);
            buffer.add(uf);
        }
        TelaSender.tprincipalSender.getJanela().escreverPckt("Iniciando o envio");
        windowManager();
        TelaSender.tprincipalSender.getJanela().escreverPckt("Envio concluído");
    }

    private void packetSend(UDPFile uf) throws SocketException, IOException {

        DatagramSocket clientSocket = new DatagramSocket();
        byte[] fileByte = Serializer.serializeObj(uf);
        DatagramPacket sendPacket = new DatagramPacket(fileByte, fileByte.length, ip, port);
        clientSocket.send(sendPacket);
        if (uf.getSequence() != -1) {
            TelaSender.tprincipalSender.getJanela().escreverPckt("Pacote Enviado: " + uf.getSequence());
        }
        clientSocket.close();
    }

    private void windowManager() throws IOException, InterruptedException {
        //ExecutorService requisitionPool = Executors.newFixedThreadPool(1);
        int[] xSent = new int[buffer.size()];
        Map<Integer, Long> timeEnvio = new HashMap<>();
        String printxSent = "";
        String printJanelas = "";
        DatagramSocket ackSocket = new DatagramSocket(port + 1);
        //ackSocket.setSoTimeout(20000);
        ackControl = new ACKReceiver(ackSocket, buffer.size());
        ackControl.start();
        int pau = 0;
        while (ackControl.getWaitingFor() <= buffer.get(buffer.size() - 1).getSequence()) {
            while (ackControl.getNextSend() < buffer.size() && buffer.get(ackControl.getNextSend()).getSequence() - ackControl.getWaitingFor() < windowSize) {
                if (timeEnvio.get(ackControl.getNextSend()) != null && timeEnvio.get(ackControl.getNextSend()) + timeOut > System.currentTimeMillis()) {
                    continue;
                }
                timeEnvio.put(ackControl.getNextSend(), System.currentTimeMillis());
                pau = ackControl.getNextSend();
                packetSend(buffer.get(ackControl.getNextSend()));
                xSent[buffer.get(ackControl.getNextSend()).getSequence()] = xSent[buffer.get(ackControl.getNextSend()).getSequence()] + 1; //imprimir quantidade de vezes enviado
                printJanelas = "Janela Atual: ";
                for (int i = ackControl.getWaitingFor(); i < ackControl.getWaitingFor() + windowSize && i < buffer.size(); i++) {
                    printJanelas += i + " ";
                }
                TelaSender.tprincipalSender.getJanela().escreverJanelas(printJanelas);
                if (pau == ackControl.getNextSend()) {
                    ackControl.setNextSend(ackControl.getNextSend() + 1);
                }
            }
            
        }

        for (int h = 0; h < buffer.size(); h++) {
            if(xSent[h]!=1) printxSent += "\nPacote: " + h + "\nEnviado: " + xSent[h] + " vezes";
        }
        
        TelaSender.tprincipalSender.getJanela().escreverReenvio(printxSent);
        UDPFile finish = new UDPFile();
        finish.setSequence(-1);
        packetSend(finish);
        //ackSocket.close();

    }


    @Override
    public ACKReceiver getAckControl() {
        return ackControl;
    }

    @Override
    public void setAckControl(ACKReceiver ackSender) {
        this.ackControl = ackSender;
    }

    @Override
    public ByteArrayOutputStream getBaos() {
        return null;
    }

    @Override
    public int getPort() {
        return port;
    }
}
