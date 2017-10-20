package model;

import java.io.Serializable;
import java.util.Date;

public class UDPFileInformation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int sizeFile;
    private int numPackets;    

    public UDPFileInformation() {
    }

    public UDPFileInformation(String name, int sizeFile, int numPackets) {
        this.name = name;
        this.sizeFile = sizeFile;
        this.numPackets = numPackets;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSizeFile() {
        return sizeFile;
    }

    public void setSizeFile(int sizeFile) {
        this.sizeFile = sizeFile;
    }

    public int getNumPackets() {
        return numPackets;
    }

    public void setNumPackets(int numPackets) {
        this.numPackets = numPackets;
    }
    
}
