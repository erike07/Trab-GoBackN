/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import controller.UDPSender;

/**
 *
 * @author 2224715
 */
public class TelaSender extends JFrame {

    UDPSender sender;
    private JLabel lbTit, lbFile, lbIPDst, lbPort, lbRTT, lbMSS, lbWsize, lbTimeout;
    private JFileChooser fcDir;
    private JTextField tfIP, tfPort, tfDir, tfRTT, tfMSS, tfWsize, tfTimeout;
    private JButton btSend, btChoose;
    private String dir = "", fileName = "";
    private File selectedFile;
    private JTextField tfBuf;
    private JanelaSender txtPanel;
    public static TelaSender tprincipalSender;

    public JanelaSender getJanela() {
        return txtPanel;
    }

    public static void main(String args[]) throws Exception {
        tprincipalSender = new TelaSender();
        tprincipalSender.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        tprincipalSender.setLocation((screen.width - tprincipalSender.getWidth()) / 2, (screen.height - tprincipalSender.getHeight()) / 2);
        tprincipalSender.setVisible(true);

    }

    public TelaSender() throws ParseException, UnknownHostException {
        initComp();
        events();

    }

    private void initComp() throws ParseException, UnknownHostException {
        setTitle("Trabalho GoBackN");
        setBounds(0, 0, 900, 900);
        setLayout(null);

        tfBuf = new JTextField();
        tfBuf.setBounds(20, 10, 250, 25);
        tfBuf.setEnabled(false);

        lbTit = new JLabel("Trabalho de Redes - Protocolo Go-Back-N", JLabel.RIGHT);
        lbTit.setBounds(230, 15, 300, 25);

        lbFile = new JLabel("Selecione um Arquivo:", JLabel.RIGHT);
        lbFile.setBounds(137, 60, 200, 25);

        tfDir = new JTextField();
        tfDir.setBounds(340, 60, 270, 25);
        tfDir.setEnabled(false);

        btChoose = new JButton("...");
        btChoose.setBounds(610, 60, 25, 25);

        lbIPDst = new JLabel("IP de Destino:", JLabel.RIGHT);
        lbIPDst.setBounds(237, 90, 100, 25);

        tfIP = new JTextField();
        tfIP.setBounds(340, 90, 150, 25);
        tfIP.setText("localhost");

        lbPort = new JLabel("Porta:", JLabel.RIGHT);
        lbPort.setBounds(237, 120, 100, 25);

        tfPort = new JTextField();
        tfPort.setBounds(340, 120, 150, 25);
        tfPort.setText("4444");

        lbMSS = new JLabel("MSS:", JLabel.RIGHT);
        lbMSS.setBounds(237, 150, 100, 25);

        tfMSS = new JTextField();
        tfMSS.setBounds(340, 150, 150, 25);
        tfMSS.setText("1400");

//        lbRTT = new JLabel("RTT (ms):",JLabel.RIGHT);
//        lbRTT.setBounds(237,180,100,25);
//        
//        tfRTT = new JTextField();
//        tfRTT.setBounds(340,180,150,25);
//        tfRTT.setText("200");
        lbWsize = new JLabel("Tamanho da Janela:", JLabel.RIGHT);
        lbWsize.setBounds(137, 180, 200, 25);

        tfWsize = new JTextField();
        tfWsize.setBounds(340, 180, 150, 25);
        tfWsize.setText("4");

        lbTimeout = new JLabel("Timeout da Janela (ms):", JLabel.RIGHT);
        lbTimeout.setBounds(137, 210, 200, 25);

        tfTimeout = new JTextField();
        tfTimeout.setBounds(340, 210, 150, 25);
        tfTimeout.setText("3000");

        btSend = new JButton("Enviar");
        btSend.setBounds(340, 240, 80, 30);
        btSend.setEnabled(false);

        add(lbFile);
        add(tfDir);
        add(btChoose);
        add(lbIPDst);
        add(tfIP);
        add(lbPort);
        add(tfPort);
        add(btSend);
        add(lbMSS);
        add(tfMSS);
        //add(tfRTT);
        //add(lbRTT);
        add(lbWsize);
        add(tfWsize);
        add(tfTimeout);
        add(lbTimeout);
        add(lbTit);
        txtPanel = new JanelaSender();
        txtPanel.setBounds(50, 250, 750, 600);
        add(txtPanel);

    }

    private void events() {
        btChoose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                fcDir = new JFileChooser();
                fcDir.setBounds(160, 50, 400, 250);
                fcDir.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fcDir.setDialogTitle("Selecione um Arquivo");

                int i = fcDir.showSaveDialog(null);

                if (i == 1) {
                    tfDir.setText("");
                } else {
                    selectedFile = fcDir.getSelectedFile();
//                    System.out.println(selectedFile.getName());
                    fileName = selectedFile.getName();
                    dir = selectedFile.getPath();
                    System.out.println("Arquivo " + fileName + " selecionado.");
                    tfDir.setText(selectedFile.getPath());
                    btSend.setEnabled(true);
                }
            }
        });

        btSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
//                    ip = InetAddress.getByName(tfIP.getText().trim());
//                    port = Integer.parseInt(tfPort.getText());
//                    fileName = selectedFile.getPath();
//                    dir = selectedFile.getName();
                    sender = new UDPSender(fileName, dir, InetAddress.getByName(tfIP.getText().trim()), Integer.parseInt(tfPort.getText()), Integer.parseInt(tfWsize.getText()), Integer.parseInt(tfTimeout.getText()), Integer.parseInt(tfMSS.getText()));
                    sender.start();
                    //sender.processAndSend();

                } catch (UnknownHostException ex) {
                    Logger.getLogger(UDPSender.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(UDPSender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
