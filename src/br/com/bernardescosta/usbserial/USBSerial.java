package br.com.bernardescosta.usbserial;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

    
public class USBSerial implements SerialPortEventListener {
    CommPortIdentifier cp;
    SerialPort port;
    private OutputStream out;
    private InputStream in; 
            
    String port_name;
    int baudrate;
    int timeout;
    
    boolean canRead;
    boolean canWrite;
    
    ByteBuffer dataBuffer;
    
    public USBSerial() {
        dataBuffer = ByteBuffer.allocate(65535);
    }
    
    /**
     * 
     * @param p
     * @param b
     * @param t
     * 
     * @return 0 - Sucess
     * @return 1 - No Port encountered
     * @return 2 - Port in use
     * @return 3 - Port don't operate this way
     * @return 4 - Can't get port IO
     * @return 5 - Listener Error
     */
    public int setConnection(String p, int b, int t) {
        this.port_name = p;
        this.baudrate = b;
        this.timeout = t;
        
        try {
            this.cp = CommPortIdentifier.getPortIdentifier(this.port_name);
        } catch (NoSuchPortException ex) { return 1; }
        if(cp == null) return 1;
        
        try {
            port = (SerialPort) cp.open("Serial1", this.timeout);
            //port.setSerialPortParams(this.baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
          
            
            in  = port.getInputStream();
            out = port.getOutputStream();
            
            port.addEventListener(this);
            port.notifyOnDataAvailable(canRead);
        } 
        catch (TooManyListenersException ex) { return 5; }
        catch (IOException ex) { return 4; }
        //catch (UnsupportedCommOperationException ex) { return 3; } 
        catch (PortInUseException ex) { return 2; }
        
        return 0;
    }
    
    public void setReadable(boolean read) {
        this.canRead = read;
    }
    
    public void setWritable(boolean write) {
        this.canWrite = write;
    }
    
    public boolean hasData() {
        return dataBuffer.hasArray();
    }

    /**
     * @TODO
     */
    public byte readData() {
        byte data = dataBuffer.get(0);
        return '\0';
    }
    
    public boolean writeData(byte[] b) {
        if(!canWrite) return false;
        
        try {
            out.write(b);
            return true;
        } catch (IOException ex) { return false; }
    }
    
    public static String[] getPorts() {
        String ports[] = new String[20];
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        
        int i = 0;
        while(portList.hasMoreElements()) {
            CommPortIdentifier ips = (CommPortIdentifier) portList.nextElement();
            ports[i] = ips.getName();
            i++;
        }
        
        return ports;
    }
    
    public void close() {
        port.close();
    }

    @Override
    public void serialEvent(SerialPortEvent spe) {
        if(spe.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            int newData = 0;
            while(newData != -1) {
                try {
                    newData = this.in.read();
                    if(newData == -1) return;
                    
                    if('\r' == (char) newData) dataBuffer.putChar('\n');
                } catch (IOException ex) {}
            }
        }
    }
}
