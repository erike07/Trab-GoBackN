package model;


import controller.PcktReceiver;
import java.beans.Transient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] content;
    private int sequence; // Dois bytes = 65536
    private transient long tn;
    
    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public long getTn() {
        return tn;
    }

    public void setTn(long tn) {
        this.tn = tn;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.sequence;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UDPFile other = (UDPFile) obj;
        if (this.sequence != other.sequence) {
            return false;
        }
        return true;
    }
       
}
