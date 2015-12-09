package br.com.bernardescosta.test;

import br.com.bernardescosta.usbserial.USBSerial;

public class Test_USB {
    public static void main(String[] args) {
        System.out.println("Pesquisando portas seriais\n======================");
        var_dump(USBSerial.getPorts(), 20);
        
        USBSerial serial = new USBSerial();
        
        System.out.println("\nEfetuando a conexao\n======================");
        int r = serial.setConnection("/dev/ttyUSB0", 6400, 10000);
        System.out.println(r);
        
        serial.setWritable(true);
        System.out.println("\nEscrevendo dados\n======================");
        
        while(true)
            serial.writeData(new byte[]{'\0'});
        
        //serial.close();
    }
    
    private static void var_dump(String array[], int size) {
        System.out.println("Dump {");
        for(int i=0; i<size; i++) {
            if(array[i] != null)
                System.out.println(array[i]);
        }
        System.out.println("}");
    }
}
